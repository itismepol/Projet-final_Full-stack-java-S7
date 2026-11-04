package altn72.prj.controleur;

import altn72.prj.modele.Incident;
import altn72.prj.modele.IncidentPriorite;
import altn72.prj.service.IncidentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.security.core.Authentication;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }


    @GetMapping("/{idIncident}")
    public ResponseEntity<Incident> afficherUnIncident(@PathVariable("idIncident") Integer idInc) {
        return incidentService.getIncident(idInc)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{idIncident}")
    public ResponseEntity<Void> deleteIncident(@PathVariable("idIncident") Integer idIncident) {
        if (incidentService.supprimerIncident(idIncident)) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Incident> creerIncident(@RequestBody CreationIncidentRequest dataForm, Authentication auth) {
        return incidentService.creerIncident(dataForm, auth.getName())
                .map(incident -> {
                    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                            .path("/{id}").buildAndExpand(incident.getId()).toUri();
                    return ResponseEntity.created(location).body(incident);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idIncident}")
    public ResponseEntity<Incident> modifierIncident(@PathVariable Integer idIncident, @ModelAttribute Incident incidentModifie) {
        return incidentService.modifierIncident(idIncident, incidentModifie)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idIncident}/resolve")
    public ResponseEntity<Incident> resoudreIncident(@PathVariable Integer idIncident) {
        return incidentService.resolveIncident(idIncident)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{idIncident}/prestataire")
    public ResponseEntity<Incident> affecterPrestataire(@PathVariable Integer idIncident, @RequestBody AffectationRequest requete) {
        return incidentService.affectPresta(idIncident, requete.prestataireId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("Historique/Incident")
    public ResponseEntity<List<Incident>> listerIncidents() {
        return incidentService.getHistorique().isEmpty() ? ResponseEntity.ok(incidentService.getHistorique()) : ResponseEntity.noContent().build();
    }

    @PostMapping("/{idIncident}/evaluation")
    public ResponseEntity<Incident> evaluateIncident(@PathVariable Integer idIncident, @RequestBody EvaluationRequest requete, Authentication authentication) {
        return incidentService.evaluateIncident(idIncident, requete, authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public record CreationIncidentRequest(Integer equipementId, String titre, String description, IncidentPriorite priorite) {}
    public record AffectationRequest(Integer prestataireId) {}
    public record EvaluationRequest(Integer note, String commentaire) {}

}
