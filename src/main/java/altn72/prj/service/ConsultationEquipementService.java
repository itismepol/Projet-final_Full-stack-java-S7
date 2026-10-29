package altn72.prj.service;

import altn72.prj.modele.Equipement;
import altn72.prj.modele.Exception.StationIntrouvableException;
import altn72.prj.modele.Station;
import altn72.prj.modele.repository.EquipementRepository;
import altn72.prj.modele.repository.StationRepository;
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

    public ConsultationEquipementService(EquipementRepository equipementRepository, StationRepository stationRepository){
        this.equipementRepository = equipementRepository;
        this.stationRepository = stationRepository;
    }

    public Optional<Station> consulterStation(Long idStation) {
        return stationRepository.findById(idStation);
    }

    public List<Station> listerStations() {
        return stationRepository.findAllByOrderByNom();
    }

    public Optional<Equipement> getEquipement(Long idEquipement) {
        return equipementRepository.findById(idEquipement);
    }

    public List<Station> getStations() {
        return stationRepository.findAll();
    }
    public List<Equipement> getEquipements() {
        return equipementRepository.findAll();
    }

    public List<Equipement> listerEquipementsStation(Long idStation) {
        consulterStation(idStation);
        return equipementRepository.findByStationIdOrderByLibelle(idStation);
    }

    @Transactional
    public boolean supprimerStation(Long idStation) {
        if (!stationRepository.existsById(idStation)) return false;
        stationRepository.deleteById(idStation);
        return true;
    }

    public Station ajouterStation(Station station) {
        station.setId(null);
        return stationRepository.save(station);
    }

    @Transactional
    public Optional<Station> modifierStation(Long idStation, Station stationModifie) {
        return stationRepository.findById(idStation).map(stationToModify -> {
            BeanUtils.copyProperties(stationModifie, stationToModify, "id");
            return stationToModify;
        });
    }

    @Transactional
    public boolean supprimerEquipement(Long idEquipement) {
        if (!stationRepository.existsById(idEquipement)) return false;
        stationRepository.deleteById(idEquipement);
        return true;
    }

    public Equipement ajouterStation(Equipement equipement) {
        equipement.setId(null);
        return equipementRepository.save(equipement);
    }

    @Transactional
    public Optional<Equipement> modifierStation(Long idEquipement, Equipement equipementModifie) {
        return equipementRepository.findById(idEquipement).map(equipementToModify -> {
            BeanUtils.copyProperties(equipementModifie, equipementToModify, "id");
            return equipementToModify;
        });
    }

}
