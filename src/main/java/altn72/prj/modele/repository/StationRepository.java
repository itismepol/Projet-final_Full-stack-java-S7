package altn72.prj.modele.repository;

import altn72.prj.modele.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    List<Station> findAllByOrderByNom();
}
