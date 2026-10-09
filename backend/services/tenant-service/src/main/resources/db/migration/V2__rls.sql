-- Isolation multi-tenant au niveau base : boucle sur toutes les tables du
-- schema tenant. La colonne tenant_id est posee si elle manque et les
-- policies USING/WITH CHECK comparent la valeur de la connexion courante.
-- Le DEFAULT recentre la colonne sur la connexion qui insere.
DO $$
DECLARE t text;
BEGIN
    FOR t IN SELECT tablename FROM pg_tables
             WHERE schemaname = 'tenant'
               AND tablename <> 'flyway_tenant_history'
    LOOP
        EXECUTE format(
            'ALTER TABLE tenant.%I ADD COLUMN IF NOT EXISTS tenant_id UUID '
            || 'DEFAULT NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid',
            t);
        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS idx_%I_tenant ON tenant.%I (tenant_id)',
            t, t);
        EXECUTE format('ALTER TABLE tenant.%I ENABLE ROW LEVEL SECURITY', t);
        EXECUTE format('ALTER TABLE tenant.%I FORCE ROW LEVEL SECURITY', t);
        EXECUTE format(
            'CREATE POLICY %I_tenant ON tenant.%I '
            || 'USING (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid) '
            || 'WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid)',
            t, t);
    END LOOP;
END
$$;