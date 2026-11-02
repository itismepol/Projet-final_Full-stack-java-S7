package altn72.prj.repository;

import altn72.prj.modele.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Integer> {
    List<Station> findAllByOrderByNom();
}
