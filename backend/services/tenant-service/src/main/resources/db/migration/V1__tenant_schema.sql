CREATE SCHEMA IF NOT EXISTS tenant;

-- ============================================================
-- AGREGAT : ETABLISSEMENT
-- D8 : Etablissement.id = tenant_id = en-tete X-Tenant-Id.
-- ============================================================

CREATE TABLE IF NOT EXISTS tenant.etablissement (
    id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    denomination VARCHAR(150) NOT NULL,
    sigle VARCHAR(20),
    profil VARCHAR(20) NOT NULL,
    statut VARCHAR(20) NOT NULL,
    adr_rue VARCHAR(120),
    adr_avenue VARCHAR(120),
    adr_quartier VARCHAR(120),
    adr_commune VARCHAR(120),
    adr_ville VARCHAR(120),
    adr_province VARCHAR(120),
    adr_territoire VARCHAR(120),
    adr_code_postal VARCHAR(20),
    adr_pays VARCHAR(60),
    telephone VARCHAR(30),
    email_officiel VARCHAR(120),
    devise VARCHAR(10),
    pays VARCHAR(60),
    fuseau_horaire VARCHAR(40),
    capacite_accueil INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tenant.site (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES tenant.etablissement (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    adr_rue VARCHAR(120),
    adr_avenue VARCHAR(120),
    adr_quartier VARCHAR(120),
    adr_commune VARCHAR(120),
    adr_ville VARCHAR(120),
    adr_province VARCHAR(120),
    adr_territoire VARCHAR(120),
    adr_code_postal VARCHAR(20),
    adr_pays VARCHAR(60),
    PRIMARY KEY (id)
);
CREATE INDEX idx_site_etablissement ON tenant.site (etablissement_id);

CREATE TABLE IF NOT EXISTS tenant.parametre (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES tenant.etablissement (id),
    cle VARCHAR(80) NOT NULL,
    valeur VARCHAR(500),
    type_valeur VARCHAR(20) NOT NULL,
    categorie VARCHAR(60),
    description VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE (etablissement_id, cle)
);
CREATE INDEX idx_parametre_etablissement ON tenant.parametre (etablissement_id);

CREATE TABLE IF NOT EXISTS tenant.grille_preset (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES tenant.etablissement (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    profil VARCHAR(20) NOT NULL,
    description VARCHAR(255),
    systeme BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_grille_preset_etablissement ON tenant.grille_preset (etablissement_id);

CREATE TABLE IF NOT EXISTS tenant.cycle (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES tenant.etablissement (id),
    code VARCHAR(20) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    rang INTEGER NOT NULL,
    profil VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (etablissement_id, code)
);
CREATE INDEX idx_cycle_etablissement ON tenant.cycle (etablissement_id);

CREATE TABLE IF NOT EXISTS tenant.filiere (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES tenant.etablissement (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(150) NOT NULL,
    departement VARCHAR(120),
    diplome VARCHAR(120),
    niveau_diplome VARCHAR(30),
    duree_annees INTEGER,
    cycle_id UUID REFERENCES tenant.cycle (id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_filiere_etablissement ON tenant.filiere (etablissement_id);

CREATE TABLE IF NOT EXISTS tenant.programme (
    id UUID NOT NULL,
    filiere_id UUID NOT NULL REFERENCES tenant.filiere (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(150) NOT NULL,
    parcours JSONB DEFAULT '[]',
    total_credits NUMERIC(8,2),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_programme_filiere ON tenant.programme (filiere_id);

-- ============================================================
-- AGREGAT : ANNEE SCOLAIRE / ACADEMIQUE
-- ============================================================

CREATE TABLE IF NOT EXISTS tenant.annee_scolaire (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES tenant.etablissement (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    type VARCHAR(20) NOT NULL,
    date_debut TIMESTAMPTZ NOT NULL,
    date_fin TIMESTAMPTZ NOT NULL,
    statut VARCHAR(20) NOT NULL,
    annee_reference INTEGER NOT NULL,
    site_par_defaut_id UUID REFERENCES tenant.site (id),
    PRIMARY KEY (id)
);
CREATE INDEX idx_annee_etablissement ON tenant.annee_scolaire (etablissement_id);

CREATE TABLE IF NOT EXISTS tenant.periode (
    id UUID NOT NULL,
    annee_id UUID NOT NULL REFERENCES tenant.annee_scolaire (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    rang INTEGER NOT NULL,
    date_debut TIMESTAMPTZ NOT NULL,
    date_fin TIMESTAMPTZ NOT NULL,
    type VARCHAR(20) NOT NULL,
    cloturee BOOLEAN NOT NULL DEFAULT FALSE,
    date_cloture TIMESTAMPTZ,
    PRIMARY KEY (id)
);
CREATE INDEX idx_periode_annee ON tenant.periode (annee_id);

-- ============================================================
-- GRILLE DE NOTATION  (aucune valeur en dur : donnees versionnees)
-- ============================================================

CREATE TABLE IF NOT EXISTS tenant.grille_notation (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES tenant.etablissement (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    profil VARCHAR(20) NOT NULL,
    annee_id UUID REFERENCES tenant.annee_scolaire (id),
    date_activation TIMESTAMPTZ,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    modifiable BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_grille_notation_etablissement ON tenant.grille_notation (etablissement_id);

CREATE TABLE IF NOT EXISTS tenant.bareme (
    id UUID NOT NULL,
    grille_id UUID NOT NULL REFERENCES tenant.grille_notation (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    type VARCHAR(20) NOT NULL,
    bareme_max NUMERIC(8,2) NOT NULL,
    note_minimale NUMERIC(8,2) NOT NULL,
    precision INTEGER,
    arrondi VARCHAR(20) NOT NULL,
    defaut BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_bareme_grille ON tenant.bareme (grille_id);

CREATE TABLE IF NOT EXISTS tenant.regle_ponderation (
    id UUID NOT NULL,
    grille_id UUID NOT NULL REFERENCES tenant.grille_notation (id),
    libelle VARCHAR(120) NOT NULL,
    portee VARCHAR(30) NOT NULL,
    profil VARCHAR(20),
    cycle_id UUID REFERENCES tenant.cycle (id),
    niveau VARCHAR(60),
    parcours VARCHAR(60),
    programme VARCHAR(60),
    semestre VARCHAR(60),
    ue VARCHAR(60),
    cours VARCHAR(60),
    matiere VARCHAR(60),
    enseignant_id UUID,
    classe_id UUID,
    type_evaluation VARCHAR(30),
    type_session VARCHAR(30),
    coefficient NUMERIC(6,2),
    seuil_inclusion NUMERIC(6,2),
    plafond_contribution NUMERIC(6,2),
    poids_max NUMERIC(6,2),
    priorite INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_regle_ponderation_grille ON tenant.regle_ponderation (grille_id);

CREATE TABLE IF NOT EXISTS tenant.config_cascade (
    id UUID NOT NULL,
    grille_id UUID NOT NULL UNIQUE REFERENCES tenant.grille_notation (id),
    niveaux_actifs JSONB NOT NULL DEFAULT '[]',
    strategie VARCHAR(30) NOT NULL,
    arrondi VARCHAR(20) NOT NULL,
    precision INTEGER NOT NULL DEFAULT 2,
    seuil_absorption NUMERIC(6,2),
    regle_equivalence_id UUID,
    ordre_conversion_lettre BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_config_cascade_grille ON tenant.config_cascade (grille_id);

CREATE TABLE IF NOT EXISTS tenant.regle_equivalence (
    id UUID NOT NULL,
    grille_id UUID NOT NULL REFERENCES tenant.grille_notation (id),
    strategie VARCHAR(30) NOT NULL,
    nombre_max_ignorees INTEGER,
    moyenne_minimale_ignoree NUMERIC(6,2),
    PRIMARY KEY (id)
);
CREATE INDEX idx_regle_equivalence_grille ON tenant.regle_equivalence (grille_id);

ALTER TABLE tenant.config_cascade
    ADD CONSTRAINT fk_config_cascade_regle_equivalence
    FOREIGN KEY (regle_equivalence_id) REFERENCES tenant.regle_equivalence (id);

CREATE TABLE IF NOT EXISTS tenant.principe_equivalence (
    id UUID NOT NULL,
    regle_equivalence_id UUID NOT NULL REFERENCES tenant.regle_equivalence (id),
    type VARCHAR(40) NOT NULL,
    cible VARCHAR(60),
    ponderation NUMERIC(6,2),
    PRIMARY KEY (id)
);
CREATE INDEX idx_principe_equivalence ON tenant.principe_equivalence (regle_equivalence_id);

CREATE TABLE IF NOT EXISTS tenant.seuil_decision (
    id UUID NOT NULL,
    grille_id UUID NOT NULL REFERENCES tenant.grille_notation (id),
    libelle VARCHAR(80) NOT NULL,
    portee VARCHAR(30) NOT NULL,
    cycle_id UUID REFERENCES tenant.cycle (id),
    niveau VARCHAR(60),
    parcours VARCHAR(60),
    programme VARCHAR(60),
    moyenne_minimale NUMERIC(5,2),
    moyenne_maximale NUMERIC(5,2),
    taux_reussite_minimale NUMERIC(5,2),
    moyenne_de_reussite NUMERIC(5,2),
    absences_maximales INTEGER,
    decision_resultante VARCHAR(20) NOT NULL,
    rang_min INTEGER,
    rang_max INTEGER,
    priorite INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX idx_seuil_decision_grille ON tenant.seuil_decision (grille_id);

CREATE TABLE IF NOT EXISTS tenant.regle_assiduite (
    id UUID NOT NULL,
    grille_id UUID NOT NULL REFERENCES tenant.grille_notation (id),
    portee VARCHAR(30) NOT NULL,
    cycle_id UUID REFERENCES tenant.cycle (id),
    niveau VARCHAR(60),
    absences_max_par_periode INTEGER,
    absences_par_jour_max INTEGER,
    taux_presence_minimale NUMERIC(5,2),
    absences_justifiees_exonerees BOOLEAN NOT NULL DEFAULT FALSE,
    retard_compte_comme_absence BOOLEAN NOT NULL DEFAULT FALSE,
    seuil_retard_minutes INTEGER,
    consequence VARCHAR(20),
    priorite INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX idx_regle_assiduite_grille ON tenant.regle_assiduite (grille_id);

CREATE TABLE IF NOT EXISTS tenant.mention (
    id UUID NOT NULL,
    grille_id UUID NOT NULL REFERENCES tenant.grille_notation (id),
    code VARCHAR(20) NOT NULL,
    libelle VARCHAR(80) NOT NULL,
    symbole VARCHAR(10),
    seuil_minimum NUMERIC(5,2) NOT NULL,
    seuil_maximum NUMERIC(5,2),
    couleur VARCHAR(20),
    appreciation_associee VARCHAR(120),
    ordre INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX idx_mention_grille ON tenant.mention (grille_id);

CREATE TABLE IF NOT EXISTS tenant.regle_appreciation (
    id UUID NOT NULL,
    grille_id UUID NOT NULL REFERENCES tenant.grille_notation (id),
    portee VARCHAR(30) NOT NULL,
    moyenne_minimale NUMERIC(5,2) NOT NULL,
    moyenne_maximale NUMERIC(5,2),
    libelle VARCHAR(120) NOT NULL,
    modele_texte VARCHAR(500),
    mention_id UUID REFERENCES tenant.mention (id),
    PRIMARY KEY (id)
);
CREATE INDEX idx_regle_appreciation_grille ON tenant.regle_appreciation (grille_id);

-- ============================================================
-- GRILLE DE STOCK
-- ============================================================

CREATE TABLE IF NOT EXISTS tenant.grille_stock (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES tenant.etablissement (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    profil VARCHAR(20) NOT NULL,
    annee_id UUID REFERENCES tenant.annee_scolaire (id),
    date_activation TIMESTAMPTZ,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    modifiable BOOLEAN NOT NULL DEFAULT TRUE,
    strategie_rotation_lot VARCHAR(30) NOT NULL,
    duree_quarantaine_jours INTEGER,
    seuil_alerte_peremption_jours INTEGER,
    tolerance_ecart_inventaire_pct NUMERIC(5,2),
    delai_reappro_jours INTEGER,
    seuil_alerte_taux_rotation_pct NUMERIC(5,2),
    autoriser_peremption_en_sortie BOOLEAN NOT NULL DEFAULT FALSE,
    exiger_controle_reception BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_grille_stock_etablissement ON tenant.grille_stock (etablissement_id);

CREATE TABLE IF NOT EXISTS tenant.alerte_stock (
    id UUID NOT NULL,
    grille_id UUID NOT NULL REFERENCES tenant.grille_stock (id),
    reference VARCHAR(60) NOT NULL,
    depot VARCHAR(60),
    seuil NUMERIC(12,2) NOT NULL,
    niveau VARCHAR(20) NOT NULL,
    destinataires JSONB DEFAULT '[]',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_alerte_stock_grille ON tenant.alerte_stock (grille_id);

-- ============================================================
-- GRILLE DE CONFORMITE
-- ============================================================

CREATE TABLE IF NOT EXISTS tenant.grille_conformite (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES tenant.etablissement (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    profil VARCHAR(20) NOT NULL,
    annee_id UUID REFERENCES tenant.annee_scolaire (id),
    date_activation TIMESTAMPTZ,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    modifiable BOOLEAN NOT NULL DEFAULT TRUE,
    duree_retention_dossier_medical INTEGER,
    duree_conservation_resultats INTEGER,
    duree_conservation_images INTEGER,
    duree_conservation_echantillons INTEGER,
    chiffrement_au_repos BOOLEAN NOT NULL DEFAULT FALSE,
    acces_tracable BOOLEAN NOT NULL DEFAULT FALSE,
    delai_declaration_resultat_critique INTEGER,
    niveau_acces_resultat_critique VARCHAR(60),
    archivable BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_grille_conformite_etablissement ON tenant.grille_conformite (etablissement_id);

-- ============================================================
-- REFERENTIELS PARAMETRABLES
-- ============================================================

CREATE TABLE IF NOT EXISTS tenant.referentiel (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES tenant.etablissement (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    nature VARCHAR(40) NOT NULL,
    ordre INTEGER NOT NULL DEFAULT 0,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_referentiel_etablissement ON tenant.referentiel (etablissement_id);

CREATE TABLE IF NOT EXISTS tenant.referentiel_item (
    id UUID NOT NULL,
    referentiel_id UUID NOT NULL REFERENCES tenant.referentiel (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    valeur VARCHAR(255),
    meta_donnees JSONB DEFAULT '{}',
    ordre INTEGER NOT NULL DEFAULT 0,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    comptabilise_presence BOOLEAN,
    bloque_saisie BOOLEAN,
    PRIMARY KEY (id)
);
CREATE INDEX idx_referentiel_item_referentiel ON tenant.referentiel_item (referentiel_id);