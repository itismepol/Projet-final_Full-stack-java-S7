package altn72.prj.repository;

import altn72.prj.modele.Prestataire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrestataireRepository extends JpaRepository<Prestataire, Integer> {
    List<Prestataire> findByActifTrue();
}
