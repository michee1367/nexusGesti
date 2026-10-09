# Guide utilisateur — NexusGesti

Guide pas-à-pas de la plateforme, de l'ouverture de session jusqu'aux écrans livrés
aujourd'hui (**socle** + **pédagogie · structure & personnes**).

> Ce document est écrit pour un utilisateur non technique. Chaque terme métier est défini
> dans le **Glossaire** (§4). Les identifiants sont donnés au §3.

---

## 1. Qu'est-ce que NexusGesti ?

NexusGesti est une plateforme de gestion d'établissements scolaires, **multi-établissements**
(« multi-tenant »). Une seule installation héberge plusieurs établissements dont les données
sont totalement isolées les unes des autres.

Elle est organisée en **domaines** :

| Domaine | Contenu | État |
|---|---|---|
| **Socle** | Réglages de l'établissement, années scolaires, grilles, référentiels, utilisateurs, audit, rapports | Disponible |
| **Pédagogie** | Structure (classes, salles, emplois du temps…) et Personnes (élèves, parents, enseignants, personnel) | Disponible |
| **Hôpital** | Patients, rendez-vous, consultations, pharmacie | À venir |
| **Finance** | Comptabilité OHADA / SYSCOHADA (RDC) | À venir |

Il existe **deux profils d'utilisateur** :

- **ADMIN** : peut tout voir **et** tout modifier.
- **CUSTOMER** : peut **consulter** les données (lecture seule).

---

## 2. Démarrage (technique, une seule fois)

Depuis la racine du projet, deux scripts démarrent tout :

```bash
# 1) Backend (base de données, cache, messagerie, services) — laisser tourner
./scripts/backend-up.sh        # git-bash / Linux / macOS
./scripts/backend-up.ps1       # PowerShell

# 2) Console web — dans un second terminal
./scripts/front-up.sh          # ou front-up.ps1
```

Au premier lancement, le backend compile et démarre **9 services** + l'infrastructure
(base PostgreSQL, Redis, Kafka, Keycloak, Prometheus, Grafana, Vault). Comptez quelques minutes.

Quand les scripts affichent « UP », ouvrez le navigateur.

---

## 3. Accès et identifiants (credentials)

| Quoi | Adresse | Identifiant | Mot de passe |
|---|---|---|---|
| **Console NexusGesti** | http://localhost:3001 | `demo.admin@nexusgesti.com` | `Admin_pass123!` |
| Console — profil lecture seule | http://localhost:3001 | `demo.customer@nexusgesti.com` | `demo_secret_customer` |
| Keycloak (administration des comptes) | http://localhost:8180 | `admin` | `admin_secret` |
| Grafana (métriques) | http://localhost:3000 | `admin` | `admin` |
| PostgreSQL (base de données) | `localhost:5433` — base `nexusgesti` | `nexusgesti` | `nexusgesti_secret` |
| Redis (cache) | `localhost:6379` | — | `redis_secret` |
| Vault (secrets) | http://localhost:8200 | jeton `dev-root-token` | — |

> **À retenir :** pour utiliser la console au quotidien, retenez uniquement les deux premières
> lignes. Le port **3001** est celui de la console (le port 3000 est occupé par Grafana).

> ⚠️ **Identifiants de démonstration.** Ils sont réservés au développement local. En production,
> ils sont remplacés par des comptes réels gérés dans Keycloak.

---

## 4. Glossaire — définitions des termes

**Établissement (tenant)** : l'école ou l'université. Chaque établissement est un « locataire »
isolé de la plateforme. On change d'établissement via le **sélecteur de tenant** en haut de
l'écran (voir §6).

**Profil d'établissement** : type d'école — `PRIMAIRE`, `SECONDAIRE` ou `UNIVERSITAIRE`. Il
active ou non certains modules (ECTS, délibération par UE…).

**Année scolaire** : période de référence (ex. 2025-2026). Elle contient des **périodes**
(trimestres, semestres…). Une année peut être *prévue*, *ouverte* puis *clôturée*.

**Grille** : jeu de règles versionnées (notes, stock, conformité) appliquées à un
établissement. Une grille s'*active*, se *fige* (le figement interdit toute modification) et
se *duplique*.

**Référentiel** : liste de valeurs de référence (types de frais, motifs d'absence, mentions…).
Un établissement possède 17 référentiels, créés automatiquement à sa création.

**Niveau** : étape du parcours (1re, 2e, 6e, L1, M1…). Chaque niveau appartient à un **cycle**
(primaire, secondaire, supérieur…).

**Classe** : groupe d'élèves suivant un enseignement, rattaché à un **niveau** et une **année**.
Elle peut être *ouverte*, *en cours* ou *clôturée*.

**Salle** : espace physique (salle de cours, laboratoire, amphithéâtre…).

**Créneau** : tranche horaire élémentaire (jour + heure de début + heure de fin).

**Régime horaire** : organisation hebdomadaire des créneaux (nombre de créneaux par jour, pauses…).

**Emploi du temps** : planning hebdomadaire d'une **classe**. Il est composé de **plages**
(un cours dans un créneau, une salle, un enseignant). Le système **détecte les conflits**
(même salle double, même enseignant double…) avant de *publier* l'emploi du temps.

**Matière** : discipline enseignée (mathématiques, histoire…).

**Cours / ECUE** : unité concrète de cours (une matière pour une classe donnée).

**UE (Unité d'Enseignement)** : regroupement de cours dans le cursus universitaire, avec
crédits et coefficients.

**Promotion** : cohorte d'étudiants rattachée à un programme et un semestre.

**Élève** : l'élève inscrit. Son **matricule** l'identifie. Il possède :
- un **dossier** (pièces justificatives : photo, acte de naissance…) ;
- des **inscriptions** (par année et par classe) ;
- des **congés** ;
- des **sanctions** disciplinaires ;
- des **liens de parenté** avec des **parents/tuteurs**.

**Enseignant** : membre du corps enseignant. Il possède des **pièces d'identité** et des
**affectations** (classe + matière). Une affectation peut être **titulaire**.

**Personnel** : regroupe les **services** de l'établissement (direction, scolarité…) et les
**agents** administratifs.

**Rôle** : ensemble de permissions (ADMIN, CUSTOMER…). **Permission** : droit élémentaire
(verbe + ressource).

**Audit** : journal horodaté des actions importantes (création d'élève, envoi de notification…).

**Rapport** : document généré (PDF, CSV, JSON) à partir des données.

---

## 5. Se connecter

1. Ouvrez **http://localhost:3001**.
2. Saisissez l'adresse e-mail et le mot de passe (voir §3).
3. Cliquez sur **Se connecter**.
4. Vous arrivez sur le **Tableau de bord**.

En haut à droite, l'en-tête affiche votre nom, votre établissement courant et un bouton de
déconnexion. Le menu de gauche (barre latérale) donne accès à toutes les sections.

---

## 6. Comprendre le multi-établissement

Toutes les données appartiennent à **un** établissement. La plateforme sait toujours « qui
regarde » grâce à un **jeton transmis à chaque requête** (`X-Tenant-Id`).

- Un **sélecteur de tenant** (dans l'en-tête) permet de basculer d'un établissement à l'autre.
- **Règle de sécurité fondamentale :** les données de l'établissement A ne sont **jamais**
  visibles depuis l'établissement B. C'est garanti par la base de données elle-même
  (« Row Level Security »), pas seulement par l'interface.

> Si vous ne voyez pas les données attendues, vérifiez d'abord **quel établissement est
> sélectionné** en haut de l'écran.

---

## 7. Les écrans, pas-à-pas

### 7.1 Tableau de bord
Vue synthétique de l'établissement. Point de départ.

### 7.2 Établissement (`Établissement`)
Réglages de l'identité de l'école et de son organisation :
- **Identité** : nom, sigle, profil, coordonnées.
- **Sites** : implantations géographiques (campus, annexes).
- **Filières / Programmes / Cycles** : offre de formation.
- **Paramètres** : réglages divers.
- **Presets de grilles** : modèles de grilles réutilisables.

### 7.3 Années scolaires (`Années scolaires`)
1. Créez une **année** (code, libellé, dates, type).
2. Ajoutez des **périodes** (trimestres/semestres).
3. **Ouvrez** l'année pour la rendre courante.
4. **Clôturez** une période ou l'année en fin de cycle.

### 7.4 Grilles (`Grilles`)
Trois familles de grilles : **notation**, **stock**, **conformité**.
Sur chaque grille : **Activer**, **Figer** (définitif), **Dupliquer**. La page de détail
`Grille > [type]` permet de saisir barèmes, seuils de décision, mentions, règles d'assiduité…

### 7.5 Référentiels (`Référentiels`)
17 conteneurs de valeurs. Dépliez un conteneur, ajoutez des **éléments** (code, libellé,
valeur), ou **désactivez** un élément.

### 7.6 Écrans réservés ADMIN
Visibles uniquement avec le profil ADMIN :
- **Tenants** : créer/gérer un établissement. À la création, les 17 référentiels sont
  générés automatiquement.
- **Utilisateurs** : comptes, rôles, cycle de vie (activer/désactiver/suspendre), réinitialisation
  du mot de passe.
- **Rôles & permissions** : définir des rôles et leurs permissions.
- **Notifications** : consulter/envoyer des notifications.
- **Audit** : rechercher dans le journal des actions.
- **Rapports** : demander un rapport (PDF/CSV/JSON) et le télécharger.

### 7.7 Pédagogie · Structure
Menu de gauche, groupe **« Pédagogie · Structure »** :
- **Niveaux**, **Matières**, **Salles**, **Créneaux**, **Régimes horaires** : listes simples
  avec **Nouveau / Modifier / Supprimer**.
- **Classes** : création + boutons **Ouvrir** et **Clôturer**.
- **Promotions**, **Semestres**, **UE**, **Cours / ECUE** : rattachements (le formulaire propose
  des listes déroulantes vers les entités liées).
- **Emplois du temps** : créez un emploi du temps, ouvrez-le, ajoutez des **plages**
  (cours + créneau + salle + enseignant), consultez les **conflits**, puis **Publier**.

### 7.8 Pédagogie · Personnes
Menu de gauche, groupe **« Pédagogie · Personnes »** :
- **Élèves** : liste + bouton **œil** pour ouvrir la fiche. La fiche comporte 6 onglets :
  *Identité, Dossier, Inscriptions, Congés, Sanctions, Parents*. Actions **Transférer** et
  **Radier** en haut.
- **Parents** : parents/tuteurs.
- **Enseignants** : liste + fiche (onglets *Identité, Pièces, Affectations*, changement de
  statut).
- **Personnel** : deux onglets — **Agents** et **Services**.

> **Astuce :** les listes ont une **barre de recherche** (matricule, nom, code…). Le nombre
> d'éléments est affiché sous le tableau.

---

## 8. Ce que peut faire chaque profil (récapitulatif)

| Action | ADMIN | CUSTOMER |
|---|---|---|
| Consulter le socle et la pédagogie | Oui | Oui |
| Créer / modifier / supprimer | Oui | Non |
| Créer un établissement, gérer les utilisateurs | Oui | Non |
| Envoyer une notification, lancer un rapport | Oui | Non |

En tant que CUSTOMER, les boutons de création/modification n'apparaissent pas : c'est normal.

---

## 9. Messages d'erreur fréquents

| Message / code | Signification | Que faire |
|---|---|---|
| `TENANT_ALREADY_EXISTS` (409) | L'établissement existe déjà | Choisir un autre code / identifiant |
| `GRILLE_FIGEE` (409) | La grille est figée, donc non modifiable | La dupliquer puis modifier la copie |
| `TRANSITION_INVALIDE` (409) | Changement d'état non autorisé | Vérifier l'état courant (ex. clôturer une classe déjà clôturée) |
| `ITEM_EXISTANT` / `MATRICULE_EXISTANT` (409) | Doublon de code ou de matricule | Utiliser une valeur unique |
| `AUTH_INVALID_CREDENTIALS` (401) | E-mail ou mot de passe incorrect | Vérifier les identifiants (§3) |
| `Invalid X-Tenant-Id header` (400) | Établissement non sélectionné ou invalide | Choisir un établissement dans le sélecteur |
| `KEYCLOAK_INDISPONIBLE` (503) | Le service d'authentification est arrêté | Relancer le backend |

---

## 10. Dépannage express

- **La page ne charge rien / erreur réseau** : vérifiez que le backend et la console tournent
  (§2), et que la console est bien sur **http://localhost:3001**.
- **Je ne vois pas un menu** : certaines entrées (surtout « Plateforme ») sont réservées à ADMIN.
- **Mes données ont disparu** : changez-vous d'établissement sans vous en rendre compte ?
  Regardez le sélecteur de tenant.
- **Session expirée** : le jeton d'accès expire rapidement en démonstration ; rechargez et
  reconnectez-vous.

---

## 11. Parcours conseillé pour une première découverte

1. Se connecter en **ADMIN**.
2. **Établissement** : vérifier l'identité et créer un **site**.
3. **Années scolaires** : créer une année et l'ouvrir.
4. **Référentiels** : parcourir quelques conteneurs.
5. **Pédagogie · Structure** : créer un **niveau**, une **salle**, une **matière**.
6. Créer une **classe** (rattachée au niveau et à l'année), puis l'**ouvrir**.
7. Créer un **emploi du temps**, ajouter des **plages**, observer les **conflits**, **publier**.
8. **Pédagogie · Personnes** : créer un **élève**, ouvrir sa fiche, ajouter une **inscription**.
9. Créer un **enseignant** et une **affectation**.
10. Se reconnecter en **CUSTOMER** et constater que tout est **en lecture seule**.
