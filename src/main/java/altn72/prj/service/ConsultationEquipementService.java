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
    private final EquipementFragileService equipementFragileService;

    public ConsultationEquipementService(EquipementRepository equipementRepository, StationRepository stationRepository, EquipementFragileService equipementFragileService) {
        this.equipementRepository = equipementRepository;
        this.stationRepository = stationRepository;
        this.equipementFragileService = equipementFragileService;
    }

    public Equipement getEquipement(Integer idEquipement) {
        Equipement equipement = equipementRepository.findById(idEquipement)
                .orElseThrow(() -> new RessourceIntrouvableException("Équipement", idEquipement));

        equipement.setFragile(equipementFragileService.estFragile(equipement.getId()));
        return equipement;
    }

    public List<Equipement> listerEquipementsStation(Integer idStation) {
        if (!stationRepository.existsById(idStation)) {
            throw new RessourceIntrouvableException("Station", idStation);
        }
        return equipementRepository.findByStationIdOrderByLibelle(idStation);
    }

}
