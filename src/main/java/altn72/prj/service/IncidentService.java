package altn72.prj.service;

import altn72.prj.controleur.IncidentController;
import altn72.prj.modele.Incident;

import altn72.prj.modele.IncidentStatut;
import altn72.prj.repository.EquipementRepository;
import altn72.prj.repository.IncidentRepository;
import altn72.prj.repository.UtilisateurRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final EquipementRepository equipmentRepository;
    private final UtilisateurRepository userRepository;

    public IncidentService(IncidentRepository incidentRepository, EquipementRepository equipmentRepository, UtilisateurRepository userRepository){
        this.incidentRepository = incidentRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
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
    public Optional<Incident> modifierIncident(Integer idIncident, Incident incidentModifie) {
        return incidentRepository.findById(idIncident).map(incidentToModify -> {
            BeanUtils.copyProperties(incidentModifie, incidentToModify, "id");
            return incidentToModify;
        });
    }
}
