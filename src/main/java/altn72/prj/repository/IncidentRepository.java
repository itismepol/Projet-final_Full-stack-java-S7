package altn72.prj.repository;

import altn72.prj.modele.Incident;
import altn72.prj.modele.IncidentStatut;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Integer> {
    List<Incident> findByStatutOrderByDateResolutionDesc(IncidentStatut statut);
    List<Incident> findByEquipement(Integer idEquipement);

    @Query("SELECT i FROM Incident i WHERE i.statut = altn72.prj.modele.IncidentStatut.RESOLU AND i.equipement.station.id = :idStation ORDER BY i.dateResolution DESC")
    List<Incident> getAllResolveIncidentsByStation(@Param("idStation") Integer idStation);

    @Query("SELECT i FROM Incident i WHERE i.statut = altn72.prj.modele.IncidentStatut.RESOLU AND i.equipement.id = :idEquipment ORDER BY i.dateResolution DESC")
    List<Incident> getAllResolveIncidentsByEquipment(@Param("idEquipment") Integer idEquipment);

    @Query("SELECT i FROM Incident i WHERE i.statut <> altn72.prj.modele.IncidentStatut.RESOLU AND i.equipement.id = :idEquipment ORDER BY i.dateOuverture")
    List<Incident> getIncidentsByEquipment(@Param("idEquipment") Integer idEquipment);
}
