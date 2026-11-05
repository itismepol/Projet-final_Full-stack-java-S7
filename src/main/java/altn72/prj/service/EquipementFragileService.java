package altn72.prj.service;

import altn72.prj.repository.IncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class EquipementFragileService {

    public static final int SEUIL_INCIDENTS = 3;
    public static final int FENETRE_JOURS = 30;

    private final IncidentRepository incidentRepository;

    public EquipementFragileService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public long compterIncidentsRecents(Integer equipementId) {
        LocalDateTime dateLimite = LocalDateTime.now().minusDays(FENETRE_JOURS);

        return incidentRepository.countByEquipementIdAndDateOuvertureGreaterThanEqual(equipementId,dateLimite);
    }

    public boolean estFragile(Integer equipementId) {
        return compterIncidentsRecents(equipementId) >= SEUIL_INCIDENTS;
    }
}