package altn72.prj.repository;

import altn72.prj.modele.Equipement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipementRepository extends JpaRepository<Equipement, Integer> {

    @EntityGraph(attributePaths = "type")
    List<Equipement> findByStationIdOrderByLibelle(Integer stationId);

}
