package altn72.prj.service;

import altn72.prj.modele.Equipement;
import altn72.prj.modele.Incident;
import altn72.prj.modele.Station;
import altn72.prj.exception.EquipementNotFoundException;
import altn72.prj.repository.EquipementRepository;
import altn72.prj.repository.IncidentRepository;
import altn72.prj.repository.StationRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ConsultationEquipementService {

    private final EquipementRepository equipementRepository;
    private final StationRepository stationRepository;
    private final IncidentRepository incidentRepository;

    public ConsultationEquipementService(EquipementRepository equipementRepository, StationRepository stationRepository, IncidentRepository incidentRepository) {
        this.equipementRepository = equipementRepository;
        this.stationRepository = stationRepository;
        this.incidentRepository = incidentRepository;
    }

    public Optional<Station> consulterStation(Integer idStation) {
        return stationRepository.findById(idStation);
    }

    public List<Station> listerStations() {
        return stationRepository.findAllByOrderByNom();
    }

    public Optional<Equipement> getEquipement(Integer idEquipement) {
        return equipementRepository.findById(idEquipement);
    }

    public List<Station> getStations() {
        return stationRepository.findAll();
    }

    public List<Equipement> getEquipements() {
        return equipementRepository.findAll();
    }

    public List<Equipement> listerEquipementsStation(Integer idStation) {
        return equipementRepository.findByStationIdOrderByLibelle(idStation);
    }

    public EquipementEtIncidents getEquipementEtIncidents(Integer idEquipement) {
        Equipement equipement = getEquipement(idEquipement).orElseThrow(() -> new EquipementNotFoundException(idEquipement));
        return new EquipementEtIncidents(equipement, incidentRepository.findByEquipement(idEquipement));
    }

    @Transactional
    public boolean supprimerStation(Integer idStation) {
        if (!stationRepository.existsById(idStation)) return false;
        stationRepository.deleteById(idStation);
        return true;
    }

    public Station ajouterStation(Station station) {
        station.setId(null);
        return stationRepository.save(station);
    }

    @Transactional
    public Optional<Station> modifierStation(Integer idStation, Station stationModifie) {
        return stationRepository.findById(idStation).map(stationToModify -> {
            BeanUtils.copyProperties(stationModifie, stationToModify, "id");
            return stationToModify;
        });
    }

    @Transactional
    public boolean supprimerEquipement(Integer idEquipement) {
        if (!stationRepository.existsById(idEquipement)) return false;
        stationRepository.deleteById(idEquipement);
        return true;
    }

    public Equipement ajouterStation(Equipement equipement) {
        equipement.setId(null);
        return equipementRepository.save(equipement);
    }

    @Transactional
    public Optional<Equipement> modifierStation(Integer idEquipement, Equipement equipementModifie) {
        return equipementRepository.findById(idEquipement).map(equipementToModify -> {
            BeanUtils.copyProperties(equipementModifie, equipementToModify, "id");
            return equipementToModify;
        });


    }

    public record EquipementEtIncidents(Equipement equipement, List<Incident> incidents) {}

}
