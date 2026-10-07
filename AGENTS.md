# AGENTS.md — NexusGesti

Mémoire de travail pour les agents IA et les sessions futures.
Ce fichier décrit **l'état réel du dépôt**, pas les intentions.

---

## 1. Projet

Plateforme SaaS multi-tenant pour trois domaines :

| Domaine | Métier |
|---|---|
| **Socle** | tenancy, identité, auth, notification, reporting, audit |
| **Pédagogie** | écoles primaire/secondaire + université |
| **Hôpital** | patients, RDV, consultations, pharmacie |
| **Finance** | comptabilité OHADA / SYSCOHADA RDC |

**Périmètre actuel : documentation + backend `./backend` en cours d'implémentation.**
Le frontend `./frontend` n'existe **pas** encore : ne pas supposer de `package.json`, de Next ou de tests front.
Les 25 services sont déclarés en routes sur la passerelle, mais **seuls 7 modules Maven sont construits** (voir §9).
Ne pas supposer qu'un service existe sous prétexte que sa route existe.

---

## 2. Arborescence

```
README.md                      vue d'ensemble + conventions
docs/architecture.md           Mermaid : vue globale, flux, multi-tenancy, OHADA
docs/diagrammes/
  socle/       6 modèles + 6 cas d'utilisation
  pedagogie/   7 modèles + 7 cas d'utilisation
  hopital/     6 modèles + 6 cas d'utilisation
  finance/     6 modèles + 6 cas d'utilisation
  sequences/   6 séquences (paiement, délibération, rappel lot, clôture, séjour, acte → facture)
  rendu/       56 SVG à générer — ACTUELLEMENT VIDE
backend/       Maven parent + 7 modules construits (voir §9)
```

**56 fichiers `.puml`.** Ne pas casser le compte.
**0 SVG pour l'instant** : `docs/diagrammes/rendu/` est vide, le rendu n'a jamais été fait ni versionné.
§4 reste la procédure de rendu à suivre un jour ; entre temps, ne pas écrire « 56 SVG » dans un fichier de suivi.

Services hôpital : `patient`, `rdv`, `consultation`, `pharmacie`, `hospitalisation`, `plateau-technique`. 25 services au total.

---

## 3. Convention de nommage (non négociable)

Le nom du diagramme doit être **exactement** le nom du fichier sans `.puml` :

```
@startuml modele-fin-etats-service
```

PlantUML dérive le nom du SVG du libellé `@startuml`, pas du nom du fichier.
Si les deux divergent, les SVG s'écrasent silencieusement et on perd des livrables.

---

## 4. Rendu et validation

Graphviz n'est **pas installé**. Utiliser le moteur smetana.

```bash
# rendu
java -jar plantuml.jar -Playout=smetana -tsvg -o docs/diagrammes/rendu docs/diagrammes/*/*.puml

# validation seule (penser à capturer stdout : PlantUML sort en code 0 même en cas d'erreur)
java -jar plantuml.jar -Playout=smetana -checkonly <fichier.puml>
```

Le jar est dans `C:\Users\PC\AppData\Local\Temp\opencode\plantuml.jar` — **répertoire temporaire, il sera effacé.**
Le re-télécharger si absent :

```bash
curl -o plantuml.jar https://repo1.maven.org/maven2/net/sourceforge/plantuml/plantuml/1.2024.7/plantuml-1.2024.7.jar
```

Boucle de validation complète :

```bash
for f in $(find docs -name "*.puml" | sort); do
  msg=$(java -jar plantuml.jar -Playout=smetana -tsvg -o docs/diagrammes/rendu "$f" 2>&1)
  if echo "$msg" | grep -q "Error"; then echo "KO $(basename $f)"; echo "$msg" | head -2; fi
done
```

---

## 5. Pièges PlantUML déjà payés

Ne pas retester ces erreurs à l'aveugle.

| Piège | Correctif |
|---|---|
| `enum X { A  B  C }` sur une ligne | multiligne strict, `}` seul sur sa ligne |
| `{` collé à la fin d'une ligne d'enum | `}` sur ligne dédiée |
| `A "1" -- "1" B <<sterereo>> : label` | stéréotype **interdit** en milieu de relation → supprimer |
| `</note>` | `end note` |
| `Foo <<external>>` sans `class` | `class Foo <<external>> #transparent` |
| Nom d'attribut en double dans une classe | dédoublonner (PlantUML ignore silencieusement le 2e) |
| `note bottom of` sur un acteur | utiliser un rectangle englobant |
| Accents / caractères CJK dans un attribut | ASCII uniquement dans les modèles de classes |

---

## 6. Conventions de modélisation

**Langue** : diagrammes en français.
Modèles de classes en ASCII (compatibilité PlantUML) ; accents autorisés dans les cas d'utilisation et les notes.

**Aucune valeur métier en dur.** Coefficients, seuils, barèmes, taux, plafonds, durées = données versionnées dans des grilles versionnées `activer()` / `figer()`.
Le nom réel des grilles, tel qu'écrit dans les `.puml` :

| Grille | Fichier de définition |
|---|---|
| `GrilleNotation` | `socle/modele-tenant-service.puml` (`<<tenant-service>>`) |
| `GrilleTarifaire` | `pedagogie/modele-academic-fees-service.puml:91` |
| `GrilleConformite` | `socle/modele-tenant-service.puml` |
| `GrilleStock` | `socle/modele-tenant-service.puml` |
| `RegleAssiduite` | `socle/modele-tenant-service.puml` |

**`GrilleAssiduite` n'existe pas.** Le nom réel est `RegleAssiduite`. Ne pas écrire `GrilleAssiduite` :
c'est une faute présente dans `docs/architecture.md:187`, elle n'a jamais existé dans aucun `.puml`.

Une correction crée une **nouvelle version** ; les pièces déjà émises gardent leur version d'origine.
Une pièce fige sa version au moment de l'émission : ligne de tarification à l'admission, demande d'examen à la prescription, lot à la réception.

**Entités partagées entre services** → déclarées `<<external>> #transparent`, jamais dupliquées :
`Patient`, `Tiers`, `Article`, `Depot`, `Compte`, `Journal`, `Exercice`.
Pour les classes issues du dépôt, préférer le stéréotype du propriétaire (`<<hopital-patient>>`, `<<hopital-hospitalisation>>`, `<<fin-facturation>>`, `<<tenant-service>>`) à un `<<external>>` nu.

**Explication dans le diagramme.** Les règles métier (formules, seuils, pourquoi) sont écrites dans des `note` du fichier `.puml`, pas dans une annexe externe. Le diagramme doit être auto-suffisant.

---

## 7. Décisions métier figées

- **Cœur pédagogique partagé** : un seul modèle pour primaire / secondaire / université. `ProfilEtablissement` active les modules (UE, ECTS, délibération par UE) sans dupliquer les classes.
- **Cascade de pondération** : `Evaluation → CoursECUE → UE → Periode → Annee → Cycle`. Résolution par spécificité décroissante.
  - PRIMAIRE / SECONDAIRE : `matière → période → année → cycle`
  - UNIVERSITAIRE : `coursECUE → UE → semestre → cycle`
- **Deux flux d'argent distincts** : `CouponNotes` (bulletin) et `CouponPaiement` (frais). Ne jamais les fusionner.
- **Séance ≠ examen.** Une `SeanceExamen` est une occurrence ; une `SessionExamen` est une session portant plusieurs épreuves.
- **Sécurité des paiements** : clé d'idempotence obligatoire sur `POST paiements`, empreinte SHA-256 sur coupon, coupon numéroté séquentiellement par année.
- **Multi-tenancy hybride** : SaaS mutualisé (RLS PostgreSQL) et on-premise dédié (base par instance).
- **Finance RDC** : devise fonctionnelle **USD**, CDF pour l'affichage légal, taux moyen pondéré figé à la clôture, TVA 0/5/10/16 %, états certifiés signés numériquement, FEC exportable.

### Hôpital

- **`Service` appartient à `hospitalisation`.** Les trois lectures — service d'accueil d'un RDV, service demandeur d'une prescription, `TypeDestination` de sortie de stock — désignent le même objet.
- **`plateau-technique` regroupe labo et imagerie** dans un seul service. Point d'entrée unique : une `LignePrescription` de type `ANALYSE`, `IMAGERIE` ou `EXAMEN` devient une `DemandeExamen`, qui aiguille. Aucun examen ne se crée directement, jamais sans prescription tracable.
- **Rien ne sort avant validation biologique.** Un `ResultatAnalyse` saisi mais non validé n'est pas lisible du prescripteur, ne figure sur aucun compte rendu et ne déclenche aucun acte facturable.
- **Pas d'examen sans équipement calibré.** `Equipement.estRealisable()` refuse tout appareil hors calibration ou bloqué par une maintenance non soldée. Aucun acte facturable ne peut en sortir.
- **Le module hospitalier n'encaisse rien.** Il émet `hopital.acte.facturable` sur le topic `hopital.events` ; `fin-compta-core` produit `D 411 / C 706`. Clé d'idempotence dérivée de `sourceId` + `codeActe`.
- **`Patient.tiersId` est le pont vers la comptabilité.** Créé paresseusement au premier acte facturable, jamais à l'admission. La création du tiers et le contrôle d'encours restent à `fin-facturation-service`.
- **Périmètre hospitalisation = le séjour seulement.** Pas d'observations quotidiennes, pas de transmissions, pas de dossier de soins continu.
- **`NatureProduit` distingue `MEDICAMENT`, `REACTIF`, `CONTRASTE`, `CONSOMMABLE`.** Un produit de contraste n'est pas un médicament : les confondre produirait une dispensation involontaire et des rappels de lot qui ne remontent pas vers les patients injectés.
- **`Lot` porte trois quantités** : disponible, réservée, consommée. Un réactif réservé puis prélevé n'est pas re-livrable.
- **`CompteRendu` appartient à `consultation`.** L'imagerie y rattache son `compteRenduId`, elle ne redéclare pas la classe.

---

## 8. Références externes (hors dépôt)

| Chemin | Usage |
|---|---|
| `C:\Users\PC\Documents\villages-backend` | **source du backend** : a été recopié dans `./backend` en phase 1. Référence structurelle uniquement, ne plus modifier depuis ce dépôt |
| `C:\Users\PC\Documents\ged_ministere\front` | référence frontend (Next 16, React 19, TS, Tailwind v4) — à copier dans `./frontend` en phase 7 |
| `C:\Users\PC\Documents\gestion_fin\docs\diagrams` | 10 000 lignes de diagrammes OHADA, source du module finance |
| `C:\Users\PC\Documents\nexusGesti\docs\diagrammes\finance` | adaptation OHADA de NexusGesti |

---

## 9. État de l'implémentation backend

Ordre des phases validé : **socle → pédagogie → hôpital → finance.** Une brique = un commit.

| Phase | Périmètre | État |
|---|---|---|
| 0 | Rendu SVG des 56 diagrammes | **écartée par l'utilisateur** |
| 1 | Socle technique (`shared/*` + passerelle + infra) | **VALIDÉE** — 5 services bootent, `/actuator/health` → 200, Prometheus 5 cibles `up`, **RLS prouvé de bout en bout** (§11, 2026-10-07), D3 et D4 appliqués, `mvn test` vert (59), **commit `5158d45`** |
| 2 | `tenant-service` (agégat `Etablissement`, `ProfilEtablissement`, grilles) | à faire |
| 3 | reste du socle (`identity`, `auth`, `notification`, `audit`, `reporting`) | à faire |
| 4 | pédagogie (7 services `academic-*`) | à faire |
| 5 | hôpital (6 services) | à faire |
| 6 | finance (6 services, OHADA) | à faire |
| 7 | frontend `./frontend` (copie de `ged_ministere/front`) | à faire |

### Modules réellement construits (7)

```
shared/common-domain     noyau pur, aucune dépendance Spring
shared/common-lib        tenancy, sécurité, idempotence
services/api-gateway     25 routes, filtre X-Tenant-Id
services/auth-service
services/notification-service
services/audit-service
services/reporting-service
```

`mvn test` au niveau racine : **BUILD SUCCESS**, ~59 tests verts sur ces 7 modules.

### Les 25 routes de la passerelle ≠ les modules construits

Toutes sont déclarées dans `services/api-gateway/src/main/resources/application.yml`
mais **une route vers un module inexistant renverra un refus de connexion**, pas un 404.
Vérifier l'existence du module avant d'appeler un service.

| Domaine | Préfixe | Services (port) |
|---|---|---|
| socle | `/api/v1/socle/**` | `tenant` 8101 · `identity` 8102 · `auth` 8103 · `notification` 8104 · `audit` 8105 · `reporting` 8106 |
| pédagogie | `/api/v1/ped/**` | `academic-structure` 8111 · `academic-people` 8112 · `academic-enrollment` 8113 · `academic-attendance` 8114 · `academic-assessment` 8115 · `academic-exam` 8116 · `academic-fees` 8117 |
| hôpital | `/api/v1/hop/**` | `patient` 8121 · `rdv` 8122 · `consultation` 8123 · `pharmacie` 8124 · `hospitalisation` 8125 · `plateau-technique` 8126 |
| finance | `/api/v1/fin/**` | `fin-compta-core` 8131 · `fin-facturation` 8132 · `fin-stock` 8133 · `fin-inventaire` 8134 · `fin-immobilisation` 8135 · `fin-etats` 8136 |
| passerelle | — | `api-gateway` 8080 (Keycloak sur 8180) |

Pas de `fin-tresorerie` : la trésorerie est un préfixe de `fin-compta-core` (`/api/v1/fin/tresorerie/**`).

### Multi-tenancy — implémenté en phase 1

- En-tête **`X-Tenant-Id`**, constante `TenantHeaders.HEADER` dans `common-domain`.
- Contrôlé en entrée par `TenantHeaderFilter` (GlobalFilter, ordre -100, exempte `/actuator`,
  `/v3/api-docs`, `/swagger-ui`, `/fallback`) puis repris par `TenantContext`.
- **`TenantAwareDataSource`** : à chaque emprunt de connexion, `set_config('app.tenant_id', ?, false)`,
  réinitialisé au `close()`. Un tenant ne peut jamais fuiter vers le suivant, ni vers le pool suivant.
- `common-lib` ne peut pas être dépendance de `api-gateway` : WebFlux réactif incompatible avec le filtre servlet.
  La passerelle ne dépend que de `common-domain`.
- Les politiques RLS sont posées **dans les migrations de chaque service**. Une migration qui ne les pose pas n'est pas terminée.
  Schéma par service, **pas** de schéma monolithe `V1__init_schema.sql`.

### Décisions arbitrées (ne pas rouvrir)

| # | Sujet | Décision |
|---|---|---|
| D1 | SVG | Rendu reporté (phase 0 écartée). Ne pas affirmer que les SVG existent. |
| D2 | `GrilleAssiduite` | N'existe pas → vrai nom `RegleAssiduite`. |
| D3 | `GrilleTarifaire` | Reste propriété de `academic-fees-service`, **mais** ajouter `version : Integer` + `figer()` à `pedagogie/modele-academic-fees-service.puml:91` (exigé par `sq-hopital-acte-facturation.puml:23` et `modele-hopital-plateau-technique-service.puml:600`). Les deux déclarations `<<tenant-service>>` à `hopital/modele-hopital-hospitalisation-service.puml:318` et `hopital/modele-hopital-plateau-technique-service.puml:580` sont donc à corriger en `<<academic-fees>>`. **Appliqué le 2026-10-07.** |
| D4 | `Ecriture` | **Tranché et appliqué le 2026-10-07 : propriétaire `fin-compta-core`.** La classe complète (attributs + méthodes) et les enums `StatutEcriture` / `OrigineEcriture` sont déclarées dans `finance/modele-fin-compta-core-service.puml` (package `ecritures`), avec `Ecriture --> Journal` et `Ecriture --> Exercice`. `finance/modele-fin-etats-service.puml` ne garde qu'une référence `class Ecriture <<fin-compta-core>> #transparent`, ses relations (`*-- LigneEcriture`, `--> Piece`, `--> ModeleEcriture`, `o-- Lettrage`) et sa note `REGLE D EQUILIBRE`. `SensEcriture` reste dans `fin-etats` (type de `LigneEcriture`). |
| D5 | Idempotence | `IdempotencyService` prend une clé `String`. Clé composite `sourceId` + `codeActe` via `SourceId.compositeWith(CodeActe)`. |

### Points de vigilance payés

- **Kafka : deux listeners obligatoires.** Un listener unique annonçant `localhost` bloque tout client
  conteneur (cela a fait tourner `kafka-init` 15 h en boucle silencieuse).
  `INTERNAL://kafka:29092` pour les conteneurs, `EXTERNAL://localhost:9092` pour le poste de dev.
  Le healthcheck doit tester `kafka:29092` : un healthcheck `localhost` prouve que le broker tourne,
  pas qu'un autre conteneur peut le joindre.
- **Tests d'intégration** portent `@Tag("integration")`, exclu de Surefire
  (`surefire.excludedGroups`). Sans Docker, `mvn test` doit rester vert.
- **Modèle d'un nouveau service** (à reprendre tel quel, sinon il paie les mêmes erreurs) :
  `spring-boot-starter-actuator` **et** `micrometer-registry-prometheus` dans le `pom.xml`,
  `management.endpoints.web.exposure.include` dans le `application.yml`,
  `spring.data.redis.password: ${REDIS_PASSWORD:redis_secret}` (Redis impose `requirepass`),
  et **`/error` dans les `permitAll()`** — sinon tout 4xx/5xx ressort en 401.
  Sans `starter-actuator`, `/actuator/**` renvoie 404 qui bascule sur `/error` et se lit comme un 401,
  ce qui masque la vraie cause.
- **`TenantFilter` exige un UUID** dans `X-Tenant-Id` : `demo` produit 400
  (`Invalid X-Tenant-Id header: expected a UUID`). Ne pas interpréter ce 401/400 comme un défaut de routage.
- **Pas de commentaires superflus dans le code.**

---

## 10. Questions ouvertes — ne pas trancher sans arbitrage humain

1. Structure universitaire réelle et conversion ECTS (notes ou présence / absence ?)
2. Rattachement de la délibération : `exam-service` (retenu) ou service dédié ?
3. Gel des grilles de notation après publication d'un bulletin, ou versionnement libre ?
4. ~~`fin-paiement-service`~~ **tranché** : absent des 25 routes de la passerelle. `ped-fees` encaisse
   (`/api/v1/ped/paiements/**`), `fin-compta-core` enregistre (`/api/v1/fin/reglements/**`). Ne pas le réintroduire.

---

## 11. Historique de session

2026-10-02 — création complète des 50 diagrammes (socle, pédagogie, hôpital, finance, séquences) + `README.md` + `docs/architecture.md`. Tous validés au rendu SVG.

2026-10-02 — extension du domaine hôpital, 50 → 56 diagrammes :
- **créés** : `modele/uc-hopital-hospitalisation-service`, `modele/uc-hopital-plateau-technique-service`, `sq-hopital-sejour`, `sq-hopital-acte-facturation`
- **`GrilleStock` et `GrilleConformite` désormais définies** dans `modele-tenant-service.puml` (elles étaient référencées 6 fois sans exister)
- **`Patient.tiersId`** ajouté : c'était le point de rupture entre hôpital et facturation, `Patient` et `Tiers` étaient deux agrégats isolés
- **`Consultation.imagerieId` et `vemChargeId` supprimés** (références orphelines) ; la cible d'un examen passe par `LignePrescription → DemandeExamen`
- **`TERMINEE_PARTIELLE` déclaré** dans `StatutConsultation`, il était utilisé 2 fois sans exister
- **`EXAMEN_COMPLÉMENTAIRE` passé en ASCII** (violation de §6)
- **`Service` a désormais un propriétaire** (`hospitalisation`) ; il était déclaré `<<external>>` sans exister
- **`GrilleNotation`** repassée en `<<tenant-service>>` dans `assessment` au lieu d'être redéfinie en dur
- **les 10 `@startuml` non conformes corrigés** (4 UC hôpital, 6 UC finance) : §3 est désormais respecté partout
- `architecture.md`, `README.md` et ce fichier mis à jour

2026-10-03 — **phase 1 terminée** (socle technique). Repartage de `villages-backend` dans `./backend` :
- `pom.xml` réécrit : `com.nexusgesti` / `nexusgesti-backend`, 7 modules réellement construits ;
  13 modules villagesat supprimés (`wallet`, `payment`, `merchant`, `card`, `fraud`, `compliance`,
  `admin`, `transaction`, `mobile-money-integration`, `banking-integration`, `afidingpay-e2e`,
  `user-service`, `database/`)
- renommage complet `com.villagesat` → `com.nexusgesti` (poms, 121 `.java`, `.yml`),
  `VillageSatAutoConfiguration` → `NexusGestiAutoConfiguration`, realm Keycloak → `realm-nexusgesti.json`
- **`shared/common-domain`** créé : `Money`, `EtablissementId`, `PatientId`, `TiersId`, `ExerciceId`,
  `SourceId.compositeWith(CodeActe)`, `CodeActe`, `CorrelationId`, `TenantHeaders`
- **`shared/common-lib`** : `TenantContext`, `TenantFilter`, `TenantAwareDataSource`,
  `TenantDataSourceBeanPostProcessor`, `IdempotencyService` (clé `String` + `releaseLock`)
- **passerelle** : 25 routes NexusGesti, `TenantHeaderFilter`, `tenantKeyResolver`,
  CORS avec `X-Tenant-Id`
- **infra** : `docker-compose.yml` réécrit (postgres/redis/kafka/keycloak/minio/vault/prometheus/grafana),
  `infrastructure/kafka/topics.sh` créant les 5 topics
- **correction Kafka** : `kafka-init` était bloqué 15 h car `KAFKA_ADVERTISED_LISTENERS` annonçait
  `localhost:9092` — un client conteneur résolvait `localhost` sur lui-même. Passé à deux listeners.
- **encore à appliquer** : D3 (grilles tarifaires) et D4 (`Ecriture`)
- état git : 0 commit, tout en `staged`

2026-10-06 — mise en route des services du socle. **Phase 1 toujours ouverte.**

- **ports réalignés sur §9** : audit 8091→8105, notification 8085→8104, reporting 8090→8106,
  auth→8103. `services/auth-service/src/main/resources/application.yml` **créé** : il n'existait pas
  (datasource/Flyway schéma `auth`, redis, Keycloak, port 8103).
- **postgres déplacé sur 5433** (`- "5433:5432"`) : le service Windows `postgresql-x64-18` occupe 5432.
  `DB_PORT:5433` dans les 4 services, CI garde `5432` explicite. Mdp redis ajouté à la passerelle et à auth.
- **MinIO retiré** du `docker-compose.yml` (décision utilisateur).
- **passerelle** : default-filter `RewritePath` → `/api/v1/(socle|ped|hop|fin)/(.*)` → `/api/v1/$2`
  (sinon la cible reçoit `/api/v1/socle/...`).
  **Piège payé** : la clé d'argument s'appelle **`regexp`**, pas `regex`. Avec `regex`, `Config.regexp`
  reste `null` et `RewritePathGatewayFilterFactory.apply` plante au boot en
  `NullPointerException: Cannot invoke "String.isEmpty()" because "this.pattern" is null`.
- **deux `SecurityFilterChain` « any request »** (conflit de boot) résolus en ajoutant
  `nexusgesti.security.oauth2.enabled: false` à `audit` et `reporting` : seule reste la chaîne déclarée
  dans le `SecurityConfig` du service. `notification` n'a pas le basculeur →
  `OAuth2ResourceServerConfig` de `common-lib` reste active chez lui.
  `InternalServiceAuthFilter` ne sert que `/internal/**` (`shouldNotFilter` sur le préfixe).
- **code Kafka villagesat réécrit** (décision utilisateur) :
  - supprimés : `KycNotificationConsumer`, `WalletNotificationConsumer`, `TransactionNotificationConsumer` ;
  - créés : `TenantNotificationConsumer` (`tenant.events` / `user.created`),
    `HopitalNotificationConsumer` (`hopital.events` / `resultat.critique` → CRITICAL),
    `FinanceNotificationConsumer` (`fin.events` / `controle.facturation`, ignore `BLOQUANT`) ;
  - `UserRegistrationKafkaPublisher.TOPIC` : `user.events` → **`tenant.events`** ;
  - `AuditKafkaConsumer` écoute `tenant.events`, `ped.events`, `hopital.events`, `fin.events`
    (`extractServiceName` = `topic.replace(".events","-service")`, compatible).
- **`mvn clean test` = BUILD SUCCESS**, ~59 tests verts (passerelle : 6 sur `TenantHeaderFilter`).
- **les 5 services démarrent** : auth 8103, notification 8104, audit 8105, reporting 8106,
  passerelle 8080 (Flyway exécutée). Infra healthy : postgres 5433, redis, kafka (5 topics, `kafka-init`
  exit 0), Keycloak 8180 → 200 (fix `KC_HOSTNAME_STRICT=false`), Prometheus, Grafana, Vault.

### `401` sur `/actuator/**` — résolu (causes réelles)

Symptôme : audit / notification / reporting répondaient 401 `WWW-Authenticate: Bearer` sur
**tous** les `/actuator/**`, alors que les chaînes déclarent `permitAll()`.

La trace `logging.level.org.springframework.security=TRACE` montrait que l'autorisation passait
(`Secured GET /actuator/health`) puis qu'un **deuxième dispatch vers `/error`** était refusé par
`anyRequest().authenticated()`. Le 401 masquait donc la vraie réponse. Trois causes cumulées :

1. **`spring-boot-starter-actuator` absent** de `audit-service`, `notification-service`,
   `reporting-service` (présent dans `api-gateway` et `auth-service` seulement). Sans le starter,
   `/actuator/**` → 404 → dispatch `/error` → 401. Le bloc `management:` de leurs `application.yml`
   était donc inerte.
2. **`/error` n'était pas `permitAll`** : tout 4xx/5xx servait un 401 à la place du vrai statut.
   Ajouté dans `audit/SecurityConfig`, `reporting/SecurityConfig` et
   `common-lib/OAuth2ResourceServerConfig`.
3. **Redis sans mot de passe** dans ces 3 mêmes services (Redis impose `requirepass redis_secret`,
   seuls gateway et auth l'avaient) → indicateur `redis` DOWN → `/actuator/health` 503.
   Bloc `spring.data.redis` ajouté aux 3 `application.yml`.

Aujourd'hui : `health` → **200 `{"status":"UP"}`** sur les 5 services, `info` et `metrics` → 200,
`/error` → 400/404 (plus jamais 401), `/actuator/inexistant` → 404.

### Rendu observé

- **bouchon bout en bout** : `POST /api/v1/socle/auth/login` sur la passerelle →
  `401 AUTH_INVALID_CREDENTIALS` renvoyé par `auth-service` (la réécriture `RewritePath` et le
  filtre `TenantHeaderFilter` fonctionnent). Sans `X-Tenant-Id` → 400 ; avec `demo` → 400
  (`TenantFilter` exige un **UUID**).
- **`micrometer-registry-prometheus`** ajouté aux 5 poms (absent partout) ; `actuator` ajouté
  aux 3 services qui en manquaient.
- **`infrastructure/docker/prometheus/prometheus.yml` réécrit** : il listait encore les services
  villagesat (`user-service`, `wallet-service`, `transaction-service`, `payment-service`,
  `compliance-service`, `fraud-service`, `admin-service`, `mobile-money-integration`,
  `banking-integration`, `merchant-service`) sur les ports 8081-8094, plus trois exporters
  (`postgres-exporter`, `redis-exporter`, `kafka-exporter`) absents du compose. Remplacé par les
  5 services construits : 8080, 8103, 8104, 8105, 8106 → **5 cibles `health: up`, `lastError: ""`**.
- **`SecurityConfig` de la passerelle** : `pathMatchers("/api/v1/auth/**")` ne couvrait pas les
  chemins préfixés par domaine → ajout de `/api/v1/*/auth/**`, sinon le login passait en 401 à la
  passerelle avant tout renvoi vers `auth-service`.
- `mvn test` : **BUILD SUCCESS** après ces modifications.

2026-10-07 — **RLS prouvé de bout en bout**, D3 et D4 appliqués, contrat villagesat nettoyé.

Le RLS existait déjà (migrations `V*__rls.sql` + `V*__app_role.sql` des 4 services) ; il était
inerte pour une raison différente : le bean qui enveloppe le DataSource n'était **jamais créé**.

**Défauts trouvés et corrigés** (dans l'ordre où ils ont bloqué) :

1. **`TenantDataSourceBeanPostProcessor` jamais enregistré** — `@ConditionalOnMissingBean` sans type
   sur une méthode retournant `BeanPostProcessor` se base sur le type de retour et trouve des BPP
   d'infrastructures → condition non satisfaite → bean absent → `TenantAwareDataSource` jamais posé
   → `set_config('app.tenant_id')` jamais exécuté. Correctif : type explicite
   (`TenantDataSourceBeanPostProcessor.class`) + méthode `static` (un BPP non-statique force la
   création anticipée de la classe de configuration).
2. **Flyway plantait au boot dès que le DataSource était enveloppé** — sans `spring.flyway.url`,
   `FlywayAutoConfiguration.getMigrationDataSource` fait `DataSourceBuilder.derivedFrom(dataSource)`
   qui lit `getUrl()`/`getUsername()` par **reflection** : notre enveloppe ne les expose plus →
   `UnsupportedDataSourcePropertyException: Unable to find suitable method for url`.
   Correctif : `spring.flyway.url` explicite dans les 4 `application.yml`.
3. **Keycloak : tokens sans `sub` ni `realm_access`** → 500 (`UUID.fromString(jwt.getSubject())`) et
   403 partout. Le claim `sub` vient de la client scope **`basic`**, que l'import du realm ne
   rattachait à aucun client. Correctif : `defaultClientScopes` ajoutés aux 2 clients dans
   `infrastructure/keycloak/realm-nexusgesti.json` (vérifié par import d'un realm sonde, supprimé
   ensuite).
4. **Rôles absents des autorités** : `common-lib` portait déjà `KeycloakRoleConverter`, la passerelle
   et `audit`/`reporting` en redéclaraient un chacun → extracteur partagé
   `KeycloakJwtConfig.jwtAuthenticationConverter` (bean `JwtAuthenticationConverter`) branché dans
   les `SecurityConfig`, convertisseurs doublons retirés.
5. **`ReportRequestEntity.parameters` (String) lié à une colonne `jsonb`** → `42804` à l'insert.
   Correctif : `@JdbcTypeCode(SqlTypes.JSON)`.
6. **Fallback du CircuitBreaker en `@GetMapping` seul** : le fallback réutilise la méthode
   d'origine → un POST qui bascule reçoit **405** (et le corps d'erreur porte le chemin original,
   ce qui fait croire à une erreur de routage). Correctif : toutes les méthodes +
   `@ResponseStatus(SERVICE_UNAVAILABLE)`.
7. **TimeLimiter 1 s par défaut** contre un générateur de rapport à 2 s : la passerelle répondait
   avant la fin du traitement. `resilience4j.timelimiter.configs.default.timeoutDuration: 10s`.
8. **NPE-500 sur `reportType`/`format` absents** : `@NotNull` ajoutés sur les deux champs
   (`spring-boot-starter-validation` déjà présent) → 400.
9. **`@Async` sur `ReportService.generateAsync` est inerte** (appel `this.` depuis la même classe) :
   la génération reste synchrone dans le thread de requête. Non corrigé (voir Restant).

**Décisions traitées** :

- **D3 appliqué** : `version : Integer` + `figer()` ajoutés à `GrilleTarifaire`
  (`pedagogie/modele-academic-fees-service.puml:91`), note `VERSIONNEMENT`, stéréotypes
  `<<tenant-service>>` → `<<academic-fees>>` dans `hopital/modele-hopital-hospitalisation-service.puml:318`
  et `hopital/modele-hopital-plateau-technique-service.puml:580`. Les 3 fichiers rendent en SVG.
- **D4 appliqué** : `Ecriture` + `StatutEcriture` + `OrigineEcriture` déplacés de
  `finance/modele-fin-etats-service.puml` vers le package `ecritures` de
  `finance/modele-fin-compta-core-service.puml` (avec `Ecriture --> Journal` et
  `Ecriture --> Exercice`, les deux classes étant détenues par compta-core). `fin-etats` ne garde
  qu'une référence `class Ecriture <<fin-compta-core>> #transparent`, ses propres relations et sa
  note `REGLE D EQUILIBRE` ; `SensEcriture` y reste (type de `LigneEcriture`). Les 3 références
  `<<fin-compta-core>>` existantes (hospitalisation, plateau-technique, academic-fees) sont donc
  désormais exactes.
- **`@enduml` manquant** dans `socle/modele-tenant-service.puml` : PlantUML rendait
  `No diagram found` (aucun SVG), défaut latent depuis le 2026-10-02. Ajouté.
- **Validation complète** : 56 fichiers `.puml` → **56 SVG, 0 erreur** (`-Playout=smetana`,
  sortie temporaire, rien de commité : le rendu en `docs/diagrammes/rendu/` reste écarté, D1).
- **`kycLevel` supprimé** de `RegisteredUser`, `AuthService.register`, `AuthController.RegisterResponse`
  et du payload `UserRegistrationKafkaPublisher`. Reste : `"kyc_level": ["0"]` dans
  `infrastructure/keycloak/realm-nexusgesti.json` (métadonnée Keycloak, sans impact).

**Preuve RLS (erreurs réelles passées à des réussites)** — tenant A, tenant B, user `nexusgesti_app` :

| Requête | Avant | Après |
|---|---|---|
| `SELECT count(*)` sans contexte | toutes les lignes | **0** |
| `SELECT` contexte A / B | — | **1 / 0** |
| `INSERT` sans contexte | accepté | **`new row violates row-level security policy`** |
| `tenant_id` de la ligne insérée | NULL | **= uuid du tenant A** |

**Bout en bout par la passerelle** : login `demo.customer@nexusgesti.com` → `POST
/api/v1/socle/reports/request` → **201** (ligne `READY`, `tenant_id` renseigné), `GET .../reports`
tenant A → 1 ligne, tenant B → **[]** ; `POST` sans `format` → **400**. Contrôle des rôles :
`GET .../notifications` (CUSTOMER) → 200, `GET .../audit/search` et `POST .../notifications/send`
(ADMIN requis) → **403** — 403 et non 401 : authentification et authorization distinctes.

`mvn test` : **BUILD SUCCESS, 59 tests, 0 échec**.

### Restant

- **Commit `5158d45` (251 fichiers)** : docs + `.gitignore` + `backend/`. Phase 1 close.
- `@Async` de `ReportService.generateAsync` inerte (self-invocation) : génération synchrone.
  À corriger si le rapport doit vraiment être émis hors du thread de requête.
- `"kyc_level": ["0"]` dans `realm-nexusgesti.json` et `ReportType.KYC_SUMMARY` /
  templates `kyc.*` : restes villagesat signalés hors périmètre par l'utilisateur.
- Prochaine étape §9 : phase 2 (`tenant-service`).