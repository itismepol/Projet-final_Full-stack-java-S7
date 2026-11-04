package altn72.prj.service;

import java.util.List;

import altn72.prj.exception.RessourceIntrouvableException;
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

    public Station consulterStation(Integer idStation) {
        return stationRepository.findById(idStation)
                .orElseThrow(() -> new RessourceIntrouvableException("Station", idStation));

    }
}