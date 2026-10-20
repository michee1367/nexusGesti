-- Isolation multi-tenant au niveau base : boucle sur toutes les tables du
-- schema boulangerie_approvisionnement. La colonne tenant_id est posee si elle manque et
-- les policies USING/WITH CHECK comparent la valeur de la connexion courante.
-- La table d'historique Flyway est exclue : boucler dessus produirait de la DDL
-- sur la table que Flyway utilise et bloquerait la migration sans erreur.
DO $$
DECLARE t text;
BEGIN
    FOR t IN SELECT tablename FROM pg_tables
             WHERE schemaname = 'boulangerie_approvisionnement'
               AND tablename <> 'flyway_boulangerie_approvisionnement_history'
    LOOP
        EXECUTE format(
            'ALTER TABLE boulangerie_approvisionnement.%I ADD COLUMN IF NOT EXISTS tenant_id UUID '
            || 'DEFAULT NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid',
            t);
        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS idx_%I_tenant ON boulangerie_approvisionnement.%I (tenant_id)',
            t, t);
        EXECUTE format('ALTER TABLE boulangerie_approvisionnement.%I ENABLE ROW LEVEL SECURITY', t);
        EXECUTE format('ALTER TABLE boulangerie_approvisionnement.%I FORCE ROW LEVEL SECURITY', t);
        EXECUTE format(
            'CREATE POLICY %I_tenant ON boulangerie_approvisionnement.%I '
            || 'USING (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid) '
            || 'WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid)',
            t, t);
    END LOOP;
END
$$;

-- Unicite metier scopee tenant.
CREATE UNIQUE INDEX IF NOT EXISTS ux_fournisseur_tenant_code
    ON boulangerie_approvisionnement.fournisseur (tenant_id, code);
CREATE UNIQUE INDEX IF NOT EXISTS ux_commande_achat_tenant_numero
    ON boulangerie_approvisionnement.commande_achat (tenant_id, numero);
CREATE UNIQUE INDEX IF NOT EXISTS ux_reception_tenant_numero
    ON boulangerie_approvisionnement.reception (tenant_id, numero);
