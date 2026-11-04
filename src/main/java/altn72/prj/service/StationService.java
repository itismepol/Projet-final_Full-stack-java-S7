package altn72.prj.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import altn72.prj.modele.Station;
import altn72.prj.repository.StationRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    public Optional<Station> consulterStation(Integer idStation) {
        return stationRepository.findById(idStation);
    }

    public List<Station> listerStations() {
        return stationRepository.findAllByOrderByNom();
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

}
