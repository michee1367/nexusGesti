-- Isolation multi-tenant au niveau base.
ALTER TABLE reporting.report_requests
    ADD COLUMN tenant_id UUID DEFAULT NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid;

CREATE INDEX IF NOT EXISTS idx_reports_tenant ON reporting.report_requests (tenant_id);

ALTER TABLE reporting.report_requests ENABLE ROW LEVEL SECURITY;
ALTER TABLE reporting.report_requests FORCE ROW LEVEL SECURITY;
CREATE POLICY report_requests_tenant ON reporting.report_requests
    USING (tenant_id = NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid)
    WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid);
