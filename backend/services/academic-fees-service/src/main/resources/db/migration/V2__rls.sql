-- Isolation multi-tenant au niveau base : boucle sur toutes les tables du
-- schema academic_fees. La colonne tenant_id est posee si elle manque et
-- les policies USING/WITH CHECK comparent la valeur de la connexion courante.
-- La table d'historique Flyway est exclue : boucler dessus produirait de la DDL
-- sur la table que Flyway utilise et bloquerait la migration sans erreur.
DO $$
DECLARE t text;
BEGIN
    FOR t IN SELECT tablename FROM pg_tables
             WHERE schemaname = 'academic_fees'
               AND tablename <> 'flyway_fees_history'
    LOOP
        EXECUTE format(
            'ALTER TABLE academic_fees.%I ADD COLUMN IF NOT EXISTS tenant_id UUID '
            || 'DEFAULT NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid',
            t);
        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS idx_%I_tenant ON academic_fees.%I (tenant_id)',
            t, t);
        EXECUTE format('ALTER TABLE academic_fees.%I ENABLE ROW LEVEL SECURITY', t);
        EXECUTE format('ALTER TABLE academic_fees.%I FORCE ROW LEVEL SECURITY', t);
        EXECUTE format(
            'CREATE POLICY %I_tenant ON academic_fees.%I '
            || 'USING (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid) '
            || 'WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid)',
            t, t);
    END LOOP;
END
$$;

-- Unicite metier scopee tenant.
CREATE UNIQUE INDEX IF NOT EXISTS ux_grille_tarifaire_tenant_code
    ON academic_fees.grille_tarifaire (tenant_id, code);
CREATE UNIQUE INDEX IF NOT EXISTS ux_reduction_tenant_grille_code
    ON academic_fees.reduction (tenant_id, grille_id, code);
CREATE UNIQUE INDEX IF NOT EXISTS ux_avis_paiement_tenant_reference
    ON academic_fees.avis_paiement (tenant_id, reference);
CREATE UNIQUE INDEX IF NOT EXISTS ux_paiement_tenant_reference
    ON academic_fees.paiement (tenant_id, reference);
CREATE UNIQUE INDEX IF NOT EXISTS ux_coupon_paiement_tenant_annee_sequence
    ON academic_fees.coupon_paiement (tenant_id, annee, sequence);
CREATE UNIQUE INDEX IF NOT EXISTS ux_journalier_tenant_site_date
    ON academic_fees.journalier (tenant_id, site, date);