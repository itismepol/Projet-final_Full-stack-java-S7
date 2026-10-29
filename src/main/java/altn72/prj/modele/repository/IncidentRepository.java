package altn72.prj.modele.repository;

import altn72.prj.modele.Equipement;
import altn72.prj.modele.Incident;
import altn72.prj.modele.IncidentStatut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByStatutOrderByDateResolutionDesc(IncidentStatut statut);
    List<Incident> findByEquipement(Long idEquipement);
}
