-- Schema fusionne du domaine boulangerie : produit, production, approvisionnement, vente.
CREATE SCHEMA IF NOT EXISTS boulangerie;


-- ============================================================
-- AGREGAT : GAMME (reference catalogue)
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.gamme (
    id UUID NOT NULL,
    code VARCHAR(60) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    ordre INTEGER NOT NULL DEFAULT 0,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);

-- ============================================================
-- AGREGAT : CATEGORIE PRODUIT (reference catalogue)
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.categorie_produit (
    id UUID NOT NULL,
    code VARCHAR(60) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    ordre INTEGER NOT NULL DEFAULT 0,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);

-- ============================================================
-- AGREGAT : PRODUIT (racine) - porte le metier, reflete un Article fin-stock
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.produit (
    id UUID NOT NULL,
    code VARCHAR(60) NOT NULL,
    designation VARCHAR(200) NOT NULL,
    gamme_code VARCHAR(60),
    categorie_code VARCHAR(60),
    article_code VARCHAR(60),
    unite_vente VARCHAR(20) NOT NULL,
    poids_unitaire_grammes INTEGER,
    prix_vente_ht NUMERIC(14,2) NOT NULL DEFAULT 0,
    taux_tva NUMERIC(6,2) NOT NULL DEFAULT 0,
    code_barre VARCHAR(120),
    dlc_heures INTEGER,
    allergenes JSONB,
    fabricable BOOLEAN NOT NULL DEFAULT FALSE,
    vendable_au_detail BOOLEAN NOT NULL DEFAULT TRUE,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_produit_gamme ON boulangerie.produit (gamme_code);
CREATE INDEX IF NOT EXISTS idx_produit_categorie ON boulangerie.produit (categorie_code);
CREATE INDEX IF NOT EXISTS idx_produit_article ON boulangerie.produit (article_code);

-- ============================================================
-- AGREGAT : FICHE RECETTE (racine) - versionnee, cout de revient
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.fiche_recette (
    id UUID NOT NULL,
    code VARCHAR(60) NOT NULL,
    produit_code VARCHAR(60) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    rendement_quantite NUMERIC(14,4) NOT NULL DEFAULT 0,
    rendement_unite VARCHAR(20) NOT NULL,
    temps_preparation_min INTEGER NOT NULL DEFAULT 0,
    temps_cuisson_min INTEGER NOT NULL DEFAULT 0,
    temperature_cuisson_c INTEGER,
    instructions TEXT,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    modifiable BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_fiche_recette_produit ON boulangerie.fiche_recette (produit_code);

-- ============================================================
-- COMPOSE : INGREDIENT RECETTE - reference un Article par code (fin-stock)
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.ingredient_recette (
    id UUID NOT NULL,
    recette_id UUID NOT NULL,
    article_code VARCHAR(60) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    quantite NUMERIC(14,4) NOT NULL DEFAULT 0,
    unite VARCHAR(20) NOT NULL,
    taux_perte NUMERIC(6,4) NOT NULL DEFAULT 0,
    ordre INTEGER NOT NULL DEFAULT 0,
    comptabilise_cout BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ingredient_recette_recette ON boulangerie.ingredient_recette (recette_id);

-- ============================================================
-- COMPOSE : LIGNE COUT - main d'oeuvre, energie, emballage (hors matiere)
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.ligne_cout (
    id UUID NOT NULL,
    recette_id UUID NOT NULL,
    nature VARCHAR(30) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    montant NUMERIC(14,2) NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ligne_cout_recette ON boulangerie.ligne_cout (recette_id);


-- ============================================================
-- AGREGAT : PLAN PRODUCTION (racine) - planifie une campagne de fournees
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.plan_production (
    id UUID NOT NULL,
    date DATE NOT NULL,
    atelier_code VARCHAR(60),
    statut VARCHAR(20) NOT NULL DEFAULT 'BROUILLON',
    besoins_matiere JSONB,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_plan_production_date ON boulangerie.plan_production (date);

-- ============================================================
-- COMPOSE : LIGNE PLAN - une recette versionnee a produire
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.ligne_plan (
    id UUID NOT NULL,
    plan_id UUID NOT NULL,
    recette_code VARCHAR(60) NOT NULL,
    version_recette INTEGER,
    quantite_prevue NUMERIC(14,4) NOT NULL DEFAULT 0,
    produit_code VARCHAR(60),
    four_code VARCHAR(60),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ligne_plan_plan ON boulangerie.ligne_plan (plan_id);

-- ============================================================
-- AGREGAT : FOURNEE (racine) - execution d'une recette, tracabilite par lot
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.fournee (
    id UUID NOT NULL,
    numero VARCHAR(40) NOT NULL,
    recette_code VARCHAR(60) NOT NULL,
    version_recette INTEGER,
    produit_code VARCHAR(60),
    four_code VARCHAR(60),
    operateur_id UUID,
    date_debut TIMESTAMP,
    date_fin TIMESTAMP,
    quantite_prevue NUMERIC(14,4) NOT NULL DEFAULT 0,
    quantite_realisee NUMERIC(14,4) NOT NULL DEFAULT 0,
    statut VARCHAR(20) NOT NULL DEFAULT 'PLANIFIEE',
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_fournee_recette ON boulangerie.fournee (recette_code);

-- ============================================================
-- COMPOSE : LIGNE CONSOMMATION - sortie de stock reelle par lot (FEFO)
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.ligne_consommation (
    id UUID NOT NULL,
    fournee_id UUID NOT NULL,
    article_code VARCHAR(60) NOT NULL,
    lot_numero VARCHAR(60),
    quantite_theorique NUMERIC(14,4) NOT NULL DEFAULT 0,
    quantite_reelle NUMERIC(14,4) NOT NULL DEFAULT 0,
    prix_moyen NUMERIC(14,4) NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ligne_consommation_fournee ON boulangerie.ligne_consommation (fournee_id);

-- ============================================================
-- AGREGAT : PERTE PRODUCTION - perte valorisee au prix moyen
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.perte_production (
    id UUID NOT NULL,
    fournee_id UUID NOT NULL,
    article_code VARCHAR(60) NOT NULL,
    quantite NUMERIC(14,4) NOT NULL DEFAULT 0,
    type_perte VARCHAR(30) NOT NULL,
    motif TEXT,
    valorisation NUMERIC(14,2) NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_perte_production_fournee ON boulangerie.perte_production (fournee_id);


-- ============================================================
-- AGREGAT : FOURNISSEUR (reference approvisionnement)
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.fournisseur (
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

CREATE TABLE IF NOT EXISTS boulangerie.commande_achat (
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
    ON boulangerie.commande_achat (fournisseur_tiers_code);

-- ============================================================
-- COMPOSE : LIGNE COMMANDE ACHAT
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.ligne_commande_achat (
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
    ON boulangerie.ligne_commande_achat (commande_id);

-- ============================================================
-- AGREGAT : RECEPTION (racine) - controle bloquant avant stock
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.reception (
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
    ON boulangerie.reception (commande_id);

-- ============================================================
-- COMPOSE : LIGNE RECEPTION - lot et peremption par ligne
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.ligne_reception (
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
    ON boulangerie.ligne_reception (reception_id);


-- ============================================================
-- AGREGAT : SESSION DE CAISSE (racine) - cloture bloquante
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.session_caisse (
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

CREATE TABLE IF NOT EXISTS boulangerie.vente (
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
    ON boulangerie.vente (session_id);

-- ============================================================
-- COMPOSE : LIGNE VENTE
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.ligne_vente (
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
    ON boulangerie.ligne_vente (vente_id);

-- ============================================================
-- COMPOSE : PAIEMENT VENTE - rendu en especes
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.paiement_vente (
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
    ON boulangerie.paiement_vente (vente_id);

-- ============================================================
-- AGREGAT : COMMANDE CLIENT (racine) - acompte et solde
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.commande_client (
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
    ON boulangerie.commande_client (client_code);

-- ============================================================
-- COMPOSE : LIGNE COMMANDE CLIENT
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.ligne_commande_client (
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
    ON boulangerie.ligne_commande_client (commande_id);

-- ============================================================
-- COMPOSE : ACOMPTE - garantie sur la commande client
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie.acompte (
    id UUID NOT NULL,
    commande_id UUID NOT NULL,
    montant NUMERIC(14,2) NOT NULL,
    date DATE NOT NULL,
    mode VARCHAR(20) NOT NULL,
    reference VARCHAR(120),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_acompte_commande
    ON boulangerie.acompte (commande_id);
