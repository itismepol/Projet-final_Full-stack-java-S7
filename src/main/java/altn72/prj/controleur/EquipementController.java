package altn72.prj.controleur;

import altn72.prj.modele.Equipement;
import altn72.prj.modele.Incident;
import altn72.prj.service.ConsultationEquipementService;

import java.util.List;

import altn72.prj.service.IncidentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/equipements")
public class EquipementController {

    private final ConsultationEquipementService consultationEquipementService;
    private final IncidentService incidentService;

    public EquipementController(ConsultationEquipementService consultationEquipementService, IncidentService incidentService) {
        this.consultationEquipementService = consultationEquipementService;
        this.incidentService = incidentService;
    }

    @GetMapping("{idEquipement}")
    public Equipement getEquipementEtIncidents(@PathVariable("idEquipement") Integer idEquipement) {
        return consultationEquipementService.getEquipement(idEquipement);
    }

    @GetMapping("/{idEquipement}/incidents")
    public List<Incident> getIncidentsByEquipment(@PathVariable Integer idEquipement){
        return incidentService.getIncidentsByEquipment(idEquipement);
    }

    @GetMapping("/{idEquipement}/history")
    public List<Incident> getHistoryIncidentByEquipment(@PathVariable Integer idEquipement){
        return incidentService.getAllResolveIncidentsByEquipment(idEquipement);
    }

}
