-- Isolation multi-tenant au niveau base : boucle sur toutes les tables du
-- schema boulangerie_production. La colonne tenant_id est posee si elle manque et
-- les policies USING/WITH CHECK comparent la valeur de la connexion courante.
-- La table d'historique Flyway est exclue : boucler dessus produirait de la DDL
-- sur la table que Flyway utilise et bloquerait la migration sans erreur.
DO $$
DECLARE t text;
BEGIN
    FOR t IN SELECT tablename FROM pg_tables
             WHERE schemaname = 'boulangerie_production'
               AND tablename <> 'flyway_boulangerie_production_history'
    LOOP
        EXECUTE format(
            'ALTER TABLE boulangerie_production.%I ADD COLUMN IF NOT EXISTS tenant_id UUID '
            || 'DEFAULT NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid',
            t);
        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS idx_%I_tenant ON boulangerie_production.%I (tenant_id)',
            t, t);
        EXECUTE format('ALTER TABLE boulangerie_production.%I ENABLE ROW LEVEL SECURITY', t);
        EXECUTE format('ALTER TABLE boulangerie_production.%I FORCE ROW LEVEL SECURITY', t);
        EXECUTE format(
            'CREATE POLICY %I_tenant ON boulangerie_production.%I '
            || 'USING (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid) '
            || 'WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid)',
            t, t);
    END LOOP;
END
$$;

-- Unicite metier scopee tenant.
CREATE UNIQUE INDEX IF NOT EXISTS ux_fournee_tenant_numero
    ON boulangerie_production.fournee (tenant_id, numero);
