package altn72.prj.service;

import altn72.prj.FabriqueDonnees;
import altn72.prj.exception.DonneeInvalideException;
import altn72.prj.exception.OperationInterditeException;
import altn72.prj.exception.RessourceIntrouvableException;
import altn72.prj.modele.Prestataire;
import altn72.prj.repository.IncidentRepository;
import altn72.prj.repository.PrestataireRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrestataireServiceTest {

    @Mock private PrestataireRepository prestataireRepository;
    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private PrestataireService service;

    @Test
    void unPrestataireSansMoyenDeContactEstRefuse() {
        Prestataire prestataire = new Prestataire();
        prestataire.setNom("TechniGate");
        prestataire.setEmail("   ");

        assertThrows(DonneeInvalideException.class, () -> service.ajouterPrestataire(prestataire));
        verify(prestataireRepository, never()).save(any());
    }

    @Test
    void unNomDejaUtiliseEstRefuse() {
        Prestataire prestataire = FabriqueDonnees.prestataire(0, true);
        when(prestataireRepository.existsByNom(prestataire.getNom())).thenReturn(true);

        assertThrows(OperationInterditeException.class, () -> service.ajouterPrestataire(prestataire));
        verify(prestataireRepository, never()).save(any());
    }

    @Test
    void ajouterUnPrestatairePartToujoursSansIdentifiantImpose() {
        Prestataire prestataire = FabriqueDonnees.prestataire(42, true);
        when(prestataireRepository.save(any(Prestataire.class))).thenAnswer(appel -> appel.getArgument(0));

        assertNull(service.ajouterPrestataire(prestataire).getId());
    }

    @Test
    void prestataireInconnuLeveUneException() {
        assertThrows(RessourceIntrouvableException.class, () -> service.consulterPrestataire(99));
    }

    @Test
    void incidentsEvaluesDUnPrestataireInconnuLevent404() {
        assertThrows(RessourceIntrouvableException.class, () -> service.getIncidentsEvaluesByPrestataire(99));
        verifyNoInteractions(incidentRepository);
    }

    @Test
    void prestataireSansEvaluationRenvoieUneListeVide() {
        when(prestataireRepository.findById(3)).thenReturn(Optional.of(FabriqueDonnees.prestataire(3, true)));

        assertTrue(service.getIncidentsEvaluesByPrestataire(3).isEmpty());
    }
}