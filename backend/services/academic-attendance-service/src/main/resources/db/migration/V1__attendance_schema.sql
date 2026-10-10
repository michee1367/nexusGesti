CREATE SCHEMA IF NOT EXISTS academic_attendance;

-- ============================================================
-- AGREGAT : SEANCE (occurrence de cours)
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_attendance.seance (
    id UUID NOT NULL,
    classe_id UUID,
    cours_id UUID,
    enseignant_id UUID,
    salle_id UUID,
    annee_id UUID,
    periode_id UUID,
    date DATE,
    heure_debut TIME,
    heure_fin TIME,
    volume_horaire_reel NUMERIC(6,2),
    type VARCHAR(20),
    statut VARCHAR(20),
    annulee BOOLEAN NOT NULL DEFAULT FALSE,
    motif_annulation VARCHAR(255),
    PRIMARY KEY (id)
);
CREATE INDEX idx_seance_classe ON academic_attendance.seance (classe_id);
CREATE INDEX idx_seance_date ON academic_attendance.seance (date);

-- ============================================================
-- AGREGAT : FEUILLE DE PRESENCE (une par seance)
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_attendance.feuille_presence (
    id UUID NOT NULL,
    seance_id UUID,
    classe_id UUID,
    cours_id UUID,
    enseignant_id UUID,
    date DATE,
    statut VARCHAR(20) NOT NULL,
    methode VARCHAR(20),
    ouverte_par UUID,
    ouverte_le TIMESTAMPTZ,
    soumise_le TIMESTAMPTZ,
    validee_par UUID,
    validee_le TIMESTAMPTZ,
    effectif_theorique INTEGER,
    effectif_present INTEGER,
    observations VARCHAR(500),
    PRIMARY KEY (id)
);
CREATE INDEX idx_feuille_seance ON academic_attendance.feuille_presence (seance_id);
CREATE INDEX idx_feuille_classe ON academic_attendance.feuille_presence (classe_id);
CREATE INDEX idx_feuille_date ON academic_attendance.feuille_presence (date);

CREATE TABLE IF NOT EXISTS academic_attendance.presence_eleve (
    id UUID NOT NULL,
    feuille_id UUID NOT NULL REFERENCES academic_attendance.feuille_presence (id),
    eleve_id UUID NOT NULL,
    statut VARCHAR(30),
    heure_arrivee TIME,
    minutes_retard INTEGER,
    heure_depart TIME,
    depart_anticipe BOOLEAN NOT NULL DEFAULT FALSE,
    type_absence VARCHAR(30),
    justificatif_id UUID,
    saisie_par UUID,
    saisie_le TIMESTAMPTZ,
    justifiee BOOLEAN NOT NULL DEFAULT FALSE,
    justifiee_le TIMESTAMPTZ,
    modifiee_apres_validation BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_presence_feuille ON academic_attendance.presence_eleve (feuille_id);
CREATE INDEX idx_presence_eleve ON academic_attendance.presence_eleve (eleve_id);

-- ============================================================
-- AGREGAT : JUSTIFICATIF
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_attendance.justificatif (
    id UUID NOT NULL,
    eleve_id UUID NOT NULL,
    type VARCHAR(30),
    date_reference DATE,
    date_depot DATE,
    motif VARCHAR(500),
    fichier_uri VARCHAR(500),
    statut VARCHAR(20) NOT NULL,
    traite_par UUID,
    traite_le TIMESTAMPTZ,
    motif_rejet VARCHAR(255),
    PRIMARY KEY (id)
);
CREATE INDEX idx_justificatif_eleve ON academic_attendance.justificatif (eleve_id);
