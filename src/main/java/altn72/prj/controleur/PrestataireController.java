package altn72.prj.controleur;

import altn72.prj.modele.Incident;
import altn72.prj.modele.Prestataire;
import altn72.prj.service.PrestataireService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('RESPONSABLE_EXPLOITATION')")
@RequestMapping("/prestataires")
public class PrestataireController {

    private final PrestataireService prestataireService;

    public PrestataireController(PrestataireService prestataireService) {
        this.prestataireService = prestataireService;
    }

    @GetMapping
    public List<Prestataire> getPrestataires() {
        return prestataireService.getAllPrestataires();
    }

    @GetMapping("/active")
    public List<Prestataire> getActivePrestataires() {
        return prestataireService.getAllActivePrestataires();
    }

    @GetMapping("/{idPrestataire}")
    public Prestataire getPrestataire(@PathVariable Integer idPrestataire) {
        return prestataireService.consulterPrestataire(idPrestataire);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Prestataire createPrestataire(@Valid @RequestBody Prestataire prestataire) {
        return prestataireService.ajouterPrestataire(prestataire);
    }

    @GetMapping("/{idPrestataire}/incidents-evalues")
    public List<Incident> getIncidentsEvalues(@PathVariable Integer idPrestataire) {
        return prestataireService.getIncidentsEvaluesByPrestataire(idPrestataire);
    }
}
