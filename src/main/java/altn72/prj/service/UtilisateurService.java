package altn72.prj.service;

import altn72.prj.connexion.UtilisateurDto;
import altn72.prj.exception.RessourceIntrouvableException;
import altn72.prj.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public UtilisateurDto trouverParLogin(String login) {
        return utilisateurRepository.findByLogin(login)
                .map(UtilisateurDto::depuis)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur", login));
    }
}
