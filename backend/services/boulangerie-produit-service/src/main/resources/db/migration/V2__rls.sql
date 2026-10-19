-- Isolation multi-tenant au niveau base : boucle sur toutes les tables du
-- schema boulangerie_produit. La colonne tenant_id est posee si elle manque et
-- les policies USING/WITH CHECK comparent la valeur de la connexion courante.
-- La table d'historique Flyway est exclue : boucler dessus produirait de la DDL
-- sur la table que Flyway utilise et bloquerait la migration sans erreur.
DO $$
DECLARE t text;
BEGIN
    FOR t IN SELECT tablename FROM pg_tables
             WHERE schemaname = 'boulangerie_produit'
               AND tablename <> 'flyway_boulangerie_produit_history'
    LOOP
        EXECUTE format(
            'ALTER TABLE boulangerie_produit.%I ADD COLUMN IF NOT EXISTS tenant_id UUID '
            || 'DEFAULT NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid',
            t);
        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS idx_%I_tenant ON boulangerie_produit.%I (tenant_id)',
            t, t);
        EXECUTE format('ALTER TABLE boulangerie_produit.%I ENABLE ROW LEVEL SECURITY', t);
        EXECUTE format('ALTER TABLE boulangerie_produit.%I FORCE ROW LEVEL SECURITY', t);
        EXECUTE format(
            'CREATE POLICY %I_tenant ON boulangerie_produit.%I '
            || 'USING (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid) '
            || 'WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid)',
            t, t);
    END LOOP;
END
$$;

-- Unicite metier scopee tenant.
CREATE UNIQUE INDEX IF NOT EXISTS ux_gamme_tenant_code
    ON boulangerie_produit.gamme (tenant_id, code);
CREATE UNIQUE INDEX IF NOT EXISTS ux_categorie_produit_tenant_code
    ON boulangerie_produit.categorie_produit (tenant_id, code);
CREATE UNIQUE INDEX IF NOT EXISTS ux_produit_tenant_code
    ON boulangerie_produit.produit (tenant_id, code);
CREATE UNIQUE INDEX IF NOT EXISTS ux_fiche_recette_tenant_code_version
    ON boulangerie_produit.fiche_recette (tenant_id, code, version);
