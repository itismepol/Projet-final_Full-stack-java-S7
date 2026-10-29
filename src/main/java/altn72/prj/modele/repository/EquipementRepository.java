package altn72.prj.modele.repository;

import altn72.prj.modele.Equipement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipementRepository extends JpaRepository<Equipement, Long> {

    @EntityGraph(attributePaths = "type")
    List<Equipement> findByStationIdOrderByLibelle(Long stationId);

}
