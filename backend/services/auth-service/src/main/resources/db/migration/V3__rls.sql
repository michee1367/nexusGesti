-- Isolation multi-tenant au niveau base.
-- tenant_id est pris dans app.tenant_id (pose a chaque emprunt de connexion par
-- TenantAwareDataSource) : un INSERT hors contexte tenant produit NULL et la
-- policy le rejette (fail closed).
ALTER TABLE auth.sessions
    ADD COLUMN tenant_id UUID DEFAULT NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid;
ALTER TABLE auth.mfa_secrets
    ADD COLUMN tenant_id UUID DEFAULT NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid;
ALTER TABLE auth.login_audit
    ADD COLUMN tenant_id UUID DEFAULT NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid;

CREATE INDEX IF NOT EXISTS idx_sessions_tenant ON auth.sessions (tenant_id);
CREATE INDEX IF NOT EXISTS idx_mfa_secrets_tenant ON auth.mfa_secrets (tenant_id);
CREATE INDEX IF NOT EXISTS idx_login_audit_tenant ON auth.login_audit (tenant_id);

-- FORCE : l'application se connecte comme proprietaire du schema, et le
-- proprietaire est exempt de RLS sans cette option.
ALTER TABLE auth.sessions ENABLE ROW LEVEL SECURITY;
ALTER TABLE auth.sessions FORCE ROW LEVEL SECURITY;
CREATE POLICY sessions_tenant ON auth.sessions
    USING (tenant_id = NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid)
    WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid);

ALTER TABLE auth.mfa_secrets ENABLE ROW LEVEL SECURITY;
ALTER TABLE auth.mfa_secrets FORCE ROW LEVEL SECURITY;
CREATE POLICY mfa_secrets_tenant ON auth.mfa_secrets
    USING (tenant_id = NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid)
    WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid);

ALTER TABLE auth.login_audit ENABLE ROW LEVEL SECURITY;
ALTER TABLE auth.login_audit FORCE ROW LEVEL SECURITY;
CREATE POLICY login_audit_tenant ON auth.login_audit
    USING (tenant_id = NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid)
    WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid);
