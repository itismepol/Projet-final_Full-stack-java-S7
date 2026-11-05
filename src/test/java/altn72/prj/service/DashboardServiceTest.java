package altn72.prj.service;

import altn72.prj.FabriqueDonnees;
import altn72.prj.dto.DashboardDto;
import altn72.prj.dto.DashboardIncidentDto;
import altn72.prj.modele.Incident;
import altn72.prj.modele.IncidentPriorite;
import altn72.prj.modele.IncidentStatut;
import altn72.prj.modele.Prestataire;
import altn72.prj.repository.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    private static final List<IncidentStatut> STATUTS_ACTIFS =
            List.of(IncidentStatut.OUVERT, IncidentStatut.AFFECTE);

    @Mock
    private IncidentRepository incidentRepository;

    private DashboardService service;

    @BeforeEach
    void initialiser() {
        service = new DashboardService(incidentRepository, new IndicateurUrgenceService());
    }

    @Test
    void neDemandeQueLesIncidentsNonResolus() {
        service.obtenirTableauDeBord();

        verify(incidentRepository).findByStatutInOrderByDateOuvertureAsc(STATUTS_ACTIFS);
    }

    @Test
    void renvoieUnTableauVideSansIncidentActif() {
        DashboardDto tableau = service.obtenirTableauDeBord();

        assertTrue(tableau.incidents().isEmpty());
        assertEquals(0, tableau.totalActifs());
        assertEquals(0, tableau.urgents());
    }

    @Test
    void attribueLeBonNiveauAChaqueIncident() {
        when(incidentRepository.findByStatutInOrderByDateOuvertureAsc(STATUTS_ACTIFS))
                .thenReturn(incidentsDeDemonstration());

        Map<Integer, String> niveaux = service.obtenirTableauDeBord().incidents().stream()
                .collect(Collectors.toMap(DashboardIncidentDto::id, DashboardIncidentDto::niveauAttention));

        assertEquals("VERT", niveaux.get(1));
        assertEquals("ORANGE", niveaux.get(2));
        assertEquals("ROUGE", niveaux.get(3));
        assertEquals("AFFECTE", niveaux.get(4));
    }

    @Test
    void calculeLesIndicateursSynthetiques() {
        when(incidentRepository.findByStatutInOrderByDateOuvertureAsc(STATUTS_ACTIFS))
                .thenReturn(incidentsDeDemonstration());

        DashboardDto tableau = service.obtenirTableauDeBord();

        assertEquals(4, tableau.totalActifs());
        assertEquals(3, tableau.enAttenteAffectation());
        assertEquals(1, tableau.affectes());
        assertEquals(1, tableau.urgents());
        assertEquals(2, tableau.prioriteCritique());
    }

    @Test
    void incidentAffecteExposeSonPrestataire() {
        Prestataire prestataire = FabriqueDonnees.prestataire(3, true);
        when(incidentRepository.findByStatutInOrderByDateOuvertureAsc(STATUTS_ACTIFS))
                .thenReturn(List.of(FabriqueDonnees.incident(4, IncidentStatut.AFFECTE,
                        IncidentPriorite.NORMALE, 100, prestataire)));

        DashboardIncidentDto incident = service.obtenirTableauDeBord().incidents().getFirst();

        assertEquals(3, incident.prestataireId());
        assertEquals("Prestataire 3", incident.prestataireNom());
    }

    private List<Incident> incidentsDeDemonstration() {
        return List.of(
                FabriqueDonnees.incident(1, IncidentStatut.OUVERT, IncidentPriorite.CRITIQUE, 5, null),
                FabriqueDonnees.incident(2, IncidentStatut.OUVERT, IncidentPriorite.NORMALE, 49, null),
                FabriqueDonnees.incident(3, IncidentStatut.OUVERT, IncidentPriorite.HAUTE, 80, null),
                FabriqueDonnees.incident(4, IncidentStatut.AFFECTE, IncidentPriorite.CRITIQUE, 100,
                        FabriqueDonnees.prestataire(3, true)));
    }
}