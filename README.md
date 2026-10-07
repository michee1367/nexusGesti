# NexusGesti

Plateforme de gestion SaaS multi-tenant pour **établissements scolaires**, **hôpitaux** et **cabinets de gestion financière OHADA**.

## Modélisation

Toute la conception est en **PlantUML** (`.puml`) : classes, cas d'utilisation, séquences.

```
docs/
├── diagrammes/
│   ├── socle/        6 modèles + 6 cas d'utilisation
│   ├── pedagogie/    7 modèles + 7 cas d'utilisation
│   ├── hopital/      6 modèles + 6 cas d'utilisation
│   ├── finance/      6 modèles + 6 cas d'utilisation
│   ├── sequences/    6 diagrammes de séquence des flux critiques
│   └── rendu/        SVG générés
└── architecture.md
```

| Module | Services | Diagrammes |
|---|---|---|
| Socle | `tenant`, `identity`, `auth`, `notification`, `reporting`, `audit` | 12 |
| Pédagogie | `structure`, `people`, `enrollment`, `attendance`, `assessment`, `exam`, `fees` | 14 |
| Hôpital | `patient`, `rdv`, `consultation`, `pharmacie`, `hospitalisation`, `plateau-technique` | 12 |
| Finance | `compta-core`, `facturation`, `stock`, `inventaire`, `immobilisation`, `etats` | 12 |
| Séquences | paiement, délibération, rappel de lot, clôture d'exercice, séjour, acte → facture | 6 |
| **Total** | **25 services** | **56** |

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

**Traçabilité** — toute action sensible émet un événement vers `audit-service`.