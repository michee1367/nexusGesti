-- Isolation multi-tenant au niveau base : boucle sur toutes les tables du
-- schema boulangerie. La colonne tenant_id est posee si elle manque et les
-- policies USING/WITH CHECK comparent la valeur de la connexion courante.
-- Le DEFAULT recentre la colonne sur la connexion qui insere.
SET lock_timeout = '5s';

DO $$
DECLARE t text;
BEGIN
    FOR t IN SELECT c.relname FROM pg_class c
             JOIN pg_namespace n ON n.oid = c.relnamespace
             WHERE n.nspname = 'boulangerie'
               AND c.relkind = 'r'
               AND NOT c.relispartition
               AND c.relname <> 'flyway_boulangerie_history'
    LOOP
        EXECUTE format(
            'ALTER TABLE boulangerie.%I ADD COLUMN IF NOT EXISTS tenant_id UUID '
            || 'DEFAULT NULLIF(current_setting(''app.tenant_id'', true), '''')::uuid',
            t);
        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS idx_%I_tenant ON boulangerie.%I (tenant_id)',
            t, t);
        EXECUTE format('ALTER TABLE boulangerie.%I ENABLE ROW LEVEL SECURITY', t);
        EXECUTE format('ALTER TABLE boulangerie.%I FORCE ROW LEVEL SECURITY', t);
        EXECUTE format('DROP POLICY IF EXISTS tenant_policy ON boulangerie.%I', t);
        EXECUTE format(
            'CREATE POLICY tenant_policy ON boulangerie.%I '
            || 'USING (tenant_id = NULLIF(current_setting(''app.tenant_id'', true), '''')::uuid) '
            || 'WITH CHECK (tenant_id = NULLIF(current_setting(''app.tenant_id'', true), '''')::uuid)',
            t);
    END LOOP;
END
$$;

-- Unicites metier scopees tenant.
CREATE UNIQUE INDEX IF NOT EXISTS ux_gamme_tenant_code
    ON boulangerie.gamme (tenant_id, code);
CREATE UNIQUE INDEX IF NOT EXISTS ux_categorie_produit_tenant_code
    ON boulangerie.categorie_produit (tenant_id, code);
CREATE UNIQUE INDEX IF NOT EXISTS ux_produit_tenant_code
    ON boulangerie.produit (tenant_id, code);
CREATE UNIQUE INDEX IF NOT EXISTS ux_fiche_recette_tenant_code_version
    ON boulangerie.fiche_recette (tenant_id, code, version);
CREATE UNIQUE INDEX IF NOT EXISTS ux_fournee_tenant_numero
    ON boulangerie.fournee (tenant_id, numero);
CREATE UNIQUE INDEX IF NOT EXISTS ux_fournisseur_tenant_code
    ON boulangerie.fournisseur (tenant_id, code);
CREATE UNIQUE INDEX IF NOT EXISTS ux_commande_achat_tenant_numero
    ON boulangerie.commande_achat (tenant_id, numero);
CREATE UNIQUE INDEX IF NOT EXISTS ux_reception_tenant_numero
    ON boulangerie.reception (tenant_id, numero);
CREATE UNIQUE INDEX IF NOT EXISTS ux_vente_tenant_cle_idempotence
    ON boulangerie.vente (tenant_id, cle_idempotence);
CREATE UNIQUE INDEX IF NOT EXISTS ux_vente_tenant_session_ticket
    ON boulangerie.vente (tenant_id, session_id, numero_ticket);
CREATE UNIQUE INDEX IF NOT EXISTS ux_commande_client_tenant_numero
    ON boulangerie.commande_client (tenant_id, numero);
