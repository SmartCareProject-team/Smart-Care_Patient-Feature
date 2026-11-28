-- Smart Care - schema.sql (source of truth, owner: Dev 3)
-- spring.jpa.hibernate.ddl-auto=validate => les entites JPA doivent correspondre.

DROP TABLE IF EXISTS chat_message CASCADE;
DROP TABLE IF EXISTS prescription CASCADE;
DROP TABLE IF EXISTS dossier_medical CASCADE;
DROP TABLE IF EXISTS rendez_vous CASCADE;
DROP TABLE IF EXISTS medecin CASCADE;
DROP TABLE IF EXISTS patient CASCADE;
DROP TABLE IF EXISTS specialite CASCADE;
DROP TABLE IF EXISTS app_user CASCADE;

CREATE TABLE app_user (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role          VARCHAR(20)  NOT NULL CHECK (role IN ('PATIENT', 'MEDECIN'))
);

CREATE TABLE specialite (
    id  BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE patient (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT NOT NULL UNIQUE REFERENCES app_user(id) ON DELETE CASCADE,
    nom            VARCHAR(80),
    prenom         VARCHAR(80),
    cin            VARCHAR(20) UNIQUE,
    date_naissance DATE,
    telephone      VARCHAR(20),
    adresse        VARCHAR(255),
    groupe_sanguin VARCHAR(3) CHECK (groupe_sanguin IN ('A+','A-','B+','B-','AB+','AB-','O+','O-')),
    antecedents    TEXT,
    allergies      TEXT
);

CREATE TABLE medecin (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL UNIQUE REFERENCES app_user(id) ON DELETE CASCADE,
    nom           VARCHAR(80) NOT NULL,
    prenom        VARCHAR(80) NOT NULL,
    specialite_id BIGINT NOT NULL REFERENCES specialite(id)
);

CREATE TABLE rendez_vous (
    id         BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patient(id) ON DELETE CASCADE,
    medecin_id BIGINT NOT NULL REFERENCES medecin(id),
    date_heure TIMESTAMP NOT NULL,
    type       VARCHAR(20) NOT NULL CHECK (type IN ('PRESENTIEL', 'EN_LIGNE')),
    statut     VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE'
               CHECK (statut IN ('EN_ATTENTE', 'CONFIRME', 'ANNULE', 'TERMINE')),
    motif      VARCHAR(255),
    room_name  VARCHAR(80) UNIQUE   -- smartcare-<uuid>, jamais l'id du rendez-vous
);

CREATE TABLE dossier_medical (
    id           BIGSERIAL PRIMARY KEY,
    patient_id   BIGINT NOT NULL UNIQUE REFERENCES patient(id) ON DELETE CASCADE,
    diagnostic   TEXT,
    observations TEXT,
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE prescription (
    id             BIGSERIAL PRIMARY KEY,
    patient_id     BIGINT NOT NULL REFERENCES patient(id) ON DELETE CASCADE,
    medecin_id     BIGINT NOT NULL REFERENCES medecin(id),
    rendez_vous_id BIGINT REFERENCES rendez_vous(id),
    date_emission  DATE NOT NULL DEFAULT CURRENT_DATE,
    medicaments    TEXT NOT NULL,
    posologie      TEXT,
    remarques      TEXT
);

CREATE TABLE chat_message (
    id             BIGSERIAL PRIMARY KEY,
    rendez_vous_id BIGINT NOT NULL REFERENCES rendez_vous(id) ON DELETE CASCADE,
    sender_role    VARCHAR(20) NOT NULL CHECK (sender_role IN ('PATIENT', 'MEDECIN')),
    contenu        TEXT NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_rdv_patient      ON rendez_vous(patient_id);
CREATE INDEX idx_rdv_medecin      ON rendez_vous(medecin_id);
CREATE INDEX idx_rdv_statut       ON rendez_vous(statut);
CREATE INDEX idx_presc_patient    ON prescription(patient_id);
CREATE INDEX idx_chat_rdv_created ON chat_message(rendez_vous_id, created_at);
