CREATE SCHEMA IF NOT EXISTS identity;

-- Entite proxy : porte le lien vers l'utilisateur Keycloak (keycloak_user_id)
-- et les attributs que Keycloak ne fournit pas nativement.
CREATE TABLE IF NOT EXISTS identity.utilisateur (
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

CREATE TABLE IF NOT EXISTS identity.motif_desactivation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    utilisateur_id UUID NOT NULL REFERENCES identity.utilisateur(id) ON DELETE CASCADE,
    date TIMESTAMPTZ NOT NULL,
    motif VARCHAR(255) NOT NULL,
    decideur UUID
);

CREATE TABLE IF NOT EXISTS identity.permission (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    code VARCHAR(120) NOT NULL,
    libelle VARCHAR(150),
    service VARCHAR(60),
    ressource VARCHAR(120),
    action VARCHAR(40),
    CONSTRAINT uq_permission_code UNIQUE (tenant_id, code)
);

CREATE TABLE IF NOT EXISTS identity.role_permission (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    role_code VARCHAR(120) NOT NULL,
    permission_id UUID NOT NULL REFERENCES identity.permission(id) ON DELETE CASCADE,
    granted_at TIMESTAMPTZ NOT NULL,
    granted_by UUID,
    expire_le DATE
);

CREATE TABLE IF NOT EXISTS identity.role_template (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    code VARCHAR(120) NOT NULL,
    libelle VARCHAR(150),
    profil VARCHAR(60),
    permissions JSONB NOT NULL DEFAULT '[]',
    roles JSONB NOT NULL DEFAULT '[]',
    CONSTRAINT uq_role_template_code UNIQUE (tenant_id, code)
);
