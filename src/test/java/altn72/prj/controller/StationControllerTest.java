package altn72.prj.controller;

import altn72.prj.connexion.SecurityConfig;
import altn72.prj.connexion.WebConfig;
import altn72.prj.controleur.StationController;
import altn72.prj.exception.RessourceIntrouvableException;
import altn72.prj.service.ConsultationEquipementService;
import altn72.prj.service.IncidentService;
import altn72.prj.service.StationService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StationController.class)
@Import({SecurityConfig.class, WebConfig.class})
@WithMockUser(roles = "AGENT")
class StationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private StationService stationService;
    @MockitoBean private ConsultationEquipementService consultationEquipementService;
    @MockitoBean private IncidentService incidentService;

    @Test
    void stationSansEquipementRenvoie200EtUneListeVide() throws Exception {
        when(consultationEquipementService.listerEquipementsStation(1)).thenReturn(List.of());

        mockMvc.perform(get("/api/stations/1/equipements"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void stationInexistanteRenvoie404() throws Exception {
        when(consultationEquipementService.listerEquipementsStation(99))
                .thenThrow(new RessourceIntrouvableException("Station", 99));

        mockMvc.perform(get("/api/stations/99/equipements"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Station introuvable : 99"));
    }

    @Test
    void historiqueVideDUneStationRenvoie200EtUneListeVide() throws Exception {
        when(incidentService.getAllResolveIncidentsByStation(1)).thenReturn(List.of());

        mockMvc.perform(get("/api/stations/1/history"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}