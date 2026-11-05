package altn72.prj.service;

import altn72.prj.modele.Incident;
import altn72.prj.modele.IncidentStatut;
import altn72.prj.modele.Prestataire;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class IndicateurUrgenceServiceTest {

    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 11, 5, 12, 0);

    private final IndicateurUrgenceService service = new IndicateurUrgenceService();

    @Test
    void incidentOuvertDepuis47h59EstVert() {
        assertEquals("VERT", service.calculerNiveauAttention(incidentOuvertDepuis(2879, IncidentStatut.OUVERT), MAINTENANT));
    }

    @Test
    void incidentOuvertDepuis48hEstOrange() {
        assertEquals("ORANGE", service.calculerNiveauAttention(incidentOuvertDepuis(2880, IncidentStatut.OUVERT), MAINTENANT));
    }

    @Test
    void incidentOuvertDepuis71h59EstOrange() {
        assertEquals("ORANGE", service.calculerNiveauAttention(incidentOuvertDepuis(4319, IncidentStatut.OUVERT), MAINTENANT));
    }

    @Test
    void incidentOuvertDepuis72hEstRouge() {
        assertEquals("ROUGE", service.calculerNiveauAttention(incidentOuvertDepuis(4320, IncidentStatut.OUVERT), MAINTENANT));
    }
    @Test
    void incidentAffecteNeRecoitJamaisUneCouleur() {
        Incident incident = incidentOuvertDepuis(6000, IncidentStatut.AFFECTE);

        assertEquals("AFFECTE", service.calculerNiveauAttention(incident, MAINTENANT));
    }

    @Test
    void incidentAvecPrestataireEstAffecteMemeSiSonStatutEstOuvert() {
        Incident incident = incidentOuvertDepuis(6000, IncidentStatut.OUVERT);
        incident.setPrestataire(new Prestataire());

        assertEquals("AFFECTE", service.calculerNiveauAttention(incident, MAINTENANT));
    }

    @Test
    void dureeEstCalculeeEnMinutes() {
        assertEquals(90, service.calculerDureeMinutes(incidentOuvertDepuis(90, IncidentStatut.OUVERT), MAINTENANT));
    }

    @Test
    void dureeNeDevientJamaisNegative() {
        assertEquals(0, service.calculerDureeMinutes(incidentOuvertDepuis(-30, IncidentStatut.OUVERT), MAINTENANT));
    }

    private Incident incidentOuvertDepuis(long minutes, IncidentStatut statut) {
        Incident incident = new Incident();
        incident.setStatut(statut);
        incident.setDateOuverture(MAINTENANT.minusMinutes(minutes));
        return incident;
    }
}