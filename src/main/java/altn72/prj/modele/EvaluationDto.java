package altn72.prj.modele;

import java.time.LocalDateTime;

public record EvaluationDto(
        Integer id,
        Integer incidentId,
        int note,
        String commentaire,
        LocalDateTime dateEvaluation,
        Integer evalueParId) {

    public static EvaluationDto depuis(Evaluation evaluation) {
        return new EvaluationDto(
                evaluation.getId(),
                evaluation.getIncident().getId(),
                evaluation.getNote(),
                evaluation.getCommentaire(),
                evaluation.getDateEvaluation(),
                evaluation.getEvaluePar() == null ? null : evaluation.getEvaluePar().getId());
    }
}
