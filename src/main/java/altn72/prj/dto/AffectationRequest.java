package altn72.prj.dto;

import jakarta.validation.constraints.NotNull;

public record AffectationRequest(@NotNull(message = "Le prestataire est obligatoire.") Integer prestataireId) {}