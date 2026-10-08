# AGENTS.md — Console socle NexusGesti (frontend)

Mémoire de contexte pour les agents IA. Se référer ici avant toute modification.

## Vue d'ensemble

Frontend de la **Console socle** de NexusGesti (SaaS multi-tenant : socle, pédagogie, hôpital, finance).
Base : copie adaptée de `C:\Users\PC\Documents\ged_ministere\front`, **tout contenu GED supprimé**.
Backend : passerelle Spring Cloud Gateway sur `http://localhost:8080/api/v1` (voir `.env`, non versionné).
Le périmètre actuel ne couvre que le **socle** (tenants, établissements, grilles, référentiels, années).
Les domaines pédagogie / hôpital / finance n'ont pas encore de pages dans cette console.

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
  `AUTH_INVALID_CREDENTIALS` 401.
- **Routes** :
  - Publiques : `/` → redirection `/dashboard`, `/login`.
  - Authentifiées (groupe `(app)` avec `AppShell` = Sidebar + Header ; le groupe `(app)`
    est aplati dans l'URL) :
    `/dashboard`, `/tenant`, `/grilles`, `/grilles/[type]?id=…`, `/referentiels`,
    `/annees` ; réservées `ADMIN` : `/tenants`, `/notifications`, `/audit`, `/rapports`.
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
- Champs obligatoires `POST /socle/tenants` : `code` (≤30), `denomination` (≤150), `profil`
  (`PRIMAIRE | SECONDAIRE | UNIVERSITAIRE`), `statut`.
- Ports : passerelle 8080 (API), Keycloak 8180 (tokens), tenant-service 8101.
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