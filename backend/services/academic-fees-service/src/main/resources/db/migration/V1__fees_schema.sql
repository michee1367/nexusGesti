CREATE SCHEMA IF NOT EXISTS academic_fees;

-- ============================================================
-- AGREGAT : GRILLE TARIFAIRE (racine)
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_fees.grille_tarifaire (
    id UUID NOT NULL,
    code VARCHAR(60) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    annee UUID,
    profil VARCHAR(40),
    statut VARCHAR(20) NOT NULL,
    devise VARCHAR(10),
    date_activation DATE,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    version INTEGER NOT NULL DEFAULT 1,
    modifiable BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_grille_tarifaire_annee ON academic_fees.grille_tarifaire (annee);
CREATE INDEX IF NOT EXISTS idx_grille_tarifaire_profil ON academic_fees.grille_tarifaire (profil);

CREATE TABLE IF NOT EXISTS academic_fees.ligne_grille (
    id UUID NOT NULL,
    grille_id UUID NOT NULL,
    type_frais VARCHAR(30) NOT NULL,
    categorie VARCHAR(20) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    montant NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_promo NUMERIC(14,2),
    devise VARCHAR(10),
    obligatoire BOOLEAN NOT NULL DEFAULT TRUE,
    periodicite VARCHAR(20) NOT NULL DEFAULT 'PONCTUELLE',
    jour INTEGER,
    nb_echeances INTEGER,
    ordre INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ligne_grille_grille ON academic_fees.ligne_grille (grille_id);

CREATE TABLE IF NOT EXISTS academic_fees.reduction (
    id UUID NOT NULL,
    grille_id UUID NOT NULL,
    code VARCHAR(60) NOT NULL,
    libelle VARCHAR(200),
    type VARCHAR(20) NOT NULL,
    valeur NUMERIC(14,2) NOT NULL DEFAULT 0,
    cumulable BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_reduction_grille ON academic_fees.reduction (grille_id);

CREATE TABLE IF NOT EXISTS academic_fees.condition_reduction (
    id UUID NOT NULL,
    reduction_id UUID NOT NULL,
    critere VARCHAR(60) NOT NULL,
    operateur VARCHAR(10) NOT NULL,
    valeur VARCHAR(120) NOT NULL,
    cumulable BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_condition_reduction_reduction
    ON academic_fees.condition_reduction (reduction_id);

-- ============================================================
-- AGREGAT : AVIS DE PAIEMENT (racine)
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_fees.avis_paiement (
    id UUID NOT NULL,
    reference VARCHAR(60) NOT NULL,
    eleve UUID NOT NULL,
    annee UUID,
    classe UUID,
    grille_id UUID,
    date_emission DATE,
    date_echeance_globale DATE,
    statut VARCHAR(30) NOT NULL,
    montant_brut NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_reduction NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_net NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_regle NUMERIC(14,2) NOT NULL DEFAULT 0,
    solde_du NUMERIC(14,2) NOT NULL DEFAULT 0,
    devise VARCHAR(10),
    taux_change NUMERIC(14,6),
    journalier UUID,
    ecriture_generee UUID,
    motif_annulation VARCHAR(255),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_avis_paiement_eleve ON academic_fees.avis_paiement (eleve);
CREATE INDEX IF NOT EXISTS idx_avis_paiement_annee ON academic_fees.avis_paiement (annee);

CREATE TABLE IF NOT EXISTS academic_fees.ligne_frais (
    id UUID NOT NULL,
    avis_id UUID NOT NULL,
    type_frais VARCHAR(30) NOT NULL,
    libelle VARCHAR(200),
    montant_brut NUMERIC(14,2) NOT NULL DEFAULT 0,
    taux_reduction NUMERIC(6,4),
    montant_reduction NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_net NUMERIC(14,2) NOT NULL DEFAULT 0,
    nb_echeances INTEGER NOT NULL DEFAULT 1,
    annee_reference VARCHAR(20),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ligne_frais_avis ON academic_fees.ligne_frais (avis_id);

CREATE TABLE IF NOT EXISTS academic_fees.echeance (
    id UUID NOT NULL,
    ligne_id UUID NOT NULL,
    numero INTEGER NOT NULL,
    date_echeance DATE,
    montant NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_regle NUMERIC(14,2) NOT NULL DEFAULT 0,
    statut VARCHAR(30) NOT NULL DEFAULT 'A_VENIR',
    date_reglement DATE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_echeance_ligne ON academic_fees.echeance (ligne_id);

-- ============================================================
-- AGREGAT : PAIEMENT (racine)
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_fees.paiement (
    id UUID NOT NULL,
    reference VARCHAR(60) NOT NULL,
    avis_id UUID,
    eleve UUID NOT NULL,
    date DATE,
    montant NUMERIC(14,2) NOT NULL DEFAULT 0,
    moyen VARCHAR(20) NOT NULL,
    compte_debit VARCHAR(120),
    reference_transaction VARCHAR(120),
    id_evenement VARCHAR(120),
    beneficiaire VARCHAR(20) NOT NULL DEFAULT 'ELEVE',
    reference_beneficiaire VARCHAR(120),
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    encaisse_par UUID,
    reste_a_rembourser NUMERIC(14,2) NOT NULL DEFAULT 0,
    coupon_genere UUID,
    ecriture_generee UUID,
    motif_annulation VARCHAR(255),
    motif_rejet VARCHAR(255),
    date_validation TIMESTAMPTZ,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_paiement_eleve ON academic_fees.paiement (eleve);
CREATE INDEX IF NOT EXISTS idx_paiement_avis ON academic_fees.paiement (avis_id);

CREATE TABLE IF NOT EXISTS academic_fees.imputation (
    id UUID NOT NULL,
    paiement_id UUID NOT NULL,
    echeance_id UUID NOT NULL,
    montant NUMERIC(14,2) NOT NULL DEFAULT 0,
    date_imputation DATE,
    ordre INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_imputation_paiement ON academic_fees.imputation (paiement_id);
CREATE INDEX IF NOT EXISTS idx_imputation_echeance ON academic_fees.imputation (echeance_id);

CREATE TABLE IF NOT EXISTS academic_fees.paiement_mobile_money (
    id UUID NOT NULL,
    paiement_id UUID NOT NULL,
    operateur VARCHAR(60),
    numero_telephone VARCHAR(30),
    reference_transaction VARCHAR(120),
    date_transaction TIMESTAMPTZ,
    statut_transaction VARCHAR(40),
    frais NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_recu NUMERIC(14,2) NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_mobile_money_paiement
    ON academic_fees.paiement_mobile_money (paiement_id);

CREATE TABLE IF NOT EXISTS academic_fees.paiement_virement (
    id UUID NOT NULL,
    paiement_id UUID NOT NULL,
    banque VARCHAR(120),
    numero_compte VARCHAR(60),
    reference_virement VARCHAR(120),
    date_valeur DATE,
    commission NUMERIC(14,2) NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_virement_paiement ON academic_fees.paiement_virement (paiement_id);

CREATE TABLE IF NOT EXISTS academic_fees.paiement_especes (
    id UUID NOT NULL,
    paiement_id UUID NOT NULL,
    caja UUID,
    numero_recepisse VARCHAR(60),
    agent UUID,
    contresigne BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_especes_paiement ON academic_fees.paiement_especes (paiement_id);

CREATE TABLE IF NOT EXISTS academic_fees.remboursement (
    id UUID NOT NULL,
    paiement_id UUID NOT NULL,
    montant NUMERIC(14,2) NOT NULL DEFAULT 0,
    motif VARCHAR(255),
    date DATE,
    moyen VARCHAR(20),
    reference_transaction VARCHAR(120),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_remboursement_paiement ON academic_fees.remboursement (paiement_id);

-- ============================================================
-- AGREGAT : COUPON DE PAIEMENT
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_fees.coupon_paiement (
    id UUID NOT NULL,
    numero VARCHAR(40) NOT NULL,
    sequence INTEGER NOT NULL,
    annee UUID NOT NULL,
    eleve UUID NOT NULL,
    paiement UUID,
    avis UUID,
    ligne_frais UUID,
    date_edition DATE,
    montant NUMERIC(14,2) NOT NULL DEFAULT 0,
    libelle VARCHAR(200),
    moyen VARCHAR(20),
    statut VARCHAR(20) NOT NULL DEFAULT 'A_TIRER',
    devise VARCHAR(10),
    mention VARCHAR(200),
    url_qr VARCHAR(500),
    code_barre VARCHAR(120),
    empreinte VARCHAR(128),
    remis_le TIMESTAMPTZ,
    remis_a UUID,
    reference_signature VARCHAR(120),
    annule BOOLEAN NOT NULL DEFAULT FALSE,
    motif_annulation VARCHAR(255),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_coupon_paiement_eleve ON academic_fees.coupon_paiement (eleve);
CREATE INDEX IF NOT EXISTS idx_coupon_paiement_annee ON academic_fees.coupon_paiement (annee);

-- ============================================================
-- JOURNALIER
-- ============================================================

CREATE TABLE IF NOT EXISTS academic_fees.journalier (
    id UUID NOT NULL,
    date DATE,
    site UUID,
    statut VARCHAR(20) NOT NULL DEFAULT 'OUVERT',
    fond NUMERIC(14,2) NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_journalier_date ON academic_fees.journalier (date);
CREATE INDEX IF NOT EXISTS idx_journalier_site ON academic_fees.journalier (site);