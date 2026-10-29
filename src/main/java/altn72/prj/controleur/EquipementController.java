package altn72.prj.controleur;

import altn72.prj.modele.Equipement;
import altn72.prj.service.ConsultationEquipementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/equipements")
public class EquipementController {

    private final ConsultationEquipementService consultationEquipementService;

    public EquipementController(ConsultationEquipementService consultationEquipementService) {
        this.consultationEquipementService = consultationEquipementService;
    }

    @GetMapping("{idEquipement}")
    public ResponseEntity<Equipement> getEquipement(@PathVariable("idEquipement") Long idEquipement) {

    }
}
