-- Isolation multi-tenant au niveau base : boucle sur toutes les tables du
-- schema academic_assessment. La colonne tenant_id est posee si elle manque et
-- les policies USING/WITH CHECK comparent la valeur de la connexion courante.
-- La table d'historique Flyway est exclue : boucler dessus produirait de la DDL
-- sur la table que Flyway utilise et bloquerait la migration sans erreur.
DO $$
DECLARE t text;
BEGIN
    FOR t IN SELECT tablename FROM pg_tables
             WHERE schemaname = 'academic_assessment'
               AND tablename <> 'flyway_assessment_history'
    LOOP
        EXECUTE format(
            'ALTER TABLE academic_assessment.%I ADD COLUMN IF NOT EXISTS tenant_id UUID '
            || 'DEFAULT NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid',
            t);
        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS idx_%I_tenant ON academic_assessment.%I (tenant_id)',
            t, t);
        EXECUTE format('ALTER TABLE academic_assessment.%I ENABLE ROW LEVEL SECURITY', t);
        EXECUTE format('ALTER TABLE academic_assessment.%I FORCE ROW LEVEL SECURITY', t);
        EXECUTE format(
            'CREATE POLICY %I_tenant ON academic_assessment.%I '
            || 'USING (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid) '
            || 'WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid)',
            t, t);
    END LOOP;
END
$$;

-- Unicite metier scopee tenant : la reference d un bulletin est unique par tenant.
CREATE UNIQUE INDEX IF NOT EXISTS ux_bulletin_tenant_reference
    ON academic_assessment.bulletin (tenant_id, reference);
CREATE UNIQUE INDEX IF NOT EXISTS ux_evaluation_tenant_reference
    ON academic_assessment.evaluation (tenant_id, reference);
CREATE UNIQUE INDEX IF NOT EXISTS ux_coupon_notes_tenant_numero
    ON academic_assessment.coupon_notes (tenant_id, numero);
