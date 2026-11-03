package altn72.prj.exception;

public class UtilisateurIntrouvableException extends RuntimeException {
    public UtilisateurIntrouvableException(String login) {
        super("Utilisateur introuvable : " + login);
    }
}
