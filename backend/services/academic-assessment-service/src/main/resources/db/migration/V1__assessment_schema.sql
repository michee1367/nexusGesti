CREATE SCHEMA IF NOT EXISTS academic_assessment;

-- ============================================================
-- AGREGAT : EVALUATION
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_assessment.evaluation (
    id UUID NOT NULL,
    reference VARCHAR(60) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    type VARCHAR(30) NOT NULL,
    classe_id UUID,
    cours_id UUID,
    ue_id UUID,
    semestre_id UUID,
    matiere_id UUID,
    enseignant_id UUID,
    annee_id UUID,
    periode_id UUID,
    date_evaluation DATE,
    heure_debut TIME,
    duree_minutes INTEGER,
    salle_id UUID,
    statut VARCHAR(20) NOT NULL,
    grille_id UUID,
    grille_version INTEGER,
    bareme_type VARCHAR(30),
    note_max NUMERIC(6,2),
    coefficient_declare NUMERIC(6,2),
    consignes TEXT,
    anonymise BOOLEAN NOT NULL DEFAULT FALSE,
    comptee_dans_moyenne BOOLEAN NOT NULL DEFAULT TRUE,
    declenche_session BOOLEAN NOT NULL DEFAULT FALSE,
    annule_le TIMESTAMPTZ,
    motif_annulation VARCHAR(255),
    cree_le TIMESTAMPTZ,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_evaluation_classe ON academic_assessment.evaluation (classe_id);
CREATE INDEX IF NOT EXISTS idx_evaluation_periode ON academic_assessment.evaluation (periode_id);
CREATE INDEX IF NOT EXISTS idx_evaluation_cours ON academic_assessment.evaluation (cours_id);

-- ============================================================
-- AGREGAT : FEUILLE DE NOTES (generee par l evaluation)
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_assessment.feuille_note (
    id UUID NOT NULL,
    evaluation_id UUID NOT NULL,
    statut VARCHAR(20),
    nb_notes_attendues INTEGER NOT NULL DEFAULT 0,
    nb_notes_saisies INTEGER NOT NULL DEFAULT 0,
    nb_absents INTEGER NOT NULL DEFAULT 0,
    ouverte_le TIMESTAMPTZ,
    cloturee_le TIMESTAMPTZ,
    verrouillee BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX IF NOT EXISTS ux_feuille_note_evaluation
    ON academic_assessment.feuille_note (evaluation_id);

-- ============================================================
-- NOTE (eleve)
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_assessment.note (
    id UUID NOT NULL,
    evaluation_id UUID NOT NULL,
    feuille_id UUID,
    eleve_id UUID NOT NULL,
    valeur NUMERIC(6,2),
    statut VARCHAR(20) NOT NULL,
    absent BOOLEAN NOT NULL DEFAULT FALSE,
    exclue BOOLEAN NOT NULL DEFAULT FALSE,
    motif_exclusion VARCHAR(255),
    coefficient_reel NUMERIC(6,2),
    source VARCHAR(30),
    saisie_par UUID,
    saisie_le TIMESTAMPTZ,
    approuvee BOOLEAN NOT NULL DEFAULT FALSE,
    motif_contestation VARCHAR(255),
    date_contestation TIMESTAMPTZ,
    remplacee_par UUID,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_note_evaluation ON academic_assessment.note (evaluation_id);
CREATE INDEX IF NOT EXISTS idx_note_eleve ON academic_assessment.note (eleve_id);
CREATE UNIQUE INDEX IF NOT EXISTS ux_note_evaluation_eleve
    ON academic_assessment.note (evaluation_id, eleve_id);

-- ============================================================
-- AGREGAT : BULLETIN
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_assessment.bulletin (
    id UUID NOT NULL,
    reference VARCHAR(60) NOT NULL,
    eleve_id UUID NOT NULL,
    annee_id UUID,
    periode_id UUID,
    classe_id UUID,
    type VARCHAR(30) NOT NULL,
    statut VARCHAR(20) NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    grille_id UUID,
    grille_version INTEGER,
    grille_date_activation DATE,
    cascade JSONB,
    moyenne_generale NUMERIC(6,2),
    moyenne_periode NUMERIC(6,2),
    moyenne_annuelle NUMERIC(6,2),
    rang INTEGER,
    effectif INTEGER,
    appreciation_generale VARCHAR(500),
    mention_generale VARCHAR(60),
    decision VARCHAR(120),
    nb_absences_justifiees INTEGER NOT NULL DEFAULT 0,
    nb_absences_injustifiees INTEGER NOT NULL DEFAULT 0,
    nb_retards INTEGER NOT NULL DEFAULT 0,
    taux_presence NUMERIC(7,4),
    regle_assiduite VARCHAR(120),
    respecte_regle_assiduite BOOLEAN,
    avertissement_assiduite VARCHAR(500),
    retard_jours INTEGER,
    retard_heures INTEGER,
    genere_le TIMESTAMPTZ,
    publie_le TIMESTAMPTZ,
    genere_par UUID,
    rectifie_le TIMESTAMPTZ,
    motif_rectification VARCHAR(255),
    cree_par UUID,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_bulletin_classe ON academic_assessment.bulletin (classe_id);
CREATE INDEX IF NOT EXISTS idx_bulletin_periode ON academic_assessment.bulletin (periode_id);
CREATE INDEX IF NOT EXISTS idx_bulletin_eleve ON academic_assessment.bulletin (eleve_id);

-- ============================================================
-- LIGNE DE BULLETIN
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_assessment.ligne_bulletin (
    id UUID NOT NULL,
    bulletin_id UUID NOT NULL,
    niveau VARCHAR(40) NOT NULL,
    code VARCHAR(60) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    parent_code VARCHAR(60),
    coefficient NUMERIC(8,4) NOT NULL DEFAULT 1,
    poids_normalise NUMERIC(8,4),
    moyenne_periode NUMERIC(6,2),
    moyenne_annuelle NUMERIC(6,2),
    moyenne_ue NUMERIC(6,2),
    moyenne_cours NUMERIC(6,2),
    rang INTEGER,
    effectif INTEGER,
    mention VARCHAR(60),
    appreciation VARCHAR(500),
    absent BOOLEAN NOT NULL DEFAULT FALSE,
    cas_particulier VARCHAR(255),
    ue_id UUID,
    cours_id UUID,
    matiere_id UUID,
    detail JSONB,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ligne_bulletin_bulletin
    ON academic_assessment.ligne_bulletin (bulletin_id);

-- ============================================================
-- COUPON DE NOTES (partie perforee du bulletin)
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_assessment.coupon_notes (
    id UUID NOT NULL,
    numero VARCHAR(40) NOT NULL,
    bulletin_id UUID NOT NULL,
    eleve_id UUID NOT NULL,
    annee_id UUID,
    periode_id UUID,
    classe VARCHAR(120),
    date_edition DATE NOT NULL,
    statut VARCHAR(30) NOT NULL,
    note_moyenne NUMERIC(6,2),
    mention VARCHAR(60),
    url_qr VARCHAR(500),
    code_barre VARCHAR(120),
    empreinte VARCHAR(128),
    detache_le TIMESTAMPTZ,
    detache_par UUID,
    signe_par UUID,
    signe_le TIMESTAMPTZ,
    observation VARCHAR(255),
    date_rendu TIMESTAMPTZ,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_coupon_notes_bulletin
    ON academic_assessment.coupon_notes (bulletin_id);
