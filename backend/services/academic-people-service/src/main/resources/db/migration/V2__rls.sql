-- Isolation multi-tenant au niveau base : boucle sur toutes les tables du
-- schema academic_people. La colonne tenant_id est posee si elle manque et
-- les policies USING/WITH CHECK comparent la valeur de la connexion courante.
-- La table d'historique Flyway est exclue : boucler dessus produirait de la DDL
-- sur la table que Flyway utilise et bloquerait la migration sans erreur.
DO $$
DECLARE t text;
BEGIN
    FOR t IN SELECT tablename FROM pg_tables
             WHERE schemaname = 'academic_people'
               AND tablename <> 'flyway_people_history'
    LOOP
        EXECUTE format(
            'ALTER TABLE academic_people.%I ADD COLUMN IF NOT EXISTS tenant_id UUID '
            || 'DEFAULT NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid',
            t);
        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS idx_%I_tenant ON academic_people.%I (tenant_id)',
            t, t);
        EXECUTE format('ALTER TABLE academic_people.%I ENABLE ROW LEVEL SECURITY', t);
        EXECUTE format('ALTER TABLE academic_people.%I FORCE ROW LEVEL SECURITY', t);
        EXECUTE format(
            'CREATE POLICY %I_tenant ON academic_people.%I '
            || 'USING (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid) '
            || 'WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid)',
            t, t);
    END LOOP;
END
$$;

-- Unicite metier scopee tenant : un matricule est unique par tenant.
CREATE UNIQUE INDEX IF NOT EXISTS ux_eleve_tenant_matricule
    ON academic_people.eleve (tenant_id, matricule);
CREATE UNIQUE INDEX IF NOT EXISTS ux_agent_tenant_matricule
    ON academic_people.agent (tenant_id, matricule);
CREATE UNIQUE INDEX IF NOT EXISTS ux_enseignant_tenant_matricule
    ON academic_people.enseignant (tenant_id, matricule);
CREATE UNIQUE INDEX IF NOT EXISTS ux_service_etablissement_tenant_code
    ON academic_people.service_etablissement (tenant_id, code);
