package altn72.prj.controleur;

import altn72.prj.connexion.UtilisateurConnecte;
import altn72.prj.connexion.UtilisateurDto;
import altn72.prj.service.UtilisateurService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/me")
    public UtilisateurDto utilisateurCourant(@AuthenticationPrincipal UtilisateurConnecte principal) {
        return utilisateurService.trouverParLogin(principal.getUsername());
    }

}
