package altn72.prj.service;

import altn72.prj.exception.DonneeInvalideException;
import altn72.prj.exception.OperationInterditeException;
import altn72.prj.exception.RessourceIntrouvableException;
import altn72.prj.modele.Incident;
import altn72.prj.modele.Prestataire;
import altn72.prj.repository.IncidentRepository;
import altn72.prj.repository.PrestataireRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PrestataireService {

    private final PrestataireRepository prestataireRepository;
    private final IncidentRepository incidentRepository;

    public PrestataireService(PrestataireRepository prestataireRepository, IncidentRepository incidentRepository) {
        this.prestataireRepository = prestataireRepository;
        this.incidentRepository = incidentRepository;
    }

    public List<Prestataire> getAllPrestataires() {
        return prestataireRepository.findAll();
    }

    public List<Prestataire> getAllActivePrestataires() {
        return prestataireRepository.findByActifTrue();
    }

    public Prestataire consulterPrestataire(Integer idPrestataire) {
        return prestataireRepository.findById(idPrestataire)            .orElseThrow(() -> new RessourceIntrouvableException("Prestataire", idPrestataire));
    }

    @Transactional
    public Prestataire ajouterPrestataire(Prestataire prestataire) {
        if (!StringUtils.hasText(prestataire.getEmail()) && !StringUtils.hasText(prestataire.getTelephone())) {
            throw new DonneeInvalideException("Renseignez au moins un moyen de contact (email ou téléphone).");
        }
        if (prestataireRepository.existsByNom(prestataire.getNom())) {
            throw new OperationInterditeException("Un prestataire porte déjà ce nom.");
        }
        prestataire.setId(null);
        return prestataireRepository.save(prestataire);
    }

    public List<Incident> getIncidentsEvaluesByPrestataire(Integer idPrestataire) {
        consulterPrestataire(idPrestataire);
        return incidentRepository.getIncidentsEvaluesByPrestataire(idPrestataire);
    }
}
