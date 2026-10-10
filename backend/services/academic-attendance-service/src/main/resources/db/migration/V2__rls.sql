-- Isolation multi-tenant au niveau base : boucle sur toutes les tables du
-- schema academic_attendance. La colonne tenant_id est posee si elle manque et
-- les policies USING/WITH CHECK comparent la valeur de la connexion courante.
-- La table d'historique Flyway est exclue : boucler dessus produirait de la DDL
-- sur la table que Flyway utilise et bloquerait la migration sans erreur.
DO $$
DECLARE t text;
BEGIN
    FOR t IN SELECT tablename FROM pg_tables
             WHERE schemaname = 'academic_attendance'
               AND tablename <> 'flyway_attendance_history'
    LOOP
        EXECUTE format(
            'ALTER TABLE academic_attendance.%I ADD COLUMN IF NOT EXISTS tenant_id UUID '
            || 'DEFAULT NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid',
            t);
        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS idx_%I_tenant ON academic_attendance.%I (tenant_id)',
            t, t);
        EXECUTE format('ALTER TABLE academic_attendance.%I ENABLE ROW LEVEL SECURITY', t);
        EXECUTE format('ALTER TABLE academic_attendance.%I FORCE ROW LEVEL SECURITY', t);
        EXECUTE format(
            'CREATE POLICY %I_tenant ON academic_attendance.%I '
            || 'USING (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid) '
            || 'WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid)',
            t, t);
    END LOOP;
END
$$;

-- Unicite metier scopee tenant : une seule ligne de presence par eleve et par feuille.
CREATE UNIQUE INDEX IF NOT EXISTS ux_presence_feuille_eleve
    ON academic_attendance.presence_eleve (tenant_id, feuille_id, eleve_id);
