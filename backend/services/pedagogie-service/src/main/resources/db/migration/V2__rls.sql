-- Isolation multi-tenant au niveau base : boucle sur toutes les tables du
-- schema pedagogie. La colonne tenant_id est posee si elle manque et les
-- policies USING/WITH CHECK comparent la valeur de la connexion courante.
-- Le DEFAULT recentre la colonne sur la connexion qui insere.
SET lock_timeout = '5s';

DO $$
DECLARE t text;
BEGIN
    FOR t IN SELECT c.relname FROM pg_class c
             JOIN pg_namespace n ON n.oid = c.relnamespace
             WHERE n.nspname = 'pedagogie'
               AND c.relkind = 'r'
               AND NOT c.relispartition
               AND c.relname <> 'flyway_pedagogie_history'
    LOOP
        EXECUTE format(
            'ALTER TABLE pedagogie.%I ADD COLUMN IF NOT EXISTS tenant_id UUID '
            || 'DEFAULT NULLIF(current_setting(''app.tenant_id'', true), '''')::uuid',
            t);
        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS idx_%I_tenant ON pedagogie.%I (tenant_id)',
            t, t);
        EXECUTE format('ALTER TABLE pedagogie.%I ENABLE ROW LEVEL SECURITY', t);
        EXECUTE format('ALTER TABLE pedagogie.%I FORCE ROW LEVEL SECURITY', t);
        EXECUTE format('DROP POLICY IF EXISTS tenant_policy ON pedagogie.%I', t);
        EXECUTE format(
            'CREATE POLICY tenant_policy ON pedagogie.%I '
            || 'USING (tenant_id = NULLIF(current_setting(''app.tenant_id'', true), '''')::uuid) '
            || 'WITH CHECK (tenant_id = NULLIF(current_setting(''app.tenant_id'', true), '''')::uuid)',
            t);
    END LOOP;
END
$$;

-- Unicites metier scopees tenant.
CREATE UNIQUE INDEX IF NOT EXISTS ux_regime_horaire_tenant_code
    ON pedagogie.regime_horaire (tenant_id, code);
CREATE UNIQUE INDEX IF NOT EXISTS ux_eleve_tenant_matricule
    ON pedagogie.eleve (tenant_id, matricule);
CREATE UNIQUE INDEX IF NOT EXISTS ux_agent_tenant_matricule
    ON pedagogie.agent (tenant_id, matricule);
CREATE UNIQUE INDEX IF NOT EXISTS ux_enseignant_tenant_matricule
    ON pedagogie.enseignant (tenant_id, matricule);
CREATE UNIQUE INDEX IF NOT EXISTS ux_service_etablissement_tenant_code
    ON pedagogie.service_etablissement (tenant_id, code);
CREATE UNIQUE INDEX IF NOT EXISTS ux_groupe_tenant_code
    ON pedagogie.groupe (tenant_id, code);
CREATE UNIQUE INDEX IF NOT EXISTS ux_presence_feuille_eleve
    ON pedagogie.presence_eleve (tenant_id, feuille_id, eleve_id);
CREATE UNIQUE INDEX IF NOT EXISTS ux_bulletin_tenant_reference
    ON pedagogie.bulletin (tenant_id, reference);
CREATE UNIQUE INDEX IF NOT EXISTS ux_evaluation_tenant_reference
    ON pedagogie.evaluation (tenant_id, reference);
CREATE UNIQUE INDEX IF NOT EXISTS ux_coupon_notes_tenant_numero
    ON pedagogie.coupon_notes (tenant_id, numero);
CREATE UNIQUE INDEX IF NOT EXISTS ux_session_examen_tenant_reference
    ON pedagogie.session_examen (tenant_id, reference);
CREATE UNIQUE INDEX IF NOT EXISTS ux_epreuve_tenant_reference
    ON pedagogie.epreuve (tenant_id, reference);
CREATE UNIQUE INDEX IF NOT EXISTS ux_convocation_tenant_session_numero
    ON pedagogie.convocation (tenant_id, session_id, numero);
CREATE UNIQUE INDEX IF NOT EXISTS ux_deliberation_tenant_reference
    ON pedagogie.deliberation (tenant_id, reference);
CREATE UNIQUE INDEX IF NOT EXISTS ux_proces_verbal_tenant_reference
    ON pedagogie.proces_verbal (tenant_id, reference);
CREATE UNIQUE INDEX IF NOT EXISTS ux_resultat_officiel_tenant_reference
    ON pedagogie.resultat_officiel (tenant_id, reference);
CREATE UNIQUE INDEX IF NOT EXISTS ux_grille_tarifaire_tenant_code
    ON pedagogie.grille_tarifaire (tenant_id, code);
CREATE UNIQUE INDEX IF NOT EXISTS ux_reduction_tenant_grille_code
    ON pedagogie.reduction (tenant_id, grille_id, code);
CREATE UNIQUE INDEX IF NOT EXISTS ux_avis_paiement_tenant_reference
    ON pedagogie.avis_paiement (tenant_id, reference);
CREATE UNIQUE INDEX IF NOT EXISTS ux_paiement_tenant_reference
    ON pedagogie.paiement (tenant_id, reference);
CREATE UNIQUE INDEX IF NOT EXISTS ux_coupon_paiement_tenant_annee_sequence
    ON pedagogie.coupon_paiement (tenant_id, annee, sequence);
CREATE UNIQUE INDEX IF NOT EXISTS ux_journalier_tenant_site_date
    ON pedagogie.journalier (tenant_id, site, date);
