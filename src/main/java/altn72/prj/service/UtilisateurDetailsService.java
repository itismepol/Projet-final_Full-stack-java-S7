package altn72.prj.service;

import altn72.prj.modele.UtilisateurConnecte;
import altn72.prj.modele.repository.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UtilisateurDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        return utilisateurRepository.findByLogin(login)
                .map(UtilisateurConnecte::new)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur inconnu : " + login));
    }

}
