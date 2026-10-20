# AGENTS.md — Console socle NexusGesti (frontend)

Mémoire de contexte pour les agents IA. Se référer ici avant toute modification.

## Vue d'ensemble

Frontend de la **Console socle** de NexusGesti (SaaS multi-tenant : socle, pédagogie, hôpital, finance).
Base : copie adaptée de `C:\Users\PC\Documents\ged_ministere\front`, **tout contenu GED supprimé**.
Backend : passerelle Spring Cloud Gateway sur `http://localhost:8080/api/v1` (voir `.env`, non versionné).
Le périmètre couvre le **socle** (tenants, établissements, grilles, référentiels, années,
utilisateurs, rôles/permissions) et **toute la pédagogie — lots 4a à 4g** (structure,
people, enrollment, attendance, assessment, exam, fees), branchée sur les services
`academic-*` 8111→8117, ainsi que le **lot 1 boulangerie** (produits, recettes, gammes,
catégories) branché sur `boulangerie-produit-service` 8141, le **lot 2 boulangerie**
(plans de production, fournées) branché sur `boulangerie-production-service` 8142 et le
**lot 3 boulangerie** (fournisseurs, besoins, commandes d'achat, réceptions) branché sur
`boulangerie-approvisionnement-service` 8143.
Hôpital / finance n'ont pas encore de pages.

## Stack

- Next.js 16.2 (App Router, Turbopack) + React 19 + TypeScript
- Tailwind CSS v4 + composants shadcn/ui (`src/components/ui`)
- `@tanstack/react-table`, `react-hook-form` + `zod`, `recharts`, `sonner`, `lucide-react`, `date-fns`

## Commandes

```bash
npm run dev       # serveur de dev (hostname 0.0.0.0)
npm run build     # build production
npm run start     # serveur de prod
npm run lint      # eslint
npx tsc --noEmit  # typecheck
```

Le port de dev par défaut (3000) est **occupé par Grafana** → lancer la console sur **3001** :
`PORT=3001 npm run dev` (git-bash) / `$env:PORT=3001; npm run dev` (PowerShell), ou via
`scripts/front-up.{sh,ps1}` (à la racine du dépôt). Le CORS passerelle autorise 3000→3004.

## Architecture

- `src/app/layout.tsx` : `AuthProvider` → `TenantProvider` → `Toaster` (sonner, `top-right` richColors).
- **Auth** (`src/components/providers/auth-provider.tsx`) : **direct-grant** via le socle.
  - `login(email, password)` → `POST /socle/auth/login` (body `{email, password}`) — JWT dans
    `localStorage` (`token`, `refresh_token`), profil dans `auth_user`.
  - Rôles lus dans le claim Keycloak `realm_access.roles` : `ADMIN` (toutes les pages) et
    `CUSTOMER` (pages socle en lecture + grilles). `isAdmin` = rôle `ADMIN` présent.
  - `logout()` → `POST /socle/auth/logout`.
  - Refresh automatique : `POST /socle/auth/refresh` à la 401, puis nouvel essai (voir `api.ts`).
  - **Login email-first** (`src/app/login/page.tsx`) : étape 1 `POST /socle/auth/resolve-tenant
    {email}` → `{tenantId}` (route **exempte** de `X-Tenant-Id`), le client pose
    `setTenantId(tenantId)` puis `GET /socle/tenants/{id}/identite` → `{denomination, code, profil,
    statut}` affiché à l'écran ; étape 2 `login(email, password)`. Repli manuel (saisie d'UUID)
    conservé (`manualMode`). Comptes démo : l'attribut Keycloak `tenant_id` est posé par
    `provision.sh`.
- **Tenant** (`src/components/providers/tenant-provider.tsx`) : en-tête `X-Tenant-Id` (UUID),
  persévéré dans `localStorage` (`x_tenant_id`), sélecteur dans le header. Une valeur non UUID
  produit un 400 du backend — ne pas laisser passer. `setTenantId` écrit dans `localStorage`
  synchrone ; `rawFetch` **écrase** `X-Tenant-Id` depuis `localStorage` (priorité au provider).
  `header.tsx` affiche la dénomination (`tenantApi.identite`).
- **API** (`src/services/api.ts`) : wrapper `rawFetch` — en-têtes `Authorization` (si token)
  et `X-Tenant-Id` (si tenant), erreurs normalisées en `ApiError {status, code, message}`.
  Codes métier utiles : `TENANT_ALREADY_EXISTS` 409, `GRILLE_FIGEE` 409, `TRANSITION_INVALIDE` 409,
  `UTILISATEUR_EXISTANT` 409, `ROLE_EXISTANT` 409, `PERMISSION_EXISTANTE` 409,
  `KEYCLOAK_INDISPONIBLE` 503, `AUTH_INVALID_CREDENTIALS` 401 ; pédagogie : `ETAT_INVALIDE` 409,
  `CONFLIT` / `CONFLIT_EMPLOI` 409, `GROUPE_COMPLET` 409, `ITEM_EXISTANT` 409,
  `IDEMPOTENCE_EN_COURS` 409, `INVALIDE` 400, `NOT_FOUND` 404.
- **Routes** :
  - Publiques : `/` (landing marketing), `/login` (email-first : résolution du tenant par email,
    puis mot de passe ; repli UUID), `/signup` (self-service : crée compte + établissement,
    `X-Tenant-Id` = UUID généré côté client, auto-connexion puis redirection `/dashboard`).
  - Authentifiées (groupe `(app)` avec `AppShell` = Sidebar + Header ; le groupe `(app)`
    est aplati dans l'URL) :
    `/dashboard`, `/tenant`, `/grilles`, `/grilles/[type]?id=…`, `/referentiels`,
    `/annees` ; réservées `ADMIN` : `/tenants`, `/utilisateurs`, `/roles`, `/notifications`,
    `/audit`, `/rapports`.
  - **Pédagogie** (liste ouverte `ADMIN`|`CUSTOMER`, mutations `ADMIN`) :
    - Structure (4a) : `/pedagogie/niveaux`, `/pedagogie/classes`, `/pedagogie/promotions`,
      `/pedagogie/semestres`, `/pedagogie/ues`, `/pedagogie/matieres`, `/pedagogie/cours`,
      `/pedagogie/salles`, `/pedagogie/creneaux`, `/pedagogie/regimes-horaires`,
      `/pedagogie/emplois-du-temps` (+ `/pedagogie/emplois-du-temps/[id]` : plages, conflits,
      publier).
    - Personnes (4b) : `/pedagogie/eleves` (+ `/pedagogie/eleves/[id]` : identité, dossier,
      inscriptions, congés, sanctions, parents), `/pedagogie/parents`, `/pedagogie/enseignants`
      (+ `/pedagogie/enseignants/[id]` : pièces, affectations, statut), `/pedagogie/personnel`
      (onglets Agents / Services).
    - Inscriptions (4c) : `/pedagogie/inscriptions-ue`, `/pedagogie/groupes`
      (+ `/pedagogie/groupes/[id]` : membres, ouvrir/clore), `/pedagogie/regles-inscription`,
      `/pedagogie/admissions` (évaluation d'éligibilité + parcours d'un élève).
    - Assiduité (4d) : `/pedagogie/seances`, `/pedagogie/appels`
      (+ `/pedagogie/appels/[id]` : pointage, absence, retard, soumettre/valider/clôturer),
      `/pedagogie/justificatifs`, `/pedagogie/assiduite` (suivi élève + taux par classe + registre).
    - Évaluations (4e) : `/pedagogie/evaluations` (+ `/pedagogie/evaluations/[id]` : feuille,
      notes, absence, exclusion, contestation), `/pedagogie/bulletins`
      (+ `/pedagogie/bulletins/[id]` : lignes, absences, calculer/publier/générer/signer/rectifier,
      coupons), `/pedagogie/moyennes` (évaluation, élève, classement de classe).
    - Examens (4f) : `/pedagogie/sessions` (+ `/pedagogie/sessions/[id]` : épreuves, créneaux,
      convocations, notes d'examen, corrections), `/pedagogie/deliberations`
      (+ `/pedagogie/deliberations/[id]` : décisions individuelles/UE, résultats),
      `/pedagogie/rattrapages`, `/pedagogie/proces-verbaux`.
    - Frais (4g) : `/pedagogie/grilles-tarifaires` (+ `/pedagogie/grilles-tarifaires/[id]` :
      lignes, réductions, activer/figer/archiver), `/pedagogie/avis-paiements`
      (+ `/pedagogie/avis-paiements/[id]` : échéances, remise, valider/émettre/annuler),
      `/pedagogie/paiements`, `/pedagogie/coupons-paiement`, `/pedagogie/journaliers`.
  - **Boulangerie** (liste ouverte `ADMIN`|`CUSTOMER`, mutations `ADMIN`) : lot 1 —
    `/boulangerie/produits` (+ `/boulangerie/produits/[id]` : caractéristiques, recettes du produit,
    activer/désactiver), `/boulangerie/recettes` (+ `/boulangerie/recettes/[id]` : ingrédients,
    lignes de coût, besoins matières, coût de revient, activer/figer),
    `/boulangerie/gammes`, `/boulangerie/categories`. Lot 2 (production) —
    `/boulangerie/plans` (+ `[id]` : lignes, besoins matières agrégés, valider/lancer/clôturer ;
    création avec lignes imbriquées via formulaire custom, le backend exige ≥1 ligne),
    `/boulangerie/fournees` (+ `[id]` : cycle PLANIFIEE→EN_COURS→CUITE→VALIDEE avec rendu,
    cuisson, validation, annulation motivée, consommations par lot, pertes valorisées).
    Lot 3 (approvisionnement) —
    `/boulangerie/fournisseurs` (+ `[id]` : identité, évaluation `getEvaluation`,
    activer/désactiver), `/boulangerie/besoins` (saisies multi-lignes seuil/conso/dispo →
    `POST /besoins/calcule` et `/besoins/sous-seuil`, badges urgence),
    `/boulangerie/commandes` (+ `[id]` : lignes, montants HT/TVA/TTC, reste à recevoir,
    transitions valider/envoyer/annuler avec motif inline, onglet rapprochement concordant/écarts),
    `/boulangerie/receptions` (+ `[id]` : cycles contrôler/accepter/refuser(motif),
    conformité par ligne ; création avec lignes — non conforme ⇒ motif obligatoire).
- `ProtectedLayout` engage la session (`ANONYME` → `/login`) mais ne coupe pas par rôle :
  les pages ADMIN utilisent `useAuth().user?.isAdmin` pour afficher / bloquer.

## Points clés / conventions

- **Ne pas rouvrir D1-D12 ni le multi-tenancy** (voir `AGENTS.md` racine). Le `X-Tenant-Id` est
  l'id de l'établissement/tenant (D8) ; `POST /socle/tenants` exige `X-Tenant-Id` = UUID du nouvel
  établissement (en-tête, jamais le corps) — géré dans la page `/app/tenants`.
- Les DTO du socle sont dans `src/types/index.ts` (champs alignés sur les modèles Java tenant-service),
  *pas* dans `services/api.ts`.
- Toute nouvelle route API doit d'abord être vérifiée dans
  `backend/services/tenant-service/.../adapter/in/web/*Controller.java` puis ajoutée à `api.ts`.
- **Pages identité** (`/utilisateurs`, `/roles`, ADMIN) : consomment `identity-service` via la
  passerelle (`/socle/utilisateurs/**`, `/socle/identites/**`). DTO dans `src/types/index.ts`
  (`Utilisateur`, `Role`, `Permission` + enums `StatutUtilisateur`, `TypeCompte`, `CategorieRole`),
  clients `utilisateurApi` / `identiteApi` dans `api.ts` : création/invitation, cycle de vie
  (activer/désactiver/suspendre), reset mot de passe, attribution de rôles, permissions par rôle.
  Les GET sont ouverts à `CUSTOMER`, les mutations réservées `ADMIN` (garde `isAdmin` en page).
- **Pages Pédagogie** : DTO 4a/4b dans `src/types/index.ts` (`Niveau`, `Classe`, `Salle`,
  `Creneau`, `RegimeHoraire`, `EmploiDuTemps`, `Eleve`, `Enseignant`, `Parent`, `Agent`,
  `ServiceEtablissement`… + enums), clients `structureApi` / `peopleApi` dans `api.ts`
  (préfixe passerelle `/ped/**`). Les pages CRUD simples s'appuient sur le composant générique
  `src/components/pedagogie/resource-crud.tsx` (`ResourceCrud<T>`, prop `embedded` pour les
  onglets) ; helpers `constants.ts` (options d'enums) et `format.ts` (dates/lieux sérialisés en
  tableaux `[y,m,d]` / `[h,m]`). Les écrans détail (`[id]`) utilisent `Tabs` + `useApi`.
  Les clés d'id diffèrent selon l'entité (`idNiveau`, `idClasse`, `idAnnee`, `idSite`,
  `idProgramme`…) — vérifier le DTO avant de les utiliser pour les listes déroulantes.
- **Pages Pédagogie 4c-4g** : DTO (InscriptionUE, Groupe, Seance, FeuillePresence, Justificatif,
  SuiviAssiduite, Evaluation, Bulletin, CouponNotes, SessionExamen, Epreuve, Convocation,
  NoteExamen, Deliberation, Rattrapage, ProcesVerbal, GrilleTarifaire, AvisPaiement, Paiement,
  CouponPaiement, Journalier… + enums), clients `enrollmentApi`, `attendanceApi`, `assessmentApi`,
  `examApi`, `feesApi` dans `api.ts`. Attention aux clés d'id non homogènes : `idInscription`,
  `idGroupe`, `idRegle`, `idSeance`, `idFeuille`, `idJustificatif`, `idEvaluation`, `idBulletin`,
  `idSession`, `idAvis`, `idPaiement` ; les autres entités (Deliberation, Rattrapage, ProcesVerbal,
  GrilleTarifaire, CouponPaiement, Journalier…) exposent `id`.
  `feesApi.paiements.create(body, idempotencyKey)` renseigne l'en-tête `Idempotency-Key`
  (**obligatoire** sur `POST /ped/paiements`).
- **Pages Boulangerie (lots 1-3)** : DTO dans `src/types/index.ts` (`Gamme`, `CategorieProduit`,
  `Produit`, `FicheRecette`, `IngredientRecette`, `LigneCout`, `BesoinMatiere`, `CoutRecette` +
  enums `UniteVente`, `Allergene`, `NatureCout` ; lot 2 : `PlanProduction`, `LignePlan`, `Fournee`,
  `LigneConsommation`, `PerteProduction` + enums `StatutPlan`, `StatutFournee`, `TypePerte` ;
  lot 3 : `FournisseurBoulangerie`, `EvaluationFournisseur`, `SaisieBesoin`,
  `BesoinApprovisionnement`, `CommandeAchat`, `LigneCommandeAchat`, `Reception`, `LigneReception`,
  `Rapprochement` + enums `StatutCommandeAchat`, `StatutReception`, `UrgenceAppro`),
  client `boulangerieApi` dans `api.ts` (préfixe passerelle `/boul/**`, groupes `gammes`,
  `categories`, `produits`, `recettes`, `plans`, `fournees`, `fournisseurs`, `besoins`,
  `commandes`, `receptions`). Clés d'id **non homogènes** :
  `Produit` expose `idProduit`, `FicheRecette` expose `idRecette`, `Gamme`/`CategorieProduit`
  exposent `id`, `PlanProduction` expose `idPlan`, `Fournee` expose `idFournee`,
  `PerteProduction` expose `idPerte`, `FournisseurBoulangerie` expose `idFournisseur`,
  `CommandeAchat` expose `idCommande`, `Reception` expose `idReception`. Les FK sont des
  **codes** (`gammeCode`, `categorieCode`, `produitCode`, `articleCode`, `recetteCode`,
  `fournisseurTiersCode`, `commandeId` en UUID sur `Reception`) — les lookups
  `useGammesMap`/`useCategoriesMap`/`useProduitsMap`/`useRecettesCodeMap` sont donc
  **keyed par `code`**.
  `boulangerieApi.recettes.cout(id, prix)` construit les paramètres répétés `prix=CODE:MONTANT`.
  Backend sans `PUT`/`DELETE` sur les lots 1-2 : `ResourceCrud` est utilisé avec `create` seul
  (pas d'`update`/`remove`) ; le lot 3 ajoute `PUT /fournisseurs/{id}` → `ResourceCrud` avec
  `create` + `update`.
  Le lot 2 utilise des formulaires custom (plans : lignes imbriquées obligatoires à la création ;
  fournées : cycle de vie + consommations/pertes sur la page détail).
  Le lot 3 utilise des formulaires custom (commandes : lignes imbriquées ; réceptions : lignes
  avec drapeau `conforme` + motif exigé ; besoins : saisies multi-lignes vers le calcul),
  les motifs d'annulation/refus sont saisis **inline en page détail** (jamais `prompt`).
- **Sélecteurs « par nom » (jamais d'UUID saisi)** : les FK ne sont plus des `<Input>` d'UUID
  mais des listes déroulantes affichant un libellé.
  - `src/components/pedagogie/labels.ts` : label par entité (`eleveLabel`, `classeLabel`, `ueLabel`,
    `enseignantLabel`, `sessionLabel`, `epreuveLabel`, `grilleLabel`, `reductionLabel`,
    `anneeLabel`, `periodeLabel`, `siteLabel`, `matiereLabel`, `coursLabel`, `salleLabel`,
    `creneauLabel`, `niveauLabel`, `promotionLabel`, `semestreLabel`, `programmeLabel`,
    `seanceLabel`, `evaluationLabel`, `avisLabel`, `codeLabel`, `parentLabel`) + `toSelectOptions`
    et `lookupLabel(map, id, fallback)`.
  - `src/hooks/use-lookup.ts` : `useXxxMap([tenantId])` construisent des `Map<id,label>` (élève,
    personnes, structure, exam, fees, année) ; `usePeriodesMap`/`useReductionsMap` aplatissent les
    collections imbriquées.
  - Formulaire : `src/components/common/entity-select.tsx` (`EntitySelect<T>`, props `fetcher`/
    `keyOf`/`labelOf`/`value`/`onChange`/`deps`).
  - Liste : `ResourceCrud` accepte `FieldDef.entityKey` (→ `<select>` alimenté par `entities`) et
    `ColumnDef.lookupKey` (→ cellule résolue par `entities`), avec la prop
    `entities={{ classes, ues, … }}`. `render` prime sur `lookupKey`.
  - Reste au backlog : `coupons-paiement` « Remettre » demande encore le destinataire via `prompt`
    (UUID) ; `saisiePar`/`affectePar` affichés en id court (pas de map utilisateur).
- Transitions d'état vérifiées côté domaine Java : `SessionExamen` BROUILLON→PROGRAMMEE→EN_COURS→…
  (**pas de statut « OUVERTE »**), `Deliberation` PLANIFIEE→EN_COURS→CLOTUREE (**pas de BROUILLON**,
  décisions uniquement EN_COURS), `ProcesVerbal` BROUILLON→SOUMIS→SIGNE→HOMOLOGUE (**pas
  d'EN_SIGNATURE**), `AvisPaiement` BROUILLON→EMIS (**pas de statut « VALIDE »**).
- Champs obligatoires `POST /socle/tenants` : `code` (≤30), `denomination` (≤150), `profil`
  (`PRIMAIRE | SECONDAIRE | UNIVERSITAIRE`), `statut`.
- Ports : console **3001** (Grafana occupe 3000), passerelle 8080 (API), Keycloak 8180 (tokens),
  tenant-service 8101, identity-service 8102, academic-structure 8111, academic-people 8112,
  academic-enrollment 8113, academic-attendance 8114,   academic-assessment 8115, academic-exam 8116,
  academic-fees 8117, boulangerie-produit 8141, boulangerie-production 8142,
  boulangerie-approvisionnement 8143.
  Utilisateurs de test (runtime, provisionnés par `backend/infrastructure/keycloak/provision.sh`) :
  `demo.admin@nexusgesti.com` / `Admin_pass123!` (ADMIN), `demo.customer@nexusgesti.com` /
  `demo_secret_customer` (CUSTOMER).
- Git-bash : `export MSYS_NO_PATHCONV=1` avant `docker exec ... /opt/...`.

## Dette technique / attention

- Ne pas réintroduire les reliquats GED : `src/features`, `src/mock-data`, `services/local-db.ts`,
  `services/stats-service.ts`, `data-provider*.tsx` ont été **supprimés**.
- Le login direct-grant exige un `X-Tenant-Id` valide dès le POST `/socle/auth/login` : le flux
  email-first résout d'abord le tenant (`POST /socle/auth/resolve-tenant`, exempté du header) puis
  `TenantProvider.setTenantId` (persisté `localStorage`) l'applique ; le repli manuel et l'UUID
  par défaut (`11111111-2222-3333-4444-555555555570`) restent disponibles.
- En production, `NEXT_PUBLIC_API_URL` doit pointer sur la passerelle sans préfixe de domaine.