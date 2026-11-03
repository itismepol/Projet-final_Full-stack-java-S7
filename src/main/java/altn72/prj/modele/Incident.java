package altn72.prj.modele;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "incident")
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int UNSIGNED not null")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipement_id", nullable = false)
    private Equipement equipement;

    @Column(name = "titre", length = 150)
    private String titre;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priorite", nullable = false)
    private IncidentPriorite priorite = IncidentPriorite.NORMALE;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private IncidentStatut statut = IncidentStatut.OUVERT;

    @Column(name = "date_ouverture", nullable = false)
    private LocalDateTime dateOuverture;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "prestataire_id")
    private Prestataire prestataire;

    @Column(name = "date_affectation")
    private LocalDateTime dateAffectation;

    @Column(name = "date_resolution")
    private LocalDateTime dateResolution;

    @OneToOne(mappedBy = "incident", fetch = FetchType.LAZY)
    private Evaluation evaluation;

    @Column(name = "commentaire_resolution", columnDefinition = "TEXT")
    private String commentaireResolution;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "declare_par")
    private Utilisateur declarePar;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public IncidentPriorite getPriorite() {
        return priorite;
    }

    public void setPriorite(IncidentPriorite priorite) {
        this.priorite = priorite;
    }

    public IncidentStatut getStatut() {
        return statut;
    }

    public void setStatut(IncidentStatut statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(LocalDateTime dateOuverture) {
        this.dateOuverture = dateOuverture;
    }

    public Prestataire getPrestataire() {
        return prestataire;
    }

    public void setPrestataire(Prestataire prestataire) {
        this.prestataire = prestataire;
    }

    public LocalDateTime getDateAffectation() {
        return dateAffectation;
    }

    public void setDateAffectation(LocalDateTime dateAffectation) {
        this.dateAffectation = dateAffectation;
    }

    public LocalDateTime getDateResolution() {
        return dateResolution;
    }

    public void setDateResolution(LocalDateTime dateResolution) {
        this.dateResolution = dateResolution;
    }

    public Utilisateur getDeclarePar() {
        return declarePar;
    }

    public void setDeclarePar(Utilisateur declarePar) {
        this.declarePar = declarePar;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    public String getCommentaireResolution() {
        return commentaireResolution;
    }

    public void setCommentaireResolution(String commentaireResolution) {
        this.commentaireResolution = commentaireResolution;
    }
}