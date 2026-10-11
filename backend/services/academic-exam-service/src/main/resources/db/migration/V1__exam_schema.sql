CREATE SCHEMA IF NOT EXISTS academic_exam;

-- ============================================================
-- AGREGAT : SESSION D EXAMEN (racine)
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_exam.session_examen (
    id UUID NOT NULL,
    reference VARCHAR(60) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    type VARCHAR(40) NOT NULL,
    annee_id UUID,
    periode_id UUID,
    classe_id UUID,
    date_ouverture DATE,
    date_cloture DATE,
    statut VARCHAR(30) NOT NULL,
    annee_universitaire BOOLEAN NOT NULL DEFAULT FALSE,
    convocation_emise BOOLEAN NOT NULL DEFAULT FALSE,
    proces_verbal_id UUID,
    grille_deliberation VARCHAR(120),
    nb_candidats_attendus INTEGER,
    nb_candidats_convoques INTEGER,
    motif_annulation VARCHAR(255),
    annule_le TIMESTAMPTZ,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_session_examen_annee ON academic_exam.session_examen (annee_id);
CREATE INDEX IF NOT EXISTS idx_session_examen_classe ON academic_exam.session_examen (classe_id);

-- ============================================================
-- EPREUVE
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_exam.epreuve (
    id UUID NOT NULL,
    session_id UUID NOT NULL,
    reference VARCHAR(60) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    matiere_id UUID,
    ue_id UUID,
    type VARCHAR(30) NOT NULL,
    date_epreuve DATE,
    heure_debut TIME,
    duree_minutes INTEGER,
    bareme VARCHAR(60),
    coefficient NUMERIC(6,2),
    statut VARCHAR(20) NOT NULL,
    comptabilise_dans_moyenne BOOLEAN NOT NULL DEFAULT TRUE,
    periode_rattachement UUID,
    evaluation_origine UUID,
    salle_par_defaut UUID,
    consignes TEXT,
    anonymise BOOLEAN NOT NULL DEFAULT FALSE,
    note_eliminatoire NUMERIC(6,2),
    declaree_hors_moyenne BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_epreuve_session ON academic_exam.epreuve (session_id);

-- ============================================================
-- CRENEAU
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_exam.creneau (
    id UUID NOT NULL,
    session_id UUID NOT NULL,
    date_creneau DATE,
    type VARCHAR(20) NOT NULL,
    heure_debut TIME,
    heure_fin TIME,
    ordre INTEGER,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_creneau_session ON academic_exam.creneau (session_id);

-- ============================================================
-- CONVOCATION
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_exam.convocation (
    id UUID NOT NULL,
    session_id UUID NOT NULL,
    eleve_id UUID NOT NULL,
    numero VARCHAR(40) NOT NULL,
    date_convocation DATE,
    statut VARCHAR(20) NOT NULL,
    generee_le TIMESTAMPTZ,
    notifiee_le TIMESTAMPTZ,
    canal VARCHAR(40),
    accuse BOOLEAN NOT NULL DEFAULT FALSE,
    qr_code VARCHAR(200),
    emplacement VARCHAR(200),
    url_telechargement VARCHAR(500),
    signee BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_convocation_session ON academic_exam.convocation (session_id);
CREATE INDEX IF NOT EXISTS idx_convocation_eleve ON academic_exam.convocation (eleve_id);

CREATE TABLE IF NOT EXISTS academic_exam.ligne_convocation (
    id UUID NOT NULL,
    convocation_id UUID NOT NULL,
    epreuve_id UUID,
    date_ligne DATE,
    heure_debut TIME,
    salle VARCHAR(120),
    numero_place VARCHAR(40),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ligne_convocation_conv
    ON academic_exam.ligne_convocation (convocation_id);

-- ============================================================
-- NOTE D EXAMEN
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_exam.note_examen (
    id UUID NOT NULL,
    epreuve_id UUID NOT NULL,
    eleve_id UUID NOT NULL,
    valeur NUMERIC(6,2),
    statut VARCHAR(20) NOT NULL,
    absent BOOLEAN NOT NULL DEFAULT FALSE,
    hors_moyenne BOOLEAN NOT NULL DEFAULT FALSE,
    exclue BOOLEAN NOT NULL DEFAULT FALSE,
    motif_exclusion VARCHAR(255),
    periode_imputation UUID,
    correcteur UUID,
    seconde_correction BOOLEAN NOT NULL DEFAULT FALSE,
    coefficient NUMERIC(6,2),
    saisi_le TIMESTAMPTZ,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_note_examen_epreuve ON academic_exam.note_examen (epreuve_id);
CREATE INDEX IF NOT EXISTS idx_note_examen_eleve ON academic_exam.note_examen (eleve_id);
CREATE UNIQUE INDEX IF NOT EXISTS ux_note_examen_epreuve_eleve
    ON academic_exam.note_examen (epreuve_id, eleve_id);

-- ============================================================
-- CORRECTION
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_exam.correction (
    id UUID NOT NULL,
    epreuve_id UUID NOT NULL,
    correcteur UUID,
    date_correction DATE,
    statut VARCHAR(20) NOT NULL,
    nb_notes_corrigees INTEGER NOT NULL DEFAULT 0,
    moyenne_brute NUMERIC(6,2),
    moyenne_retenue NUMERIC(6,2),
    plus_forte_moyenne NUMERIC(6,2),
    plus_faible_moyenne NUMERIC(6,2),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_correction_epreuve ON academic_exam.correction (epreuve_id);

-- ============================================================
-- AGREGAT : DELIBERATION
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_exam.deliberation (
    id UUID NOT NULL,
    session_id UUID,
    reference VARCHAR(60) NOT NULL,
    date_deliberation DATE,
    statut VARCHAR(20) NOT NULL,
    president UUID,
    membres JSONB,
    quorum_atteint BOOLEAN NOT NULL DEFAULT FALSE,
    grille_version INTEGER,
    date_cloture TIMESTAMPTZ,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_deliberation_session ON academic_exam.deliberation (session_id);

CREATE TABLE IF NOT EXISTS academic_exam.decision (
    id UUID NOT NULL,
    deliberation_id UUID NOT NULL,
    eleve_id UUID NOT NULL,
    classe_id UUID,
    moyenne_generale NUMERIC(6,2),
    moyenne_periode NUMERIC(6,2),
    rang INTEGER,
    effectif INTEGER,
    taux_reussite NUMERIC(6,2),
    nb_absences INTEGER,
    decision VARCHAR(20),
    mention VARCHAR(60),
    seuil_applique VARCHAR(120),
    cr VARCHAR(5),
    motif VARCHAR(255),
    date_decision DATE,
    notifie BOOLEAN NOT NULL DEFAULT FALSE,
    date_notification TIMESTAMPTZ,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_decision_deliberation ON academic_exam.decision (deliberation_id);

CREATE TABLE IF NOT EXISTS academic_exam.decision_ue (
    id UUID NOT NULL,
    deliberation_id UUID NOT NULL,
    eleve_id UUID NOT NULL,
    ue_id UUID,
    moyenne_ue NUMERIC(6,2),
    credits NUMERIC(8,2),
    decision VARCHAR(20),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_decision_ue_deliberation ON academic_exam.decision_ue (deliberation_id);

CREATE TABLE IF NOT EXISTS academic_exam.detail_ue (
    id UUID NOT NULL,
    decision_ue_id UUID NOT NULL,
    cours_id UUID,
    moyenne_cours NUMERIC(6,2),
    coefficient NUMERIC(6,2),
    admis BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_detail_ue_decision ON academic_exam.detail_ue (decision_ue_id);

CREATE TABLE IF NOT EXISTS academic_exam.resultat_officiel (
    id UUID NOT NULL,
    decision_id UUID,
    reference VARCHAR(60) NOT NULL,
    publie BOOLEAN NOT NULL DEFAULT FALSE,
    date_publication TIMESTAMPTZ,
    generateur UUID,
    url_document VARCHAR(500),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_resultat_officiel_decision ON academic_exam.resultat_officiel (decision_id);

-- ============================================================
-- RATTRAPAGE
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_exam.rattrapage (
    id UUID NOT NULL,
    session_origine_id UUID,
    session_rattrapage_id UUID,
    eleve_id UUID NOT NULL,
    epreuves_concernees JSONB,
    convocation_id UUID,
    ne_pas_presenter BOOLEAN NOT NULL DEFAULT FALSE,
    motif VARCHAR(255),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_rattrapage_eleve ON academic_exam.rattrapage (eleve_id);

-- ============================================================
-- AGREGAT : PROCES VERBAL
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_exam.proces_verbal (
    id UUID NOT NULL,
    session_id UUID,
    reference VARCHAR(60) NOT NULL,
    statut VARCHAR(20) NOT NULL,
    date_redaction DATE,
    redige_par UUID,
    presence_directeur BOOLEAN NOT NULL DEFAULT FALSE,
    observations TEXT,
    nb_absences INTEGER NOT NULL DEFAULT 0,
    nb_incidents INTEGER NOT NULL DEFAULT 0,
    date_cloture TIMESTAMPTZ,
    motif_contestation VARCHAR(255),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_proces_verbal_session ON academic_exam.proces_verbal (session_id);

CREATE TABLE IF NOT EXISTS academic_exam.signature (
    id UUID NOT NULL,
    proces_verbal_id UUID NOT NULL,
    signataire UUID,
    role VARCHAR(60),
    date_signature TIMESTAMPTZ,
    empreinte VARCHAR(128),
    validee BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_signature_pv ON academic_exam.signature (proces_verbal_id);
