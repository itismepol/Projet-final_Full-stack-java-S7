package altn72.prj.controleur;

import altn72.prj.modele.Incident;
import altn72.prj.modele.Prestataire;
import altn72.prj.service.PrestataireService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prestataires")
public class PrestataireController {

    private final PrestataireService prestataireService;

    public PrestataireController(PrestataireService prestataireService) {
        this.prestataireService = prestataireService;
    }

    @GetMapping
    public ResponseEntity<List<Prestataire>> getPrestataires() {
        return ResponseEntity.ok(prestataireService.getAllPrestataires());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Prestataire>> getActivePrestataires() {
        return ResponseEntity.ok(prestataireService.getAllActivePrestataires());
    }

    @GetMapping("/{idPrestataire}")
    public ResponseEntity<Prestataire> getPrestataire(@PathVariable Integer idPrestataire) {
        return prestataireService.consulterPrestataire(idPrestataire)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Prestataire> createPrestataire(@RequestBody Prestataire prestataire) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(prestataireService.ajouterPrestataire(prestataire));
    }

    @GetMapping("/{idPrestataire}/incidents-evalues")
    public ResponseEntity<List<Incident>> getIncidentsEvalues(@PathVariable Integer idPrestataire) {
        return prestataireService.getIncidentsEvaluesByPrestataire(idPrestataire)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
