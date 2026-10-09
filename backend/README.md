# NexusGesti Backend

Plateforme SaaS multi-tenant : socle, pedagogie, hopital, finance OHADA/RDC.

## Stack

| Couche | Technologie |
|--------|-------------|
| Backend | Java 21 + Spring Boot 3.4, architecture hexagonale |
| Persistance | PostgreSQL 16 + Flyway, un schema par service, RLS |
| Cache | Redis 7 |
| Evenements | Apache Kafka, 5 topics |
| API Gateway | Spring Cloud Gateway |
| Identite | Keycloak 25 (OIDC, JWT) |
| Conteneurs | Docker, Docker Compose |

## Modules partages

| Module | Role |
|--------|------|
| `shared/common-domain` | Noyau pur : `Money`, `EtablissementId`, `PatientId`, `TiersId`, `ExerciceId`, `CodeActe`, `SourceId`, `CorrelationId`, `TenantHeaders`. Aucune dependance Spring |
| `shared/common-lib` | Tenancy (`TenantContext`, `TenantFilter`, `TenantAwareDataSource`), securite, `IdempotencyService`, erreurs, crypto |

## Multi-tenancy

Deux modes cohabitent :

| Mode | Isolation |
|------|-----------|
| SaaS mutualise | `etablissement_id` sur chaque table + RLS PostgreSQL |
| On-premise dedie | base dediee par instance |

Le client porte son etablissement dans l'en-tete `X-Tenant-Id`. Il est valide par
la passerelle (`TenantHeaderFilter`) puis repris par `TenantContext`. Chaque
connexion obtenue par `TenantAwareDataSource` porte le reglage `app.tenant_id`,
remis a zero au retour dans le pool pour qu'un tenant ne fuite jamais vers le suivant.

Une migration qui ne pose pas ses politiques RLS n'est pas terminee.

## Domaines et ports

| Domaine | Services | Ports |
|---------|----------|-------|
| Passerelle | `api-gateway` | 8080 |
| Socle | `tenant` `identity` `auth` `notification` `audit` `reporting` | 8101-8106 |
| Pedagogie | `academic-structure` `people` `enrollment` `attendance` `assessment` `exam` `fees` | 8111-8117 |
| Hopital | `patient` `rdv` `consultation` `pharmacie` `hospitalisation` `plateau-technique` | 8121-8126 |
| Finance | `fin-compta-core` `facturation` `stock` `inventaire` `immobilisation` `etats` | 8131-8136 |

Preprefixes d'API : `/api/v1/socle/**`, `/api/v1/ped/**`, `/api/v1/hop/**`, `/api/v1/fin/**`.

## Topics Kafka

`tenant.events` · `ped.events` · `hopital.events` · `fin.events` · `audit.events`

Crees par `infrastructure/kafka/topics.sh` au demarrage de Kafka.

## Demarrage local

```bash
# Infra + les 7 services du socle + attente /actuator/health (git-bash)
scripts/backend-up.sh          # ou scripts/backend-up.ps1 (PowerShell)
scripts/front-up.sh            # console socle sur http://localhost:3001

docker compose up -d           # infra seule
mvn test                       # tests unitaires
```

## Regle transverse

Les regles metier sont des donnees versionnees, jamais des constantes codees :
grilles de notation, tarifaire, assiduite, conformite, stock. Une correction cree
une nouvelle version ; une piece emise fige celle qui s'appliquait au moment de
son emission.