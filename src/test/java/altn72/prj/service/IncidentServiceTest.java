package altn72.prj.service;

import altn72.prj.FabriqueDonnees;
import altn72.prj.dto.CreationIncidentRequest;
import altn72.prj.dto.EvaluationRequest;
import altn72.prj.exception.OperationInterditeException;
import altn72.prj.exception.RessourceIntrouvableException;
import altn72.prj.modele.*;
import altn72.prj.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;
    @Mock private StationRepository stationRepository;
    @Mock private EquipementRepository equipmentRepository;
    @Mock private UtilisateurRepository userRepository;
    @Mock private PrestataireRepository prestaRepository;
    @Mock private EvaluationRepository evalRepo;

    @InjectMocks
    private IncidentService service;

    @Test
    void incidentInexistantLeveUneExceptionDeRessourceIntrouvable() {
        assertThrows(RessourceIntrouvableException.class, () -> service.getIncident(99));
    }

    @Test
    void historiqueNeContientQueDesIncidentsResolus() {
        List<Incident> resolus = List.of(FabriqueDonnees.incident(1, IncidentStatut.RESOLU, 10));
        when(incidentRepository.findByStatutOrderByDateResolutionDesc(IncidentStatut.RESOLU)).thenReturn(resolus);

        assertSame(resolus, service.getHistorique());
    }

    @Test
    void historiqueDUneStationInexistanteLeveUneException() {
        when(stationRepository.existsById(99)).thenReturn(false);

        assertThrows(RessourceIntrouvableException.class, () -> service.getAllResolveIncidentsByStation(99));
        verifyNoInteractions(incidentRepository);
    }

    @Test
    void historiqueDUneStationSansIncidentRenvoieUneListeVide() {
        when(stationRepository.existsById(1)).thenReturn(true);

        assertTrue(service.getAllResolveIncidentsByStation(1).isEmpty());
    }

    @Test
    void incidentsDUnEquipementInexistantLevent404() {
        when(equipmentRepository.existsById(99)).thenReturn(false);

        assertThrows(RessourceIntrouvableException.class, () -> service.getIncidentsByEquipment(99));
    }

    @Test
    void incidentDeclareDemarreOuvertSansPrestataire() {
        when(equipmentRepository.findById(10)).thenReturn(Optional.of(FabriqueDonnees.equipement()));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(appel -> appel.getArgument(0));
        LocalDateTime avant = LocalDateTime.now();

        Incident incident = service.creerIncident(new CreationIncidentRequest(10, "Panne", "Bloqué", null), "tech1");

        assertEquals(IncidentStatut.OUVERT, incident.getStatut());
        assertNull(incident.getPrestataire());
        assertEquals(IncidentPriorite.NORMALE, incident.getPriorite());
        assertFalse(incident.getDateOuverture().isBefore(avant));
    }

    @Test
    void declarerUnIncidentSurUnEquipementInexistantEstRefuse() {
        assertThrows(RessourceIntrouvableException.class,
                () -> service.creerIncident(new CreationIncidentRequest(99, "Panne", "Bloqué", null), "tech1"));
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void affecterUnPrestataireFaitPasserLIncidentAAffecte() {
        Incident incident = FabriqueDonnees.incident(1, IncidentStatut.OUVERT, 5);
        Prestataire prestataire = FabriqueDonnees.prestataire(3, true);
        when(incidentRepository.findById(1)).thenReturn(Optional.of(incident));
        when(prestaRepository.findById(3)).thenReturn(Optional.of(prestataire));

        Incident resultat = service.affectPresta(1, 3);

        assertEquals(IncidentStatut.AFFECTE, resultat.getStatut());
        assertSame(prestataire, resultat.getPrestataire());
        assertNotNull(resultat.getDateAffectation());
    }

    @Test
    void unIncidentAffectePeutEtreReaffecte() {
        Incident incident = FabriqueDonnees.incident(1, IncidentStatut.AFFECTE, IncidentPriorite.NORMALE, 5,
                FabriqueDonnees.prestataire(3, true));
        Prestataire autre = FabriqueDonnees.prestataire(4, true);
        when(incidentRepository.findById(1)).thenReturn(Optional.of(incident));
        when(prestaRepository.findById(4)).thenReturn(Optional.of(autre));

        Incident resultat = service.affectPresta(1, 4);

        assertSame(autre, resultat.getPrestataire());
        assertEquals(IncidentStatut.AFFECTE, resultat.getStatut());
    }

    @Test
    void unIncidentResoluNePeutPlusEtreAffecte() {
        Incident incident = FabriqueDonnees.incident(1, IncidentStatut.RESOLU, 50);
        when(incidentRepository.findById(1)).thenReturn(Optional.of(incident));
        when(prestaRepository.findById(3)).thenReturn(Optional.of(FabriqueDonnees.prestataire(3, true)));

        assertThrows(OperationInterditeException.class, () -> service.affectPresta(1, 3));
        assertNull(incident.getPrestataire());
    }

    @Test
    void unPrestataireInactifNePeutPasEtreAffecte() {
        when(incidentRepository.findById(1)).thenReturn(Optional.of(FabriqueDonnees.incident(1, IncidentStatut.OUVERT, 5)));
        when(prestaRepository.findById(3)).thenReturn(Optional.of(FabriqueDonnees.prestataire(3, false)));

        assertThrows(OperationInterditeException.class, () -> service.affectPresta(1, 3));
    }

    @Test
    void affecterUnPrestataireInexistantLeveUneException() {
        when(incidentRepository.findById(1)).thenReturn(Optional.of(FabriqueDonnees.incident(1, IncidentStatut.OUVERT, 5)));

        assertThrows(RessourceIntrouvableException.class, () -> service.affectPresta(1, 99));
    }

    @Test
    void resoudreUnIncidentAffecteEnregistreLaDateDeResolution() {
        Incident incident = FabriqueDonnees.incident(1, IncidentStatut.AFFECTE, IncidentPriorite.NORMALE, 5,
                FabriqueDonnees.prestataire(3, true));
        when(incidentRepository.findById(1)).thenReturn(Optional.of(incident));

        Incident resultat = service.resolveIncident(1);

        assertEquals(IncidentStatut.RESOLU, resultat.getStatut());
        assertFalse(resultat.getDateResolution().isBefore(resultat.getDateOuverture()));
    }

    @Test
    void ilFautAffecterUnIncidentAvantDeLeResoudre() {
        Incident incident = FabriqueDonnees.incident(1, IncidentStatut.OUVERT, 5);
        when(incidentRepository.findById(1)).thenReturn(Optional.of(incident));

        assertThrows(OperationInterditeException.class, () -> service.resolveIncident(1));
        assertNull(incident.getDateResolution());
    }

    @Test
    void unIncidentDejaResoluNePeutPasEtreResoluDeNouveau() {
        when(incidentRepository.findById(1)).thenReturn(Optional.of(FabriqueDonnees.incident(1, IncidentStatut.RESOLU, 50)));

        assertThrows(OperationInterditeException.class, () -> service.resolveIncident(1));
    }

    @Test
    void evaluerUnIncidentNonResoluEstRefuse() {
        when(incidentRepository.findById(1)).thenReturn(Optional.of(FabriqueDonnees.incident(1, IncidentStatut.AFFECTE, 5)));

        assertThrows(OperationInterditeException.class,
                () -> service.evaluateIncident(1, new EvaluationRequest(4, null), "resp.expl"));
        verify(evalRepo, never()).save(any());
    }

    @Test
    void unIncidentNePeutEtreEvalueQuUneFois() {
        Incident incident = FabriqueDonnees.incident(1, IncidentStatut.RESOLU, 50);
        incident.setEvaluation(new Evaluation());
        when(incidentRepository.findById(1)).thenReturn(Optional.of(incident));

        assertThrows(OperationInterditeException.class,
                () -> service.evaluateIncident(1, new EvaluationRequest(4, null), "resp.expl"));
        verify(evalRepo, never()).save(any());
    }

    @Test
    void evaluerUnIncidentResoluEnregistreLaNote() {
        Incident incident = FabriqueDonnees.incident(1, IncidentStatut.RESOLU, 50);
        when(incidentRepository.findById(1)).thenReturn(Optional.of(incident));

        service.evaluateIncident(1, new EvaluationRequest(4, "Bien"), "resp.expl");

        ArgumentCaptor<Evaluation> evaluation = ArgumentCaptor.forClass(Evaluation.class);
        verify(evalRepo).save(evaluation.capture());
        assertEquals(4, evaluation.getValue().getNote());
        assertSame(incident, evaluation.getValue().getIncident());
    }
}