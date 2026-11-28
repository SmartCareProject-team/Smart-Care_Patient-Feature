-- Smart Care - seed.sql (owner: Dev 3)
-- Mot de passe de tous les comptes : Password123 (BCrypt)

INSERT INTO app_user (email, password_hash, role) VALUES
 ('patient@smartcare.tn', '$2a$10$wKbvRSEjysyJBtuWtXWt0OMnUDAW7iYMglurjNFGTLszDIKm5r4Xq', 'PATIENT'),
 ('autre.patient@smartcare.tn', '$2a$10$wKbvRSEjysyJBtuWtXWt0OMnUDAW7iYMglurjNFGTLszDIKm5r4Xq', 'PATIENT'),
 ('dr.ben.salah@smartcare.tn', '$2a$10$wKbvRSEjysyJBtuWtXWt0OMnUDAW7iYMglurjNFGTLszDIKm5r4Xq', 'MEDECIN'),
 ('dr.trabelsi@smartcare.tn', '$2a$10$wKbvRSEjysyJBtuWtXWt0OMnUDAW7iYMglurjNFGTLszDIKm5r4Xq', 'MEDECIN'),
 ('dr.gharbi@smartcare.tn', '$2a$10$wKbvRSEjysyJBtuWtXWt0OMnUDAW7iYMglurjNFGTLszDIKm5r4Xq', 'MEDECIN');

INSERT INTO specialite (nom) VALUES ('Médecine générale'), ('Cardiologie'), ('Dermatologie');

INSERT INTO patient (user_id, nom, prenom, cin, date_naissance, telephone, adresse, groupe_sanguin, antecedents, allergies) VALUES
 (1, 'Amri', 'Sara', '12345678', '2000-05-14', '22123456', 'Sidi Bouzid', 'O+', 'Asthme léger', 'Pénicilline'),
 (2, 'Jlassi', 'Omar', '87654321', '1995-09-02', '98123456', 'Sfax', 'A+', NULL, NULL);

INSERT INTO medecin (user_id, nom, prenom, specialite_id) VALUES
 (3, 'Ben Salah', 'Karim', 1),
 (4, 'Trabelsi', 'Leila', 2),
 (5, 'Gharbi', 'Nizar', 3);

INSERT INTO rendez_vous (patient_id, medecin_id, date_heure, type, statut, motif, room_name) VALUES
 (1, 1, NOW() + INTERVAL '2 days', 'EN_LIGNE',   'EN_ATTENTE', 'Fièvre persistante', 'smartcare-3f6c1d9e-5b7a-4c2e-9a11-0d2b8f4e7a01'),
 (1, 2, NOW() + INTERVAL '3 days', 'EN_LIGNE',   'CONFIRME',   'Contrôle cardiaque', 'smartcare-8a42b7c0-1e5d-4f93-b6a2-7c9d3e1f5b02'),
 (1, 3, NOW() - INTERVAL '5 days', 'PRESENTIEL', 'ANNULE',     'Éruption cutanée', NULL),
 (1, 1, NOW() - INTERVAL '10 days','PRESENTIEL', 'TERMINE',    'Consultation générale', NULL);

INSERT INTO dossier_medical (patient_id, diagnostic, observations) VALUES
 (1, 'Rhinopharyngite virale', 'Repos, hydratation. Contrôle si fièvre > 3 jours.'),
 (2, NULL, NULL);

INSERT INTO prescription (patient_id, medecin_id, rendez_vous_id, medicaments, posologie, remarques) VALUES
 (1, 1, 4, 'Paracétamol 1 g', '1 comprimé toutes les 8 heures pendant 3 jours', 'Ne pas dépasser 3 g par jour');

INSERT INTO chat_message (rendez_vous_id, sender_role, contenu) VALUES
 (2, 'MEDECIN', 'Bonjour, je vous retrouve dans la salle vidéo.');
