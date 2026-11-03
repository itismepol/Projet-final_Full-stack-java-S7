package altn72.prj.controleur;

import altn72.prj.modele.Equipement;
import altn72.prj.modele.Station;
import altn72.prj.service.ConsultationEquipementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {

    private final ConsultationEquipementService consultationEquipementService;


    public StationController(ConsultationEquipementService consultationEquipementService) {
        this.consultationEquipementService = consultationEquipementService;
    }

    @GetMapping("{idStation}")
    public ResponseEntity<Station> afficherUneStation(@PathVariable("idStation") Integer idStation) {
        return consultationEquipementService.consulterStation(idStation)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{idStation}/equipements")
    public ResponseEntity<List<Equipement>> afficherEquipementsStation(@PathVariable Integer idStation){
        List<Equipement> equipements = consultationEquipementService.listerEquipementsStation(idStation);
        return equipements.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(equipements);

    }

}
