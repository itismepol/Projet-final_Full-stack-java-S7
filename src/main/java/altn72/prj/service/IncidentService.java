package altn72.prj.service;

import altn72.prj.modele.Incident;

import altn72.prj.modele.IncidentStatut;
import altn72.prj.modele.repository.IncidentRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository){
        this.incidentRepository = incidentRepository;
    }

    public Optional<Incident> getIncident(Long idIncident) {
        return incidentRepository.findById(idIncident);
    }

    public List<Incident> getIncidents() {
        return incidentRepository.findAll();
    }

    public List<Incident> getHistorique() {
        return incidentRepository.findByStatutOrderByDateResolutionDesc(IncidentStatut.RESOLU);
    }


    @Transactional
    public boolean supprimerIncident(Long idIncident) {
        if (!incidentRepository.existsById(idIncident)) return false;
        incidentRepository.deleteById(idIncident);
        return true;
    }

     public Incident ajouterIncident(Incident incident) {
        incident.setId(null);
        return incidentRepository.save(incident);
    }

    @Transactional
    public Optional<Incident> modifierIncident(Long idIncident, Incident incidentModifie) {
        return incidentRepository.findById(idIncident).map(incidentToModify -> {
            BeanUtils.copyProperties(incidentModifie, incidentToModify, "id");
            return incidentToModify;
        });
    }
}
