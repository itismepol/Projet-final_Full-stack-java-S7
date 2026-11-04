package altn72.prj.service;

import altn72.prj.modele.Incident;
import altn72.prj.modele.IncidentStatut;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class IndicateurUrgenceService {

    public static final long SEUIL_ORANGE_MINUTES = 48L * 60;
    public static final long SEUIL_ROUGE_MINUTES = 72L * 60;

    public long calculerDureeMinutes(Incident incident, LocalDateTime maintenant) {
        return Math.max(0, Duration.between(incident.getDateOuverture(), maintenant).toMinutes());
    }

    public String calculerNiveauAttention(Incident incident, LocalDateTime maintenant) {
        if (incident.getStatut() == IncidentStatut.AFFECTE || incident.getPrestataire() != null) {
            return "AFFECTE";
        }

        long duree = calculerDureeMinutes(incident, maintenant);

        if (duree >= SEUIL_ROUGE_MINUTES) return "ROUGE";
        if (duree >= SEUIL_ORANGE_MINUTES) return "ORANGE";

        return "VERT";
    }
}