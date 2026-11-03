package altn72.prj.modele;

import altn72.prj.repository.UtilisateurRepository;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class SuiviConnexion {

    private final UtilisateurRepository utilisateurRepository;

    public SuiviConnexion(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @EventListener
    @Transactional
    public void enregistrerConnexion(AuthenticationSuccessEvent evenement) {
        utilisateurRepository.findByLogin(evenement.getAuthentication().getName())
                .ifPresent(utilisateur -> utilisateur.setDerniereConnexion(LocalDateTime.now()));
    }
}
