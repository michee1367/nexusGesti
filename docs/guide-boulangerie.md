# NexusGesti — Guide Boulangerie

**Produits, production et approvisionnement**
Document utilisateur — console NexusGesti, lots 1 à 3.

---

## Sommaire

1. [Avant de commencer](#1-avant-de-commencer)
2. [Se connecter et retrouver son établissement](#2-se-connecter-et-retrouver-son-établissement)
3. [Vue d'ensemble du menu Boulangerie](#3-vue-densemble-du-menu-boulangerie)
4. [Gammes et catégories](#4-gammes-et-catégories)
5. [Produits](#5-produits)
6. [Fiches recette](#6-fiches-recette)
7. [Plans de production](#7-plans-de-production)
8. [Fournées](#8-fournées)
9. [Approvisionnement : fournisseurs](#9-approvisionnement--fournisseurs)
10. [Besoins d'approvisionnement](#10-besoins-dapprovisionnement)
11. [Commandes d'achat](#11-commandes-dachat)
12. [Réceptions](#12-réceptions)
13. [Scénario complet d'une journée](#13-scénario-complet-dune-journée)
14. [Statuts et règles](#14-statuts-et-règles)
15. [Erreurs fréquentes](#15-erreurs-fréquentes)
16. [Limites connues](#16-limites-connues)
17. [Checklist de prise en main](#17-checklist-de-prise-en-main)

---

## 1. Avant de commencer

Ce guide couvre les écrans Boulangerie livrés dans la console :

- **Produits** : gammes, catégories, catalogue, fiches recette versionnées.
- **Production** : plans de production, besoins matières, fournées, consommations et pertes.
- **Approvisionnement** : fournisseurs, besoins calculés, commandes d'achat, réceptions contrôlées.

Il ne couvre pas encore la **vente/caisse** : l'API du lot 4 existe, mais aucun écran
n'est disponible dans cette version de la console.

### Rôles

| Rôle | Accès |
|---|---|
| `ADMIN` | Consultation et toutes les créations, modifications et transitions |
| `CUSTOMER` | Consultation en lecture seule |

Les boutons d'écriture (création, modification, transition) ne sont pas affichés
pour un compte `CUSTOMER`. Si un bouton est présent mais qu'une action renvoie
**403**, le compte n'a pas le rôle requis.

### Vocabulaire

| Terme | Définition |
|---|---|
| **Produit** | Article vendu ou fabriqué : pain, viennoiserie, pâtisserie… |
| **Gamme** | Grande famille commerciale (pain, viennoiserie…). |
| **Catégorie** | classement plus fin (baguette, croissant…). |
| **Recette** | Formule de fabrication versionnée : ingrédients, rendement, cuisson. |
| **Ingrédient** | Article consommé par une recette (farine, beurre…). |
| **Plan de production** | Programme journalier de fournées par atelier. |
| **Fournée** | Fabrication réelle d'une recette, avec quantités, consommations et pertes. |
| **Rendement** | Quantité réellement produite par rapport à la quantité prévue. |
| **Article (stock)** | Code logique d'une matière ou d'un produit dans le stock. Le stock n'est pas géré dans cette console. |
| **Lot** | Numéro de lot d'une matière reçue ou consommée. |
| **Tiers** | Code du fournisseur dans la comptabilité. Il peut être vide à ce stade. |

> **Important** — une recette **figée** est en lecture seule. Une correction crée
> une **nouvelle version** ; les fournées déjà produites gardent la version utilisée.

---

## 2. Se connecter et retrouver son établissement

### 2.1 Connexion

1. Ouvrir l'adresse de la console fournie par votre administrateur.
2. Sur la page d'accueil, cliquer sur **Connexion**.
3. Saisir l'**adresse email** du compte, puis **Continuer**.
4. La console affiche l'établissement retrouvé. Saisir le **mot de passe**, puis
   **Se connecter**.
5. Le **tableau de bord** s'ouvre.

Si l'email n'est pas reconnu, utiliser le lien de **saisie manuelle de
l'identifiant de l'établissement** et coller l'UUID fourni par l'administrateur.

### 2.2 Vérifier le bon établissement

Le nom de l'établissement est affiché dans l'en-tête. Toutes les données créées
sont rattachées à cet établissement. Si ce n'est pas le bon, se déconnecter puis
se reconnecter avec le compte correspondant.

### 2.3 En cas de session expirée

Le jeton de connexion expire après quelques minutes. Si les actions échouent avec
un message d'authentification, recharger la page, se reconnecter, puis reprendre.

---

## 3. Vue d'ensemble du menu Boulangerie

Le menu latéral contient trois groupes :

| Groupe | Entrées |
|---|---|
| **Boulangerie · Produits** | Produits, Fiches recette, Gammes, Catégories |
| **Boulangerie · Production** | Plans de production, Fournées |
| **Boulangerie · Approvisionnement** | Fournisseurs, Besoins d'appro., Commandes d'achat, Réceptions |

Ordre de mise en place recommandé :

1. Gammes et catégories.
2. Produits.
3. Fiches recette et ingrédients.
4. Fournisseurs.
5. Plans de production.
6. Fournées.
7. Besoins, commandes et réceptions.

---

## 4. Gammes et catégories

### 4.1 Gammes

**Menu : Boulangerie · Produits → Gammes**

Une gamme regroupe des produits de même famille commerciale.

1. Cliquer sur **Nouvelle gamme**.
2. Renseigner :

| Champ | Obligatoire | Exemple |
|---|---|---|
| Code | Oui | `PAIN` |
| Libellé | Oui | Pains |
| Ordre | Non | `1` |
| Actif | Non (coché par défaut) | coché |

3. Valider.

La liste affiche le code, le libellé, l'ordre et le statut **Actif / Inactif**.

### 4.2 Catégories

**Menu : Boulangerie · Produits → Catégories**

Même principe, avec un classement plus fin :

| Champ | Obligatoire | Exemple |
|---|---|---|
| Code | Oui | `BAGUETTE` |
| Libellé | Oui | Baguettes |
| Ordre | Non | `1` |
| Actif | Non (coché par défaut) | coché |

> Les écrans Gammes et Catégories servent uniquement à **créer** et **consulter** :
> il n'y a ni modification ni suppression. Le statut **Actif** est choisi à la
> création et ne peut plus être changé depuis la console. Créer une nouvelle
> entrée avec un autre code si un autre statut ou libellé est nécessaire.

---

## 5. Produits

**Menu : Boulangerie · Produits → Produits**

### 5.1 Créer un produit

1. Cliquer sur **Nouveau produit**.
2. Renseigner les champs :

| Champ | Obligatoire | Exemple | Remarque |
|---|---|---|---|
| Code | Oui | `BAG-TRAD` | Identifiant court et stable. |
| Désignation | Oui | Baguette tradition | Nom affiché. |
| Gamme | Non | `PAIN` | Choix dans la liste. |
| Catégorie | Non | `BAGUETTE` | Choix dans la liste. |
| Unité de vente | Oui | `PIECE` | Voir la liste ci-dessous. |
| Prix de vente HT | Oui (`0` accepté) | `1.20` | Prix hors taxe. |
| Taux TVA (%) | Oui (`0` accepté) | `16` | Taux appliqué au produit. |
| Poids unitaire (g) | Non | `250` | Poids d'une unité. |
| DLC (heures) | Non | `24` | Durée de vie après production. |
| Article (stock) | Non | `ART-BAG-TRAD` | Code logique pour le stock. |
| Code-barres | Non | `…` | Code EAN/UPC si disponible. |
| Allergènes | Non | `GLUTEN` | Plusieurs valeurs possibles. |
| Vendable au détail | Non (coché par défaut) | coché | Autorise la vente à l'unité. |

3. Valider. Le produit apparaît dans la liste.

**Unités de vente disponibles** : `PIECE`, `LOT`, `KG`, `GRAMME`, `TRANCHE`,
`SACHET`, `PLATEAU`.

**Allergènes disponibles** : `GLUTEN`, `LACTOSE`, `OEUF`, `ARACHIDE`, `SOJA`,
`NOIX`, `POISSON`, `CRUSTACE`, `SESAME`, `SULFITE`.

### 5.2 Consulter un produit

Cliquer sur le code du produit ou sur **Détail**. La fiche affiche :

- le code, la gamme, la catégorie et l'article de stock ;
- le prix HT, la TVA, le **prix TTC calculé** ;
- le poids, la DLC, le code-barres et les allergènes ;
- les badges **Actif / Inactif**, **Fabricable / Non fabricable** et
  **Vente au détail** ;
- la liste des **fiches recette** liées au produit.

Un produit devient **Fabricable** lorsqu'au moins une de ses recettes est active.

### 5.3 Activer ou désactiver

Sur la liste ou la fiche, utiliser **Activer** ou **Désactiver**. Un produit
inactif reste consultable mais ne doit plus être utilisé pour de nouvelles
ventes ou productions.

> Il n'y a pas de modification ni de suppression d'un produit dans cette
> version. Pour changer un prix ou une caractéristique, créer un nouveau produit
> avec un nouveau code, puis désactiver l'ancien.

---

## 6. Fiches recette

**Menu : Boulangerie · Produits → Fiches recette**

### 6.1 Créer une recette

1. Cliquer sur **Nouvelle recette**.
2. Renseigner :

| Champ | Obligatoire | Exemple |
|---|---|---|
| Code | Oui | `REC-BAG-TRAD` |
| Produit | Oui | Baguette tradition |
| Libellé | Oui | Baguette tradition 250 g |
| Rendement (quantité) | Oui (`1` par défaut) | `1` |
| Unité de rendement | Oui | `PIECE` |
| Préparation (min) | Oui (`0` accepté) | `30` |
| Cuisson (min) | Oui (`0` accepté) | `18` |
| Température cuisson (°C) | Non | `240` |
| Instructions | Non | Pétrissage, pointage, façonnage… |

3. Valider. La recette est créée en **version 1**, inactive.

### 6.2 Ajouter des ingrédients

Ouvrir la recette, puis dans la carte **Ingrédients** :

| Champ | Obligatoire | Exemple |
|---|---|---|
| Article | Oui | `ART-FARINE-T55` |
| Libellé | Non | Farine T55 |
| Quantité | Oui | `1` |
| Unité | Non | `KG` |
| Taux de perte | Non | `0.02` |
| Ordre | Non | `1` |
| Coût | Non (coché) | coché |

Cliquer sur **Ajouter**. La ligne affiche la quantité nette, la quantité brute
(calculée avec le taux de perte) et si l'ingrédient entre dans le coût.

Le taux de perte est une **fraction** : `0.02` correspond à 2 %.

Répéter pour chaque ingrédient. L'ordre permet de présenter la recette dans
l'ordre de fabrication.

> Les ingrédients ne peuvent plus être ajoutés lorsque la recette est figée.

### 6.3 Calculer les besoins matières

Dans la carte **Besoins matières**, saisir la **quantité produite** (par exemple
`20`), puis **Calculer**. Le tableau affiche pour chaque article :

- la quantité nette ;
- la quantité brute ;
- l'unité.

Le calcul n'est pas enregistré : il est recalculé à la demande.

### 6.4 Calculer le coût de revient

Dans la carte **Coût de revient**, saisir un **prix moyen** pour chaque ingrédient
comptabilisé, puis **Calculer le coût**. Le résultat affiche :

- le **coût matière** ;
- le **coût de revient** ;
- un indicateur **Complet / Incomplet** selon les prix disponibles.

Les prix sont saisis manuellement dans cette version : le stock et les prix
moyens ne sont pas encore connectés à la console boulangerie.

### 6.5 Activer, figer, versionner

- **Activer** : rend la recette utilisable et le produit **Fabricable**.
- **Figer** : verrouille la recette en lecture seule. Les ingrédients et coûts ne
  sont plus modifiables.
- **Nouvelle version** : créer une nouvelle recette avec **le même code**. Le
  système attribue automatiquement la version suivante (`v2`, `v3`…).
- La précédente version reste consultable et les fournées passées conservent
  leur version d'origine.

> Ne pas figer une recette avant d'avoir vérifié ses ingrédients et son
> rendement. Une recette figée ne se corrige pas : il faut créer une nouvelle
> version.

---

## 7. Plans de production

**Menu : Boulangerie · Production → Plans de production**

Le plan de production organise la journée : quelles recettes, quelles quantités,
sur quel four, pour quel atelier.

### 7.1 Créer un plan

1. Cliquer sur **Nouveau plan**.
2. Renseigner la **date** (obligatoire) et l'**atelier** (facultatif).
3. Ajouter **au moins une ligne** :

| Champ | Obligatoire | Exemple |
|---|---|---|
| Recette | Oui | `REC-BAG-TRAD` |
| Produit | Non | Baguette tradition |
| Four | Non | `FOUR-1` |
| Quantité prévue | Oui (> 0) | `120` |

4. Utiliser **Ajouter une ligne** pour compléter, puis **Créer le plan**.

Un plan est créé au statut **BROUILLON**.

### 7.2 Consulter et faire avancer un plan

Ouvrir le plan via **Détail**. L'écran affiche les lignes et les **besoins
matières agrégés** (quantités nettes et brutes par article).

Cycle de vie :

```text
BROUILLON → VALIDE → LANCE → CLOTURE
```

| Action | Condition | Effet |
|---|---|---|
| **Valider** | Statut `BROUILLON` | Le plan est confirmé pour la production. |
| **Lancer** | Statut `VALIDE` | La production démarre. |
| **Clôturer** | Statut `LANCE` | La journée de production est terminée. |

Un plan ne se modifie ni ne s'annule pas dans cette version. Vérifier les lignes
avant de le valider.

---

## 8. Fournées

**Menu : Boulangerie · Production → Fournées**

Une fournée est la fabrication réelle d'une recette. Elle porte les quantités,
les consommations de matières, les pertes et le rendement.

### 8.1 Créer une fournée

1. Cliquer sur **Nouvelle fournée**.
2. Renseigner :

| Champ | Obligatoire | Exemple |
|---|---|---|
| Recette | Oui | `REC-BAG-TRAD` |
| Version recette | Non (`1` par défaut) | `1` |
| Produit | Non | Baguette tradition |
| Four | Non | `FOUR-1` |
| Quantité prévue | Non (`1` par défaut) | `120` |

3. Cliquer sur **Créer la fournée**.

La fournée est créée au statut **PLANIFIEE**.

### 8.2 Cycle de vie

```text
PLANIFIEE → EN_COURS → CUITE → VALIDEE
                    ↘ ANNULEE (avec motif)
```

| Action | Condition | Effet |
|---|---|---|
| **Lancer** | `PLANIFIEE` | Passe en `EN_COURS`, enregistre le début. |
| **Enregistrer le rendu** | `EN_COURS` | Saisit la quantité réellement produite (> 0). |
| **Cuire** | `EN_COURS` | Passe en `CUITE`. |
| **Valider** | `CUITE` | Passe en `VALIDEE`, enregistre la fin. |
| **Annuler** | Ni `VALIDEE`, ni `ANNULEE` | Passe en `ANNULEE` ; un **motif est obligatoire**. |

Une fournée validée ou annulée ne peut plus être modifiée.

### 8.3 Déclarer les consommations

Dans la carte **Consommations matières (par lot)** :

| Champ | Obligatoire | Exemple |
|---|---|---|
| Article | Oui | `ART-FARINE-T55` |
| Lot | Non | `LOT-FAR-2026-01` |
| Quantité théorique | Non | `60` |
| Quantité réelle | Oui | `61.2` |
| Prix moyen | Non | `0.85` |

Le tableau affiche l'écart entre théorique et réel. L'ajout d'une consommation
est désactivé dès que la fournée est annulée ou validée.

### 8.4 Déclarer les pertes

Dans la carte **Pertes valorisées** :

| Champ | Obligatoire | Exemple |
|---|---|---|
| Article | Oui | `PATE-FERMENTEE` |
| Quantité | Oui (> 0) | `2` |
| Type | Oui | `PATE_PERDUE` |
| Motif | Non | Fournée interrompue |
| Prix moyen | Non | `0.50` |

**Types de perte** : `PATE_PERDUE`, `CUISSON_RATEE`, `CASSE`,
`INVENDU_DESTRUCTION`, `DEMARQUE`, `ECHANTILLON`.

La **valorisation** est calculée à partir de la quantité et du prix moyen fourni.

### 8.5 Lire les indicateurs

- **Rendement** : pourcentage entre quantité réalisée et quantité prévue.
- **Écart matière** : somme des écarts entre quantités théoriques et réelles.
- Les badges signalent une fournée annulée et son motif.

---

## 9. Approvisionnement : fournisseurs

**Menu : Boulangerie · Approvisionnement → Fournisseurs**

### 9.1 Créer ou modifier un fournisseur

1. Cliquer sur **Nouveau fournisseur**.
2. Renseigner :

| Champ | Obligatoire | Exemple |
|---|---|---|
| Code | Oui | `FOURN-MOULIN` |
| Raison sociale | Oui | Moulins du Congo |
| Tiers (code) | Non | `TIER-0001` |
| Délai de livraison (jours) | Non (`0` par défaut) | `3` |
| Catégorie de produits | Non | Farines et céréales |
| Conditions de règlement | Non | 30 jours fin de mois |
| Montant mini commande | Non | `500` |
| Actif | Non (coché par défaut) | coché |

3. Valider.

Le fournisseur peut être **modifié** depuis la liste, **désactivé** ou
**réactivé** depuis sa fiche.

### 9.2 Lire l'évaluation

La fiche fournisseur affiche :

- la **fiabilité** (`Fiable` ou `À surveiller`) ;
- le **délai moyen constaté** ;
- le nombre de **commandes** ;
- le nombre de **réceptions honorées / refusées**.

L'évaluation est calculée à partir de l'historique ; elle n'est pas saisie.

---

## 10. Besoins d'approvisionnement

**Menu : Boulangerie · Approvisionnement → Besoins d'appro.**

Cet écran calcule les quantités à recommander. **Aucune donnée n'est stockée** :
les résultats sont recalculés à la demande. Le calcul est accessible aux comptes
`ADMIN` et `CUSTOMER` car il ne persiste rien.

### 10.1 Saisir les articles suivis

Pour chaque article :

| Champ | Obligatoire | Exemple |
|---|---|---|
| Article | Oui | `ART-FARINE-001` |
| Libellé | Non | Farine T55 |
| Disponible | Non (`0`) | `40` |
| Seuil | Non (`0`) | `100` |
| Consommation prévue | Non (`0`) | `200` |
| Délai réappro (j) | Non | `5` |

Utiliser **Ajouter un article** pour compléter la liste.

### 10.2 Calculer

- **Calculer les besoins** affiche pour chaque article la **quantité suggérée** et
  l'**urgence**.
- **Alertes sous-seuil** affiche uniquement les articles en `RUPTURE` ou
  `HAUTE` (disponible nul/négatif ou inférieur au seuil).

Formule appliquée :

```text
quantité suggérée = max(0, seuil + consommation prévue − disponible)
```

| Urgence | Signification |
|---|---|
| `RUPTURE` | Disponible nul ou négatif. |
| `HAUTE` | Disponible inférieur au seuil. |
| `NORMALE` | Disponible suffisant. |

Ces résultats aident à préparer une commande ; ils ne créent pas la commande
automatiquement.

---

## 11. Commandes d'achat

**Menu : Boulangerie · Approvisionnement → Commandes d'achat**

### 11.1 Créer une commande

1. Cliquer sur **Nouvelle commande**.
2. Renseigner :

| Champ | Obligatoire | Exemple |
|---|---|---|
| Fournisseur | Oui | Moulins du Congo |
| Date | Oui | date du jour |
| Livraison prévue | Non | date souhaitée |
| Article | Oui | `ART-FARINE-001` |
| Désignation | Oui (dès la création) | Farine T55 |
| Quantité | Oui (> 0) | `100` |
| Prix unitaire HT | Non (`0`) | `0.80` |
| TVA (%) | Non (`16`) | `16` |

3. Ajouter les lignes nécessaires, puis **Créer la commande**.

La commande est créée en **BROUILLON** avec un numéro `CMD-…`. La désignation
doit être renseignée dès la création du brouillon ; elle est également contrôlée
lors de la **validation**.

### 11.2 Cycle de vie

```text
BROUILLON → VALIDEE → ENVOYEE → PARTIELLE / RECEPTIONNEE → FACTUREE
                            ↘ ANNULEE (avec motif)
```

| Action | Condition | Effet |
|---|---|---|
| **Valider** | `BROUILLON` | La commande est confirmée. |
| **Envoyer** | `VALIDEE` | La commande est transmise au fournisseur. |
| **Annuler** | Hors `RECEPTIONNEE`, `FACTUREE`, `ANNULEE` | Annulation avec **motif obligatoire**. |
| **Réceptions** | `ENVOYEE` ou `PARTIELLE` | Les réceptions font évoluer la commande. |

`PARTIELLE`, `RECEPTIONNEE` et `FACTUREE` sont pilotés par les réceptions et la
facturation. La facture fournisseur relève de la phase finance.

### 11.3 Lire la fiche commande

La fiche affiche :

- le statut, les dates, les montants **HT / TVA / TTC** et le **reste à recevoir** ;
- les lignes avec quantité commandée, quantité reçue, reste, prix unitaire et
  montant HT ;
- le motif d'annulation s'il existe ;
- le **rapprochement commande ↔ réceptions**.

### 11.4 Rapprochement

Cliquer sur **Vérifier** dans la carte de rapprochement. Le résultat indique :

- **Concordant** ou **Écart détecté** ;
- le montant commandé TTC et le montant des réceptions TTC ;
- les écarts par article : quantité commandée, quantité reçue, prix commande,
  prix d'achat et écart de prix.

Un écart de prix ou de quantité n'empêche pas la réception : il est signalé pour
analyse.

---

## 12. Réceptions

**Menu : Boulangerie · Approvisionnement → Réceptions**

### 12.1 Créer une réception

1. Cliquer sur **Nouvelle réception**.
2. Renseigner :

| Champ | Obligatoire | Exemple |
|---|---|---|
| Commande | Oui | `CMD-00001` |
| Date | Oui | date de livraison |
| Bon de livraison | Non | `BL-2026-001` |
| Article | Oui | `ART-FARINE-001` |
| Quantité reçue | Oui (> 0) | `100` |
| Prix d'achat | Non (`0`) | `0.82` |
| Lot | Non | `LOT-FAR-001` |
| Conforme | Non (coché) | coché |
| Motif non-conformité | Si non conforme | DLC trop courte |

3. Créer la réception. Elle est au statut **ATTENDUE**.

> La commande doit être au statut **ENVOYEE** ou **PARTIELLE** ; une commande
> encore en brouillon ou seulement validée ne peut pas être réceptionnée.

> Chaque ligne non conforme exige un **motif**. Sans motif, la création est
> refusée. La quantité reçue ne peut pas dépasser le **reste à recevoir** sur la
> commande.

### 12.2 Contrôler, accepter ou refuser

| Action | Condition | Effet |
|---|---|---|
| **Contrôler** | `ATTENDUE` | Passe en `EN_CONTROLE`. |
| **Accepter** | `EN_CONTROLE` et toutes les lignes conformes | Passe en `ACCEPTEE` ou `PARTIELLE`. |
| **Refuser** | `EN_CONTROLE` | Passe en `REFUSEE` ; **motif obligatoire**. |

Le bouton **Refuser** apparaît aussi au statut `ATTENDUE`, mais l'action n'est
acceptée qu'après **Contrôler** (sinon **409**).

Si une ligne est non conforme, l'acceptation est refusée (**409**). Il faut
d'abord contrôler puis refuser la réception, et enfin créer une nouvelle
réception conforme. Une réception ne se modifie pas dans cette version.

### 12.3 Effets sur la commande

Une réception acceptée met à jour :

- la quantité reçue et le **reste à recevoir** de la commande ;
- le statut de la commande : `PARTIELLE` ou `RECEPTIONNEE` ;
- les entrées de stock, sous forme d'**intentions de mouvement** transmises aux
  événements. Le stock lui-même n'est pas encore affiché dans cette console.

### 12.4 Lire la fiche réception

La fiche affiche le numéro, la commande liée, la date, le bon de livraison, le
statut, la conformité globale, le motif de refus et les lignes reçues avec lot,
péremption, conformité et motif de non-conformité.

---

## 13. Scénario complet d'une journée

Cet exemple reprend les valeurs d'une boulangerie fictive.

### Étape 1 — Référentiels

1. Créer la gamme `PAIN` / « Pains ».
2. Créer la catégorie `BAGUETTE` / « Baguettes ».

### Étape 2 — Produit

Créer le produit :

- code `BAG-TRAD`, désignation « Baguette tradition » ;
- gamme `PAIN`, catégorie `BAGUETTE`, unité `PIECE` ;
- prix HT `1.20`, TVA `16`, poids `250`, DLC `24` ;
- allergène `GLUTEN`, vendable au détail.

### Étape 3 — Recette

Créer `REC-BAG-TRAD` pour le produit `BAG-TRAD`, rendement `1 PIECE`, cuisson
`18 min` à `240 °C`.

Ajouter :

| Article | Libellé | Quantité | Unité | Perte |
|---|---|---:|---|---:|
| `ART-FARINE-T55` | Farine T55 | 1 | KG | 0.02 |
| `ART-EAU` | Eau | 0.65 | L | 0 |
| `ART-SEL` | Sel | 0.018 | KG | 0 |
| `ART-LEVURE` | Levure | 0.01 | KG | 0 |

Calculer les besoins pour `120` pièces, puis calculer le coût avec les prix
moyens. **Activer** la recette, puis la **figer** après validation.

### Étape 4 — Fournisseur

Créer `FOURN-MOULIN` / « Moulins du Congo », délai `3` jours, catégorie
« Farines et céréales », minimum `500`.

### Étape 5 — Plan

Créer un plan pour aujourd'hui, atelier `ATELIER-1`, avec :

- recette `REC-BAG-TRAD`, produit `BAG-TRAD`, four `FOUR-1`, quantité `120`.

Valider puis lancer le plan.

### Étape 6 — Fournée

Créer une fournée pour `REC-BAG-TRAD`, version `1`, four `FOUR-1`, quantité
prévue `120`.

1. **Lancer** la fournée.
2. Déclarer la consommation : farine `120` théorique / `122.4` réelle, lot
   `LOT-FAR-2026-01`.
3. Déclarer une perte de `2` unités de `PATE-FERMENTEE`, type `PATE_PERDUE`.
4. Enregistrer le rendu : `118`.
5. **Cuire**, puis **Valider**.

### Étape 7 — Approvisionnement

1. Dans **Besoins**, saisir la farine : disponible `40`, seuil `100`,
   consommation prévue `200`. Le calcul propose `260` en urgence `HAUTE`.
2. Créer une commande pour `FOURN-MOULIN`, article `ART-FARINE-T55`, quantité
   `100`, prix `0.80`, TVA `16`.
3. **Valider** puis **Envoyer** la commande.
4. À la livraison, créer une réception liée à la commande, lot `LOT-FAR-001`,
   quantité `100`, prix `0.82`, conforme.
5. **Contrôler**, puis **Accepter**.
6. Ouvrir la commande et **Vérifier** le rapprochement.

---

## 14. Statuts et règles

### Produits et recettes

| Objet | Statuts | Règle |
|---|---|---|
| Produit | Actif / Inactif | Un produit actif peut être utilisé. |
| Produit | Fabricable / Non fabricable | Devient fabricable dès qu'une recette est active. |
| Recette | Active / Inactive | Utiliser de préférence une recette active pour la production ; la liste de choix n'est pas encore filtrée. |
| Recette | Modifiable / Figée | Une recette figée est en lecture seule. |

### Production

| Objet | Statuts |
|---|---|
| Plan | `BROUILLON`, `VALIDE`, `LANCE`, `CLOTURE` |
| Fournée | `PLANIFIEE`, `EN_COURS`, `CUITE`, `VALIDEE`, `ANNULEE` |

### Approvisionnement

| Objet | Statuts |
|---|---|
| Commande d'achat | `BROUILLON`, `VALIDEE`, `ENVOYEE`, `PARTIELLE`, `RECEPTIONNEE`, `FACTUREE`, `ANNULEE` |
| Réception | `ATTENDUE`, `EN_CONTROLE`, `ACCEPTEE`, `REFUSEE`, `PARTIELLE` |
| Urgence besoin | `RUPTURE`, `HAUTE`, `NORMALE` |

### Règles à retenir

- Une fournée validée ou annulée est figée.
- Une réception non conforme ne peut pas être acceptée.
- Un refus de réception et une annulation de commande ou de fournée exigent un
  motif.
- Une recette figée ne se corrige que par une nouvelle version.
- Les calculs de besoins et de coût ne sont pas stockés.
- Les mouvements de stock sont émis comme intentions vers le domaine finance,
  mais ne sont pas affichés dans cette console.

---

## 15. Erreurs fréquentes

| Message / symptôme | Cause probable | Solution |
|---|---|---|
| **403 Accès refusé** | Compte `CUSTOMER` sur une action d'écriture | Utiliser un compte `ADMIN`. |
| **400 / 401 au chargement** | Session expirée ou établissement non sélectionné | Recharger, se reconnecter, vérifier l'établissement. |
| **409 Transition invalide** | Action non permise depuis le statut courant | Recharger la fiche et suivre l'ordre des statuts. |
| **409 Réception non conforme** | Une ligne est marquée non conforme | Contrôler, refuser, puis créer une réception conforme. |
| **Motif obligatoire** | Annulation ou refus sans motif | Saisir un motif explicite. |
| **Désignation obligatoire** | Ligne de commande sans désignation | Renseigner la désignation dès la création de la ligne. |
| **400 Quantité supérieure au reste** | Réception plus grande que le reste à recevoir | Ajuster la quantité ou vérifier les réceptions précédentes. |
| **Quantité invalide** | Quantité nulle ou négative | Saisir une quantité strictement positive. |
| **Recette non modifiable** | Recette figée | Créer une nouvelle version avec le même code. |
| **Produit non fabricable** | Aucune recette active | Activer une recette liée au produit. |
| **Aucun besoin calculé** | Aucun article saisi ou quantités à zéro | Renseigner au moins un code article. |
| **Rapprochement non concordant** | Écart de quantité ou de prix | Analyser les lignes signalées ; la réception reste possible. |
| **Service indisponible (503)** | Événement non publié, Kafka indisponible | Prévenir l'administrateur ; ne pas recréer la donnée en boucle. |
| **Données absentes** | Mauvais établissement | Se reconnecter avec le bon compte. |

---

## 16. Limites connues

Cette version couvre les **lots 1 à 3** de la boulangerie.

- **Pas d'écran de vente/caisse** : l'API du lot 4 existe, mais le menu ne
  l'expose pas encore.
- **Pas de stock visible** : les entrées, sorties, pertes et consommations sont
  émises comme intentions vers le domaine finance ; aucun écran de stock n'est livré.
- **Pas de facture fournisseur** : la facturation relève de la phase finance.
- **Pas de modification** pour les produits, plans, fournées, commandes et
  réceptions. Les fournisseurs, gammes, catégories et recettes ont leurs propres
  règles de création ou de cycle de vie.
- **Pas de suppression** : désactiver ou versionner plutôt que supprimer.
- **Prix de revient manuel** : les prix moyens ne sont pas encore lus depuis le
  stock.
- **Besoins non persistés** : les calculs sont à la demande.
- **Clients et personnel boulangerie** ne sont pas encore livrés.
- **Hôpital et finance** ne sont pas encore implémentés.

---

## 17. Checklist de prise en main

- [ ] Gammes créées.
- [ ] Catégories créées.
- [ ] Produits créés avec prix, TVA, allergènes et unité de vente.
- [ ] Recettes créées avec rendement, cuisson et ingrédients.
- [ ] Besoins matières calculés et cohérents.
- [ ] Coût de revient vérifié.
- [ ] Recette activée, puis figée.
- [ ] Fournisseurs créés avec délai et minimum de commande.
- [ ] Plan de production créé, validé et lancé.
- [ ] Fournée lancée, consommations et pertes déclarées, rendu enregistré,
      cuisson et validation effectuées.
- [ ] Besoins d'approvisionnement calculés.
- [ ] Commande créée, validée et envoyée.
- [ ] Réception contrôlée et acceptée.
- [ ] Rapprochement commande/réceptions vérifié.

---

*NexusGesti — Guide Boulangerie. Document autonome destiné aux utilisateurs de
la console. Les intitulés, champs et règles correspondent aux écrans livrés pour
les lots 1 à 3.*
