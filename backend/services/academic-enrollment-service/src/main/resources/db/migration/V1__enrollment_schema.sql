CREATE SCHEMA IF NOT EXISTS academic_enrollment;

-- ============================================================
-- AGREGAT : INSCRIPTION A L'UE (universite)
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_enrollment.inscription_ue (
    id UUID NOT NULL,
    eleve_id UUID NOT NULL,
    programme_id UUID,
    promotion_id UUID,
    ue_id UUID NOT NULL,
    annee_id UUID,
    semestre_id UUID,
    statut VARCHAR(30) NOT NULL,
    date_inscription DATE,
    credits_reconnus NUMERIC(8,2),
    moyenne_annee_precedente NUMERIC(6,2),
    moyenne_ue_precedente NUMERIC(6,2),
    option VARCHAR(120),
    motif_refus VARCHAR(255),
    rang_liste_attente INTEGER,
    valide_le TIMESTAMPTZ,
    valide_par UUID,
    PRIMARY KEY (id)
);
CREATE INDEX idx_inscription_ue_eleve ON academic_enrollment.inscription_ue (eleve_id);
CREATE INDEX idx_inscription_ue_ue ON academic_enrollment.inscription_ue (ue_id);

-- ============================================================
-- AGREGAT : GROUPE (TD / TP) ET SES MEMBRES
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_enrollment.groupe (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    type VARCHAR(20) NOT NULL,
    cours_id UUID,
    ue_id UUID,
    classe_id UUID,
    enseignant_id UUID,
    salle_id UUID,
    creneau_id UUID,
    jour VARCHAR(20),
    capacite INTEGER NOT NULL DEFAULT 0,
    effectif INTEGER NOT NULL DEFAULT 0,
    statut VARCHAR(20) NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX idx_groupe_classe ON academic_enrollment.groupe (classe_id);
CREATE INDEX idx_groupe_ue ON academic_enrollment.groupe (ue_id);

CREATE TABLE IF NOT EXISTS academic_enrollment.membre_groupe (
    id UUID NOT NULL,
    groupe_id UUID NOT NULL REFERENCES academic_enrollment.groupe (id),
    eleve_id UUID NOT NULL,
    date_affectation DATE,
    affecte_par UUID,
    rang INTEGER,
    PRIMARY KEY (id)
);
CREATE INDEX idx_membre_groupe_groupe ON academic_enrollment.membre_groupe (groupe_id);

-- ============================================================
-- AGREGAT : REGLE D'INSCRIPTION (eligibilite)
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_enrollment.regle_inscription (
    id UUID NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    programme_id UUID,
    condition_regle VARCHAR(255),
    credits_min NUMERIC(8,2),
    moyenne_min NUMERIC(6,2),
    creneau_age INTEGER,
    priorite INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
