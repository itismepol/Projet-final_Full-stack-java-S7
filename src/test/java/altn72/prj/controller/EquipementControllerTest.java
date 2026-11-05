package altn72.prj.controller;

import altn72.prj.connexion.SecurityConfig;
import altn72.prj.connexion.WebConfig;
import altn72.prj.controleur.EquipementController;
import altn72.prj.exception.RessourceIntrouvableException;
import altn72.prj.service.ConsultationEquipementService;
import altn72.prj.service.IncidentService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EquipementController.class)
@Import({SecurityConfig.class, WebConfig.class})
@WithMockUser(roles = "AGENT")
class EquipementControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ConsultationEquipementService consultationEquipementService;
    @MockitoBean
    private IncidentService incidentService;

    @Test
    void equipementInexistantRenvoie404() throws Exception {
        when(consultationEquipementService.getEquipement(99))
                .thenThrow(new RessourceIntrouvableException("Équipement", 99));

        mockMvc.perform(get("/api/equipements/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void incidentsDUnEquipementInexistantRenvoient404() throws Exception {
        when(incidentService.getIncidentsByEquipment(99))
                .thenThrow(new RessourceIntrouvableException("Équipement", 99));

        mockMvc.perform(get("/api/equipements/99/incidents"))
                .andExpect(status().isNotFound());
    }

    @Test
    void equipementSansIncidentRenvoie200EtUneListeVide() throws Exception {
        when(incidentService.getIncidentsByEquipment(10)).thenReturn(List.of());

        mockMvc.perform(get("/api/equipements/10/incidents"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}