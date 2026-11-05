package altn72.prj.service;

import altn72.prj.repository.IncidentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipementFragileServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private EquipementFragileService service;

    @Test
    void deuxIncidentsRecentsNeRendentPasEquipementFragile() {
        when(incidentRepository.countByEquipementIdAndDateOuvertureGreaterThanEqual(
                eq(1), any(LocalDateTime.class)
        )).thenReturn(2L);

        assertFalse(service.estFragile(1));
    }

    @Test
    void troisIncidentsRecentsRendentEquipementFragile() {
        when(incidentRepository.countByEquipementIdAndDateOuvertureGreaterThanEqual(
                eq(1), any(LocalDateTime.class)
        )).thenReturn(3L);

        assertTrue(service.estFragile(1));
    }

    @Test
    void plusDeTroisIncidentsRecentsRendentEquipementFragile() {
        when(incidentRepository.countByEquipementIdAndDateOuvertureGreaterThanEqual(
                eq(1), any(LocalDateTime.class)
        )).thenReturn(5L);

        assertTrue(service.estFragile(1));
    }

    @Test
    void leCalculUtiliseLaDateCouranteMoinsTrenteJours() {
        LocalDateTime avantCalcul = LocalDateTime.now();

        when(incidentRepository.countByEquipementIdAndDateOuvertureGreaterThanEqual(
                eq(1), any(LocalDateTime.class)
        )).thenReturn(2L);

        service.estFragile(1);

        var dateLimite = org.mockito.ArgumentCaptor.forClass(LocalDateTime.class);

        verify(incidentRepository).countByEquipementIdAndDateOuvertureGreaterThanEqual(
                eq(1), dateLimite.capture()
        );

        assertTrue(
                dateLimite.getValue().isAfter(avantCalcul.minusDays(30).minusSeconds(2))
                        && dateLimite.getValue().isBefore(avantCalcul.minusDays(30).plusSeconds(2))
        );
    }

    @Test
    void leCalculEstEffectuePourLeBonEquipement() {
        when(incidentRepository.countByEquipementIdAndDateOuvertureGreaterThanEqual(
                eq(2), any(LocalDateTime.class)
        )).thenReturn(3L);

        assertTrue(service.estFragile(2));

        verify(incidentRepository).countByEquipementIdAndDateOuvertureGreaterThanEqual(
                eq(2), any(LocalDateTime.class)
        );
    }
}