package altn72.prj.controleur;

import altn72.prj.dto.AffectationRequest;
import altn72.prj.dto.CreationIncidentRequest;
import altn72.prj.dto.EvaluationRequest;
import altn72.prj.modele.Incident;
import altn72.prj.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public Incident afficherUnIncident(@PathVariable("idIncident") Integer idInc) {
        return incidentService.getIncident(idInc);
    }

    @GetMapping("/historique")
    public List<Incident> historique() {
        return incidentService.getHistorique();
    }

    @PreAuthorize("hasRole('RESPONSABLE_EXPLOITATION')")
    @DeleteMapping("/{idIncident}")
    public void deleteIncident(@PathVariable("idIncident") Integer idIncident) {
        incidentService.supprimerIncident(idIncident);
    }

    @PostMapping
    public ResponseEntity<Incident> creerIncident(@Valid @RequestBody CreationIncidentRequest dataForm,
                                                  Authentication auth) {
        Incident incident = incidentService.creerIncident(dataForm, auth.getName());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(incident.getId()).toUri();
        return ResponseEntity.created(location).body(incident);
    }

    @PreAuthorize("hasRole('RESPONSABLE_EXPLOITATION')")
    @PutMapping("/{idIncident}")
    public Incident modifierIncident(@PathVariable Integer idIncident, @ModelAttribute Incident incidentModifie) {
        return incidentService.modifierIncident(idIncident, incidentModifie);
    }

    @PreAuthorize("hasRole('RESPONSABLE_EXPLOITATION')")
    @PutMapping("/{idIncident}/resolve")
    public Incident resoudreIncident(@PathVariable Integer idIncident) {
        return incidentService.resolveIncident(idIncident);
    }

    @PreAuthorize("hasRole('RESPONSABLE_EXPLOITATION')")
    @PutMapping("/{idIncident}/prestataire")
    public Incident affecterPrestataire(@PathVariable Integer idIncident, @RequestBody AffectationRequest requete) {
        return incidentService.affectPresta(idIncident, requete.prestataireId());
    }

    @GetMapping("Historique/Incident")
    public List<Incident> listerIncidents() {
        return incidentService.getHistorique();
    }

    @PreAuthorize("hasRole('RESPONSABLE_EXPLOITATION')")
    @PostMapping("/{idIncident}/evaluation")
    public Incident evaluateIncident(@PathVariable Integer idIncident, @Valid @RequestBody EvaluationRequest requete, Authentication authentication) {
        return incidentService.evaluateIncident(idIncident, requete, authentication.getName());
    }

}
