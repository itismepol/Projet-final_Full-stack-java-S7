package altn72.prj.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EquipementNotFoundException extends RuntimeException {
    public EquipementNotFoundException(Integer id) {
        super("Équipement introuvable : " + id);
    }
}
