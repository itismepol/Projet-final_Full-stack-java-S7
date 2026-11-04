package altn72.prj.service;

import altn72.prj.dto.DashboardDto;
import altn72.prj.dto.DashboardIncidentDto;
import altn72.prj.modele.Incident;
import altn72.prj.modele.IncidentStatut;
import altn72.prj.repository.IncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final IncidentRepository incidentRepository;
    private final IndicateurUrgenceService indicateurUrgenceService;

    public DashboardService(IncidentRepository incidentRepository, IndicateurUrgenceService indicateurUrgenceService) {
        this.incidentRepository = incidentRepository;
        this.indicateurUrgenceService = indicateurUrgenceService;
    }

    public DashboardDto obtenirTableauDeBord() {

        LocalDateTime maintenant = LocalDateTime.now();

        List<DashboardIncidentDto> incidents = incidentRepository.findByStatutInOrderByDateOuvertureAsc(
                List.of(IncidentStatut.OUVERT, IncidentStatut.AFFECTE))
                .stream()
                .map(incident -> convertirIncident(incident, maintenant)).toList();

        long enAttente = incidents.stream()
                .filter(i -> "OUVERT".equals(i.statut()))
                .count();

        long affectes = incidents.stream()
                .filter(i -> "AFFECTE".equals(i.statut()))
                .count();

        long urgents = incidents.stream()
                .filter(i -> "ROUGE".equals(i.niveauAttention()))
                .count();

        long critiques = incidents.stream()
                .filter(i -> "CRITIQUE".equals(i.priorite()))
                .count();

        return new DashboardDto(
                incidents,
                incidents.size(),
                enAttente,
                affectes,
                urgents,
                critiques
        );
    }

    private DashboardIncidentDto convertirIncident(Incident incident, LocalDateTime maintenant) {
        var equipement = incident.getEquipement();
        var station = equipement.getStation();
        var prestataire = incident.getPrestataire();

        return new DashboardIncidentDto(
                incident.getId(),
                incident.getTitre(),
                incident.getDescription(),
                incident.getPriorite().name(),
                incident.getStatut().name(),
                incident.getDateOuverture(),
                indicateurUrgenceService.calculerDureeMinutes(incident, maintenant),
                indicateurUrgenceService.calculerNiveauAttention(incident, maintenant),
                equipement.getId(),
                equipement.getReference(),
                equipement.getLibelle(),
                equipement.getType().getLibelle(),
                station.getId(),
                station.getNom(),
                station.getVille(),
                prestataire == null ? null : prestataire.getId(),
                prestataire == null ? null : prestataire.getNom()
        );
    }
}