package altn72.prj.modele;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class UtilisateurConnecte extends User {

    private final String prenom;
    private final TypeProfil profil;

    public UtilisateurConnecte(Utilisateur utilisateur) {
        super(utilisateur.getLogin(),
                utilisateur.getMotDePasse(),
                utilisateur.isActif(), true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_" + utilisateur.getProfil())));
        this.prenom = utilisateur.getPrenom();
        this.profil = utilisateur.getProfil();
    }

    public String getPrenom() { return prenom; }

}
