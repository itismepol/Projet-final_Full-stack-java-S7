package altn72.prj.dto;

import altn72.prj.modele.IncidentPriorite;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreationIncidentRequest(
        @NotNull(message = "L'équipement est obligatoire.") Integer equipementId,
        @NotBlank(message = "Le titre est obligatoire.") String titre,
        @NotBlank(message = "La description est obligatoire.") String description,
        IncidentPriorite priorite) {}
