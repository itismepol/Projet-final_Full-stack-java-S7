

### F1 Connexion et déconnexion
findByLogin() Prénom Profil(ADMIN/RESPONSABLE_EXPLOITATION)
### F2 Consulter les équipements par station
findByRef() libelle, type_id, station_id, etat Where station == nomStationVoulu a voir pour une Jonction
### F3 Consulter la fiche d’un équipement
### F4 Déclarer un incident
### F5 Consulter le détail d’un incident
### F6Modifier les informations autorisées
### F7 Affecter ou réaffecter un prestataire
### F8 Déclarer l’incident comme résolu
### F9 Consulter l’historique des incidents résolus
### F10 Saisir une évaluation de l’intervention d’un prestataire
### F11 Tableau de bord des incidents actifs

//TODO : Mettre l'id de prestataire et non l'objet (ou à gérer à un autre niveau)
@PostMapping("affecter/{idIncident}")
public ResponseEntity<Incident> affecterIncident(@PathVariable Long idIncident, Prestataire prestataire) {
        
