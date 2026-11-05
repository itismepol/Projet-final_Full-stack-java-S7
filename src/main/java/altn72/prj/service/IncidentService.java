package altn72.prj.service;

import altn72.prj.dto.CreationIncidentRequest;
import altn72.prj.dto.EvaluationRequest;
import altn72.prj.exception.OperationInterditeException;
import altn72.prj.exception.RessourceIntrouvableException;
import altn72.prj.modele.*;

import altn72.prj.repository.*;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final StationRepository stationRepository;
    private final EquipementRepository equipmentRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PrestataireRepository prestaRepository;
    private final EvaluationRepository evalRepo;
    private final EquipementFragileService equipementFragileService;

    public IncidentService(IncidentRepository incidentRepository, StationRepository stationRepository, EquipementRepository equipmentRepository, UtilisateurRepository utilisateurRepository, PrestataireRepository prestaRepository, EvaluationRepository evalRepo, EquipementFragileService equipementFragileService){
        this.incidentRepository = incidentRepository;
        this.stationRepository = stationRepository;
        this.equipmentRepository = equipmentRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.prestaRepository = prestaRepository;
        this.evalRepo = evalRepo;
        this.equipementFragileService = equipementFragileService;
    }

    public Incident getIncident(Integer idIncident) {
        return incidentRepository.findById(idIncident)
                .orElseThrow(() -> new RessourceIntrouvableException("Incident", idIncident));
    }

    public List<Incident> getHistorique() {
        return incidentRepository.findByStatutOrderByDateResolutionDesc(IncidentStatut.RESOLU);
    }

    public List<Incident> getAllResolveIncidentsByStation(Integer idStation) {
        if (!stationRepository.existsById(idStation)) {
            throw new RessourceIntrouvableException("Station", idStation);
        }
        return incidentRepository.getAllResolveIncidentsByStation(idStation);
    }

    public List<Incident> getAllResolveIncidentsByEquipment(Integer idEquipment) {
        verifierEquipementExiste(idEquipment);
        return incidentRepository.getAllResolveIncidentsByEquipment(idEquipment);
    }

    public List<Incident> getIncidentsByEquipment(Integer idEquipment) {
        verifierEquipementExiste(idEquipment);
        return incidentRepository.getIncidentsByEquipment(idEquipment);
    }

    private void verifierEquipementExiste(Integer idEquipment) {
        if (!equipmentRepository.existsById(idEquipment)) {
            throw new RessourceIntrouvableException("Équipement", idEquipment);
        }
    }

    @Transactional
    public Incident creerIncident(CreationIncidentRequest dataForm, String login) {
        Equipement equipement = equipmentRepository.findById(dataForm.equipementId())
                .orElseThrow(() -> new RessourceIntrouvableException("Équipement", dataForm.equipementId()));
        Incident incident = new Incident();
        incident.setEquipement(equipement);
        incident.setTitre(dataForm.titre());
        incident.setDescription(dataForm.description());
        if (dataForm.priorite() != null) incident.setPriorite(dataForm.priorite());
        incident.setStatut(IncidentStatut.OUVERT);
        incident.setDateOuverture(LocalDateTime.now());
        utilisateurRepository.findByLogin(login).ifPresent(incident::setDeclarePar);
        Incident incidentEnregistre = incidentRepository.save(incident);
        equipement.setFragile(equipementFragileService.estFragile(equipement.getId()));

        return incidentEnregistre;
    }

    @Transactional
    public Incident affectPresta(Integer idIncident, Integer idPrestataire) {
        Incident incident = getIncident(idIncident);
        Prestataire prestataire = prestaRepository.findById(idPrestataire)
                .orElseThrow(() -> new RessourceIntrouvableException("Prestataire", idPrestataire));
        if (incident.getStatut() == IncidentStatut.RESOLU) {
            throw new OperationInterditeException("Un incident résolu ne peut plus être affecté.");
        }
        if (!prestataire.getActif()) {
            throw new OperationInterditeException("Ce prestataire est inactif.");
        }
        incident.setPrestataire(prestataire);
        incident.setDateAffectation(LocalDateTime.now());
        incident.setStatut(IncidentStatut.AFFECTE);
        return incident;
    }

    @Transactional
    public Incident resolveIncident(Integer idIncident) {
        Incident incident = getIncident(idIncident);
        if (incident.getStatut() == IncidentStatut.RESOLU) {
            throw new OperationInterditeException("Cet incident est déjà résolu.");
        }
        if (incident.getStatut() == IncidentStatut.OUVERT) {
            throw new OperationInterditeException("L'incident doit être affecté avant d'être résolu.");
        }
        incident.setStatut(IncidentStatut.RESOLU);
        incident.setDateResolution(LocalDateTime.now());
        return incident;
    }

    @Transactional
    public boolean supprimerIncident(Integer idIncident) {
        if (!incidentRepository.existsById(idIncident)) return false;
        incidentRepository.deleteById(idIncident);
        return true;
    }

    @Transactional
    public Incident modifierIncident(Integer idIncident, Incident incidentModifie) {
        return incidentRepository.findById(idIncident)
                .orElseThrow(() -> new RessourceIntrouvableException("Incident", idIncident));
    }

    @Transactional
    public Incident evaluateIncident(Integer idIncident, EvaluationRequest dataForm, String login) {
        Incident incident = getIncident(idIncident);
        if (incident.getStatut() != IncidentStatut.RESOLU) {
            throw new OperationInterditeException("Impossible d'évaluer un incident non résolu.");
        }
        if (incident.getEvaluation() != null) {
            throw new OperationInterditeException("Cet incident a déjà été évalué.");
        }
        Evaluation evaluation = new Evaluation();
        evaluation.setIncident(incident);
        evaluation.setNote(dataForm.note());
        evaluation.setCommentaire(dataForm.commentaire());
        evaluation.setDateEvaluation(LocalDateTime.now());
        utilisateurRepository.findByLogin(login).ifPresent(evaluation::setEvaluePar);
        evalRepo.save(evaluation);
        incident.setEvaluation(evaluation);
        return incident;
    }
}
