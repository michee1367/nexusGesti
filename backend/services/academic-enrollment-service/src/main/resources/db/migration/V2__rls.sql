-- Isolation multi-tenant au niveau base : boucle sur toutes les tables du
-- schema academic_enrollment. La colonne tenant_id est posee si elle manque et
-- les policies USING/WITH CHECK comparent la valeur de la connexion courante.
-- La table d'historique Flyway est exclue : boucler dessus produirait de la DDL
-- sur la table que Flyway utilise et bloquerait la migration sans erreur.
DO $$
DECLARE t text;
BEGIN
    FOR t IN SELECT tablename FROM pg_tables
             WHERE schemaname = 'academic_enrollment'
               AND tablename <> 'flyway_enrollment_history'
    LOOP
        EXECUTE format(
            'ALTER TABLE academic_enrollment.%I ADD COLUMN IF NOT EXISTS tenant_id UUID '
            || 'DEFAULT NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid',
            t);
        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS idx_%I_tenant ON academic_enrollment.%I (tenant_id)',
            t, t);
        EXECUTE format('ALTER TABLE academic_enrollment.%I ENABLE ROW LEVEL SECURITY', t);
        EXECUTE format('ALTER TABLE academic_enrollment.%I FORCE ROW LEVEL SECURITY', t);
        EXECUTE format(
            'CREATE POLICY %I_tenant ON academic_enrollment.%I '
            || 'USING (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid) '
            || 'WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid)',
            t, t);
    END LOOP;
END
$$;

-- Unicite metier scopee tenant : un code de groupe est unique par tenant.
CREATE UNIQUE INDEX IF NOT EXISTS ux_groupe_tenant_code
    ON academic_enrollment.groupe (tenant_id, code);
