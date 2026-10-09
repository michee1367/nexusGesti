-- Isolation multi-tenant au niveau base : boucle sur toutes les tables du
-- schema academic_structure. La colonne tenant_id est posee si elle manque et
-- les policies USING/WITH CHECK comparent la valeur de la connexion courante.
-- La table d'historique Flyway est exclue : boucler dessus produirait de la DDL
-- sur la table que Flyway utilise et bloquerait la migration sans erreur.
DO $$
DECLARE t text;
BEGIN
    FOR t IN SELECT tablename FROM pg_tables
             WHERE schemaname = 'academic_structure'
               AND tablename <> 'flyway_structure_history'
    LOOP
        EXECUTE format(
            'ALTER TABLE academic_structure.%I ADD COLUMN IF NOT EXISTS tenant_id UUID '
            || 'DEFAULT NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid',
            t);
        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS idx_%I_tenant ON academic_structure.%I (tenant_id)',
            t, t);
        EXECUTE format('ALTER TABLE academic_structure.%I ENABLE ROW LEVEL SECURITY', t);
        EXECUTE format('ALTER TABLE academic_structure.%I FORCE ROW LEVEL SECURITY', t);
        EXECUTE format(
            'CREATE POLICY %I_tenant ON academic_structure.%I '
            || 'USING (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid) '
            || 'WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid)',
            t, t);
    END LOOP;
END
$$;

-- Unicite metier scopee tenant : un code de regime horaire est unique par
-- tenant, jamais globalement (la table porte un id surrogate).
CREATE UNIQUE INDEX IF NOT EXISTS ux_regime_horaire_tenant_code
    ON academic_structure.regime_horaire (tenant_id, code);
