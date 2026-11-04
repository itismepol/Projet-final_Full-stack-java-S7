package altn72.prj.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GestionnaireExceptions {

    public record ErreurReponse(String message) {}

    @ExceptionHandler(RessourceIntrouvableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErreurReponse ressourceIntrouvable(RessourceIntrouvableException e) {
        return new ErreurReponse(e.getMessage());
    }

    @ExceptionHandler(DonneeInvalideException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErreurReponse donneeInvalide(DonneeInvalideException e) {
        return new ErreurReponse(e.getMessage());
    }

    @ExceptionHandler(OperationInterditeException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErreurReponse operationInterdite(OperationInterditeException e) {
        return new ErreurReponse(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErreurReponse validationEchouee(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(" "));
        return new ErreurReponse(message);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErreurReponse contrainteViolee() {
        return new ErreurReponse("Cette opération entre en conflit avec des données existantes.");
    }
}