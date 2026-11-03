package altn72.prj.connexion;

import altn72.prj.modele.Utilisateur;

import java.time.LocalDateTime;

public record UtilisateurDto(
        Integer id,
        String login,
        String nom,
        String prenom,
        String email,
        String profil,
        String libelleProfil,
        Boolean actif,
        LocalDateTime dateCreation,
        LocalDateTime derniereConnexion) {

    public static UtilisateurDto depuis(Utilisateur utilisateur) {
        return new UtilisateurDto(
                utilisateur.getId(),
                utilisateur.getLogin(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getProfil().name(),
                utilisateur.getProfil().getLibelle(),
                utilisateur.isActif(),
                utilisateur.getDateCreation(),
                utilisateur.getDerniereConnexion());
    }
}
