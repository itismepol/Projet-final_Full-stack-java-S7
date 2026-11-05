package altn72.prj.controller;

import altn72.prj.connexion.SecurityConfig;
import altn72.prj.connexion.UtilisateurConnecte;
import altn72.prj.connexion.UtilisateurDto;
import altn72.prj.connexion.WebConfig;
import altn72.prj.controleur.UtilisateurController;
import altn72.prj.modele.TypeProfil;
import altn72.prj.modele.Utilisateur;
import altn72.prj.service.UtilisateurService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UtilisateurController.class)
@Import({SecurityConfig.class, WebConfig.class})
class UtilisateurControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UtilisateurService utilisateurService;

    @Test
    void utilisateurNonConnecteRecoit401() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void renvoieLeProfilSansExposerLeMotDePasse() throws Exception {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(2);
        utilisateur.setLogin("resp.expl");
        utilisateur.setMotDePasse("hash-bcrypt");
        utilisateur.setPrenom("Bruno");
        utilisateur.setProfil(TypeProfil.RESPONSABLE_EXPLOITATION);
        when(utilisateurService.trouverParLogin("resp.expl")).thenReturn(UtilisateurDto.depuis(utilisateur));

        mockMvc.perform(get("/api/me").with(user(new UtilisateurConnecte(utilisateur))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prenom").value("Bruno"))
                .andExpect(jsonPath("$.profil").value("RESPONSABLE_EXPLOITATION"))
                .andExpect(jsonPath("$.motDePasse").doesNotExist());
    }
}