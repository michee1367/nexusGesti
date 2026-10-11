-- Isolation multi-tenant au niveau base : boucle sur toutes les tables du
-- schema academic_exam. La colonne tenant_id est posee si elle manque et
-- les policies USING/WITH CHECK comparent la valeur de la connexion courante.
-- La table d'historique Flyway est exclue : boucler dessus produirait de la DDL
-- sur la table que Flyway utilise et bloquerait la migration sans erreur.
DO $$
DECLARE t text;
BEGIN
    FOR t IN SELECT tablename FROM pg_tables
             WHERE schemaname = 'academic_exam'
               AND tablename <> 'flyway_exam_history'
    LOOP
        EXECUTE format(
            'ALTER TABLE academic_exam.%I ADD COLUMN IF NOT EXISTS tenant_id UUID '
            || 'DEFAULT NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid',
            t);
        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS idx_%I_tenant ON academic_exam.%I (tenant_id)',
            t, t);
        EXECUTE format('ALTER TABLE academic_exam.%I ENABLE ROW LEVEL SECURITY', t);
        EXECUTE format('ALTER TABLE academic_exam.%I FORCE ROW LEVEL SECURITY', t);
        EXECUTE format(
            'CREATE POLICY %I_tenant ON academic_exam.%I '
            || 'USING (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid) '
            || 'WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid)',
            t, t);
    END LOOP;
END
$$;

-- Unicite metier scopee tenant.
CREATE UNIQUE INDEX IF NOT EXISTS ux_session_examen_tenant_reference
    ON academic_exam.session_examen (tenant_id, reference);
CREATE UNIQUE INDEX IF NOT EXISTS ux_epreuve_tenant_reference
    ON academic_exam.epreuve (tenant_id, reference);
CREATE UNIQUE INDEX IF NOT EXISTS ux_convocation_tenant_session_numero
    ON academic_exam.convocation (tenant_id, session_id, numero);
CREATE UNIQUE INDEX IF NOT EXISTS ux_deliberation_tenant_reference
    ON academic_exam.deliberation (tenant_id, reference);
CREATE UNIQUE INDEX IF NOT EXISTS ux_proces_verbal_tenant_reference
    ON academic_exam.proces_verbal (tenant_id, reference);
CREATE UNIQUE INDEX IF NOT EXISTS ux_resultat_officiel_tenant_reference
    ON academic_exam.resultat_officiel (tenant_id, reference);
