package altn72.prj.controleur;

import altn72.prj.modele.Incident;
import altn72.prj.modele.IncidentPriorite;
import altn72.prj.modele.IncidentStatut;
import altn72.prj.modele.Prestataire;
import altn72.prj.service.IncidentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.security.core.Authentication;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    //TODO : Définir ce qui doit être modifier et par qui
    @PutMapping("/{idIncident}")
    public ResponseEntity<Incident> modifierIncident(@PathVariable Integer idIncident, @ModelAttribute Incident incidentModifie) {
        return incidentService.modifierIncident(idIncident, incidentModifie)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/resolu/{idIncident}")
    public ResponseEntity<Incident> fermeIncident(@PathVariable Integer idIncident) {
        Optional<Incident> incidentFerme = incidentService.getIncident(idIncident);

        if (incidentFerme.isPresent() && incidentFerme.get().getStatut() != IncidentStatut.RESOLU) {
            incidentFerme.get().setStatut(IncidentStatut.RESOLU);
            incidentFerme.get().setDateResolution(LocalDateTime.now());
        }else  return ResponseEntity.notFound().build();

        return incidentService.modifierIncident(idIncident, incidentFerme.get())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //TODO : Mettre l'id de prestataire et non l'objet (ou à gérer à un autre niveau)
    @PostMapping("affecter/{idIncident}")
    public ResponseEntity<Incident> affecterIncident(@PathVariable Integer idIncident, Prestataire prestataire) {
        Optional<Incident> incidentFerme = incidentService.getIncident(idIncident);

        if (incidentFerme.isPresent() && incidentFerme.get().getStatut() != IncidentStatut.RESOLU) {
            incidentFerme.get().setPrestataire(prestataire);
            incidentFerme.get().setDateAffectation(LocalDateTime.now());
        }else  return ResponseEntity.notFound().build();

        return incidentService.modifierIncident(idIncident, incidentFerme.get())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("Historique/Incident")
    public ResponseEntity<List<Incident>> listerIncidents() {
        return incidentService.getHistorique().isEmpty() ? ResponseEntity.ok(incidentService.getHistorique()) : ResponseEntity.noContent().build();
    }

    public record CreationIncidentRequest(Integer equipementId, String titre, String description, IncidentPriorite priorite) {}

}
