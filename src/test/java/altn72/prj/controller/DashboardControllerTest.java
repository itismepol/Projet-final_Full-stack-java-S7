package altn72.prj.controller;

import altn72.prj.connexion.SecurityConfig;
import altn72.prj.connexion.WebConfig;
import altn72.prj.controleur.DashboardController;
import altn72.prj.dto.DashboardDto;
import altn72.prj.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@Import({SecurityConfig.class, WebConfig.class})
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private DashboardService dashboardService;

    @Test
    void utilisateurNonConnecteRecoit401() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "AGENT")
    void unAgentNaPasAccesAuTableauDeBord() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(dashboardService);
    }

    @Test
    @WithMockUser(roles = "RESPONSABLE_EXPLOITATION")
    void leResponsableRecoitLeTableauDeBord() throws Exception {
        when(dashboardService.obtenirTableauDeBord()).thenReturn(new DashboardDto(List.of(), 0, 0, 0, 0, 0));

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalActifs").value(0))
                .andExpect(jsonPath("$.incidents").isArray());
    }
}
