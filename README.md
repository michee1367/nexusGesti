# NexusGesti

Plateforme de gestion SaaS multi-tenant pour **établissements scolaires**, **hôpitaux**, **boulangeries / pâtisseries** et **cabinets de gestion financière OHADA**.

## Documentation

- [Guide utilisateur](docs/guide-utilisateur.md) — socle, pédagogie et prise en main de la console.
- [Guide Boulangerie](docs/guide-boulangerie.md) — produits, recettes, production et approvisionnement (lots 1 à 3).
- [Guide de déploiement en production](backend/infrastructure/deploiement/README.md) — VPS, PostgreSQL, Keycloak, Nginx, sauvegardes, mise à jour.

## Modélisation

Toute la conception est en **PlantUML** (`.puml`) : classes, cas d'utilisation, séquences.

```
docs/
├── diagrammes/
│   ├── socle/        6 modèles + 6 cas d'utilisation
│   ├── pedagogie/    7 modèles + 7 cas d'utilisation
│   ├── hopital/      6 modèles + 6 cas d'utilisation
│   ├── finance/      6 modèles + 6 cas d'utilisation
│   ├── boulangerie/  6 modèles + 6 cas d'utilisation
│   ├── sequences/    9 diagrammes de séquence des flux critiques
│   └── rendu/        sortie des SVG (dossier vide)
└── architecture.md
```

| Module | Services | Diagrammes |
|---|---|---|
| Socle | `tenant`, `identity`, `auth`, `notification`, `reporting`, `audit` | 12 |
| Pédagogie | `structure`, `people`, `enrollment`, `attendance`, `assessment`, `exam`, `fees` | 14 |
| Hôpital | `patient`, `rdv`, `consultation`, `pharmacie`, `hospitalisation`, `plateau-technique` | 12 |
| Finance | `compta-core`, `facturation`, `stock`, `inventaire`, `immobilisation`, `etats` | 12 |
| Boulangerie | `produit`, `production`, `vente`, `clients`, `approvisionnement`, `personnel` | 12 |
| Séquences | paiement, délibération, rappel de lot, clôture d'exercice, séjour, acte → facture, fournée, vente/caisse, réception | 9 |
| **Total** | **31 services** | **71** |

## Implémentation actuelle

La modélisation décrit 31 services ; l'implémentation les regroupe en **3 applications métier (une par domaine)** pour réduire l'empreinte mémoire, avec un schéma PostgreSQL par domaine et la même isolation RLS :

| Application | Services regroupés | Port | Schéma |
|---|---|---|---|
| `socle-service` | tenant, identity, auth, notification, audit, reporting | 8101 | `socle` |
| `pedagogie-service` | structure, people, enrollment, attendance, assessment, exam, fees | 8111 | `pedagogie` |
| `boulangerie-service` | produit, production, approvisionnement, vente | 8141 | `boulangerie` |
| `api-gateway` | routage, CORS, `X-Tenant-Id` | 8080 | — |

Hôpital et finance restent à implémenter ; les modules boulangerie `clients` et `personnel` aussi. Les routes et packages d'origine sont conservés (`/api/v1/socle/**`, `/api/v1/ped/**`, `/api/v1/boul/**`). Voir `backend/README.md` pour le démarrage et les limites mémoire par défaut.

## Générer les SVG

Graphviz n'est pas requis, le moteur **smetana** est utilisé.

```bash
java -jar plantuml.jar -Playout=smetana -tsvg -o docs/diagrammes/rendu docs/diagrammes/*/*.puml
```

## Conventions

- Diagrammes en français, sans accent dans les diagrammes de classes (compatibilité PlantUML) ; accents dans les cas d'utilisation et les notes.
- Aucune valeur métier en dur : coefficients, seuils, barèmes, taux et plafonds sont des **données versionnées**.
- Relations inter-services jamais dupliquées : les entités partagées sont déclarées `<<external>> #transparent`.
- notes explicatives intégrées dans les fichiers plutôt qu'en annexe.

## Principes structurants

**Cœur pédagogique partagé** — un seul modèle sert les niveaux primaire, secondaire et universitaire ; le *profil d'établissement* active les seuls modules (UE/ECTS, délibération par UE) sans dupliquer les classes.

**Cash et inter-service** — un paiement d'un coupon produit un reçu, un coupon de paiement et une imputation comptable, validés par empreinte SHA-256 et clé d'idempotence.

**Conformité OHADA/SYSCOHADA RDC** — devise fonctionnelle USD, taux moyen pondéré, plan de comptes par classes 1 à 7, états certifiés, FEC.

**Boulangerie adossée au socle finance** — le domaine `boulangerie` porte le métier (catalogue, recettes, fournées, caisse, fidélité, achats, personnel) et **réutilise la finance** : `fin-stock` porte les articles, lots et mouvements, `fin-facturation` les tiers et factures, `fin-compta-core` les écritures. Le module de vente n'encaisse rien en comptabilité : il émet un événement (`vente.encaissee`) et la comptabilité produit l'écriture (espèces `D 571 / C 701`, crédit `D 411 / C 701`).

**Traçabilité** — toute action sensible émet un événement vers `audit-service`.