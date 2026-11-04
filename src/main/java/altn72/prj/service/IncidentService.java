package altn72.prj.service;

import altn72.prj.controleur.IncidentController;
import altn72.prj.modele.Evaluation;
import altn72.prj.modele.Incident;

import altn72.prj.modele.IncidentStatut;
import altn72.prj.modele.Prestataire;
import altn72.prj.repository.EquipementRepository;
import altn72.prj.repository.EvaluationRepository;
import altn72.prj.repository.IncidentRepository;
import altn72.prj.repository.PrestataireRepository;
import altn72.prj.repository.UtilisateurRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final EquipementRepository equipmentRepository;
    private final UtilisateurRepository userRepository;
    private final PrestataireRepository prestaRepository;
    private final EvaluationRepository evalRepo;

    public IncidentService(IncidentRepository incidentRepository, EquipementRepository equipmentRepository, UtilisateurRepository userRepository, PrestataireRepository prestaRepository, EvaluationRepository evalRepo){
        this.incidentRepository = incidentRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.prestaRepository = prestaRepository;
        this.evalRepo = evalRepo;
    }

    public Optional<Incident> getIncident(Integer idIncident) {
        return incidentRepository.findById(idIncident);
    }

    public List<Incident> getIncidents() {
        return incidentRepository.findAll();
    }

    public List<Incident> getHistorique() {
        return incidentRepository.findByStatutOrderByDateResolutionDesc(IncidentStatut.RESOLU);
    }

    public List<Incident> getAllResolveIncidentsByStation(Integer idStation) {
        return incidentRepository.getAllResolveIncidentsByStation(idStation);
    }

    public List<Incident> getAllResolveIncidentsByEquipment(Integer idEquipment) {
        return incidentRepository.getAllResolveIncidentsByEquipment(idEquipment);
    }

    public List<Incident> getIncidentsByEquipment(Integer idEquipment) {
        return incidentRepository.getIncidentsByEquipment(idEquipment);
    }

    @Transactional
    public boolean supprimerIncident(Integer idIncident) {
        if (!incidentRepository.existsById(idIncident)) return false;
        incidentRepository.deleteById(idIncident);
        return true;
    }

    @Transactional
    public Optional<Incident> creerIncident(IncidentController.CreationIncidentRequest dataForm, String auth) {
        return equipmentRepository.findById(dataForm.equipementId()).map(equipement -> {
            Incident incident = new Incident();
            incident.setEquipement(equipement);
            incident.setTitre(dataForm.titre());
            incident.setDescription(dataForm.description());
            if (dataForm.priorite() != null) incident.setPriorite(dataForm.priorite());
            incident.setStatut(IncidentStatut.OUVERT);
            incident.setDateOuverture(LocalDateTime.now());
            userRepository.findByLogin(auth).ifPresent(incident::setDeclarePar);
            return incidentRepository.save(incident);
        });
    }

    @Transactional
    public Optional<Incident> affectPresta(Integer idIncident, Integer idPrestataire) {
        Optional<Incident> incident = incidentRepository.findById(idIncident);
        Optional<Prestataire> prestataire = prestaRepository.findById(idPrestataire);
        if (incident.isEmpty() || prestataire.isEmpty()) return Optional.empty();

        Incident i = incident.get();
        if (i.getStatut() == IncidentStatut.RESOLU) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Incident déjà résolu");
        }
        i.setPrestataire(prestataire.get());
        i.setDateAffectation(LocalDateTime.now());
        i.setStatut(IncidentStatut.AFFECTE);
        return Optional.of(i);
    }

    @Transactional
    public Optional<Incident> modifierIncident(Integer idIncident, Incident incidentModifie) {
        return incidentRepository.findById(idIncident).map(incidentToModify -> {
            BeanUtils.copyProperties(incidentModifie, incidentToModify, "id");
            return incidentToModify;
        });
    }

    @Transactional
    public Optional<Incident> resolveIncident(Integer idIncident) {
        return incidentRepository.findById(idIncident).map(i -> {
            if (i.getStatut() == IncidentStatut.RESOLU) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Incident déjà résolu");
            } else if (i.getStatut() == IncidentStatut.OUVERT) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "L'incident doit être affecté avant d'être résolu");
            }
            i.setStatut(IncidentStatut.RESOLU);
            i.setDateResolution(LocalDateTime.now());
            return i;
        });
    }

    @Transactional
    public Optional<Incident> evaluateIncident(Integer idIncident, IncidentController.EvaluationRequest dataForm, String auth) {
        return incidentRepository.findById(idIncident).map(incident -> {
            if (incident.getStatut() != IncidentStatut.RESOLU) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Impossible d'évaluer un incident non résolu");
            }
            if (incident.getEvaluation() != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Incident déjà évalué");
            }
            Evaluation evaluation = new Evaluation();
            evaluation.setIncident(incident);
            evaluation.setNote(dataForm.note());
            evaluation.setCommentaire(dataForm.commentaire());
            evaluation.setDateEvaluation(LocalDateTime.now());
            userRepository.findByLogin(auth).ifPresent(evaluation::setEvaluePar);
            evalRepo.save(evaluation);
            incident.setEvaluation(evaluation);
            return incident;
        });
    }
}
