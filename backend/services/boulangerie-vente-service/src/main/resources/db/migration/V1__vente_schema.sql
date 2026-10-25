CREATE SCHEMA IF NOT EXISTS boulangerie_vente;

-- ============================================================
-- AGREGAT : SESSION DE CAISSE (racine) - cloture bloquante
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_vente.session_caisse (
    id UUID NOT NULL,
    date DATE NOT NULL,
    fond_initial NUMERIC(14,2) NOT NULL DEFAULT 0,
    total_encaisse NUMERIC(14,2) NOT NULL DEFAULT 0,
    ecart NUMERIC(14,2),
    ouverte_par UUID,
    cloturee BOOLEAN NOT NULL DEFAULT FALSE,
    date_cloture TIMESTAMP,
    PRIMARY KEY (id)
);

-- ============================================================
-- AGREGAT : VENTE (racine) - encaissement idempotent
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_vente.vente (
    id UUID NOT NULL,
    numero_ticket VARCHAR(30) NOT NULL,
    date TIMESTAMP NOT NULL,
    session_id UUID NOT NULL,
    canal VARCHAR(20) NOT NULL DEFAULT 'COMPTOIR',
    client_code VARCHAR(60),
    montant_ht NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_tva NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_ttc NUMERIC(14,2) NOT NULL DEFAULT 0,
    mode_paiement VARCHAR(20) NOT NULL DEFAULT 'ESPECES',
    points_fidelite_gagnes INTEGER NOT NULL DEFAULT 0,
    cle_idempotence VARCHAR(120) NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_COURS',
    motif_annulation TEXT,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_vente_session
    ON boulangerie_vente.vente (session_id);

-- ============================================================
-- COMPOSE : LIGNE VENTE
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_vente.ligne_vente (
    id UUID NOT NULL,
    vente_id UUID NOT NULL,
    produit_code VARCHAR(60) NOT NULL,
    designation VARCHAR(200) NOT NULL,
    quantite NUMERIC(14,4) NOT NULL DEFAULT 0,
    prix_unitaire_ht NUMERIC(14,4) NOT NULL DEFAULT 0,
    taux_tva NUMERIC(6,2) NOT NULL DEFAULT 0,
    remise_pct NUMERIC(6,2) NOT NULL DEFAULT 0,
    montant_ht NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_tva NUMERIC(14,2) NOT NULL DEFAULT 0,
    lot_numero VARCHAR(60),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ligne_vente_vente
    ON boulangerie_vente.ligne_vente (vente_id);

-- ============================================================
-- COMPOSE : PAIEMENT VENTE - rendu en especes
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_vente.paiement_vente (
    id UUID NOT NULL,
    vente_id UUID NOT NULL,
    mode VARCHAR(20) NOT NULL,
    montant NUMERIC(14,2) NOT NULL DEFAULT 0,
    devise VARCHAR(3) NOT NULL DEFAULT 'USD',
    montant_recu NUMERIC(14,2) NOT NULL DEFAULT 0,
    rendu NUMERIC(14,2) NOT NULL DEFAULT 0,
    reference_externe VARCHAR(120),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_paiement_vente_vente
    ON boulangerie_vente.paiement_vente (vente_id);

-- ============================================================
-- AGREGAT : COMMANDE CLIENT (racine) - acompte et solde
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_vente.commande_client (
    id UUID NOT NULL,
    numero VARCHAR(40) NOT NULL,
    client_code VARCHAR(60) NOT NULL,
    date_commande DATE NOT NULL,
    date_retrait DATE,
    montant_ht NUMERIC(14,2) NOT NULL DEFAULT 0,
    montant_ttc NUMERIC(14,2) NOT NULL DEFAULT 0,
    acompte NUMERIC(14,2) NOT NULL DEFAULT 0,
    statut VARCHAR(20) NOT NULL DEFAULT 'BROUILLON',
    motif_annulation TEXT,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_commande_client_code
    ON boulangerie_vente.commande_client (client_code);

-- ============================================================
-- COMPOSE : LIGNE COMMANDE CLIENT
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_vente.ligne_commande_client (
    id UUID NOT NULL,
    commande_id UUID NOT NULL,
    produit_code VARCHAR(60) NOT NULL,
    designation VARCHAR(200) NOT NULL,
    quantite NUMERIC(14,4) NOT NULL DEFAULT 0,
    prix_unitaire NUMERIC(14,4) NOT NULL DEFAULT 0,
    personnalisation VARCHAR(400),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ligne_commande_client_commande
    ON boulangerie_vente.ligne_commande_client (commande_id);

-- ============================================================
-- COMPOSE : ACOMPTE - garantie sur la commande client
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_vente.acompte (
    id UUID NOT NULL,
    commande_id UUID NOT NULL,
    montant NUMERIC(14,2) NOT NULL,
    date DATE NOT NULL,
    mode VARCHAR(20) NOT NULL,
    reference VARCHAR(120),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_acompte_commande
    ON boulangerie_vente.acompte (commande_id);
