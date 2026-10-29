package altn72.prj.controleur;

import altn72.prj.modele.Incident;
import altn72.prj.modele.IncidentStatut;
import altn72.prj.modele.Prestataire;
import altn72.prj.service.IncidentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/indicateur")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }


    @GetMapping("/{idIncident}")
    public ResponseEntity<Incident> afficherUnIncident(@PathVariable("idIncident") Long idInc) {
        return incidentService.getIncident(idInc)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{idIncident}")
    public ResponseEntity<Void> deleteIncident(@PathVariable("idIncident") Long idIncident) {
        if (incidentService.supprimerIncident(idIncident)) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Incident> creerIncident(@RequestBody Incident incident) {
        Incident incidentCree = incidentService.ajouterIncident(incident);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(incidentCree.getId())
                .toUri();
        return ResponseEntity.created(location).body(incidentCree);
    }

    //TODO : Définir ce qui doit être modifier et par qui
    @PutMapping("/{idIncident}")
    public ResponseEntity<Incident> modifierIncident(@PathVariable Long idIncident, @ModelAttribute Incident incidentModifie) {
        return incidentService.modifierIncident(idIncident, incidentModifie)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/resolu/{idIncident}")
    public ResponseEntity<Incident> fermeIncident(@PathVariable Long idIncident) {
        Optional<Incident> incidentFerme = incidentService.getIncident(idIncident);

        if (incidentFerme.isPresent() && incidentFerme.get().getStatut() != IncidentStatut.RESOLU) {
            incidentFerme.get().setStatut(IncidentStatut.RESOLU);
            incidentFerme.get().setDateResolution(Instant.now());
        }else  return ResponseEntity.notFound().build();

        return incidentService.modifierIncident(idIncident, incidentFerme.get())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //TODO : Mettre l'id de prestataire et non l'objet (ou à gérer à un autre niveau)
    @PostMapping("affecter/{idIncident}")
    public ResponseEntity<Incident> affecterIncident(@PathVariable Long idIncident, Prestataire prestataire) {
        Optional<Incident> incidentFerme = incidentService.getIncident(idIncident);

        if (incidentFerme.isPresent() && incidentFerme.get().getStatut() != IncidentStatut.RESOLU) {
            incidentFerme.get().setPrestataire(prestataire);
            incidentFerme.get().setDateAffectation(Instant.now());
        }else  return ResponseEntity.notFound().build();

        return incidentService.modifierIncident(idIncident, incidentFerme.get())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("Historique/Incident")
    public ResponseEntity<List<Incident>> listerIncidents() {
        return incidentService.getHistorique().isEmpty() ? ResponseEntity.ok(incidentService.getHistorique()) : ResponseEntity.noContent().build();
    }
}
