# AGENTS.md — Console socle NexusGesti (frontend)

Mémoire de contexte pour les agents IA. Se référer ici avant toute modification.

## Vue d'ensemble

Frontend de la **Console socle** de NexusGesti (SaaS multi-tenant : socle, pédagogie, hôpital, finance).
Base : copie adaptée de `C:\Users\PC\Documents\ged_ministere\front`, **tout contenu GED supprimé**.
Backend : passerelle Spring Cloud Gateway sur `http://localhost:8080/api/v1` (voir `.env`, non versionné).
Le périmètre couvre le **socle** (tenants, établissements, grilles, référentiels, années,
utilisateurs, rôles/permissions) et la **pédagogie — lots 4a (structure) et 4b (people)**
(`academic-structure-service` 8111 + `academic-people-service` 8112).
Hôpital / finance n'ont pas encore de pages. Les lots pédagogie 4c-4g (enrollment, attendance,
assessment, exam, fees) n'ont ni backend ni front.

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
- **Tenant** (`src/components/providers/tenant-provider.tsx`) : en-tête `X-Tenant-Id` (UUID),
  persévéré dans `localStorage` (`x_tenant_id`), sélecteur dans le header. Une valeur non UUID
  produit un 400 du backend — ne pas laisser passer.
- **API** (`src/services/api.ts`) : wrapper `rawFetch` — en-têtes `Authorization` (si token)
  et `X-Tenant-Id` (si tenant), erreurs normalisées en `ApiError {status, code, message}`.
  Codes métier utiles : `TENANT_ALREADY_EXISTS` 409, `GRILLE_FIGEE` 409, `TRANSITION_INVALIDE` 409,
  `UTILISATEUR_EXISTANT` 409, `ROLE_EXISTANT` 409, `PERMISSION_EXISTANTE` 409,
  `KEYCLOAK_INDISPONIBLE` 503, `AUTH_INVALID_CREDENTIALS` 401.
- **Routes** :
  - Publiques : `/` → redirection `/dashboard`, `/login`.
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
- Champs obligatoires `POST /socle/tenants` : `code` (≤30), `denomination` (≤150), `profil`
  (`PRIMAIRE | SECONDAIRE | UNIVERSITAIRE`), `statut`.
- Ports : console **3001** (Grafana occupe 3000), passerelle 8080 (API), Keycloak 8180 (tokens),
  tenant-service 8101, identity-service 8102, academic-structure-service 8111, academic-people-service 8112.
  Utilisateurs de test (runtime, provisionnés par `backend/infrastructure/keycloak/provision.sh`) :
  `demo.admin@nexusgesti.com` / `Admin_pass123!` (ADMIN), `demo.customer@nexusgesti.com` /
  `demo_secret_customer` (CUSTOMER).
- Git-bash : `export MSYS_NO_PATHCONV=1` avant `docker exec ... /opt/...`.

## Dette technique / attention

- Ne pas réintroduire les reliquats GED : `src/features`, `src/mock-data`, `services/local-db.ts`,
  `services/stats-service.ts`, `data-provider*.tsx` ont été **supprimés**.
- Le login direct-grant exige un `X-Tenant-Id` valide dès le POST `/socle/auth/login` : le
  `TenantProvider` initialise `x_tenant_id` par défaut (UUID du premier établissement chargé).
- En production, `NEXT_PUBLIC_API_URL` doit pointer sur la passerelle sans préfixe de domaine.