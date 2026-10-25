-- Isolation multi-tenant au niveau base : boucle sur toutes les tables du
-- schema socle. La colonne tenant_id est posee si elle manque et les
-- policies USING/WITH CHECK comparent la valeur de la connexion courante.
-- Le DEFAULT recentre la colonne sur la connexion qui insere.
SET lock_timeout = '5s';

DO $$
DECLARE t text;
BEGIN
    FOR t IN SELECT c.relname FROM pg_class c
             JOIN pg_namespace n ON n.oid = c.relnamespace
             WHERE n.nspname = 'socle'
               AND c.relkind = 'r'
               AND NOT c.relispartition
               AND c.relname <> 'flyway_socle_history'
    LOOP
        EXECUTE format(
            'ALTER TABLE socle.%I ADD COLUMN IF NOT EXISTS tenant_id UUID '
            || 'DEFAULT NULLIF(current_setting(''app.tenant_id'', true), '''')::uuid',
            t);
        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS idx_%I_tenant ON socle.%I (tenant_id)',
            t, t);
        EXECUTE format('ALTER TABLE socle.%I ENABLE ROW LEVEL SECURITY', t);
        EXECUTE format('ALTER TABLE socle.%I FORCE ROW LEVEL SECURITY', t);
        EXECUTE format('DROP POLICY IF EXISTS tenant_policy ON socle.%I', t);
        EXECUTE format(
            'CREATE POLICY tenant_policy ON socle.%I '
            || 'USING (tenant_id = NULLIF(current_setting(''app.tenant_id'', true), '''')::uuid) '
            || 'WITH CHECK (tenant_id = NULLIF(current_setting(''app.tenant_id'', true), '''')::uuid)',
            t);
    END LOOP;
END
$$;
