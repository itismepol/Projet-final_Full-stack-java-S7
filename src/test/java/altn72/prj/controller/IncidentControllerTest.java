package altn72.prj.controller;

import altn72.prj.connexion.SecurityConfig;
import altn72.prj.connexion.WebConfig;
import altn72.prj.controleur.IncidentController;
import altn72.prj.dto.CreationIncidentRequest;
import altn72.prj.exception.OperationInterditeException;
import altn72.prj.exception.RessourceIntrouvableException;
import altn72.prj.modele.Incident;
import altn72.prj.service.IncidentService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IncidentController.class)
@Import({SecurityConfig.class, WebConfig.class})
class IncidentControllerTest {

    private static final String RESPONSABLE = "RESPONSABLE_EXPLOITATION";
    private static final String AGENT = "AGENT";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private IncidentService incidentService;

    @Test
    void utilisateurNonConnecteRecoit401() throws Exception {
        mockMvc.perform(get("/api/incidents/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = AGENT)
    void incidentInexistantRenvoie404AvecUnMessage() throws Exception {
        when(incidentService.getIncident(99)).thenThrow(new RessourceIntrouvableException("Incident", 99));

        mockMvc.perform(get("/api/incidents/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Incident introuvable : 99"));
    }

    @Test
    @WithMockUser(roles = AGENT)
    void historiqueVideRenvoie200EtUneListeVide() throws Exception {
        when(incidentService.getHistorique()).thenReturn(List.of());

        mockMvc.perform(get("/api/incidents/historique"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser(username = "agent23", roles = AGENT)
    void declarerUnIncidentRenvoie201AvecLocation() throws Exception {
        Incident cree = new Incident();
        cree.setId(5);
        when(incidentService.creerIncident(any(CreationIncidentRequest.class), eq("agent23"))).thenReturn(cree);

        mockMvc.perform(post("/api/incidents").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"equipementId": 10, "titre": "Panne", "description": "Bloqué"}"""))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/incidents/5"));
    }

    @Test
    @WithMockUser(roles = AGENT)
    void declarationSansTitreRenvoie400() throws Exception {
        mockMvc.perform(post("/api/incidents").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"equipementId": 10, "titre": " ", "description": "Bloqué"}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("titre")));
        verifyNoInteractions(incidentService);
    }

    @Test
    @WithMockUser(roles = AGENT)
    void requeteModifiantLesDonneesSansJetonCsrfRenvoie403() throws Exception {
        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = AGENT)
    void unAgentNePeutPasResoudreUnIncident() throws Exception {
        mockMvc.perform(put("/api/incidents/1/resolve").with(csrf()))
                .andExpect(status().isForbidden());
        verifyNoInteractions(incidentService);
    }

    @Test
    @WithMockUser(roles = RESPONSABLE)
    void leResponsablePeutResoudreUnIncident() throws Exception {
        when(incidentService.resolveIncident(1)).thenReturn(new Incident());

        mockMvc.perform(put("/api/incidents/1/resolve").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = RESPONSABLE)
    void resoudreUnIncidentDejaResoluRenvoie409() throws Exception {
        when(incidentService.resolveIncident(1))
                .thenThrow(new OperationInterditeException("Cet incident est déjà résolu."));

        mockMvc.perform(put("/api/incidents/1/resolve").with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Cet incident est déjà résolu."));
    }

    @Test
    @WithMockUser(roles = AGENT)
    void unAgentNePeutPasAffecterUnPrestataire() throws Exception {
        mockMvc.perform(put("/api/incidents/1/prestataire").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prestataireId\": 3}"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(incidentService);
    }

    @Test
    @WithMockUser(roles = RESPONSABLE)
    void affectationSansPrestataireRenvoie400() throws Exception {
        mockMvc.perform(put("/api/incidents/1/prestataire").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(incidentService);
    }

    @Test
    @WithMockUser(roles = AGENT)
    void unAgentNePeutPasEvaluer() throws Exception {
        mockMvc.perform(post("/api/incidents/1/evaluation").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\": 4}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = RESPONSABLE)
    void noteHorsBornesRenvoie400() throws Exception {
        mockMvc.perform(post("/api/incidents/1/evaluation").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\": 6}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La note doit être comprise entre 1 et 5."));
        verifyNoInteractions(incidentService);
    }
}
