package altn72.prj.service;

import altn72.prj.modele.Incident;
import altn72.prj.modele.Prestataire;
import altn72.prj.repository.IncidentRepository;
import altn72.prj.repository.PrestataireRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    public Optional<Prestataire> consulterPrestataire(Integer idPrestataire) {
        return prestataireRepository.findById(idPrestataire);
    }

    @Transactional
    public Prestataire ajouterPrestataire(Prestataire prestataire) {
        prestataire.setId(null);
        return prestataireRepository.save(prestataire);
    }

    public Optional<List<Incident>> getIncidentsEvaluesByPrestataire(Integer idPrestataire) {
        if (!prestataireRepository.existsById(idPrestataire)) return Optional.empty();
        return Optional.of(incidentRepository.getIncidentsEvaluesByPrestataire(idPrestataire));
    }
}
