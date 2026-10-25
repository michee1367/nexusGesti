-- Socle fusionne : etablissements, identite, auth, notification, audit, reporting
CREATE SCHEMA IF NOT EXISTS socle;

-- ============================================================
-- Origine : tenant
-- ============================================================

-- ============================================================
-- AGREGAT : ETABLISSEMENT
-- D8 : Etablissement.id = tenant_id = en-tete X-Tenant-Id.
-- ============================================================

CREATE TABLE IF NOT EXISTS socle.etablissement (
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

CREATE TABLE IF NOT EXISTS socle.site (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES socle.etablissement (id),
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
CREATE INDEX idx_site_etablissement ON socle.site (etablissement_id);

CREATE TABLE IF NOT EXISTS socle.parametre (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES socle.etablissement (id),
    cle VARCHAR(80) NOT NULL,
    valeur VARCHAR(500),
    type_valeur VARCHAR(20) NOT NULL,
    categorie VARCHAR(60),
    description VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE (etablissement_id, cle)
);
CREATE INDEX idx_parametre_etablissement ON socle.parametre (etablissement_id);

CREATE TABLE IF NOT EXISTS socle.grille_preset (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES socle.etablissement (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    profil VARCHAR(20) NOT NULL,
    description VARCHAR(255),
    systeme BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_grille_preset_etablissement ON socle.grille_preset (etablissement_id);

CREATE TABLE IF NOT EXISTS socle.cycle (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES socle.etablissement (id),
    code VARCHAR(20) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    rang INTEGER NOT NULL,
    profil VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (etablissement_id, code)
);
CREATE INDEX idx_cycle_etablissement ON socle.cycle (etablissement_id);

CREATE TABLE IF NOT EXISTS socle.filiere (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES socle.etablissement (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(150) NOT NULL,
    departement VARCHAR(120),
    diplome VARCHAR(120),
    niveau_diplome VARCHAR(30),
    duree_annees INTEGER,
    cycle_id UUID REFERENCES socle.cycle (id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_filiere_etablissement ON socle.filiere (etablissement_id);

CREATE TABLE IF NOT EXISTS socle.programme (
    id UUID NOT NULL,
    filiere_id UUID NOT NULL REFERENCES socle.filiere (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(150) NOT NULL,
    parcours JSONB DEFAULT '[]',
    total_credits NUMERIC(8,2),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_programme_filiere ON socle.programme (filiere_id);

-- ============================================================
-- AGREGAT : ANNEE SCOLAIRE / ACADEMIQUE
-- ============================================================

CREATE TABLE IF NOT EXISTS socle.annee_scolaire (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES socle.etablissement (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    type VARCHAR(20) NOT NULL,
    date_debut TIMESTAMPTZ NOT NULL,
    date_fin TIMESTAMPTZ NOT NULL,
    statut VARCHAR(20) NOT NULL,
    annee_reference INTEGER NOT NULL,
    site_par_defaut_id UUID REFERENCES socle.site (id),
    PRIMARY KEY (id)
);
CREATE INDEX idx_annee_etablissement ON socle.annee_scolaire (etablissement_id);

CREATE TABLE IF NOT EXISTS socle.periode (
    id UUID NOT NULL,
    annee_id UUID NOT NULL REFERENCES socle.annee_scolaire (id),
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
CREATE INDEX idx_periode_annee ON socle.periode (annee_id);

-- ============================================================
-- GRILLE DE NOTATION  (aucune valeur en dur : donnees versionnees)
-- ============================================================

CREATE TABLE IF NOT EXISTS socle.grille_notation (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES socle.etablissement (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    profil VARCHAR(20) NOT NULL,
    annee_id UUID REFERENCES socle.annee_scolaire (id),
    date_activation TIMESTAMPTZ,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    modifiable BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_grille_notation_etablissement ON socle.grille_notation (etablissement_id);

CREATE TABLE IF NOT EXISTS socle.bareme (
    id UUID NOT NULL,
    grille_id UUID NOT NULL REFERENCES socle.grille_notation (id),
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
CREATE INDEX idx_bareme_grille ON socle.bareme (grille_id);

CREATE TABLE IF NOT EXISTS socle.regle_ponderation (
    id UUID NOT NULL,
    grille_id UUID NOT NULL REFERENCES socle.grille_notation (id),
    libelle VARCHAR(120) NOT NULL,
    portee VARCHAR(30) NOT NULL,
    profil VARCHAR(20),
    cycle_id UUID REFERENCES socle.cycle (id),
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
CREATE INDEX idx_regle_ponderation_grille ON socle.regle_ponderation (grille_id);

CREATE TABLE IF NOT EXISTS socle.config_cascade (
    id UUID NOT NULL,
    grille_id UUID NOT NULL UNIQUE REFERENCES socle.grille_notation (id),
    niveaux_actifs JSONB NOT NULL DEFAULT '[]',
    strategie VARCHAR(30) NOT NULL,
    arrondi VARCHAR(20) NOT NULL,
    precision INTEGER NOT NULL DEFAULT 2,
    seuil_absorption NUMERIC(6,2),
    regle_equivalence_id UUID,
    ordre_conversion_lettre BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_config_cascade_grille ON socle.config_cascade (grille_id);

CREATE TABLE IF NOT EXISTS socle.regle_equivalence (
    id UUID NOT NULL,
    grille_id UUID NOT NULL REFERENCES socle.grille_notation (id),
    strategie VARCHAR(30) NOT NULL,
    nombre_max_ignorees INTEGER,
    moyenne_minimale_ignoree NUMERIC(6,2),
    PRIMARY KEY (id)
);
CREATE INDEX idx_regle_equivalence_grille ON socle.regle_equivalence (grille_id);

ALTER TABLE socle.config_cascade
    ADD CONSTRAINT fk_config_cascade_regle_equivalence
    FOREIGN KEY (regle_equivalence_id) REFERENCES socle.regle_equivalence (id);

CREATE TABLE IF NOT EXISTS socle.principe_equivalence (
    id UUID NOT NULL,
    regle_equivalence_id UUID NOT NULL REFERENCES socle.regle_equivalence (id),
    type VARCHAR(40) NOT NULL,
    cible VARCHAR(60),
    ponderation NUMERIC(6,2),
    PRIMARY KEY (id)
);
CREATE INDEX idx_principe_equivalence ON socle.principe_equivalence (regle_equivalence_id);

CREATE TABLE IF NOT EXISTS socle.seuil_decision (
    id UUID NOT NULL,
    grille_id UUID NOT NULL REFERENCES socle.grille_notation (id),
    libelle VARCHAR(80) NOT NULL,
    portee VARCHAR(30) NOT NULL,
    cycle_id UUID REFERENCES socle.cycle (id),
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
CREATE INDEX idx_seuil_decision_grille ON socle.seuil_decision (grille_id);

CREATE TABLE IF NOT EXISTS socle.regle_assiduite (
    id UUID NOT NULL,
    grille_id UUID NOT NULL REFERENCES socle.grille_notation (id),
    portee VARCHAR(30) NOT NULL,
    cycle_id UUID REFERENCES socle.cycle (id),
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
CREATE INDEX idx_regle_assiduite_grille ON socle.regle_assiduite (grille_id);

CREATE TABLE IF NOT EXISTS socle.mention (
    id UUID NOT NULL,
    grille_id UUID NOT NULL REFERENCES socle.grille_notation (id),
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
CREATE INDEX idx_mention_grille ON socle.mention (grille_id);

CREATE TABLE IF NOT EXISTS socle.regle_appreciation (
    id UUID NOT NULL,
    grille_id UUID NOT NULL REFERENCES socle.grille_notation (id),
    portee VARCHAR(30) NOT NULL,
    moyenne_minimale NUMERIC(5,2) NOT NULL,
    moyenne_maximale NUMERIC(5,2),
    libelle VARCHAR(120) NOT NULL,
    modele_texte VARCHAR(500),
    mention_id UUID REFERENCES socle.mention (id),
    PRIMARY KEY (id)
);
CREATE INDEX idx_regle_appreciation_grille ON socle.regle_appreciation (grille_id);

-- ============================================================
-- GRILLE DE STOCK
-- ============================================================

CREATE TABLE IF NOT EXISTS socle.grille_stock (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES socle.etablissement (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    profil VARCHAR(20) NOT NULL,
    annee_id UUID REFERENCES socle.annee_scolaire (id),
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
CREATE INDEX idx_grille_stock_etablissement ON socle.grille_stock (etablissement_id);

CREATE TABLE IF NOT EXISTS socle.alerte_stock (
    id UUID NOT NULL,
    grille_id UUID NOT NULL REFERENCES socle.grille_stock (id),
    reference VARCHAR(60) NOT NULL,
    depot VARCHAR(60),
    seuil NUMERIC(12,2) NOT NULL,
    niveau VARCHAR(20) NOT NULL,
    destinataires JSONB DEFAULT '[]',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_alerte_stock_grille ON socle.alerte_stock (grille_id);

-- ============================================================
-- GRILLE DE CONFORMITE
-- ============================================================

CREATE TABLE IF NOT EXISTS socle.grille_conformite (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES socle.etablissement (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    profil VARCHAR(20) NOT NULL,
    annee_id UUID REFERENCES socle.annee_scolaire (id),
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
CREATE INDEX idx_grille_conformite_etablissement ON socle.grille_conformite (etablissement_id);

-- ============================================================
-- REFERENTIELS PARAMETRABLES
-- ============================================================

CREATE TABLE IF NOT EXISTS socle.referentiel (
    id UUID NOT NULL,
    etablissement_id UUID NOT NULL REFERENCES socle.etablissement (id),
    code VARCHAR(30) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    nature VARCHAR(40) NOT NULL,
    ordre INTEGER NOT NULL DEFAULT 0,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);
CREATE INDEX idx_referentiel_etablissement ON socle.referentiel (etablissement_id);

CREATE TABLE IF NOT EXISTS socle.referentiel_item (
    id UUID NOT NULL,
    referentiel_id UUID NOT NULL REFERENCES socle.referentiel (id),
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
CREATE INDEX idx_referentiel_item_referentiel ON socle.referentiel_item (referentiel_id);

-- ============================================================
-- Origine : identity
-- ============================================================

-- Entite proxy : porte le lien vers l'utilisateur Keycloak (keycloak_user_id)
-- et les attributs que Keycloak ne fournit pas nativement.
CREATE TABLE IF NOT EXISTS socle.utilisateur (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    keycloak_user_id UUID NOT NULL,
    email VARCHAR(255) NOT NULL,
    telephone VARCHAR(30),
    nom VARCHAR(100),
    post_nom VARCHAR(100),
    prenom VARCHAR(100),
    sexe VARCHAR(10),
    date_naissance DATE,
    nationalite VARCHAR(60),
    photo_url VARCHAR(500),
    statut VARCHAR(20) NOT NULL DEFAULT 'INVITE',
    type_compte VARCHAR(20) NOT NULL DEFAULT 'KEYCLOAK',
    langue_preferee VARCHAR(10) NOT NULL DEFAULT 'fr',
    derniere_connexion TIMESTAMPTZ,
    delombok BOOLEAN NOT NULL DEFAULT false,
    adr_rue VARCHAR(120),
    adr_quartier VARCHAR(120),
    adr_commune VARCHAR(120),
    adr_ville VARCHAR(120),
    adr_province VARCHAR(120),
    adr_pays VARCHAR(60),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    CONSTRAINT uq_utilisateur_keycloak UNIQUE (tenant_id, keycloak_user_id)
);

CREATE TABLE IF NOT EXISTS socle.motif_desactivation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    utilisateur_id UUID NOT NULL REFERENCES socle.utilisateur(id) ON DELETE CASCADE,
    date TIMESTAMPTZ NOT NULL,
    motif VARCHAR(255) NOT NULL,
    decideur UUID
);

CREATE TABLE IF NOT EXISTS socle.permission (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    code VARCHAR(120) NOT NULL,
    libelle VARCHAR(150),
    service VARCHAR(60),
    ressource VARCHAR(120),
    action VARCHAR(40),
    CONSTRAINT uq_permission_code UNIQUE (tenant_id, code)
);

CREATE TABLE IF NOT EXISTS socle.role_permission (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    role_code VARCHAR(120) NOT NULL,
    permission_id UUID NOT NULL REFERENCES socle.permission(id) ON DELETE CASCADE,
    granted_at TIMESTAMPTZ NOT NULL,
    granted_by UUID,
    expire_le DATE
);

CREATE TABLE IF NOT EXISTS socle.role_template (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    code VARCHAR(120) NOT NULL,
    libelle VARCHAR(150),
    profil VARCHAR(60),
    permissions JSONB NOT NULL DEFAULT '[]',
    roles JSONB NOT NULL DEFAULT '[]',
    CONSTRAINT uq_role_template_code UNIQUE (tenant_id, code)
);

-- ============================================================
-- Origine : auth
-- ============================================================
-- Auth service Flyway migration

CREATE TABLE IF NOT EXISTS socle.sessions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL,
    keycloak_session_id VARCHAR(255),
    refresh_token_hash  VARCHAR(64) NOT NULL,
    device_fingerprint  VARCHAR(128),
    ip_address          VARCHAR(45),
    user_agent          VARCHAR(500),
    mfa_verified        BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at          TIMESTAMPTZ NOT NULL,
    revoked_at          TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_sessions_user ON socle.sessions (user_id) WHERE revoked_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_sessions_expires ON socle.sessions (expires_at) WHERE revoked_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_sessions_refresh_hash ON socle.sessions (refresh_token_hash);

CREATE TABLE IF NOT EXISTS socle.mfa_secrets (
    user_id             UUID PRIMARY KEY,
    totp_secret_enc     BYTEA NOT NULL,
    backup_codes_hash   TEXT[],
    enabled             BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    confirmed_at        TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS socle.login_audit (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email_hash  VARCHAR(64) NOT NULL,
    ip_address  VARCHAR(45),
    success     BOOLEAN NOT NULL,
    reason      VARCHAR(100),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_login_audit_email ON socle.login_audit (email_hash, created_at DESC);

-- ============================================================
-- Origine : notification
-- ============================================================

CREATE TABLE IF NOT EXISTS socle.notifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    channel VARCHAR(10) NOT NULL,
    template_code VARCHAR(50),
    recipient_address VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    body TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(10) NOT NULL DEFAULT 'NORMAL',
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    sent_at TIMESTAMPTZ,
    failed_reason VARCHAR(500),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_notifications_user_id ON socle.notifications (user_id);
CREATE INDEX idx_notifications_status ON socle.notifications (status);

-- ============================================================
-- Origine : audit
-- ============================================================

CREATE TABLE IF NOT EXISTS socle.audit_log (
    id UUID NOT NULL,
    service_name VARCHAR(50) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    user_id UUID,
    entity_type VARCHAR(50),
    entity_id VARCHAR(100),
    action VARCHAR(50) NOT NULL,
    old_value JSONB,
    new_value JSONB,
    ip_address VARCHAR(45),
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

CREATE TABLE IF NOT EXISTS socle.audit_log_default PARTITION OF socle.audit_log DEFAULT;

CREATE INDEX idx_audit_entity ON socle.audit_log (entity_type, entity_id);
CREATE INDEX idx_audit_user ON socle.audit_log (user_id);
CREATE INDEX idx_audit_event_type ON socle.audit_log (event_type);

-- ============================================================
-- Origine : reporting
-- ============================================================

CREATE TABLE IF NOT EXISTS socle.report_requests (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    report_type VARCHAR(30) NOT NULL,
    parameters JSONB DEFAULT '{}',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    file_url VARCHAR(500),
    format VARCHAR(10) NOT NULL DEFAULT 'PDF',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMPTZ,
    failed_reason VARCHAR(500),
    version BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_reports_user_id ON socle.report_requests (user_id);

