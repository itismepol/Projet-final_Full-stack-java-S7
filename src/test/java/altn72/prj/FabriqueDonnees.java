package altn72.prj;

import altn72.prj.modele.*;

import java.time.LocalDateTime;

public final class FabriqueDonnees {

    private FabriqueDonnees() {}

    public static Equipement equipement() {
        TypeEquipement type = new TypeEquipement();
        type.setId(1);
        type.setLibelle("Ascenseur");
        Station station = new Station();
        station.setId(1);
        station.setNom("Gare Centrale");
        station.setVille("Paris");
        Equipement equipement = new Equipement();
        equipement.setId(10);
        equipement.setReference("ASC-GC-001");
        equipement.setLibelle("Ascenseur quai A");
        equipement.setType(type);
        equipement.setStation(station);
        return equipement;
    }

    public static Prestataire prestataire(int id, boolean actif) {
        Prestataire prestataire = new Prestataire();
        prestataire.setId(id);
        prestataire.setNom("Prestataire " + id);
        prestataire.setEmail("contact" + id + "@example.com");
        prestataire.setActif(actif);
        return prestataire;
    }

    public static Incident incident(int id, IncidentStatut statut, IncidentPriorite priorite,
                                    long heuresDepuisOuverture, Prestataire prestataire) {
        Incident incident = new Incident();
        incident.setId(id);
        incident.setTitre("Panne " + id);
        incident.setDescription("Description " + id);
        incident.setStatut(statut);
        incident.setPriorite(priorite);
        incident.setDateOuverture(LocalDateTime.now().minusHours(heuresDepuisOuverture));
        incident.setEquipement(equipement());
        incident.setPrestataire(prestataire);
        return incident;
    }

    public static Incident incident(int id, IncidentStatut statut, long heuresDepuisOuverture) {
        return incident(id, statut, IncidentPriorite.NORMALE, heuresDepuisOuverture, null);
    }

    public static Utilisateur utilisateur(String login) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin(login);
        return utilisateur;
    }
}
