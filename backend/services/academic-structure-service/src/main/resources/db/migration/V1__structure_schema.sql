CREATE SCHEMA IF NOT EXISTS academic_structure;

-- ============================================================
-- AGREGAT : ARBRE PEDAGOGIQUE (niveau > classe)
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_structure.niveau (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    rang INTEGER NOT NULL,
    cycle VARCHAR(20) NOT NULL,
    ordre INTEGER NOT NULL DEFAULT 0,
    niveau_superieur_id UUID,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS academic_structure.classe (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    niveau_id UUID REFERENCES academic_structure.niveau (id),
    annee_id UUID,
    site_id UUID,
    capacite INTEGER,
    effectif_reel INTEGER NOT NULL DEFAULT 0,
    statut VARCHAR(20) NOT NULL,
    salle_attitree_id UUID,
    regime_horaire_code VARCHAR(30),
    PRIMARY KEY (id)
);
CREATE INDEX idx_classe_niveau ON academic_structure.classe (niveau_id);

-- ============================================================
-- AGREGAT : PROMOTION (universite)
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_structure.promotion (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    programme_id UUID,
    semestre_id UUID,
    annee_id UUID,
    capacite INTEGER,
    effectif_reel INTEGER NOT NULL DEFAULT 0,
    statut VARCHAR(20) NOT NULL,
    PRIMARY KEY (id)
);

-- ============================================================
-- UNIVERSITE : PROGRAMME > SEMESTRE > UE > COURS
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_structure.semestre (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    rang INTEGER NOT NULL,
    periode_id UUID,
    total_credits_attendu NUMERIC(8,2),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS academic_structure.ue (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    semestre_id UUID REFERENCES academic_structure.semestre (id),
    credits NUMERIC(8,2),
    coefficient NUMERIC(6,2),
    volume_horaire NUMERIC(8,2),
    obligatoire BOOLEAN NOT NULL DEFAULT TRUE,
    niveau_acalcul VARCHAR(60),
    PRIMARY KEY (id)
);
CREATE INDEX idx_ue_semestre ON academic_structure.ue (semestre_id);

CREATE TABLE IF NOT EXISTS academic_structure.matiere (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    discipline VARCHAR(120),
    type VARCHAR(20) NOT NULL,
    couleur VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS academic_structure.cours_ecue (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    ue_id UUID REFERENCES academic_structure.ue (id),
    classe_id UUID REFERENCES academic_structure.classe (id),
    matiere_id UUID REFERENCES academic_structure.matiere (id),
    type VARCHAR(20) NOT NULL,
    volume_horaire NUMERIC(8,2),
    credits NUMERIC(8,2),
    coefficient NUMERIC(6,2),
    hebdomadaire BOOLEAN NOT NULL DEFAULT FALSE,
    regroupement VARCHAR(60),
    PRIMARY KEY (id)
);
CREATE INDEX idx_cours_ue ON academic_structure.cours_ecue (ue_id);
CREATE INDEX idx_cours_classe ON academic_structure.cours_ecue (classe_id);

-- ============================================================
-- SALLES ET EMPLOI DU TEMPS
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_structure.salle (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    type VARCHAR(30) NOT NULL,
    site_id UUID,
    capacite INTEGER NOT NULL DEFAULT 0,
    equipements JSONB DEFAULT '[]',
    accessible BOOLEAN NOT NULL DEFAULT FALSE,
    disponible BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS academic_structure.creneau (
    id UUID NOT NULL,
    jour VARCHAR(20) NOT NULL,
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    ordre INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS academic_structure.regime_horaire (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    jours JSONB NOT NULL DEFAULT '[]',
    creneaux_par_jour INTEGER NOT NULL,
    duree_creneau INTEGER NOT NULL,
    pause_matinale INTEGER,
    pause_meridienne INTEGER,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS academic_structure.emploi_du_temps (
    id UUID NOT NULL,
    annee_id UUID,
    classe_id UUID,
    version INTEGER NOT NULL DEFAULT 1,
    date_activation TIMESTAMPTZ,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    publie BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_emploi_classe ON academic_structure.emploi_du_temps (classe_id);

CREATE TABLE IF NOT EXISTS academic_structure.plage_emploi (
    id UUID NOT NULL,
    emploi_id UUID NOT NULL REFERENCES academic_structure.emploi_du_temps (id),
    cours_id UUID,
    creneau_id UUID REFERENCES academic_structure.creneau (id),
    salle_id UUID REFERENCES academic_structure.salle (id),
    enseignant_id UUID,
    PRIMARY KEY (id)
);
CREATE INDEX idx_plage_emploi ON academic_structure.plage_emploi (emploi_id);

CREATE TABLE IF NOT EXISTS academic_structure.conflit_emploi (
    id UUID NOT NULL,
    emploi_id UUID NOT NULL REFERENCES academic_structure.emploi_du_temps (id),
    type VARCHAR(30) NOT NULL,
    plage_a_id UUID,
    plage_b_id UUID,
    PRIMARY KEY (id)
);
CREATE INDEX idx_conflit_emploi ON academic_structure.conflit_emploi (emploi_id);
