CREATE SCHEMA IF NOT EXISTS boulangerie_production;

-- ============================================================
-- AGREGAT : PLAN PRODUCTION (racine) - planifie une campagne de fournees
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_production.plan_production (
    id UUID NOT NULL,
    date DATE NOT NULL,
    atelier_code VARCHAR(60),
    statut VARCHAR(20) NOT NULL DEFAULT 'BROUILLON',
    besoins_matiere JSONB,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_plan_production_date ON boulangerie_production.plan_production (date);

-- ============================================================
-- COMPOSE : LIGNE PLAN - une recette versionnee a produire
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_production.ligne_plan (
    id UUID NOT NULL,
    plan_id UUID NOT NULL,
    recette_code VARCHAR(60) NOT NULL,
    version_recette INTEGER,
    quantite_prevue NUMERIC(14,4) NOT NULL DEFAULT 0,
    produit_code VARCHAR(60),
    four_code VARCHAR(60),
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ligne_plan_plan ON boulangerie_production.ligne_plan (plan_id);

-- ============================================================
-- AGREGAT : FOURNEE (racine) - execution d'une recette, tracabilite par lot
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_production.fournee (
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
CREATE INDEX IF NOT EXISTS idx_fournee_recette ON boulangerie_production.fournee (recette_code);

-- ============================================================
-- COMPOSE : LIGNE CONSOMMATION - sortie de stock reelle par lot (FEFO)
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_production.ligne_consommation (
    id UUID NOT NULL,
    fournee_id UUID NOT NULL,
    article_code VARCHAR(60) NOT NULL,
    lot_numero VARCHAR(60),
    quantite_theorique NUMERIC(14,4) NOT NULL DEFAULT 0,
    quantite_reelle NUMERIC(14,4) NOT NULL DEFAULT 0,
    prix_moyen NUMERIC(14,4) NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_ligne_consommation_fournee ON boulangerie_production.ligne_consommation (fournee_id);

-- ============================================================
-- AGREGAT : PERTE PRODUCTION - perte valorisee au prix moyen
-- ============================================================

CREATE TABLE IF NOT EXISTS boulangerie_production.perte_production (
    id UUID NOT NULL,
    fournee_id UUID NOT NULL,
    article_code VARCHAR(60) NOT NULL,
    quantite NUMERIC(14,4) NOT NULL DEFAULT 0,
    type_perte VARCHAR(30) NOT NULL,
    motif TEXT,
    valorisation NUMERIC(14,2) NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_perte_production_fournee ON boulangerie_production.perte_production (fournee_id);
