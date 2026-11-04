package altn72.prj.service;

import altn72.prj.exception.RessourceIntrouvableException;
import altn72.prj.modele.Equipement;
import altn72.prj.repository.EquipementRepository;
import altn72.prj.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConsultationEquipementService {

    private final EquipementRepository equipementRepository;
    private final StationRepository stationRepository;


    public ConsultationEquipementService(EquipementRepository equipementRepository, StationRepository stationRepository) {
        this.equipementRepository = equipementRepository;
        this.stationRepository = stationRepository;
    }

    public Equipement getEquipement(Integer idEquipement) {
        return equipementRepository.findById(idEquipement)
                .orElseThrow(() -> new RessourceIntrouvableException("Équipement", idEquipement));
    }

    public List<Equipement> listerEquipementsStation(Integer idStation) {
        if (!stationRepository.existsById(idStation)) {
            throw new RessourceIntrouvableException("Station", idStation);
        }
        return equipementRepository.findByStationIdOrderByLibelle(idStation);
    }

}
