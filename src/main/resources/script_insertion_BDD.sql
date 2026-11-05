USE gestion_transport;

-- Stations
INSERT INTO station (nom, adresse, ville) VALUES
('Gare Centrale',   '1 place de la Gare',        'Paris'),
('République',      '12 place de la République', 'Paris'),
('Bellecour',       '5 place Bellecour',         'Lyon'),
('Vieux-Port',      '20 quai du Port',           'Marseille'),
('Capitole',        '3 place du Capitole',       'Toulouse');

-- Types d'équipement
INSERT INTO type_equipement (libelle) VALUES
('Ascenseur'),
('Escalier mécanique'),
('Portique'),
('Distributeur de titres'),
('Écran d''information');

-- Équipements
INSERT INTO equipement (reference, libelle, type_id, station_id, etat, date_mise_service) VALUES
('ASC-GC-001', 'Ascenseur quai A',          1, 1, 'HORS_SERVICE',   '2018-03-15'),
('ESC-GC-001', 'Escalator hall principal',  2, 1, 'EN_SERVICE',     '2017-06-01'),
('POR-GC-001', 'Portique entrée Nord',      3, 1, 'EN_SERVICE',     '2020-09-10'),
('DIS-GC-001', 'Distributeur hall',         4, 1, 'DEGRADE',        '2021-01-20'),
('ASC-RE-001', 'Ascenseur sortie 2',        1, 2, 'EN_SERVICE',     '2019-11-05'),
('ECR-RE-001', 'Écran quai direction Est',  5, 2, 'EN_SERVICE',     '2022-04-12'),
('ESC-BE-001', 'Escalator accès métro',     2, 3, 'EN_MAINTENANCE', '2016-02-28'),
('DIS-BE-001', 'Distributeur entrée',       4, 3, 'EN_SERVICE',     '2021-07-14'),
('POR-VP-001', 'Portique principal',        3, 4, 'EN_SERVICE',     '2020-05-30'),
('ASC-VP-001', 'Ascenseur quai Nord',       1, 4, 'EN_SERVICE',     '2018-10-09'),
('ECR-CA-001', 'Écran hall central',        5, 5, 'HORS_SERVICE',   '2022-08-01'),
('POR-CA-001', 'Portique sortie Sud',       3, 5, 'EN_SERVICE',     '2020-12-18');

-- Prestataires
INSERT INTO prestataire (nom, email, telephone, specialite) VALUES
('AscenseurPlus',     'contact@ascenseurplus.example',  '01 40 00 00 01', 'Ascenseurs et escalators'),
('TechniGate',        'support@technigate.example',     '04 72 00 00 02', 'Portiques et contrôle d''accès'),
('BilletExpress',     'sav@billetexpress.example',      '04 91 00 00 03', 'Distributeurs de titres'),
('InfoDisplay',       'hotline@infodisplay.example',    '05 61 00 00 04', 'Affichage dynamique'),
('MaintenanceGlobale','intervention@maintglob.example', '01 55 00 00 05', 'Multi-équipements');

-- Utilisateurs
-- Hash bcrypt de la chaîne "password" : UNIQUEMENT pour les tests.
-- En production, le hash est généré par l'application (bcrypt / argon2).
INSERT INTO utilisateur (login, mot_de_passe, prenom, nom, email, profil) VALUES
('agent1',    '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Alice',  'Martin',  'alice.martin@example.com', 'AGENT'),
('resp.expl1','$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Bruno',  'Durand',  'bruno.durand@example.com', 'RESPONSABLE_EXPLOITATION'),
('resp.expl2',    '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Camille','Leroy',   'camille.leroy@example.com','RESPONSABLE_EXPLOITATION'),
('agent2', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'David',  'Moreau',  NULL,                       'AGENT');

-- Incidents
-- Ouverts / en cours (date_resolution NULL)
INSERT INTO incident (equipement_id, titre, description, priorite, statut, date_ouverture, prestataire_id, date_affectation, commentaire_resolution, date_resolution, declare_par) VALUES
(1,  'Ascenseur bloqué',        'Ascenseur quai A bloqué entre deux niveaux.',        'CRITIQUE', 'AFFECTE', NOW() - INTERVAL 2 DAY,   1, NOW() - INTERVAL 46 HOUR, NULL, NULL, 3),
(4,  'Distributeur lent',       'Le distributeur n''imprime plus les tickets.',       'NORMALE',  'AFFECTE',  NOW() - INTERVAL 47 HOUR, 3, NOW() - INTERVAL 20 HOUR, NULL, NULL, 3),
(11, 'Écran éteint',            'Écran du hall central totalement éteint.',           'HAUTE',    'OUVERT',   NOW() - INTERVAL 6 HOUR,  NULL, NULL, NULL, NULL, 2),
(7,  'Bruit anormal escalator', 'Bruit métallique au démarrage de l''escalator.',     'NORMALE',  'AFFECTE', NOW() - INTERVAL 3 DAY,   1, NOW() - INTERVAL 70 HOUR,NULL, NULL, 3);

-- Résolus
INSERT INTO incident (equipement_id, titre, description, priorite, statut, date_ouverture, prestataire_id, date_affectation, date_resolution, commentaire_resolution, declare_par) VALUES
(3, 'Portique ne s''ouvre plus', 'Le portique reste fermé après validation.', 'HAUTE',   'RESOLU', NOW() - INTERVAL 20 DAY, 2, NOW() - INTERVAL 20 DAY + INTERVAL 1 HOUR, NOW() - INTERVAL 19 DAY, 'Remplacement du capteur.', 3),
(5, 'Porte ascenseur',           'La porte ne se referme pas.',                'NORMALE', 'RESOLU', NOW() - INTERVAL 15 DAY, 1, NOW() - INTERVAL 15 DAY + INTERVAL 2 HOUR, NOW() - INTERVAL 14 DAY, 'Réglage de la porte.',      2),
(8, 'Écran tactile HS',          'Écran tactile du distributeur non réactif.', 'BASSE',   'RESOLU', NOW() - INTERVAL 30 DAY, 3, NOW() - INTERVAL 30 DAY + INTERVAL 3 HOUR, NOW() - INTERVAL 27 DAY, 'Remplacement de la dalle.',  3),
(6, 'Affichage erroné',          'Horaires affichés incorrects.',              'BASSE',   'RESOLU',  NOW() - INTERVAL 5 DAY,  4, NOW() - INTERVAL 5 DAY + INTERVAL 1 HOUR,  NOW() - INTERVAL 4 DAY,  'Mise à jour du logiciel.',   2),
(9, 'Portique en alarme',        'Alarme sonore intempestive.',                'NORMALE', 'RESOLU', NOW() - INTERVAL 40 DAY, 2, NOW() - INTERVAL 40 DAY + INTERVAL 1 HOUR, NOW() - INTERVAL 39 DAY, 'Recalibrage du capteur.',   3),
(2, 'Escalator à l''arrêt',      'Arrêt d''urgence déclenché.',                'HAUTE',   'RESOLU', NOW() - INTERVAL 25 DAY, 5, NOW() - INTERVAL 25 DAY + INTERVAL 2 HOUR, NOW() - INTERVAL 24 DAY, 'Remise en route après contrôle.', 2);

-- Évaluations (ids 1 à 6, rattachées ensuite aux incidents résolus)
INSERT INTO evaluation(incident_id, note, commentaire, date_evaluation, evalue_par)VALUES
(5, 5, 'Intervention rapide et efficace.', NOW() - INTERVAL 18 DAY, 2),
(6, 3, 'Résolu, mais délai un peu long.', NOW() - INTERVAL 13 DAY, 2),
(7, 4, NULL, NOW() - INTERVAL 26 DAY, 2),
(8, 4, 'Bonne réactivité.', NOW() - INTERVAL 3 DAY, 2),
(9, 2, 'Panne revenue deux fois, travail bâclé.', NOW() - INTERVAL 38 DAY, 2),
(10, 5, NULL, NOW() - INTERVAL 23 DAY, 2);