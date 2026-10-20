CREATE SCHEMA IF NOT EXISTS boulangerie_approvisionnement;

-- ============================================================
-- AGREGAT : FOURNISSEUR (reference approvisionnement)
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_approvisionnement.fournisseur (
    id UUID NOT NULL,
    code VARCHAR(60) NOT NULL,
    tiers_code VARCHAR(60),
    raison_sociale VARCHAR(200) NOT NULL,
    delai_livraison_jours INTEGER NOT NULL DEFAULT 0,
    conditions_reglement VARCHAR(200),
    categorie_produits VARCHAR(200),
    montant_mini_commande NUMERIC(14,2),
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);

-- ============================================================
-- AGREGAT : COMMANDE ACHAT (racine) - engage l'etablissement
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_approvisionnement.commande_achat (
    id UUID NOT NULL,
    numero VARCHAR(40) NOT NULL,
    fournisseur_tiers_code VARCHAR(60) NOT NULL,
    date DATE NOT NULL,
    date_livraison_prevue DATE,
    statut VARCHAR(20) NOT NULL DEFAULT 'BROUILLON',
    montant_ht NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_tva NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_ttc NUMERIC(14,2) NOT NULL DEFAULT 0,
    devise VARCHAR(3) NOT NULL DEFAULT 'USD',
    motif_annulation TEXT,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_commande_achat_fournisseur
    ON boulangerie_approvisionnement.commande_achat (fournisseur_tiers_code);

-- ============================================================
-- COMPOSE : LIGNE COMMANDE ACHAT
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_approvisionnement.ligne_commande_achat (
    id UUID NOT NULL,
    commande_id UUID NOT NULL,
    article_code VARCHAR(60) NOT NULL,
    designation VARCHAR(200) NOT NULL,
    quantite_commandee NUMERIC(14,4) NOT NULL DEFAULT 0,
    quantite_recue NUMERIC(14,4) NOT NULL DEFAULT 0,
    prix_unitaire NUMERIC(14,4) NOT NULL DEFAULT 0,
    taux_tva NUMERIC(6,2) NOT NULL DEFAULT 0,
    montant_ht NUMERIC(14,2) NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ligne_commande_achat_commande
    ON boulangerie_approvisionnement.ligne_commande_achat (commande_id);

-- ============================================================
-- AGREGAT : RECEPTION (racine) - controle bloquant avant stock
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_approvisionnement.reception (
    id UUID NOT NULL,
    numero VARCHAR(40) NOT NULL,
    commande_id UUID NOT NULL,
    commande_numero VARCHAR(40) NOT NULL,
    date DATE NOT NULL,
    recu_par UUID,
    bon_livraison VARCHAR(120),
    statut VARCHAR(20) NOT NULL DEFAULT 'ATTENDUE',
    motif TEXT,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_reception_commande
    ON boulangerie_approvisionnement.reception (commande_id);

-- ============================================================
-- COMPOSE : LIGNE RECEPTION - lot et peremption par ligne
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_approvisionnement.ligne_reception (
    id UUID NOT NULL,
    reception_id UUID NOT NULL,
    article_code VARCHAR(60) NOT NULL,
    quantite_commandee NUMERIC(14,4) NOT NULL DEFAULT 0,
    quantite_recue NUMERIC(14,4) NOT NULL DEFAULT 0,
    prix_achat NUMERIC(14,4) NOT NULL DEFAULT 0,
    lot_numero VARCHAR(60),
    date_peremption DATE,
    conforme BOOLEAN NOT NULL DEFAULT TRUE,
    motif_non_conformite TEXT,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ligne_reception_reception
    ON boulangerie_approvisionnement.ligne_reception (reception_id);
