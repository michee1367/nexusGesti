# Architecture NexusGesti

## Vue d'ensemble

```mermaid
flowchart TB
  subgraph CLIENTS[Clients]
    WEB[Web · Next.js]
    MOB[Mobile]
    PORTAL[Portail parent]
  end

  GW[API Gateway]

  subgraph SOCLE[Socle]
    TEN[tenant-service]
    IDN[identity-service]
    AUTH[auth-service]
    NOTIF[notification-service]
    REP[reporting-service]
    AUD[audit-service]
  end

  subgraph PED[Academique]
    STR[structure]
    PPL[people]
    ENR[enrollment]
    ATT[attendance]
    ASM[assessment]
    EXM[exam]
    FEE[fees]
  end

  subgraph HOP[Hopital]
    PAT[patient]
    RDV[rdv]
    CON[consultation]
    PHA[pharmacie]
    HSP[hospitalisation]
    PLT[plateau-technique]
  end

  subgraph FIN[Finance OHADA]
    CORE[compta-core]
    FACT[facturation]
    STK[stock]
    INV[inventaire]
    IMM[immobilisation]
    STA[etats]
  end

  WEB --> GW
  MOB --> GW
  PORTAL --> GW
  GW --> SOCLE
  GW --> PED
  GW --> HOP
  GW --> FIN
```

## Flux : publication d'une session d'examen

```mermaid
sequenceDiagram
  participant CEN as Censeur
  participant EXM as exam-service
  participant ENR as enrollment-service
  participant ASM as assessment-service
  participant FEE as fees-service
  participant NT as notification-service

  CEN->>EXM: publier la session
  EXM->>ENR: candidats éligibles
  ENR-->>EXM: enrôlements + dispenses
  EXM->>NT: convocations
  CEN->>EXM: ouvrir la délibération
  EXM->>ASM: moyennes et mentions
  ASM-->>EXM: résultats consolidés
  EXM->>FEE: éligibilité aux réductions
  FEE-->>EXM: réductions applicables
  EXM->>NT: résultats officiels
```

## Flux : encaissement de frais

```mermaid
sequenceDiagram
  participant PAR as Parent
  participant FEE as fees-service
  participant ENR as enrollment-service
  participant BAN as Banque / Mobile Money
  participant CORE as compta-core
  participant NT as notification-service

  PAR->>FEE: POST paiements (Idempotency-Key)
  FEE->>ENR: vérifier inscription et blocage
  ENR-->>FEE: valide
  FEE->>BAN: mobile money / virement
  BAN-->>FEE: confirmation
  FEE->>CORE: événement comptabilisation
  CORE-->>FEE: accusé
  FEE->>NT: coupon généré
  FEE-->>PAR: reçu + coupon (QR, SHA-256)
```

## Flux : clôture d'exercice comptable

```mermaid
flowchart LR
  A[Clôture caisses] --> B[Inventaire stocks]
  B --> C[Valorisation]
  C --> D[Taux moyen pondéré]
  D --> E[Arrêt comptable]
  E --> F[Édition des états]
  F --> G[Certification]
  G --> H[Clôture + ouverture]
```

Chaque étape est **bloquante** : on ne certifie pas un bilan sans inventaire, on ne clôt pas sans états certifiés.

## Flux : parcours d'hospitalisation

```mermaid
flowchart LR
  A[Motif HOSPITALISE] --> B[Réservation de lit]
  B --> C{Trois contrôles}
  C -->|lit libre| D[Admission]
  C -->|type incompatible| E[Arbitrage chef de service]
  C -->|BlocAdmission| E
  E --> D
  D --> F[Prescriptions et soins]
  F --> G[Transferts internes]
  G --> H[Sortie + motif]
  H --> I[Compte rendu signé]
  I --> J{Clôture contrôlée}
  J -->|sortie refusée| K[Facturation suspendue]
  J -->|sortie + CR| L[Valorisation]
  L --> M[grid version figée]
```

Le périmètre du service est **le séjour** : lit, mouvements internes, sortie, valorisation. Les observations quotidiennes et les transmissions ne sont pas dans ce périmètre.

## Flux : d'un examen à la facture

```mermaid
flowchart LR
  A[LignePrescription ANALYSE/IMAGERIE] --> B[DemandeExamen]
  B --> C{Labo ou imagerie}
  C --> D[Prélèvement + lot tracé]
  D --> E[Saisie résultat]
  E --> F{Résultat critique ?}
  F -->|oui| G[Notification immédiate]
  F -->|non| H[Validation biologique / radiologique]
  G --> H
  H -->|non validé| I[Aucun acte facturable]
  H -->|validé| J[ActeFacturable]
  J --> K[scinder part assure / patient]
  K --> L[(événement hopital.acte.facturable)]
  L --> M[fin-compta-core]
  M --> N[D 411 / C 706]
```

**Le module hospitalier n'encaisse rien.** Il émet un acte facturable ; la comptabilise et le recouvrement restent dans `fin-compta-core` et `fin-facturation-service`. Même mécanisme que le paiement de frais scolaires, par topic d'événements.

Deux garde-fous structurants :

- **Rien ne sort avant validation.** Un résultat saisi mais non validé biologiquement n'est pas lisible du prescripteur, ne figure sur aucun compte rendu et ne déclenche aucune facture.
- **Pas d'examen sans équipement calibré.** Un scanner dont la date de calibration est dépassée ne produit aucun acte facturable.

## Multi-tenancy

Deux modes cohabitent :

| Mode | Isolation | Cible |
|---|---|---|
| **SaaS mutualisé** | `etablissement_id` sur chaque table, RLS PostgreSQL | petits établissements |
| **On-premise dédié** | base dédiée par instance | grands groupes, données sensibles |

Le `tenant-service` porte le `Etablissement`, son `ProfilEtablissement` et ses référentiels. Un profil **PRIMAIRE**, **SECONDAIRE** ou **UNIVERSITAIRE** active des règles du cœur pédagogique sans le dupliquer.

## Versionnement des règles métier

Aucune valeur en dur dans les services. Les règles sont des **données** :

- `GrilleNotation` — coefficients, cascade de pondération, seuils de mention, règles de rattrapage
- `GrilleTarifaire` — montants, plafonds, remises, tranches
- `RegleAssiduite` — seuils d'absence et de retard
- `GrilleStock` — durée de quarantaine, stratégie de rotation des lots, seuils d'alerte, tolérance d'écart d'inventaire, délai de réapprovisionnement
- `GrilleConformite` — durées de rétention, conservation des résultats et des images, chiffrement au repos, traçabilité des accès, délai de déclaration d'un résultat critique

Quatre sont définies dans `socle/modele-tenant-service.puml` ; `GrilleTarifaire` appartient à
`pedagogie/modele-academic-fees-service.puml`. Toutes portent le même cycle `activer()` / `figer()`.

Une correction crée une **nouvelle version** : les pièces déjà émises conservent leur version d'origine. Une ligne de tarification hospitalière fige sa version de grille à l'admission, une ligne de prescription fige celle de la demande, un lot fige celle de la règle de rotation qui lui a été appliquée.

## Cascade de pondération

```mermaid
flowchart LR
  E[Evaluation] --> C[CoursECUE]
  C --> U[UE]
  U --> P[Periode]
  P --> A[Annee]
  A --> Y[Cycle]
```

La cascade active dépend du profil :

- **PRIMAIRE / SECONDAIRE** — `matière → période → année → cycle`
- **UNIVERSITAIRE** — `coursECUE → UE → semestre → cycle`

Les coefficients sont résolus par spécificité décroissante. Le détail de pondération est opposable : il est imprimé sur le bulletin.

## Conformité OHADA / SYSCOHADA (RDC)

- Devise fonctionnelle **USD**, devise légale d'affichage **CDF**
- Taux moyen pondéré figé à la clôture : `(achats + ventes) / volumes`
- Plan de comptes par classes 1 à 7
- États SYSCOHADA : bilan, compte de résultat, tableau de financement, état de trésorerie, charges par nature
- États **certifiés** (signature numérique, non régénérables), seuls acceptés par l'administration fiscale
- **FEC** exportable à la demande
- TVA : 0 % / 5 % / 10 % / 16 %, calculée par ligne

## Traçabilité

Toute action sensible (consultation de dossier médical, lecture de résultat, encaissement, publication de bulletin, cession d'immobilisation) émet un événement vers `audit-service`, qui journalise **acteur, motif, horodatage et périmètre consulté**.

`GrilleConformite.accesTracable` impose cette émission à chaque lecture de donnée de santé. Ce drapeau ne se désactive pas sur un profil `HOSPITAL`.

## Pile technique

| Couche | Choix |
|---|---|
| Backend | Java 21, Spring Boot 3.4, Maven multi-modules, architecture hexagonale |
| Persistance | PostgreSQL 16, Flyway, RLS pour l'isolation tenant |
| Sécurité | Keycloak (OIDC), JWT, Refresh Token rotation |
| Événements | Apache Kafka |
| Cache | Redis |
| Frontend | Next.js 16, React 19, TypeScript, Tailwind v4 |
| Diagrammes | PlantUML, moteur de rendu smetana |