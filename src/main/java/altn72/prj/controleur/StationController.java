package altn72.prj.controleur;

import altn72.prj.modele.Equipement;
import altn72.prj.modele.Incident;
import altn72.prj.modele.Station;
import altn72.prj.service.ConsultationEquipementService;
import altn72.prj.service.IncidentService;
import altn72.prj.service.StationService;

import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Station>> getAllStations() {
        return ResponseEntity.ok(stationService.getAllStations());
    }

    @GetMapping("{idStation}")
    public ResponseEntity<Station> afficherUneStation(@PathVariable("idStation") Integer idStation) {
        return stationService.consulterStation(idStation)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{idStation}/equipements")
    public ResponseEntity<List<Equipement>> afficherEquipementsStation(@PathVariable Integer idStation){
        List<Equipement> equipements = consultationEquipementService.listerEquipementsStation(idStation);
        return equipements.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(equipements);
    }

    @GetMapping("{idStation}/history")
    public ResponseEntity<List<Incident>> getAllResolveIncidentsByStation(@PathVariable Integer idStation){
        List<Incident> incidents = incidentService.getAllResolveIncidentsByStation(idStation);
        return incidents.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(incidents);
    } 

}
