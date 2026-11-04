package altn72.prj.controleur;

import altn72.prj.modele.Equipement;
import altn72.prj.modele.Incident;
import altn72.prj.modele.Station;
import altn72.prj.service.ConsultationEquipementService;
import altn72.prj.service.IncidentService;
import altn72.prj.service.StationService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {

    private final StationService stationService;
    private final ConsultationEquipementService consultationEquipementService;
    private final IncidentService incidentService;

    public StationController(StationService stationService, ConsultationEquipementService consultationEquipementService, IncidentService incidentService) {
        this.stationService = stationService;
        this.consultationEquipementService = consultationEquipementService;
        this.incidentService = incidentService;
    }

    @GetMapping()
    public List<Station> getAllStations() {
        return stationService.getAllStations();
    }

    @GetMapping("{idStation}")
    public Station afficherUneStation(@PathVariable("idStation") Integer idStation) {
        return stationService.consulterStation(idStation);
    }

    @GetMapping("/{idStation}/equipements")
    public List<Equipement> afficherEquipementsStation(@PathVariable Integer idStation) {
        return consultationEquipementService.listerEquipementsStation(idStation);
    }

    @GetMapping("{idStation}/history")
    public List<Incident> getAllResolveIncidentsByStation(@PathVariable Integer idStation) {
        return incidentService.getAllResolveIncidentsByStation(idStation);
    }

}
