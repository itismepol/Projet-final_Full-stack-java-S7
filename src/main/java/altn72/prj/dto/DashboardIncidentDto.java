package altn72.prj.dto;

import java.time.LocalDateTime;

public record DashboardIncidentDto(
        Integer id,
        String titre,
        String description,
        String priorite,
        String statut,
        LocalDateTime dateOuverture,
        Long dureeMinutes,
        String niveauAttention,
        Integer equipementId,
        String equipementReference,
        String equipementLibelle,
        boolean equipementFragile,
        String typeEquipement,
        Integer stationId,
        String stationNom,
        String stationVille,
        Integer prestataireId,
        String prestataireNom
) {}
