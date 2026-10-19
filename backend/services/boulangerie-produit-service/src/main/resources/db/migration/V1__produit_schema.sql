CREATE SCHEMA IF NOT EXISTS boulangerie_produit;

-- ============================================================
-- AGREGAT : GAMME (reference catalogue)
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_produit.gamme (
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

CREATE TABLE IF NOT EXISTS boulangerie_produit.categorie_produit (
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

CREATE TABLE IF NOT EXISTS boulangerie_produit.produit (
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
CREATE INDEX IF NOT EXISTS idx_produit_gamme ON boulangerie_produit.produit (gamme_code);
CREATE INDEX IF NOT EXISTS idx_produit_categorie ON boulangerie_produit.produit (categorie_code);
CREATE INDEX IF NOT EXISTS idx_produit_article ON boulangerie_produit.produit (article_code);

-- ============================================================
-- AGREGAT : FICHE RECETTE (racine) - versionnee, cout de revient
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_produit.fiche_recette (
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
CREATE INDEX IF NOT EXISTS idx_fiche_recette_produit ON boulangerie_produit.fiche_recette (produit_code);

-- ============================================================
-- COMPOSE : INGREDIENT RECETTE - reference un Article par code (fin-stock)
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_produit.ingredient_recette (
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
CREATE INDEX IF NOT EXISTS idx_ingredient_recette_recette ON boulangerie_produit.ingredient_recette (recette_id);

-- ============================================================
-- COMPOSE : LIGNE COUT - main d'oeuvre, energie, emballage (hors matiere)
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_produit.ligne_cout (
    id UUID NOT NULL,
    recette_id UUID NOT NULL,
    nature VARCHAR(30) NOT NULL,
    libelle VARCHAR(200) NOT NULL,
    montant NUMERIC(14,2) NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ligne_cout_recette ON boulangerie_produit.ligne_cout (recette_id);
