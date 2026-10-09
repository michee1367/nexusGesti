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

**Périmètre actuel : documentation + backend `./backend` + console socle `./frontend` (phases 1-2).**
La console couvre phases 1-2 (auth direct-grant, tenancy, grilles, années, référentiels, pages ADMIN).
Toute phase suit la règle **back d'abord, front ensuite** (D13 + colonne Front en §9) :
le travail front d'une phase ne s'ouvre qu'une fois la brique backend validée.
Les 25 services sont déclarés en routes sur la passerelle, mais **seuls 9 modules Maven sont construits** (voir §9).
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
backend/       Maven parent + 9 modules construits (voir §9)
frontend/      console socle Next.js 16 (phases 1-2) — mémo dédié dans frontend/AGENTS.md
scripts/       démarrage local : backend-up.{sh,ps1} + front-up.{sh,ps1} (voir §11)
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
**Règle (D13) : back d'abord, front ensuite** — une phase n'ouvre sa console frontend
qu'une fois sa brique backend validée (build + `mvn test` + E2E passerelle + commit).

| Phase | Périmètre | État back | État front |
|---|---|---|---|
| 0 | Rendu SVG des 56 diagrammes | **écartée par l'utilisateur** | — |
| 1 | Socle technique (`shared/*` + passerelle + infra) | **VALIDÉE** — 5 services bootent, `/actuator/health` → 200, Prometheus 5 cibles `up`, **RLS prouvé de bout en bout** (§11, 2026-10-07), D3 et D4 appliqués, `mvn test` vert (59), **commit `5158d45`** | Console socle (base) : layout, auth direct-grant, tenancy — **livrée avec la console (phases 1-2)** |
| 2 | `tenant-service` (agégat `Etablissement`, `ProfilEtablissement`, grilles) | **VALIDÉE** — plan §12 exécuté le 2026-10-08 : 8+e module, 24 tables + RLS, 54 tests, grille activer/figer → 409 `GRILLE_FIGEE`, E2E passerelle complet (§11 2026-10-08), **commit `e25283e`** | pages socle : dashboard, tenant, grilles + `[type]`, annees, referentiels — **livrée avec la console (phases 1-2)** |
| 3 | reste du socle (`identity`, `auth`, `notification`, `audit`, `reporting`) | **en cours** — `identity-service` construit le 2026-10-09 (lot 1 : utilisateurs + rôles/permissions, miroir Keycloak) ; `auth` reste à faire | Console identité : pages `/utilisateurs` + `/roles` (2026-10-09, `tsc`+lint+build verts) |
| 4 | pédagogie (7 services `academic-*`) | à faire | à faire (après validation back) |
| 5 | hôpital (6 services) | à faire | à faire (après validation back) |
| 6 | finance (6 services, OHADA) | à faire | à faire (après validation back) |

### Modules réellement construits (9)

```
shared/common-domain     noyau pur, aucune dépendance Spring
shared/common-lib        tenancy, sécurité, idempotence
services/api-gateway     25 routes, filtre X-Tenant-Id
services/auth-service
services/notification-service
services/audit-service
services/reporting-service
services/tenant-service   socle métier : etablissement, grilles, referentiels (8101)
services/identity-service socle identité : utilisateurs + rôles/permissions, miroir Keycloak (8102)
```

`mvn test` au niveau racine : **BUILD SUCCESS**, ~129 tests verts (59 socle + 54 tenant-service + 16 identity-service).

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
| D6 | Portée phase 2 | Modèle **complet** de `modele-tenant-service.puml` (~24 tables). Aucun seed en migration : les lignes sont scopées tenant → les 17 conteneurs `Referentiel` sont créés dans `POST /tenants`. |
| D7 | `Filiere` / `Programme` / `Cycle` | **Propriétaire = tenant-service** (décidé le 2026-10-07). `pedagogie/modele-academic-structure-service.puml` les passe en `<<tenant-service>> #transparent` (étape 8 du plan §12). |
| D8 | Création cross-tenant | `X-Tenant-Id` = **UUID du nouveau tenant**, `Etablissement.id = tenant_id = l'en-tête`. Aucun bypass RLS, `409` si déjà présent. |
| D9 | Rôles `tenant-service` | Écriture `ADMIN`, lecture `ADMIN` ou `CUSTOMER`. |
| D10 | Doc §6 | Nettoyage **complet** des 9 duplications pédagogie, pas seulement `Filiere`/`Programme` : classes `Bareme`, `ConfigCascade`, `ReglePonderation`, `CandidatPonderation`, `Mention`, `PrincipeEquivalence` + enums `TypeBareme`, `NiveauAcalcul`, `StrategieAggregation`, `TypePrincipe`, `TypePortee`, `Decision`. |
| D11 | Tests phase 2 | Unitaires du domaine + 1 `@WebMvcTest` de sécurité. Pas de Testcontainers. |
| D12 | `GrilleStock.choisirLot` | **Non codée côté socle** : le socle expose la stratégie et les seuils ; `Lot` reste `<<fin-stock>>`, le choix du lot sera codé chez le propriétaire du stock (phase 5/6). |
| D13 | Règle de livraison | **Back d'abord, front ensuite.** Chaque phase est d'abord validée côté backend (build + `mvn test` + E2E passerelle + commit = une brique), puis sa console frontend est développée et vérifiée (`tsc` + lint + build + smoke). Le front monolithique (ex-ligne 7) est remplacé par une console livrée **par phase** (suivi dans la colonne Front du tableau §9). |

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
- **Piège Flyway + RLS — cause racine trouvée le 2026-10-09 : la boucle RLS attrapait la table
  d'historique Flyway.** Les `V2__rls.sql` de `tenant-service` et `identity-service` bouclaient sur
  `SELECT tablename FROM pg_tables WHERE schemaname = '<schéma>'` **sans exclure `flyway_*_history`**.
  Flyway crée sa table d'historique **avant** de jouer V1/V2 : la boucle V2 faisait donc de la DDL
  (`ADD COLUMN`, `ENABLE`/`FORCE RLS`, `DROP`/`CREATE POLICY`, `GRANT`) **sur la table que Flyway
  utilisait** → blocage **sans erreur** (log arrêté sur `Migrating schema ... to version "2 - rls"`,
  RLS resté `f/f`, 0 policy). L'hypothèse « advisory lock / ACCESS SHARE » du 2026-10-08 était un
  symptôme, pas la cause. `tenant` n'y avait échappé qu'**par chance** : V1/V2/V3 y furent appliquées à
  la main via psql, donc la table d'historique n'existait pas encore au moment de la boucle (historique
  = simple ligne `<< Flyway Baseline >>` v3). **Correctif appliqué (2026-10-09)** : exclure la table
  d'historique de la boucle — `AND tablename <> 'flyway_<service>_history'` (fait pour `tenant` et
  `identity`) ; `identity` aligne aussi la policy sur `current_setting('app.tenant_id', true)` +
  `NULLIF(COALESCE(...))` (fail-closed, plus d'`unrecognized configuration parameter`). Le contournement
  « psql manuel + `SPRING_FLYWAY_BASELINE_ON_MIGRATE=true SPRING_FLYWAY_BASELINE_VERSION=3` » est
  **obsolète**. **Règle phases 4-6** : toute boucle RLS doit exclure `flyway_*_history` ; les RLS
  explicites (auth/notification/audit/reporting) n'étaient pas touchées.
- **CORS passerelle 2026-10-08 — les préflights OPTIONS des routes protégées partaient en 401
  sans `Access-Control-Allow-Origin`** (`/socle/annees` bloqué depuis le navigateur). Cause : le
  `CorsWebFilter` tournait **après** Spring Security — la chaîne exigeait le token et rejetait le
  préflight (qui n'en porte jamais) avant l'ajout des en-têtes CORS. Correctif : `@Order(HIGHEST_PRECEDENCE)`
  sur le bean `CorsWebFilter` + `OPTIONS / **` permitAll dans `SecurityConfig`, et origines listées dans
  `nexusgesti.cors.allowed-origins` (surcharge `NEXUSGESTI_CORS_ALLOWED_ORIGINS`, défaut
  `http://localhost:3000`→`3004`). Vérifié : préflight 200 + ACAO pour une origine listée, 403 sinon.
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

2026-10-08 — **phase 2 terminée** (tenant-service, plan §12 exécuté) :
- **squelette** : `<module>services/tenant-service</module>`, pom clone d'`audit-service` sans Kafka,
  `TenantApplication`, `JpaConfig`, `SecurityConfig` (`@EnableMethodSecurity`, `oauth2ResourceServer`
  avec `KeycloakJwtConfig.jwtAuthenticationConverter`), `*ExceptionHandler` miroir d'`AuthExceptionHandler`,
  port **8101**, `spring.flyway.url` explicite, `nexusgesti.security.oauth2.enabled: false`.
- **migrations** : `V1__tenant_schema.sql` (24 tables + RLS), `V2__rls.sql` (boucle `DO` : column
  `tenant_id`, index, `ENABLE`+`FORCE ROW LEVEL SECURITY`, policy `USING`/`WITH CHECK`) et
  `V3__app_role.sql`. **Aucun seed** (D6).
- **contenu** : domaine pur (Etablissement, AnneeScolaire/Periode, GrilleNotation/Stock/Conformite avec
  activer/figer/dupliquer, baremes/regles/mentions, Referentiel), 5 repositories + adapters (hexagonal),
  services `EtablissementService` (création = id du header + 17 conteneurs `Referentiel`), `AnneeService`,
  `GrilleService` (+ `GET /grilles/{type}/applicable`), `ReferentielService` ; 6 contrôleurs sur les routes
  passerelle `/api/v1/socle/{tenants,etablissements,annees,grilles,referentiels}/**`.
- **correction au boot** : `GrilleStockJpaRepository.deleteByGrilleId` → `deleteById` (l'entité est
  l'agrégat racine, son `id` EST l'id de grille — défaut latent invisible en `@WebMvcTest`, trouvé au
  premier démarrage réel).
- **tests : 54 verts** (domaine + `TenantControllerSecurityTest` `@WebMvcTest` + `KeycloakRoleConverterTest`).
- **doc §6 (D7+D10 appliqués)** : `Filiere`/`Programme`/`Cycle`, `Bareme`, `ConfigCascade`,
  `PrincipeEquivalence`, `ReglePonderation`, `CandidatPonderation`, `Mention` + enums
  `TypeBareme`/`NiveauAcalcul`/`StrategieAggregation`/`TypePrincipe`/`TypePortee`/`Decision` passés en
  références `<<tenant-service>> #transparent` (exam/assessment refused local bodies). **56/56 diagrammes
  valident** (smetana, sortie temporaire, rien dans `rendu/` — D1).
- **observabilité** : 6e cible Prometheus `host.docker.internal:8101` → **6 cibles `up`**.
- **`mvn clean test` racine : BUILD SUCCESS, 113 tests**.
- **E2E passerelle** (2026-10-08) : CUSTOMER→`POST /tenants` **403** ; sans `X-Tenant-Id` **400** ;
  ADMIN→`POST` uuidA **201** (17 référentiels auto) ; re-POST **409** ; `GET /tenants/{id}` **200** ;
  isolation A/B/C (referentiél A lu depuis B **404**, tenant B lu depuis A **404**) ; grille
  notation activer→figer→modification **409 `GRILLE_FIGEE`** ; `GET /grilles/notation/applicable` A→200
  (NOT-PRIM-01), B→404.

### Console socle (frontend) — livrée (2026-10-08)

Console `./frontend` (Next.js 16 + React 19 + TS + Tailwind v4, copiée de `ged_ministere/front`,
contenu GED supprimé) :
- **plomberie** : `src/types/index.ts` (DTO alignés Java), `src/services/api.ts` (wrapper + refresh
  auto + groupes d'API), `auth-provider` (direct-grant, `realm_access.roles`, `isAdmin`),
  `tenant-provider` (`x_tenant_id`, UUID par défaut `11111111-2222-3333-4444-555555555570`),
  `layout`/`login`/`(app)/layout` + `AppShell` (sidebar/header).
- **pages socle : 1** dashboard, tenant (identité/sites/filières-programmes/cycles/paramètres/
  grille-presets), annees (+ périodes), grilles (liste + détail `[type]` : notation/stock/conformité,
  activer/figer/dupliquer), referentiels (17 conteneurs + items).
- **pages ADMIN** : tenants (création = `X-Tenant-Id`, D8), notifications, audit, rapports.
- **vérifs** : `npx tsc --noEmit` 0 erreur ; `npm run lint` 0 erreur (2 règles réactives strictes
  désactivées dans `eslint.config.mjs` : hydratation localStorage + ref du fetcher) ; `npm run build` OK.
- **route group `(app)` aplati** : URLs `/dashboard`, `/tenant`, `/grilles/[type]?id=…` (pas `/app/...`).
- **smoke passerelle** : login direct-grant `demo.admin` → 200 ; `GET /socle/{grilles,annees,
  referentiels(17),etablissements/*,notifications,reports,audit/search}` → 200 ; `/annees/courante` →
  404 métier (aucune année courante) ; CORS ci-dessus.

### CORS passerelle — corrigé (2026-10-08)

Symptôme navigateur : `NETWORK_ERROR` / « Response to preflight request doesn't pass access control check:
No 'Access-Control-Allow-Origin' header ». Le préflight OPTIONS des routes **protégées**
(`/socle/annees`, `/socle/grilles`…) répondait **401** (Spring Security exige le token, un préflight n'en
porte jamais) **sans** en-têtes CORS — le `CorsWebFilter` était déclaré sans ordre, donc exécuté **après**
la `WebFilterChainProxy` (ordre -100). Les routes `/auth/**` (permitAll) passaient par hasard,
d'où un comportement incohérent entre endpoints.

Correctif (commit CORS) :
- `CorsConfig` : `@Order(Ordered.HIGHEST_PRECEDENCE)` sur le bean `CorsWebFilter` → court-circuite le
  préflight avant Spring Security ; origines configurables `nexusgesti.cors.allowed-origins`
  (env `NEXUSGESTI_CORS_ALLOWED_ORIGINS`, défaut `http://localhost:3000`→`3004`).
- `SecurityConfig` : `.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()` (filet si l'ordre change).
- Vérifié : OPTIONS `/api/v1/socle/annees` `Origin 3001` → **200 + ACAO**, `3002` → 200, hors liste → **403** ;
  login + GET réels → 200 avec ACAO en réponse ; `/annees/courante` 404 porte aussi l'ACAO.

### identity-service — lot 1 construit (2026-10-09)

`identity-service` (port **8102**, schéma `identity`) réécrit de stub à service réel : **couche admin
mince au-dessus de Keycloak**. Périmètre lot 1 = utilisateurs + rôles/permissions ; délégation
temporaire, delombok, rattachement profil métier / `PersonneLien` et annuaire avancé restent à faire.

**Répartition source de vérité (Option A, arbitrée)** :
- **Keycloak** = identité native (`username`/`email`/`nom`/`prenom`/`enabled`), préférences
  (`attributes`), rôles (realm roles), `requiredActions` (`doitChangerMotDePasse` ← `UPDATE_PASSWORD`),
  `lastLogin` (events admin).
- **Postgres (entité proxy `utilisateur`, `tenant_id` RLS)** = attributs non natifs :
  `telephone`, `post_nom`, `sexe`, `date_naissance`, `nationalite`, `photo_url`, `type_compte`,
  `langue_preferee`, `statut`, `derniere_connexion`, `delombok` + adresse éclatée. Le lien est
  `keycloak_user_id`. **1 utilisateur Keycloak = 1 tenant.**
- Tables locales `permission`, `role_permission`, `role_template` (schéma `identity`) ; `role_code`
  référence le realm role. `JournalAccesUtilisateur` reste **domaine** : l'`audit-service` journalise.
- Client admin Keycloak : `nexusgesti.keycloak` + `nexusgesti-service` (repris d'`auth-service`),
  HTTP via **`RestClient`** (spring-web, pas de WebFlux).

**Livré** : `application.yml` (`default_schema: identity`, `spring.flyway.url`, `flyway_identity_history`,
`nexusgesti.security.oauth2.enabled: false`), `IdentityApplication` (`@ConfigurationPropertiesScan`),
`KeycloakProperties`, `SecurityConfig` (`OPTIONS /** permitAll`). `V1__identity_schema.sql` réécrite
(5 tables) ; V2 (`rls`) / V3 (`app_role`) conservées. Domaine pur (enums + `Utilisateur` avec
`activer/desactiver/suspendre/attribuerRole/...`, `Permission.correspondA`, `RolePermission.estExpiree`,
`IdentityException`), ports in/out, `KeycloakIdentityProviderAdapter`, persistance (5
entités/JPA/adapters), services `UtilisateurService`/`RoleService`, contrôleurs
`UtilisateurController` (`/api/v1/utilisateurs`) et `IdentiteController` (`/api/v1/identites`),
`GlobalExceptionHandler` (codes → 404/409/503 ; **pas de handler `Exception` générique**, sinon
l'`AccessDeniedException` de Spring Security ressort en 500 au lieu de 403).

**Tests : 16 verts** (domaine `Utilisateur`/`Permission`/`RolePermission` + `@WebMvcTest`
`UtilisateurControllerSecurityTest`). `IdentityApplicationTests` taggé `@Tag("integration")` (exclu de
Surefire sans Docker). `mvn test` racine : **BUILD SUCCESS, ~129 tests**. Prometheus : 7e cible
`host.docker.internal:8102`.

**Boot 8102** : le blocage au boot (log arrêté sur `Migrating schema "identity" to version "2 - rls"`)
avait la même cause racine que `tenant-service` — la boucle RLS attrapait `flyway_identity_history`
(voir §9 « Piège Flyway + RLS »). **Corrigé dans `identity/V2__rls.sql`** (exclusion de la table
d'historique + policy fail-closed) : plus besoin de baseline manuelle. Reste à vérifier à chaud
(boot réel 8102 + E2E passerelle) — l'utilisateur s'en charge.

### Console socle — pages identité livrées (2026-10-09)

D13 respecté (back committé `eace162` avant le front). Console `./frontend` :
- **`src/types/index.ts`** : `Utilisateur`, `Role`, `Permission` + enums `StatutUtilisateur`
  (`INVITE|ACTIF|DESACTIF|SUSPENDU|SUPPRIME`), `TypeCompte` (`LOCAL|KEYCLOAK|ANONYME`),
  `CategorieRole` (8 valeurs) ; `Adresse` existant réutilisé.
- **`src/services/api.ts`** : `utilisateurApi` (list/search, get, create, inviter, modifier,
  activer/desactiver/suspendre, reinitialiserMotDePasse, attribuer/retirerRole, definirPreferences) et
  `identiteApi` (annuaire, roles list/create/definirPermissions, permissions list/create/verifier).
- **Pages** (ADMIN, groupe `(app)` aplati) : `/utilisateurs` (recherche + table + détail : édition,
  cycle de vie, reset mot de passe, rôles ; carte création/invitation à onglets) et `/roles`
  (création de rôle — realm Keycloak — et de permission, table des rôles, matrice de permissions par
  rôle). GET ouverts à `CUSTOMER`, mutations gardées par `isAdmin`.
- **Sidebar** : entrées « Utilisateurs » (`UserCog`) et « Rôles & permissions » (`KeyRound`) sous
  « Plateforme » (ADMIN).
- **Vérifs** : `npx tsc --noEmit` 0 erreur ; `npm run lint` 0 erreur (2 warnings préexistants
  `avatar.tsx`) ; `npm run build` OK (routes `/utilisateurs` et `/roles` générées).
- **Non fait** : smoke passerelle réel (infra relancée le 2026-10-09, voir « Flyway + RLS » ci-dessous).

### Flyway + RLS — cause racine corrigée (2026-10-09)

Session de test : infra Docker relancée (postgres/redis/kafka healthy, keycloak 8180 → 200), les 7
services bootent **sauf `identity-service` (8102)**, dont le boot s'arrête sur
`Migrating schema "identity" to version "2 - rls"` (sans erreur, process tué). Diagnostic : la boucle
`pg_tables` de `V2__rls.sql` incluait `flyway_identity_history` (créée par Flyway juste avant V1/V2)
→ DDL sur la table que Flyway utilise → blocage muet (RLS resté `f/f`, 0 policy, historique figé à V1).
`tenant` y avait échappé grâce au démarrage manuel/baseline (table d'historique inexistante pendant
la boucle). **Correctif** : exclure `flyway_*_history` de la boucle (`tenant/V2` + `identity/V2`) ;
`identity/V2` passe en policy fail-closed (`current_setting(..., true)` + `NULLIF(COALESCE(...))`).
**Vérifié à chaud** : boot 8102 + health 200, Flyway V1/V2/V3 `success`, RLS `t/t` sur les 5 tables
applicatives et `f/f` sur `flyway_identity_history`. NB : le port 3000 est occupé par **Grafana**, ce
n'est pas la console (le front n'était pas lancé) — d'où le port **3001** retenu pour la console.

### Scripts de démarrage local (2026-10-09)

Quatre scripts dans `./scripts` (git-bash + PowerShell, backend + front) :
- `scripts/backend-up.sh` / `backend-up.ps1` : `docker compose up -d`, attente `postgres`/`redis`/`kafka`
  healthy + Keycloak 8180, **arrêt des services existants** (port ou `java -jar *SNAPSHOT.jar` → évite
  verrou `.jar`), `mvn -q -DskipTests package` (drapeau `--no-build`/`-NoBuild` pour l'ignorer), puis
  démarrage des **7 jars** en arrière-plan (logs `backend/logs/<svc>.log`, PID `<svc>.pid`) et attente
  de `/actuator/health` (8080 + 8101-8106). Ordre : tenant, identity, auth, notification, audit,
  reporting, api-gateway.
- `scripts/front-up.sh` / `front-up.ps1` : warn si la passerelle 8080 est absente, `npm install` si
  `node_modules` manquant, puis `npm run dev` **sur le port 3001** (Grafana occupe 3000 ; CORS
  passerelle autorise déjà 3000→3004).
- Testés : `backend-up.sh` → **7/7 UP** ; `front-up.sh` → `localhost:3001` → 307 `/login`.
- `backend/logs/` ajouté au `.gitignore` ; `frontend/.env` (`FRONT_HOST`) passé à `:3001` (champ
  inutilisé dans le code, simple cohérence). Aucune var `SPRING_FLYWAY_BASELINE_*` (correctif RLS appliqué).

### Restant

- **Commit `5158d45` (251 fichiers)** : docs + `.gitignore` + `backend/`. Phase 1 close.
- **Phase 2 commitée le 2026-10-08** : `e25283e` (tenant-service + doc D7/D10 + E2E + colonne Front).
- **Console socle commitée le 2026-10-08** : décrite au journal §11 (plomberie + pages socle + ADMIN,
  D13 appliqué).
- Prochaine étape (§9) : **phase 3** (reste du socle) — `identity-service` fait (lot 1, 2026-10-09)
  et ses pages console (`/utilisateurs`, `/roles`) livrées ; reste `auth` (durcissement + tests),
  `notification`, `audit`, `reporting` — back d'abord, front ensuite.
- `@Async` de `ReportService.generateAsync` inerte (self-invocation) : génération synchrone.
  À corriger si le rapport doit vraiment être émis hors du thread de requête.
- `"kyc_level": ["0"]` dans `realm-nexusgesti.json` et `ReportType.KYC_SUMMARY` /
  templates `kyc.*` : restes villagesat signalés hors périmètre par l'utilisateur.
- **Flyway + RLS : cause racine corrigée le 2026-10-09** (voir §9 « Piège Flyway + RLS ») — les
  `V2__rls.sql` de `tenant` et `identity` bouclaient sur `pg_tables` sans écarter la table
  d'historique Flyway. Correctif in-code appliqué ; le contournement « psql manuel + baseline » est
  obsolète. NB : `tenant.V2` est déjà baseliné en dev (aucun checksum enregistré), donc l'éditer est
  sans risque ici ; sur une base où il aurait été appliqué par Flyway, passer par `flyway repair` ou
  une nouvelle version.
- **Utilisateurs Keycloak de test (runtime, rien au dépôt)** : `demo.admin@nexusgesti.com`/`Admin_pass123!`
  (ADMIN) et `demo.customer@nexusgesti.com`/`demo_secret_customer` (CUSTOMER) provisionnés par
  `provision.sh`. Les rôles `realm-management` du service account `nexusgesti-service` sont accordés au
  runtime (non importables via KC25) ; la route `register` répond **403** tant que la faille du realm
  (rôles `realm-management` non importés) n'est pas corrigée — à faire avant la phase 3.
- Prochaine étape §9 : phase 3 (reste du socle) — back d'abord, front ensuite.

---

## 12. Plan phase 2 — `tenant-service` (validé 2026-10-07, **exécuté le 2026-10-08**, voir §11)

Ordre = build vert après chaque étape.

1. **Squelette** : `<module>services/tenant-service</module>` dans le pom racine, pom clone d'
   `audit-service` **sans** `spring-kafka` (actuator + prometheus conservés), `TenantApplication`,
   `JpaConfig`, `SecurityConfig` (`@EnableMethodSecurity`, rôles par `@PreAuthorize`,
   `oauth2ResourceServer` avec `KeycloakJwtConfig.jwtAuthenticationConverter`),
   `application.yml` port **8101**, `default_schema: tenant`, **`spring.flyway.url` explicite**,
   `nexusgesti.security.oauth2.enabled: false`, `*ExceptionHandler` miroir d'`AuthExceptionHandler`.
2. **Migrations** : `V1__tenant_schema.sql` (24 tables : `etablissement`, `site`, `parametre`,
   `grille_preset`, `annee_scolaire`, `periode`, `grille_notation`, `bareme`, `regle_ponderation`,
   `config_cascade`, `regle_equivalence`, `principe_equivalence`, `seuil_decision`,
   `regle_assiduite`, `mention`, `regle_appreciation`, `grille_stock`, `alerte_stock`,
   `grille_conformite`, `referentiel`, `referentiel_item`, `filiere`, `programme`, `cycle` ;
   `metaDonnees` en jsonb, `Adresse` en colonnes) ; `V2__rls.sql` (boucle `DO` sur le schéma
   `tenant` : `tenant_id UUID`, `ENABLE`+`FORCE ROW LEVEL SECURITY`, policy `USING`/`WITH CHECK`) ;
   `V3__app_role.sql` (grant `nexusgesti_app`, même `DO` de création de rôle que les autres services).
3. **Domaine pur + tests JUnit** (aucun Spring) : transitions `Etablissement`, `GrilleNotation`
   `activer()`/`figer()` + nouvelle version à la correction, `resoudreRegle()` par spécificité
   décroissante (§7), `SeuilDecision.contient`, arrondis `TypeArrondi`, `Mention`,
   `RegleAssiduite`, agrégation `ConfigCascade`, cycles de vie `AnneeScolaire`/`Periode`,
   retentions `GrilleConformite`, seuils `GrilleStock`, `Referentiel`. Exceptions à codes
   (`TENANT_ALREADY_EXISTS` 409, `GRILLE_FIGEE` 409, `TRANSITION_INVALIDE` 409).
4. **Persistence** : entités + repositories + adapters par agrégat (structure `audit-service`) ;
   `@JdbcTypeCode(SqlTypes.JSON)` sur `metaDonnees` ; tout passe par `TenantContext.require()`.
5. **Services applicatifs** : `EtablissementService` (création = id du header + 17 conteneurs),
   `AnneeService`, `GrilleService` (+ `GET /grilles/applicable` = UC *Consulter la grille
   applicable*), `ReferentielService`.
6. **Contrôleurs** — le service voit `/api/v1/...` après `RewritePath` :

   | Route passerelle | Contrôleur service | Rôles |
   |---|---|---|
   | `/socle/tenants/**` | `POST /` création, `GET /{id}`, `PATCH /{id}/statut` | ADMIN · ADMIN\|CUSTOMER · ADMIN |
   | `/socle/etablissements/**` | `/{id}/sites`, `/filieres`, `/programmes`, `/cycles`, `/parametres`, `/grille-presets` (+ `/dupliquer`) | écriture ADMIN, lecture ADMIN\|CUSTOMER |
   | `/socle/annees/**` | CRUD + `/{id}/cloturer`, `/{id}/periodes`, `/{id}/periodes/{pid}/cloturer`, `/courante` | idem |
   | `/socle/grilles/**` | `GET /applicable`, CRUD par type (notation/stock/conformite) + enfants + `/activer` `/figer` `/dupliquer`, `/{id}/alertes` | idem |
   | `/socle/referentiels/**` | conteneurs + `POST /{id}/items` + désactivation d'item | idem |

   L'id de `POST /tenants` vient **de l'en-tête**, jamais du corps.
7. **`@WebMvcTest(TenantController)`** : `@Import(SecurityConfig.class)` + `jwt()` de
   spring-security-test : 403 sans rôle, 200 CUSTOMER en lecture, 2xx ADMIN.
8. **Doc §6 (D7 + D10)** :
   - `pedagogie/modele-academic-structure-service.puml` : `Filiere` (:111) et `Programme` (:122)
     → `class X <<tenant-service>> #transparent` sans membres, déplacés dans le bloc
     « REFERENCES INTER-SERVICES » (:275) ; suppression de `enum StatutProgramme` (:55) ;
     relations :287-289 conservées.
   - `pedagogie/modele-academic-assessment-service.puml` : classes `Bareme` (:198),
     `ConfigCascade` (:210), `PrincipeEquivalence` (:223), `ReglePonderation` (:238),
     `CandidatPonderation` (:282), `Mention` (:367) → externes transparents **sans membres** ;
     enums `TypeBareme` (:44), `NiveauAcalcul` (:53), `StrategieAggregation` (:61),
     `TypePrincipe` (:230), `TypePortee` (:265) → déclarations externes **sans corps**
     (`enum X <<tenant-service>>`, sinon `class X <<tenant-service>> #transparent`) ;
     commentaire :180-182 réécrit (« projections spécialisées » → « références ») ;
     **toutes les relations conservées**.
   - `pedagogie/modele-academic-exam-service.puml` : `Bareme` (:146) et `enum Decision` (:69)
     → externes ; les `DecisionType`/`DecisionCode` locaux (valeurs identiques) restent.
   - Validation : **56/56 en sortie temporaire** (`-Playout=smetana`), rien dans `rendu/` (D1).
9. **Observabilité + build** : `infrastructure/docker/prometheus/prometheus.yml` → 6e cible
   `host.docker.internal:8101` (relancer prometheus/grafana) ; `mvn clean test` racine vert ;
   `mvn -q -DskipTests package` puis démarrage **8101** (tuer d'abord les `java -jar *SNAPSHOT.jar`
   : verrou sur le `.jar`).
10. **E2E par la passerelle** : health 200 + Prometheus 6 `up` ; CUSTOMER → `POST /api/v1/socle/tenants`
    **403** ; `demo.admin` créé via l'API admin Keycloak (`admin/admin_secret`, runtime, rien au dépôt)
    → POST `X-Tenant-Id: uuidA` → **201**, re-POST → **409**, 17 référentiels auto ; isolation A/B ;
    grille `activer` → `figer` → modification → **409 GRILLE_FIGEE** ; `GET /grilles/applicable` A/B ;
    sans `X-Tenant-Id` → **400**.
11. **AGENTS.md** (phase 2 VALIDÉE, 8 modules construits, journal, Restant → phase 3) +
    **commit sur demande**.

**Vigilance** : `spring.flyway.url` obligatoire, aucun seed (lignes scopées tenant),
`@JdbcTypeCode(SqlTypes.JSON)`, aucun Kafka ni `IdempotencyService` en phase 2, port 8101,
pas de commentaires superflus dans le code.