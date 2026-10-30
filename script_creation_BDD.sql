
DROP DATABASE IF EXISTS gestion_transport;
CREATE DATABASE gestion_transport
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE gestion_transport;

-- ---------------------------------------------------------------------
-- STATION
-- ---------------------------------------------------------------------
CREATE TABLE station (
    id            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nom           VARCHAR(100) NOT NULL,
    adresse       VARCHAR(255) NULL,          -- ajout : localisation
    ville         VARCHAR(100) NULL,          -- ajout
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_station_nom UNIQUE (nom)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- TYPE D'ÉQUIPEMENT (table de référence : ascenseur, escalator...)
-- ---------------------------------------------------------------------
CREATE TABLE type_equipement (
    id      INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    libelle VARCHAR(60) NOT NULL,
    CONSTRAINT uq_type_equipement_libelle UNIQUE (libelle)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- ÉQUIPEMENT
-- ---------------------------------------------------------------------
CREATE TABLE equipement (
    id                INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    reference         VARCHAR(50)  NOT NULL,
    libelle           VARCHAR(150) NOT NULL,
    description       TEXT NULL,
    type_id           INT UNSIGNED NOT NULL,
    station_id        INT UNSIGNED NOT NULL,
    etat              ENUM('EN_SERVICE','DEGRADE','EN_MAINTENANCE','HORS_SERVICE')
                      NOT NULL DEFAULT 'EN_SERVICE',
    date_mise_service DATE NULL,                                  -- ajout
    date_creation     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_equipement_reference UNIQUE (reference),
    CONSTRAINT fk_equipement_type    FOREIGN KEY (type_id)
        REFERENCES type_equipement(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_equipement_station FOREIGN KEY (station_id)
        REFERENCES station(id)         ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_equipement_station (station_id),
    INDEX idx_equipement_type    (type_id),
    INDEX idx_equipement_etat    (etat)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- PRESTATAIRE
-- ---------------------------------------------------------------------
CREATE TABLE prestataire (
    id            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nom           VARCHAR(120) NOT NULL,
    email         VARCHAR(150) NULL,
    telephone     VARCHAR(30)  NULL,
    adresse       VARCHAR(255) NULL,                              -- ajout
    specialite    VARCHAR(100) NULL,                              -- ajout
    actif         BOOLEAN NOT NULL DEFAULT TRUE,                  -- ajout
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_prestataire_nom UNIQUE (nom),
    CONSTRAINT ck_prestataire_contact
        CHECK (email IS NOT NULL OR telephone IS NOT NULL)        -- au moins un moyen de contact
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- UTILISATEUR (mot de passe stocké sous forme de hash : bcrypt/argon2)
-- ---------------------------------------------------------------------
CREATE TABLE utilisateur (
    id            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    login         VARCHAR(50)  NOT NULL,
    mot_de_passe  VARCHAR(255) NOT NULL COMMENT 'Hash uniquement, jamais en clair',
    prenom        VARCHAR(80)  NOT NULL,
    nom           VARCHAR(80)  NULL,                              -- ajout
    email         VARCHAR(150) NULL,                              -- ajout
    profil        ENUM('ADMIN','RESPONSABLE_EXPLOITATION','TECHNICIEN','LECTEUR')
                  NOT NULL DEFAULT 'LECTEUR',
    actif         BOOLEAN NOT NULL DEFAULT TRUE,                  -- ajout
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    derniere_connexion DATETIME NULL,                             -- ajout
    CONSTRAINT uq_utilisateur_login UNIQUE (login)
) ENGINE=InnoDB;


-- ---------------------------------------------------------------------
-- ÉVALUATION
-- ---------------------------------------------------------------------

CREATE TABLE evaluation (
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    note            TINYINT UNSIGNED NOT NULL,
    commentaire     TEXT NULL,
    date_evaluation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    evalue_par      INT UNSIGNED NULL,
    CONSTRAINT ck_evaluation_note CHECK (note BETWEEN 1 AND 5),
    CONSTRAINT fk_evaluation_user FOREIGN KEY (evalue_par)
        REFERENCES utilisateur(id) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- INCIDENT
-- ---------------------------------------------------------------------
CREATE TABLE incident (
    id                 INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    equipement_id      INT UNSIGNED NOT NULL,
    titre              VARCHAR(150) NULL,                         -- ajout
    description        TEXT NOT NULL,
    priorite           ENUM('BASSE','NORMALE','HAUTE','CRITIQUE')
                       NOT NULL DEFAULT 'NORMALE',                -- ajout
    statut             ENUM('OUVERT','AFFECTE','RESOLU')
                       NOT NULL DEFAULT 'OUVERT',
    date_ouverture     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    prestataire_id     INT UNSIGNED NULL,
    date_affectation   DATETIME NULL,                             -- ajout
    date_resolution    DATETIME NULL,
     commentaire_resolution TEXT NULL, 
    evaluation_id      INT UNSIGNED NULL,
    declare_par        INT UNSIGNED NULL,                         -- ajout : utilisateur déclarant
    CONSTRAINT fk_incident_equipement  FOREIGN KEY (equipement_id)
        REFERENCES equipement(id)  ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_incident_prestataire FOREIGN KEY (prestataire_id)
        REFERENCES prestataire(id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_incident_declarant   FOREIGN KEY (declare_par)
        REFERENCES utilisateur(id) ON UPDATE CASCADE ON DELETE SET NULL,
    -- la résolution ne peut pas précéder l'ouverture
    CONSTRAINT ck_incident_dates
        CHECK (date_resolution IS NULL OR date_resolution >= date_ouverture),
    -- cohérence statut / date de résolution
    CONSTRAINT ck_incident_resolution
        CHECK ( (statut IN ('RESOLU') AND date_resolution IS NOT NULL)
             OR (statut IN ('OUVERT','AFFECTE') AND date_resolution IS NULL) ),
	CONSTRAINT uq_incident_evaluation UNIQUE (evaluation_id),
    CONSTRAINT fk_incident_evaluation FOREIGN KEY (evaluation_id)
        REFERENCES evaluation(id),
    -- une évaluation n'existe que sur un incident résolu
    CONSTRAINT ck_incident_evaluation
        CHECK (evaluation_id IS NULL OR statut IN ('RESOLU','CLOTURE')),
    INDEX idx_incident_equipement  (equipement_id),
    INDEX idx_incident_prestataire (prestataire_id),
    INDEX idx_incident_statut      (statut),
    INDEX idx_incident_ouverture   (date_ouverture)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- VUE : incidents avec durée d'ouverture (utile pour le tableau de bord)
-- ---------------------------------------------------------------------
CREATE OR REPLACE VIEW v_incident_detail AS
SELECT
    i.id,
    i.titre,
    i.description,
    i.priorite,
    i.statut,
    i.date_ouverture,
    i.date_resolution,
    TIMESTAMPDIFF(MINUTE, i.date_ouverture,
                  COALESCE(i.date_resolution, NOW()))  AS duree_minutes,
    (i.date_resolution IS NULL)                        AS est_ouvert,
    e.id        AS equipement_id,
    e.reference AS equipement_reference,
    e.libelle   AS equipement_libelle,
    te.libelle  AS type_equipement,
    s.id        AS station_id,
    s.nom       AS station_nom,
    p.id        AS prestataire_id,
    p.nom       AS prestataire_nom,
    ev.note     AS note_evaluation
FROM incident i
JOIN equipement e       ON e.id = i.equipement_id
JOIN type_equipement te ON te.id = e.type_id
JOIN station s          ON s.id = e.station_id
LEFT JOIN prestataire p ON p.id = i.prestataire_id
LEFT JOIN evaluation ev ON ev.id = i.evaluation_id;