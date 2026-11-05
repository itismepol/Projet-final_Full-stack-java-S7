package altn72.prj.service;

import altn72.prj.FabriqueDonnees;
import altn72.prj.modele.Equipement;
import java.util.Optional;
import altn72.prj.exception.RessourceIntrouvableException;
import altn72.prj.repository.EquipementRepository;
import altn72.prj.repository.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConsultationEquipementServiceTest {

    @Mock
    private EquipementRepository equipementRepository;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private EquipementFragileService equipementFragileService;

    @InjectMocks
    private ConsultationEquipementService service;

    @Test
    void equipementInexistantLeveUneException() {
        assertThrows(RessourceIntrouvableException.class, () -> service.getEquipement(99));
    }

    @Test
    void equipementNonFragileEstRetourneAvecFragileAFalse() {
        Equipement equipement = FabriqueDonnees.equipement();

        when(equipementRepository.findById(10)).thenReturn(Optional.of(equipement));
        when(equipementFragileService.estFragile(10)).thenReturn(false);

        Equipement resultat = service.getEquipement(10);

        assertFalse(resultat.isFragile());
        verify(equipementFragileService).estFragile(10);
    }

    @Test
    void equipementFragileEstRetourneAvecFragileATrue() {
        Equipement equipement = FabriqueDonnees.equipement();

        when(equipementRepository.findById(10)).thenReturn(Optional.of(equipement));
        when(equipementFragileService.estFragile(10)).thenReturn(true);

        Equipement resultat = service.getEquipement(10);

        assertTrue(resultat.isFragile());
        verify(equipementFragileService).estFragile(10);
    }

    @Test
    void equipementsDUneStationInexistanteLevent404() {
        when(stationRepository.existsById(99)).thenReturn(false);

        assertThrows(RessourceIntrouvableException.class, () -> service.listerEquipementsStation(99));
        verifyNoInteractions(equipementRepository);
    }

    @Test
    void stationSansEquipementRenvoieUneListeVide() {
        when(stationRepository.existsById(1)).thenReturn(true);

        assertTrue(service.listerEquipementsStation(1).isEmpty());
    }
}
