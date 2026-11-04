package altn72.prj.controleur;

import altn72.prj.modele.Equipement;
import altn72.prj.modele.Incident;
import altn72.prj.repository.IncidentRepository;
import altn72.prj.service.ConsultationEquipementService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/equipements")
public class EquipementController {

    private final ConsultationEquipementService consultationEquipementService;
    private final IncidentRepository incidentRepository;

    public EquipementController(ConsultationEquipementService consultationEquipementService, IncidentRepository incidentRepository) {
        this.consultationEquipementService = consultationEquipementService;
        this.incidentRepository = incidentRepository;
    }

    @GetMapping("{idEquipement}")
    public ResponseEntity<Equipement> getEquipementEtIncidents(@PathVariable("idEquipement") Integer idEquipement) {
        return consultationEquipementService.getEquipement(idEquipement)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{idEquipement}/incidents")
    public ResponseEntity<List<Incident>> getIncidentsByEquipment(@PathVariable Integer idEquipement){
        return ResponseEntity.ok(incidentRepository.getIncidentsByEquipment(idEquipement));
    }

    @GetMapping("/{idEquipement}/history")
    public ResponseEntity<List<Incident>> getHistoryIncidentByEquipment(@PathVariable Integer idEquipement){
        return ResponseEntity.ok(incidentRepository.getAllResolveIncidentsByEquipment(idEquipement));
    }

}
