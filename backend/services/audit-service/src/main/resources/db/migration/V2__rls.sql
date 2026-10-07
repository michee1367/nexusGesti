-- Isolation multi-tenant au niveau base.
ALTER TABLE audit.audit_log
    ADD COLUMN tenant_id UUID DEFAULT NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid;

CREATE INDEX IF NOT EXISTS idx_audit_tenant ON audit.audit_log (tenant_id);

-- audit_log est partitionnee : les policies doivent etre posees sur la table
-- mere et sur chaque partition.
ALTER TABLE audit.audit_log ENABLE ROW LEVEL SECURITY;
ALTER TABLE audit.audit_log FORCE ROW LEVEL SECURITY;
CREATE POLICY audit_log_tenant ON audit.audit_log
    USING (tenant_id = NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid)
    WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid);

ALTER TABLE audit.audit_log_default ENABLE ROW LEVEL SECURITY;
ALTER TABLE audit.audit_log_default FORCE ROW LEVEL SECURITY;
CREATE POLICY audit_log_default_tenant ON audit.audit_log_default
    USING (tenant_id = NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid)
    WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid);
