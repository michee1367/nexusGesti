# NexusGesti Backend

Plateforme SaaS multi-tenant : socle, pedagogie, boulangerie, puis hopital et finance OHADA/RDC.

## Stack

| Couche | Technologie |
|--------|-------------|
| Backend | Java 21 + Spring Boot 3.4, architecture hexagonale |
| Persistance | PostgreSQL 16 + Flyway, **un schema par domaine**, RLS |
| Cache | Redis 7 |
| Evenements | Apache Kafka, 6 topics |
| API Gateway | Spring Cloud Gateway |
| Identite | Keycloak 25 (OIDC, JWT) |
| Conteneurs | Docker, Docker Compose |

## Applications (fusion par domaine)

Les 18 microservices d'origine sont regroupes en **3 applications** qui partagent
un processus et un schema, tout en conservant leurs packages, routes et ports
d'origine. La passerelle reste un processus separe.

| Application | Domaines inclus | Port | Schema |
|-------------|-----------------|------|--------|
| `socle-service` | tenant, identity, auth, notification, audit, reporting | 8101 | `socle` |
| `pedagogie-service` | structure, people, enrollment, attendance, assessment, exam, fees | 8111 | `pedagogie` |
| `boulangerie-service` | produit, production, approvisionnement, vente | 8141 | `boulangerie` |
| `api-gateway` | routage, CORS, controle `X-Tenant-Id` | 8080 | — |

Hopital (8121-8126) et finance (8131-8136) ne sont pas encore implementes.
Les modules boulangerie `clients` et `personnel` restent a construire.

Chaque application demarre avec une seule chaine de securite, une seule
configuration JPA/Kafka et une seule table d'historique Flyway
(`flyway_socle_history`, `flyway_pedagogie_history`, `flyway_boulangerie_history`).

## Modules partages

| Module | Role |
|--------|------|
| `shared/common-domain` | Noyau pur : `Money`, `EtablissementId`, `PatientId`, `TiersId`, `ExerciceId`, `CodeActe`, `SourceId`, `CorrelationId`, `TenantHeaders`. Aucune dependance Spring |
| `shared/common-lib` | Tenancy (`TenantContext`, `TenantFilter`, `TenantAwareDataSource`), securite, `IdempotencyService`, erreurs, crypto |

## Multi-tenancy

Deux modes cohabitent :

| Mode | Isolation |
|------|-----------|
| SaaS mutualise | `tenant_id` sur chaque table + RLS PostgreSQL |
| On-premise dedie | base dediee par instance |

Le client porte son etablissement dans l'en-tete `X-Tenant-Id`. Il est valide par
la passerelle (`TenantHeaderFilter`) puis repris par `TenantContext`. Chaque
connexion obtenue par `TenantAwareDataSource` porte le reglage `app.tenant_id`,
remis a zero au retour dans le pool pour qu'un tenant ne fuite jamais vers le suivant.

Une migration qui ne pose pas ses politiques RLS n'est pas terminee.

## Domaines et routes

| Domaine | Etat | Prefixe passerelle |
|---------|------|--------------------|
| Socle | construit | `/api/v1/socle/**` |
| Pedagogie | construit | `/api/v1/ped/**` |
| Boulangerie | produit, production, approvisionnement, vente construits | `/api/v1/boul/**` |
| Hopital | a faire | `/api/v1/hop/**` |
| Finance | a faire | `/api/v1/fin/**` |

La passerelle ne route que vers les applications reellement presentes.

## Topics Kafka

`tenant.events` · `ped.events` · `boulangerie.events` · `hopital.events` · `fin.events` · `audit.events`

Crees par `infrastructure/kafka/topics.sh` au demarrage de Kafka ; `scripts/backend-up.*`
rejoue `kafka-init` (idempotent) pour creer les topics ajoutes apres le premier demarrage.

## Demarrage local

```bash
# Infra + les 3 applications + la passerelle, puis attente /actuator/health (git-bash)
scripts/backend-up.sh          # ou scripts/backend-up.ps1 (PowerShell)
scripts/front-up.sh            # console sur http://localhost:3001

docker compose up -d           # infra seule
mvn test                       # tests unitaires
```

Limites memoire par defaut des scripts : `-Xmx512m` par application domaine,
`-Xmx256m` pour la passerelle (surchargeables par `JAVA_OPTS` et `GATEWAY_JAVA_OPTS`).

## Regle transverse

Les regles metier sont des donnees versionnees, jamais des constantes codees :
grilles de notation, tarifaire, assiduite, conformite, stock. Une correction cree
une nouvelle version ; une piece emise fige celle qui s'appliquait au moment de
son emission.
