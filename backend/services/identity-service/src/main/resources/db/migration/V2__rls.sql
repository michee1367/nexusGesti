DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'nexusgesti_app') THEN
        CREATE ROLE nexusgesti_app;
    END IF;
END $$;

DO $$
DECLARE
    tbl text;
BEGIN
    FOR tbl IN SELECT tablename FROM pg_tables WHERE schemaname = 'identity'
    LOOP
        EXECUTE format('ALTER TABLE identity.%I ADD COLUMN IF NOT EXISTS tenant_id UUID', tbl);
        EXECUTE format('ALTER TABLE identity.%I ENABLE ROW LEVEL SECURITY', tbl);
        EXECUTE format('ALTER TABLE identity.%I FORCE ROW LEVEL SECURITY', tbl);
        EXECUTE format('DROP POLICY IF EXISTS tenant_policy ON identity.%I', tbl);
        EXECUTE format('CREATE POLICY tenant_policy ON identity.%I USING (tenant_id = current_setting(''app.tenant_id'')::uuid) WITH CHECK (tenant_id = current_setting(''app.tenant_id'')::uuid)', tbl);
        EXECUTE format('GRANT SELECT, INSERT, UPDATE, DELETE ON identity.%I TO nexusgesti_app', tbl);
    END LOOP;
END $$;
