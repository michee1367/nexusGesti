# Guide utilisateur — NexusGesti

Guide pas-à-pas de la plateforme, de l'ouverture de session jusqu'aux écrans livrés
aujourd'hui (**socle** + **pédagogie** complète : structure, personnes, inscriptions,
assiduité, évaluations, examens, frais + **boulangerie** lots 1 à 3 : produits,
production, approvisionnement).

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
| **Pédagogie** | Structure, Personnes, Inscriptions (UE, groupes, admissions), Assiduité (séances, appels), Évaluations (notes, bulletins), Examens (sessions, délibérations), Frais (grilles, avis, paiements) | Disponible |
| **Boulangerie** | Produits et recettes, plans de production, fournées, fournisseurs, besoins, commandes d'achat, réceptions | Lots 1 à 3 disponibles (vente/caisse : API seulement) |
| **Hôpital** | Patients, rendez-vous, consultations, pharmacie | À venir |
| **Finance** | Comptabilité OHADA / SYSCOHADA (RDC) | À venir |

### Guides spécialisés

- [Guide Boulangerie](guide-boulangerie.md) — produits, recettes, production et
  approvisionnement, écran par écran.
- [Guide de déploiement en production](../backend/infrastructure/deploiement/README.md)
  — installation VPS, PostgreSQL, Keycloak, Nginx, sauvegardes et mise à jour.

Il existe **deux profils d'utilisateur** :

- **ADMIN** : peut tout voir **et** tout modifier.
- **CUSTOMER** : peut **consulter** les données (lecture seule).

Depuis votre navigateur, la plateforme s'ouvre sur une **page d'accueil publique** qui présente les
domaines. Elle propose deux points d'entrée : **Connexion** (compte existant, §5) et **Créer mon
établissement** (inscription en libre-service, §6).

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

Au premier lancement, le backend compile et démarre **4 applications** (socle, pédagogie,
boulangerie, passerelle) + l'infrastructure (base PostgreSQL, Redis, Kafka, Keycloak, Prometheus,
Grafana, Vault). Comptez quelques minutes.

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
l'écran (voir §7).

**Profil d'établissement** : type d'activité — `PRIMAIRE`, `SECONDAIRE`, `UNIVERSITAIRE` ou
`BOULANGERIE`. Il active ou non certains modules (ECTS, délibération par UE, production…).

**Année scolaire** : période de référence (ex. 2025-2026). Elle contient des **périodes**
(trimestres, semestres…). Une année peut être *prévue*, *ouverte* puis *clôturée*.

**Grille** : jeu de règles versionnées (notes, stock, conformité) appliquées à un
établissement. Une grille s'*active*, se *fige* (le figement interdit toute modification) et
se *duplique*.

**Référentiel** : liste de valeurs de référence (types de frais, motifs d'absence, mentions…).
Un établissement possède 23 référentiels, créés automatiquement à sa création.

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

**Inscription UE** : rattachement d'un élève à une unité d'enseignement (cursus universitaire)
pour une année. Elle se valide ou se refuse.

**Groupe TD/TP** : sous-groupe d'une classe pour les travaux dirigés ou pratiques. Sa capacité
est limitée ; un groupe plein refuse les ajouts.

**Règle d'éligibilité** : condition d'admission (crédits minimum, moyenne minimum, âge…) évaluée
pour un candidat.

**Séance** : occurrence d'un cours (une date, un créneau). Une séance se *démarre*, *termine*,
*reporte* ou s'*annule*.

**Feuille d'appel** : liste de présence d'une séance. On y pointe chaque élève (présent, absent,
retard). Validée ou clôturée, elle n'est plus modifiable.

**Justificatif** : document déposé pour excuser une absence. Un justificatif *accepté* requalifie
l'absence en absence justifiée.

**Assiduité** : indicateur calculé (taux de présence, absences justifiées / injustifiées) par
élève, par classe ou sous forme de registre.

**Évaluation** : contrôle noté (devoir, interrogation…). Elle porte une feuille de notes, puis se
*clôture* et se *publie*.

**Note** : résultat d'un élève à une évaluation ; peut être marquée absente, exclue ou contestée.

**Bulletin** : synthèse des notes d'un élève sur une période. Il se *calcule*, se *publie*, se
*signe* et peut être *rectifié* (nouvelle version).

**Coupon de notes** : extrait sécurisé d'un bulletin, numéroté et protégé par une empreinte.

**Moyenne / classement** : calcul automatique (par évaluation, par élève, par classe).

**Session d'examen** : période officielle d'examens, contenant des **épreuves** et des
**convocations**.

**Épreuve** : examen d'une matière ou d'une UE au sein d'une session.

**Convocation** : document appelant un candidat à une épreuve.

**Délibération** : réunion du jury qui décide du passage, du redoublement ou de l'admission.

**Décision** : verdict du jury pour un élève (admis, ajourné, repêché…), par classe ou par UE.

**Résultat officiel** : résultat publié d'une session.

**Rattrapage** : seconde épreuve pour un candidat ajourné.

**Procès-verbal** : document officiel d'une session, *signé* puis *homologué*.

**Grille tarifaire** : barème des frais (scolarité, inscription…), versionné ; elle s'*active* et
se *fige*.

**Avis de paiement** : appel de fonds émis pour un élève, décomposé en **échéances** ; il accepte
une **remise**.

**Paiement** : règlement d'un avis. **Idempotent** : rejouer la même demande ne crée pas de doublon.

**Coupon de paiement** : reçu numéroté d'un paiement, protégé par une empreinte.

**Journalier** : caisse journalière (ouverture, fond, clôture) d'un site.

**Rôle** : ensemble de permissions (ADMIN, CUSTOMER…). **Permission** : droit élémentaire
(verbe + ressource).

**Audit** : journal horodaté des actions importantes (création d'élève, envoi de notification…).

**Rapport** : document généré (PDF, CSV, JSON) à partir des données.

---

## 5. Se connecter

1. Ouvrez **http://localhost:3001** : vous arrivez sur la **page d'accueil publique**.
2. Cliquez sur **Connexion**.
3. **Étape 1 — votre adresse email.** Saisissez l'email du compte puis cliquez sur **Continuer**.
   La plateforme retrouve automatiquement l'établissement associé et l'affiche.
4. **Étape 2 — votre mot de passe.** Saisissez le mot de passe puis cliquez sur **Se connecter**.
5. Vous arrivez sur le **Tableau de bord**.

En haut à droite, l'en-tête affiche votre nom, votre établissement courant et un bouton de
déconnexion. Le menu de gauche (barre latérale) donne accès à toutes les sections.

> **Repli manuel :** si l'email n'est pas reconnu, le lien « Saisir l'identifiant de l'établissement
> manuellement » permet de coller l'UUID fourni par l'administrateur (voir §3 pour le compte de
> démonstration). En démonstration : `11111111-2222-3333-4444-555555555570`.

> **Pas encore de compte ?** Le lien « Créer un établissement » mène à l'inscription en libre-service
> (voir §6).

---

## 6. Créer son établissement en libre-service

Pas besoin de passer par un administrateur pour ouvrir un établissement. Depuis la **page d'accueil**
(`http://localhost:3001`), le bouton **Créer mon établissement** ouvre le formulaire d'inscription (page
`/signup`).

1. Renseignez **votre compte** : prénom, nom, adresse email, téléphone au format international
   (ex. `+243810000000`), code pays sur 2 lettres (ex. `CD`), mot de passe (au moins 12 caractères,
   avec majuscule, minuscule, chiffre et caractère spécial) et acceptez les conditions.
2. Renseignez **votre établissement** : **dénomination**, **code** et **profil** (`PRIMAIRE`,
   `SECONDAIRE`, `UNIVERSITAIRE` ou `BOULANGERIE`).
3. Validez. Le compte administrateur et l'espace de l'établissement sont créés, puis vous êtes
   **automatiquement connecté** et redirigé vers le **Tableau de bord**.

> **Sans installation :** l'inscription suffit. Le nouvel établissement est créé en statut
> *préparation* et ses **23 référentiels** sont générés automatiquement. Vous en devenez
> administrateur (rôle ADMIN).

---

## 7. Comprendre le multi-établissement

Toutes les données appartiennent à **un** établissement. La plateforme sait toujours « qui
regarde » grâce à un **jeton transmis à chaque requête** (`X-Tenant-Id`).

- Un **sélecteur de tenant** (dans l'en-tête) permet de basculer d'un établissement à l'autre.
- **Règle de sécurité fondamentale :** les données de l'établissement A ne sont **jamais**
  visibles depuis l'établissement B. C'est garanti par la base de données elle-même
  (« Row Level Security »), pas seulement par l'interface.

> Si vous ne voyez pas les données attendues, vérifiez d'abord **quel établissement est
> sélectionné** en haut de l'écran.

---

## 8. Les écrans, pas-à-pas

> **Saisie « par nom ».** La console ne demande jamais d'identifiant technique. Les rattachements
> (classe, salle, matière, cours, enseignant, année, période…) se choisissent dans des **listes
> déroulantes qui affichent le nom**, et les tableaux restituent ces libellés.

### 8.1 Tableau de bord
Vue synthétique de l'établissement. Point de départ.

### 8.2 Établissement (`Établissement`)
Réglages de l'identité de l'école et de son organisation :
- **Identité** : nom, sigle, profil, coordonnées.
- **Sites** : implantations géographiques (campus, annexes).
- **Filières / Programmes / Cycles** : offre de formation.
- **Paramètres** : réglages divers.
- **Presets de grilles** : modèles de grilles réutilisables.

### 8.3 Années scolaires (`Années scolaires`)
1. Créez une **année** (code, libellé, dates, type).
2. Ajoutez des **périodes** (trimestres/semestres).
3. **Ouvrez** l'année pour la rendre courante.
4. **Clôturez** une période ou l'année en fin de cycle.

### 8.4 Grilles (`Grilles`)
Trois familles de grilles : **notation**, **stock**, **conformité**.
Sur chaque grille : **Activer**, **Figer** (définitif), **Dupliquer**. La page de détail
`Grille > [type]` permet de saisir barèmes, seuils de décision, mentions, règles d'assiduité…

### 8.5 Référentiels (`Référentiels`)
17 conteneurs de valeurs. Dépliez un conteneur, ajoutez des **éléments** (code, libellé,
valeur), ou **désactivez** un élément.

### 8.6 Écrans réservés ADMIN
Visibles uniquement avec le profil ADMIN :
- **Tenants** : créer/gérer un établissement. À la création, les 23 référentiels sont
  générés automatiquement.
- **Utilisateurs** : comptes, rôles, cycle de vie (activer/désactiver/suspendre), réinitialisation
  du mot de passe.
- **Rôles & permissions** : définir des rôles et leurs permissions.
- **Notifications** : consulter/envoyer des notifications.
- **Audit** : rechercher dans le journal des actions.
- **Rapports** : demander un rapport (PDF/CSV/JSON) et le télécharger.

### 8.7 Pédagogie · Structure
Menu de gauche, groupe **« Pédagogie · Structure »** :
- **Niveaux**, **Matières**, **Salles**, **Créneaux**, **Régimes horaires** : listes simples
  avec **Nouveau / Modifier / Supprimer**.
- **Classes** : création + boutons **Ouvrir** et **Clôturer**.
- **Promotions**, **Semestres**, **UE**, **Cours / ECUE** : rattachements (le formulaire propose
  des listes déroulantes vers les entités liées).
- **Emplois du temps** : créez un emploi du temps, ouvrez-le, ajoutez des **plages**
  (cours + créneau + salle + enseignant), consultez les **conflits**, puis **Publier**.

### 8.8 Pédagogie · Personnes
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

### 8.9 Pédagogie · Inscriptions
Menu de gauche, groupe **« Pédagogie · Inscriptions »** (surtout utile au supérieur) :
- **Inscriptions UE** : rattachez un élève à une UE, puis **Valider** (ou refuser / mettre en
  liste d'attente / annuler).
- **Groupes TD/TP** : créez des groupes (code, capacité) et affectez des **membres** ; un groupe
  plein refuse les ajouts. Ouvrez / clôturez un groupe depuis sa fiche.
- **Règles d'éligibilité** : définissez les conditions d'admission (crédits, moyenne, âge).
- **Admissions** : évaluez un **candidat** (éligible ou non) et consultez le **parcours** d'un
  élève (crédits acquis, taux de progression).

### 8.10 Pédagogie · Assiduité
- **Séances** : créez une séance (classe, cours, date, créneau), puis **Démarrer**, **Terminer**,
  **Reporter** ou **Annuler**.
- **Feuilles d'appel** : ouvrez une feuille, **pointez** les présences, marquez une **absence** ou
  un **retard**, puis **Soumettre**, **Valider**, **Clôturer**. Après clôture, la feuille n'est
  plus pointable.
- **Justificatifs** : déposez un justificatif puis **Accepter** ou **Rejeter** ; accepté, il
  requalifie l'absence.
- **Assiduité** : consultez le **suivi d'un élève**, le **taux d'une classe** ou le **registre**.

### 8.11 Pédagogie · Évaluations
- **Évaluations** : créez une évaluation (**Ouvrir**), saisissez les **notes** sur la feuille,
  marquez une **absence**, puis **Clôturer** et **Publier**.
- **Bulletins** : créez un bulletin, ajoutez des **lignes**, puis **Calculer**, **Publier**,
  **Générer**, **Signer**. Une **rectification** crée une nouvelle version. Les **coupons** se
  tirent depuis le bulletin.
- **Moyennes & classement** : calculez la moyenne d'une **évaluation**, d'un **élève**, ou le
  **classement** d'une classe.

### 8.12 Pédagogie · Examens
- **Sessions d'examen** : créez une session puis **Programmer**, **Ouvrir**, **Clôturer**,
  **Annuler** ; ajoutez-y **épreuves**, **créneaux** et **convocations** (+ notifier).
- **Délibérations** : créez une délibération, **Ouvrir**, saisissez les **décisions**
  (individuelles ou par UE), puis **Clôturer**. Les **résultats** se publient depuis la décision.
- **Rattrapages** : déclarez un candidat **présent** ou **exempt**.
- **Procès-verbaux** : **Soumettre**, **Signer**, **Homologuer** (ou **Contester**).

### 8.13 Pédagogie · Frais
- **Grilles tarifaires** : créez une grille, ajoutez des **lignes** et des **réductions**, puis
  **Activer**, **Figer** (lecture seule) ou **Archiver**.
- **Avis de paiement** : **Émettre** un avis, appliquer une **remise**, consulter les **échéances**,
  **Annuler** si besoin.
- **Paiements** : enregistrez un paiement (la clé d'idempotence est gérée automatiquement), puis
  **Valider**, **Rejeter** ou **Annuler**.
- **Coupons** : tirez un coupon (empreinte de sécurité), **Vérifier**, **Remettre**, **Annuler**.
- **Journaliers** : ouvrez un journalier de caisse (site, date, fond) puis **Clôturer**.

---

## 9. Ce que peut faire chaque profil (récapitulatif)

| Action | ADMIN | CUSTOMER |
|---|---|---|
| Consulter le socle et la pédagogie | Oui | Oui |
| Créer / modifier / supprimer | Oui | Non |
| Créer un établissement, gérer les utilisateurs | Oui | Non |
| Envoyer une notification, lancer un rapport | Oui | Non |

En tant que CUSTOMER, les boutons de création/modification n'apparaissent pas : c'est normal.

---

## 10. Messages d'erreur fréquents

| Message / code | Signification | Que faire |
|---|---|---|
| `TENANT_ALREADY_EXISTS` (409) | L'établissement existe déjà | Choisir un autre code / identifiant |
| `GRILLE_FIGEE` (409) | La grille est figée, donc non modifiable | La dupliquer puis modifier la copie |
| `TRANSITION_INVALIDE` (409) | Changement d'état non autorisé | Vérifier l'état courant (ex. clôturer une classe déjà clôturée) |
| `ITEM_EXISTANT` / `MATRICULE_EXISTANT` (409) | Doublon de code ou de matricule | Utiliser une valeur unique |
| `ETAT_INVALIDE` (409) | Action interdite dans l'état courant | Vérifier l'état (ex. pointer une feuille clôturée) |
| `CONFLIT` / `CONFLIT_EMPLOI` (409) | Conflit détecté (emploi du temps…) | Corriger les éléments en double |
| `GROUPE_COMPLET` (409) | Le groupe TD/TP a atteint sa capacité | Choisir un autre groupe ou augmenter la capacité |
| `IDEMPOTENCE_EN_COURS` (409) | Un paiement identique est déjà en cours | Attendre puis réessayer |
| `INVALIDE` (400) | Donnée invalide (champ manquant ou incorrect) | Corriger le formulaire |
| `NOT_FOUND` (404) | Élément introuvable (souvent : mauvais établissement) | Vérifier le sélecteur de tenant |
| `AUTH_INVALID_CREDENTIALS` (401) | E-mail ou mot de passe incorrect | Vérifier les identifiants (§3) |
| `Invalid X-Tenant-Id header` (400) | Établissement non sélectionné ou invalide | Choisir un établissement dans le sélecteur |
| `KEYCLOAK_INDISPONIBLE` (503) | Le service d'authentification est arrêté | Relancer le backend |

---

## 11. Dépannage express

- **La page ne charge rien / erreur réseau** : vérifiez que le backend et la console tournent
  (§2), et que la console est bien sur **http://localhost:3001**.
- **Je ne vois pas un menu** : certaines entrées (surtout « Plateforme ») sont réservées à ADMIN.
- **Mes données ont disparu** : changez-vous d'établissement sans vous en rendre compte ?
  Regardez le sélecteur de tenant.
- **Session expirée** : le jeton d'accès expire rapidement en démonstration ; rechargez et
  reconnectez-vous.

---

## 12. Parcours conseillé pour une première découverte

1. Se connecter en **ADMIN**.
2. **Établissement** : vérifier l'identité et créer un **site**.
3. **Années scolaires** : créer une année et l'ouvrir.
4. **Référentiels** : parcourir quelques conteneurs.
5. **Pédagogie · Structure** : créer un **niveau**, une **salle**, une **matière**.
6. Créer une **classe** (rattachée au niveau et à l'année), puis l'**ouvrir**.
7. Créer un **emploi du temps**, ajouter des **plages**, observer les **conflits**, **publier**.
8. **Pédagogie · Personnes** : créer un **élève**, ouvrir sa fiche, ajouter une **inscription**.
9. Créer un **enseignant** et une **affectation**.
10. **Pédagogie · Assiduité** : créer une **séance**, ouvrir la **feuille d'appel**, pointer les élèves.
11. **Pédagogie · Évaluations** : créer une **évaluation**, saisir des **notes**, **calculer** et
    **publier** un **bulletin**.
12. **Pédagogie · Frais** : créer une **grille tarifaire**, **émettre** un **avis**, enregistrer un
    **paiement**.
13. Se reconnecter en **CUSTOMER** et constater que tout est **en lecture seule**.
