package altn72.prj.exception;

public class RessourceIntrouvableException extends RuntimeException {
    public RessourceIntrouvableException(String ressource, Object identifiant) {
        super(ressource + " introuvable : " + identifiant);
    }}
