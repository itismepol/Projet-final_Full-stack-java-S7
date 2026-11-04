package altn72.prj.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EvaluationRequest(
        @NotNull(message = "La note est obligatoire.")
        @Min(value = 1, message = "La note doit être comprise entre 1 et 5.")
        @Max(value = 5, message = "La note doit être comprise entre 1 et 5.") Integer note,
        String commentaire) {}