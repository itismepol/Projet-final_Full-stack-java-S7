package altn72.prj.modele;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "incident")
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int UNSIGNED not null")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipement_id", nullable = false)
    private Equipement equipement;

    @Column(name = "titre", length = 150)
    private String titre;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @ColumnDefault("'NORMALE'")
    @Lob
    @Column(name = "priorite", nullable = false)
    private String priorite;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private IncidentStatut statut = IncidentStatut.OUVERT;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "date_ouverture", nullable = false)
    private Instant dateOuverture;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "prestataire_id")
    private Prestataire prestataire;

    @Column(name = "date_affectation")
    private Instant dateAffectation;

    @Column(name = "date_resolution")
    private Instant dateResolution;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "declare_par")
    private Utilisateur declarePar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Equipement getEquipement() {
        return equipement;
    }

    public void setEquipement(Equipement equipement) {
        this.equipement = equipement;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriorite() {
        return priorite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public Enum<IncidentStatut> getStatut() {
        return statut;
    }

    public void setStatut(IncidentStatut statut) {
        this.statut = statut;
    }

    public Instant getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(Instant dateOuverture) {
        this.dateOuverture = dateOuverture;
    }

    public Prestataire getPrestataire() {
        return prestataire;
    }

    public void setPrestataire(Prestataire prestataire) {
        this.prestataire = prestataire;
    }

    public Instant getDateAffectation() {
        return dateAffectation;
    }

    public void setDateAffectation(Instant dateAffectation) {
        this.dateAffectation = dateAffectation;
    }

    public Instant getDateResolution() {
        return dateResolution;
    }

    public void setDateResolution(Instant dateResolution) {
        this.dateResolution = dateResolution;
    }

    public Utilisateur getDeclarePar() {
        return declarePar;
    }

    public void setDeclarePar(Utilisateur declarePar) {
        this.declarePar = declarePar;
    }

}