package altn72.prj.modele.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EquipementNotFoundException extends RuntimeException {
    public EquipementNotFoundException(Long id) {
        super("Équipement introuvable : " + id);
    }
}
