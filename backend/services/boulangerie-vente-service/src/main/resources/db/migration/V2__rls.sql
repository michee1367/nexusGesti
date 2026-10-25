-- Isolation multi-tenant au niveau base : boucle sur toutes les tables du
-- schema boulangerie_vente. La colonne tenant_id est posee si elle manque et
-- les policies USING/WITH CHECK comparent la valeur de la connexion courante.
-- La table d'historique Flyway est exclue : boucler dessus produirait de la DDL
-- sur la table que Flyway utilise et bloquerait la migration sans erreur.
DO $$
DECLARE t text;
BEGIN
    FOR t IN SELECT tablename FROM pg_tables
             WHERE schemaname = 'boulangerie_vente'
               AND tablename <> 'flyway_boulangerie_vente_history'
    LOOP
        EXECUTE format(
            'ALTER TABLE boulangerie_vente.%I ADD COLUMN IF NOT EXISTS tenant_id UUID '
            || 'DEFAULT NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid',
            t);
        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS idx_%I_tenant ON boulangerie_vente.%I (tenant_id)',
            t, t);
        EXECUTE format('ALTER TABLE boulangerie_vente.%I ENABLE ROW LEVEL SECURITY', t);
        EXECUTE format('ALTER TABLE boulangerie_vente.%I FORCE ROW LEVEL SECURITY', t);
        EXECUTE format(
            'CREATE POLICY %I_tenant ON boulangerie_vente.%I '
            || 'USING (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid) '
            || 'WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting(''app.tenant_id'', true), ''''), '''')::uuid)',
            t, t);
    END LOOP;
END
$$;

-- Unicite metier scopee tenant.
CREATE UNIQUE INDEX IF NOT EXISTS ux_vente_tenant_cle_idempotence
    ON boulangerie_vente.vente (tenant_id, cle_idempotence);
CREATE UNIQUE INDEX IF NOT EXISTS ux_vente_tenant_session_ticket
    ON boulangerie_vente.vente (tenant_id, session_id, numero_ticket);
CREATE UNIQUE INDEX IF NOT EXISTS ux_commande_client_tenant_numero
    ON boulangerie_vente.commande_client (tenant_id, numero);
