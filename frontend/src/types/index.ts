export type ProfilEtablissement = "PRIMAIRE" | "SECONDAIRE" | "UNIVERSITAIRE" | "BOULANGERIE";
export type StatutEtablissement = "PREPARATION" | "ACTIF" | "SUSPENDU" | "ARCHIVE";

export interface Adresse {
  rue?: string | null;
  avenue?: string | null;
  quartier?: string | null;
  commune?: string | null;
  ville?: string | null;
  province?: string | null;
  territoire?: string | null;
  codePostal?: string | null;
  pays?: string | null;
  telephone?: string | null;
}

export interface Etablissement {
  id: string;
  code: string;
  denomination: string;
  sigle?: string | null;
  profil: ProfilEtablissement;
  statut: StatutEtablissement;
  adresse?: Adresse;
  telephone?: string | null;
  emailOfficiel?: string | null;
  devise?: string | null;
  pays?: string | null;
  fuseauHoraire?: string | null;
  capaciteAccueil?: number | null;
}

export type TypeAnnee = "SCOLAIRE" | "UNIVERSITAIRE" | "CIVILE" | "BUDGETAIRE" | "EXERCICE";
export type StatutAnnee = "PREVUE" | "OUVERTE" | "CLOTUREE";
export type TypePeriode = "TRIMESTRE" | "SEMESTRE" | "BIMESTRE" | "MODULE" | "PERIODE";

export interface Periode {
  idPeriode: string;
  anneeId: string;
  code: string;
  libelle: string;
  rang: number;
  dateDebut: string;
  dateFin: string;
  type: TypePeriode;
  cloturee: boolean;
  dateCloture?: string | null;
}

export interface AnneeScolaire {
  idAnnee: string;
  etablissementId: string;
  code: string;
  libelle: string;
  type: TypeAnnee;
  dateDebut: string;
  dateFin: string;
  statut: StatutAnnee;
  anneeReference: number;
  siteParDefautId?: string | null;
  periodes: Periode[];
}

export interface Site {
  idSite: string;
  etablissementId: string;
  code: string;
  libelle: string;
  adresse?: Adresse;
  principal: boolean;
}

export interface Filiere {
  idFiliere: string;
  etablissementId: string;
  code: string;
  libelle: string;
  departement?: string | null;
  diplome?: string | null;
  niveauDiplome?: string | null;
  dureeAnnees?: number | null;
  cycleId?: string | null;
  active: boolean;
  programmes: Programme[];
}

export interface Programme {
  idProgramme: string;
  filiereId: string;
  code: string;
  libelle: string;
  parcours: string[];
  totalCredits?: number | null;
  active: boolean;
}

export interface Cycle {
  id: string;
  etablissementId: string;
  code: string;
  libelle: string;
  rang: number;
  profil: ProfilEtablissement;
}

export type TypeValeur = "CHAINE" | "ENTIER" | "DECIMAL" | "BOOLEEN" | "DATE" | "JSON" | "MONTANT";

export interface Parametre {
  id: string;
  etablissementId: string;
  cle: string;
  valeur?: string | null;
  typeValeur: TypeValeur;
  categorie?: string | null;
  description?: string | null;
}

export interface GrillePreset {
  idGrillePreset: string;
  etablissementId: string;
  code: string;
  libelle: string;
  profil: ProfilEtablissement;
  description?: string | null;
  systeme: boolean;
}

export type NatureReferentiel =
  | "BAREME"
  | "MENTION"
  | "LIBELLE"
  | "MOTIF_ABSENCE"
  | "MOTIF_RETARD"
  | "MOTIF_REJET"
  | "TYPE_CANAL"
  | "TYPE_FRAIS"
  | "MOTIF_FRAIS"
  | "MODE_REGLEMENT"
  | "MOTIF_ADMISSION"
  | "MOTIF_SORTIE"
  | "MOTIF_TRANSFERT_INTERNE"
  | "NATURE_PRODUIT"
  | "TYPE_ACTE_HOSPITALIER"
  | "TYPE_CHAMBRE"
  | "DELAI_VALIDATION";

export interface ReferentielItem {
  idItem: string;
  referentielId: string;
  code: string;
  libelle: string;
  valeur?: string | null;
  metaDonnees?: Record<string, string>;
  ordre: number;
  actif: boolean;
  comptabilisePresence?: boolean | null;
  bloqueSaisie?: boolean | null;
}

export interface Referentiel {
  idReferentiel: string;
  etablissementId: string;
  code: string;
  libelle: string;
  nature: NatureReferentiel;
  ordre: number;
  actif: boolean;
  items: ReferentielItem[];
}

export type TypePortee =
  | "GLOBALE"
  | "PAR_ETABLISSEMENT"
  | "PAR_SITE"
  | "PAR_PROFIL"
  | "PAR_CYCLE"
  | "PAR_NIVEAU"
  | "PAR_PARCOURS"
  | "PAR_PROGRAMME"
  | "PAR_SEMESTRE"
  | "PAR_UE"
  | "PAR_COURS"
  | "PAR_MATIERE"
  | "PAR_ENSEIGNANT"
  | "PAR_CLASSE"
  | "PAR_TYPE_EVALUATION"
  | "PAR_TYPE_SESSION";
export type Decision = "ADMIS" | "ADMIS_MENTION" | "RATTAPAGE" | "REDOUBLANT" | "REJETE";
export type TypeBareme = "POINTS" | "SUR_5" | "SUR_10" | "SUR_20" | "SUR_100" | "LETTRE" | "MENTION";
export type TypeArrondi = "AUCUN" | "DECIMALE" | "INFERIEUR" | "SUPERIEUR" | "MI_SUPERIEUR";
export type NiveauAcalcul =
  | "EVALUATION"
  | "COURS_ECUE"
  | "UE"
  | "PERIODE"
  | "ANNEE"
  | "CYCLE";
export type StrategieAggregation =
  | "MOYENNE_PONDEREE"
  | "MOYENNE_NON_PONDEREE"
  | "SOMME_PONDEREE"
  | "MEILLEURE_NOTE"
  | "MOYENNE_TRONQUEE"
  | "DERNIERE_NOTE";
export type StrategieRotationLot = "PREMIER_PERIME" | "FEFO" | "FIFO";
export type NiveauAlerte = "INFO" | "ATTENTION" | "CRITIQUE" | "BLOCAGE";

export interface Bareme {
  idBareme?: string;
  grilleId?: string;
  code: string;
  libelle: string;
  type: TypeBareme;
  baremeMax: number;
  noteMinimale: number;
  precision: number;
  arrondi: TypeArrondi;
  defaut: boolean;
}

export interface SeuilDecision {
  idSeuil?: string;
  grilleId?: string;
  libelle: string;
  portee: TypePortee;
  cycleId?: string | null;
  niveau?: string | null;
  parcours?: string | null;
  programme?: string | null;
  moyenneMinimale?: number | null;
  moyenneMaximale?: number | null;
  tauxReussiteMinimale?: number | null;
  moyenneDeReussite?: number | null;
  absencesMaximales?: number | null;
  decisionResultante: Decision;
  rangMin?: number | null;
  rangMax?: number | null;
  priorite: number;
}

export interface ReglePonderation {
  idRegle?: string;
  grilleId?: string;
  libelle: string;
  portee: TypePortee;
  profil?: ProfilEtablissement | null;
  cycleId?: string | null;
  niveau?: string | null;
  parcours?: string | null;
  programme?: string | null;
  semestre?: string | null;
  ue?: string | null;
  cours?: string | null;
  matiere?: string | null;
  enseignant?: string | null;
  classe?: string | null;
  typeEvaluation?: string | null;
  typeSession?: string | null;
  coefficient: number;
  seuilInclusion?: number | null;
  plafondContribution?: number | null;
  poidsMax?: number | null;
  priorite: number;
  active: boolean;
}

export interface RegleEquivalence {
  code?: string;
  libelle?: string;
}

export interface ConfigCascade {
  niveauxActifs: NiveauAcalcul[];
  strategie: StrategieAggregation;
  arrondi: TypeArrondi;
  precision: number;
  seuilAbsorption?: number | null;
  regleEquivalence?: RegleEquivalence | null;
  ordreConversionLettre: boolean;
}

export interface RegleAssiduite {
  idRegle?: string;
  grilleId?: string;
  portee: TypePortee;
  cycleId?: string | null;
  niveau?: string | null;
  absencesMaxParPeriode?: number | null;
  absencesParJourMax?: number | null;
  tauxPresenceMinimale?: number | null;
  absencesJustifieesExonerees: boolean;
  retardCompteCommeAbsence: boolean;
  seuilRetardMinutes?: number | null;
  consequence: Decision;
  priorite: number;
}

export interface Mention {
  idMention?: string;
  grilleId?: string;
  code: string;
  libelle: string;
  symbole?: string | null;
  seuilMinimum: number;
  seuilMaximum: number;
  couleur?: string | null;
  appreciationAssociee?: string | null;
  ordre: number;
}

export interface RegleAppreciation {
  idRegle?: string;
  grilleId?: string;
  portee: TypePortee;
  moyenneMinimale?: number | null;
  moyenneMaximale?: number | null;
  libelle: string;
  modeleTexte?: string | null;
  mentionId?: string | null;
}

export interface Grille {
  id?: string;
  etablissementId?: string;
  code: string;
  libelle: string;
  version: number;
  profil: ProfilEtablissement;
  anneeId?: string | null;
  dateActivation?: string | null;
  active: boolean;
  modifiable: boolean;
}

export interface GrilleNotation extends Grille {
  baremes: Bareme[];
  reglesPonderation: ReglePonderation[];
  configCascade?: ConfigCascade | null;
  seuilsDecision: SeuilDecision[];
  reglesAssiduite: RegleAssiduite[];
  mentions: Mention[];
  appreciations: RegleAppreciation[];
}

export interface AlerteStock {
  idAlerte?: string;
  grilleId?: string;
  reference: string;
  depot: string;
  seuil: number;
  niveau: NiveauAlerte;
  destinataires: string[];
  active: boolean;
}

export interface GrilleStock extends Grille {
  strategieRotationLot: StrategieRotationLot;
  dureeQuarantaineJours: number;
  seuilAlertePeremptionJours: number;
  toleranceEcartInventairePct: number;
  delaiReapproJours?: number | null;
  seuilAlerteTauxRotationPct?: number | null;
  autoriserPeremptionEnSortie: boolean;
  exigerControleReception: boolean;
  alertes: AlerteStock[];
}

export interface GrilleConformite extends Grille {
  dureeRetentionDossierMedical: number;
  dureeConservationResultats: number;
  dureeConservationImages: number;
  dureeConservationEchantillons?: number | null;
  chiffrementAuRepos: boolean;
  accesTracable: boolean;
  delaiDeclarationResultatCritique?: number | null;
  niveauAccesResultatCritique?: string | null;
  archivable: boolean;
}

export type GrilleType = "notation" | "stock" | "conformite";

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  tokenType: string;
  mfaRequired: boolean;
  mfaMethods: string[];
  sessionId: string;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  tokenType: string;
}

export interface SessionUser {
  sub: string | null;
  email: string;
  name: string;
  roles: string[];
  isAdmin: boolean;
}

export interface ApiErrorBody {
  code?: string;
  message?: string;
  requestId?: string;
  timestamp?: number;
}

export class ApiError extends Error {
  status: number;
  code?: string;

  constructor(status: number, message: string, code?: string) {
    super(message);
    this.status = status;
    this.code = code;
  }
}

export type NotificationChannel = "SMS" | "EMAIL" | "PUSH";
export type NotificationStatus = "PENDING" | "SENT" | "FAILED" | "DELIVERED";
export type NotificationPriority = "LOW" | "NORMAL" | "HIGH" | "CRITICAL";

export interface Notification {
  id: string;
  userId: string;
  channel: NotificationChannel;
  templateCode?: string | null;
  recipientAddress: string;
  subject?: string | null;
  body?: string | null;
  status: NotificationStatus;
  priority: NotificationPriority;
  createdAt: string;
  sentAt?: string | null;
  failedReason?: string | null;
}

export interface AuditEntry {
  id: string;
  serviceName: string;
  eventType: string;
  userId?: string | null;
  entityType?: string | null;
  entityId?: string | null;
  action?: string | null;
  oldValue?: string | null;
  newValue?: string | null;
  ipAddress?: string | null;
  timestamp: string;
  metadata?: Record<string, string>;
}

export interface AuditSearchCriteria {
  entityType?: string;
  entityId?: string;
  userId?: string;
  eventType?: string;
  from?: string;
  to?: string;
}

export type ReportType = "ACCOUNT_STATEMENT" | "TRANSACTION_HISTORY" | "KYC_SUMMARY";
export type ReportFormat = "PDF" | "CSV" | "JSON";
export type ReportStatus = "PENDING" | "GENERATING" | "READY" | "FAILED" | "EXPIRED";

export interface ReportRequest {
  id: string;
  userId: string;
  reportType: ReportType;
  parameters?: Record<string, string>;
  status: ReportStatus;
  fileUrl?: string | null;
  format: ReportFormat;
  createdAt: string;
  completedAt?: string | null;
  failedReason?: string | null;
  version?: number | null;
}

export type StatutUtilisateur = "INVITE" | "ACTIF" | "DESACTIF" | "SUSPENDU" | "SUPPRIME";
export type TypeCompte = "LOCAL" | "KEYCLOAK" | "ANONYME";
export type CategorieRole =
  | "ADMINISTRATION"
  | "PEDAGOGIQUE"
  | "SCOLARITE"
  | "SANTE"
  | "FINANCE"
  | "SECURITE"
  | "SYSTEME"
  | "CONSULTATION";

export interface Permission {
  idPermission: string;
  code: string;
  libelle?: string | null;
  service?: string | null;
  ressource?: string | null;
  action?: string | null;
}

export interface Role {
  idRole?: string | null;
  code: string;
  libelle?: string | null;
  categorie?: CategorieRole | null;
  systeme?: boolean;
  hierarchique?: number | null;
  permissions?: Permission[];
}

export interface Utilisateur {
  idUtilisateur: string;
  identifiant?: string | null;
  email: string;
  telephone?: string | null;
  nom?: string | null;
  postNom?: string | null;
  prenom?: string | null;
  sexe?: string | null;
  dateNaissance?: string | null;
  nationalite?: string | null;
  photoUrl?: string | null;
  statut: StatutUtilisateur;
  typeCompte?: TypeCompte | null;
  doitChangerMotDePasse?: boolean;
  derniereConnexion?: string | null;
  delombok?: boolean;
  languePreferee?: string | null;
  keycloakUserId?: string | null;
  adresse?: Adresse | null;
  roles?: Role[];
  suspenduJusquau?: string | null;
}

// =====================================================================
// Pédagogie — lot 4a : academic-structure-service (8111)
// =====================================================================

export type StatutClasse = "OUVERTE" | "EN_COURS" | "CLOTUREE";
export type TypeCycle = "PRIMAIRE" | "SECONDAIRE" | "SUPERIEUR" | "LICENCE" | "MASTER";
export type TypeMatiere = "OBLIGATOIRE" | "OPTION" | "CHOIX" | "LIBRE";
export type TypeEvaluation =
  | "INTERROGATION"
  | "TP"
  | "TD"
  | "PROJET"
  | "DEVOIR"
  | "COMPOSITION"
  | "ORAL"
  | "EXAMEN_BLANC"
  | "CONCOURS"
  | "DEVOIR_SURVEILLE";
export type TypeSalle =
  | "COURS"
  | "TP"
  | "LABORATOIRE"
  | "EXAMEN"
  | "AMPHITHEATRE"
  | "BUREAU"
  | "DIRECTION"
  | "MEDICALE"
  | "CHAMBRE"
  | "BLOC_OPERATOIRE";
export type JourSemaine = "LUNDI" | "MARDI" | "MERCREDI" | "JEUDI" | "VENDREDI" | "SAMEDI" | "DIMANCHE";
export type TypeConflit = "SALL_DOUBLEE" | "ENSEIGNANT_DOUBLE" | "ELEVES_DOUBLE" | "HORS_CYCLE" | "COINCIDENCE_COURS_EXAMEN";

export interface Niveau {
  idNiveau: string;
  code: string;
  libelle: string;
  rang: number;
  cycle: TypeCycle;
  ordre: number;
  niveauSuperieurId?: string | null;
}

export interface Classe {
  idClasse: string;
  code: string;
  libelle: string;
  niveauId: string;
  anneeId: string;
  siteId?: string | null;
  capacite?: number | null;
  effectifReel: number;
  statut: StatutClasse;
  salleAttitreeId?: string | null;
  regimeHoraireCode?: string | null;
}

export interface Promotion {
  idPromotion: string;
  code: string;
  libelle: string;
  programmeId?: string | null;
  semestreId?: string | null;
  anneeId: string;
  capacite?: number | null;
  effectifReel: number;
  statut: StatutClasse;
}

export interface Semestre {
  idSemestre: string;
  code: string;
  libelle: string;
  rang: number;
  periodeId?: string | null;
  totalCreditsAttendu?: number | null;
}

export interface UE {
  idUE: string;
  code: string;
  libelle: string;
  semestreId: string;
  credits?: number | null;
  coefficient?: number | null;
  volumeHoraire?: number | null;
  obligatoire: boolean;
  niveauAcalcul?: NiveauAcalcul | string | null;
}

export interface Matiere {
  idMatiere: string;
  code: string;
  libelle: string;
  discipline?: string | null;
  type: TypeMatiere;
  couleur?: string | null;
  active: boolean;
}

export interface CoursECUE {
  idCours: string;
  code: string;
  libelle: string;
  ueId?: string | null;
  classeId?: string | null;
  matiereId?: string | null;
  type: TypeEvaluation;
  volumeHoraire?: number | null;
  credits?: number | null;
  coefficient?: number | null;
  hebdomadaire: boolean;
  regroupement?: string | null;
}

export interface Salle {
  idSalle: string;
  code: string;
  libelle: string;
  type: TypeSalle;
  siteId?: string | null;
  capacite: number;
  equipements: string[];
  accessible: boolean;
  disponible: boolean;
}

export interface Creneau {
  idCreneau: string;
  jour: JourSemaine;
  heureDebut: string | number[];
  heureFin: string | number[];
  ordre: number;
}

export interface RegimeHoraire {
  code: string;
  libelle: string;
  jours: JourSemaine[];
  creneauxParJour: number;
  dureeCreneau: number;
  pauseMatinale?: number | null;
  pauseMeridienne?: number | null;
  volumeHebdomadaire?: number | null;
}

export interface PlageEmploi {
  idPlage?: string | null;
  emploiId?: string | null;
  coursId: string;
  creneauId: string;
  salleId?: string | null;
  enseignantId?: string | null;
}

export interface ConflitEmploi {
  idConflit?: string | null;
  emploiId?: string | null;
  type: TypeConflit;
  plageAId?: string | null;
  plageBId?: string | null;
}

export interface EmploiDuTemps {
  idEmploi: string;
  anneeId: string;
  classeId: string;
  version: number;
  dateActivation?: string | null;
  active: boolean;
  publie: boolean;
  plages: PlageEmploi[];
  conflits: ConflitEmploi[];
}

// =====================================================================
// Pédagogie — lot 4b : academic-people-service (8112)
// =====================================================================

export type Sexe = "M" | "F" | "AUTRE";
export type StatutEleve =
  | "PREINSCRIT"
  | "INSCRIT"
  | "REINSCRIT"
  | "TRANSFERE"
  | "ABANDON"
  | "CONGE"
  | "OBTEN_DIPLOME"
  | "RADIE";
export type StatutDossier = "EN_COURS" | "COMPLET" | "INCOMPLET" | "VERIFIE";
export type StatutInscription = "EN_ATTENTE" | "VALIDE" | "ANNULEE";
export type TypeConge = "MEDICAL" | "FAMILIAL" | "SCOLAIRE" | "FINANCIER" | "DISCIPLINAIRE";
export type TypeSanction = "AVERTISSEMENT" | "BLAME" | "EXCLUSION_TEMPORAIRE" | "EXCLUSION_DEFINITIVE";
export type TypeLien =
  | "PERE"
  | "MERE"
  | "TUTEUR"
  | "TUTEUR_LEGAL"
  | "GRAND_PARENT"
  | "FRERE"
  | "SOEUR"
  | "TUTEUR_AFFECTATIF";
export type TypeAffection = "TITULAIRE" | "CO_TITULAIRE" | "ADJOINT" | "REMPLACANT";
export type TypeService =
  | "DIRECTION"
  | "PEDAGOGIE"
  | "SCOLARITE"
  | "RESSOURCES_HUMAINES"
  | "FINANCIER"
  | "INFORMATIQUE"
  | "LABORATOIRE"
  | "BIBLIOTHEQUE"
  | "MEDECINE_SCOLAIRE"
  | "HYGIENE"
  | "SECURITE";
export type CategoriePiece = "PHOTO_IDENTITE" | "ACTE_NAISSANCE" | "BULLETIN_ANTERIEUR" | "CERTIFICAT_SCOLARITE";
export type StatutEnseignant = "ACTIF" | "CONGE" | "DETACHE" | "RETRAITE" | "INACTIF";
export type Fonction = "TITULAIRE" | "REMPLACANT" | "VACATAIRE" | "CONTRACTUEL" | "STAGIAIRE" | "CHERCHEUR";

export interface FichierJoint {
  id?: string | null;
  categorie: CategoriePiece;
  nom: string;
  typeMime?: string | null;
  taille?: number | null;
  uri?: string | null;
  dateDepot?: string | null;
  deposePar?: string | null;
  documentOfficiel: boolean;
}

export interface DossierEleve {
  id: string;
  eleveId: string;
  reference: string;
  dateOuverture?: string | null;
  statut: StatutDossier;
  pieces: FichierJoint[];
}

export interface Inscription {
  idInscription: string;
  eleveId: string;
  anneeId: string;
  classeId: string;
  dateInscription?: string | null;
  statut: StatutInscription;
  redoublant: boolean;
  nouveau: boolean;
  ecoleOrigine?: string | null;
  moyenneAnneePrecedente?: number | null;
  classementAnneePrecedente?: number | null;
  fraisRegles: boolean;
  motifAnnulation?: string | null;
}

export interface CongeEtude {
  id: string;
  eleveId: string;
  type: TypeConge;
  dateDebut: string;
  dateFin?: string | null;
  motif?: string | null;
  justificatifId?: string | null;
  reprisePrevue?: string | null;
}

export interface SanctionDisciplinaire {
  id: string;
  eleveId: string;
  dateSanction: string;
  type: TypeSanction;
  motif?: string | null;
  dureeJours?: number | null;
  dateFin?: string | null;
  decidePar?: string | null;
  instance?: string | null;
}

export interface Eleve {
  idEleve: string;
  matricule: string;
  nom: string;
  postNom?: string | null;
  prenom: string;
  sexe: Sexe;
  dateNaissance?: string | null;
  lieuNaissance?: string | null;
  nationalite?: string | null;
  adresse?: Adresse | null;
  photo?: string | null;
  statut: StatutEleve;
  dateInscription?: string | null;
  dateSortie?: string | null;
  etablissementOrigine?: string | null;
  etablissementDestination?: string | null;
  motifTransfert?: string | null;
  statutDossier?: StatutDossier | null;
  dossier?: DossierEleve | null;
  inscriptions?: Inscription[];
  conges?: CongeEtude[];
  sanctions?: SanctionDisciplinaire[];
}

export interface Parent {
  id: string;
  matricule: string;
  nom: string;
  postNom?: string | null;
  prenom: string;
  sexe?: Sexe | null;
  telephone?: string | null;
  telephoneSecondaire?: string | null;
  email?: string | null;
  adresse?: Adresse | null;
  profession?: string | null;
  statut?: string | null;
}

export interface LienParentalite {
  id?: string | null;
  eleveId?: string | null;
  parentId: string;
  type: TypeLien;
  principal: boolean;
  payeur: boolean;
  receveurNotifications: boolean;
  dateDebut?: string | null;
}

export interface Enseignant {
  id: string;
  matricule: string;
  nom: string;
  postNom?: string | null;
  prenom: string;
  sexe: Sexe;
  dateNaissance?: string | null;
  telephone?: string | null;
  email?: string | null;
  adresse?: Adresse | null;
  diplome?: string | null;
  diplomePlus?: string | null;
  specialite: string[];
  dateRecrutement?: string | null;
  statut: StatutEnseignant;
  volumeHoraireHebdo?: number | null;
  serviceRattachementId?: string | null;
  photo?: string | null;
  pieces?: PieceIdentiteEnseignant[];
  affectations?: AffectationEnseignant[];
}

export interface PieceIdentiteEnseignant {
  id?: string | null;
  enseignantId?: string | null;
  type: string;
  numero?: string | null;
  dateDelivrance?: string | null;
  dateExpiration?: string | null;
  pays?: string | null;
}

export interface AffectationEnseignant {
  id: string;
  enseignantId: string;
  classeId: string;
  matiereId?: string | null;
  type: TypeAffection;
  titulaire: boolean;
  volumeHoraireHebdo?: number | null;
  dateDebut?: string | null;
  dateFin?: string | null;
  principale: boolean;
  remplacementDe?: string | null;
  motif?: string | null;
}

export interface Titularite {
  id?: string | null;
  classeId: string;
  enseignantId: string;
  dateDelegation?: string | null;
  dateRetrait?: string | null;
  type?: string | null;
  motif?: string | null;
}

export interface Agent {
  id: string;
  matricule: string;
  nom: string;
  postNom?: string | null;
  prenom: string;
  sexe?: Sexe | null;
  dateNaissance?: string | null;
  telephone?: string | null;
  email?: string | null;
  adresse?: Adresse | null;
  fonctionOfficielle?: string | null;
  dateRecrutement?: string | null;
  statut?: string | null;
  serviceRattachementId?: string | null;
}

export interface ServiceEtablissement {
  id: string;
  code: string;
  libelle: string;
  type: TypeService;
  parentId?: string | null;
  responsableId?: string | null;
  email?: string | null;
}

export interface ResultatValidation {
  valide: boolean;
  violations: string[];
}

// =====================================================================
// Pédagogie — lot 4c : academic-enrollment-service (8113)
// =====================================================================

export type StatutInscriptionUE =
  | "EN_ATTENTE"
  | "INSCRIT"
  | "EN_LISTE_ATTENTE"
  | "VALIDE"
  | "REFUSE"
  | "RATTAPE"
  | "ANNULE"
  | "TRANSFERE";
export type StatutGroupe = "OUVERT" | "COMPLET" | "CLOS" | "ARCHIVE";
export type TypeGroupe = "TD" | "TP" | "SEMINAIRE" | "ATELIER" | "EXERCICE" | "PROJET";
export type DecisionInscription = "ELIGIBLE" | "INELIGIBLE" | "LISTE_ATTENTE" | "OBLIGATOIRE";

export interface InscriptionUE {
  idInscription: string;
  eleveId?: string | null;
  programmeId?: string | null;
  promotionId?: string | null;
  ueId?: string | null;
  anneeId?: string | null;
  semestreId?: string | null;
  statut?: StatutInscriptionUE | null;
  dateInscription?: number[] | null;
  creditsReconnus?: number | null;
  moyenneAnneePrecedente?: number | null;
  moyenneUEPrecedente?: number | null;
  option?: string | null;
  motifRefus?: string | null;
  rangListeAttente?: number | null;
  valideLe?: number | null;
  validePar?: string | null;
  creditsAcquis: number;
  creditsRestants: number;
}

export interface MembreGroupe {
  idMembre: string;
  groupeId?: string | null;
  eleveId?: string | null;
  dateAffectation?: number[] | null;
  affectePar?: string | null;
  rang?: number | null;
}

export interface Groupe {
  idGroupe: string;
  code: string;
  libelle: string;
  type: TypeGroupe;
  coursId?: string | null;
  ueId?: string | null;
  classeId?: string | null;
  enseignantId?: string | null;
  salleId?: string | null;
  creneauId?: string | null;
  jour?: string | null;
  capacite: number;
  effectif: number;
  statut?: StatutGroupe | null;
  membres: MembreGroupe[];
  placesRestantes: number;
}

export interface RegleInscription {
  idRegle: string;
  libelle: string;
  programmeId?: string | null;
  condition?: string | null;
  creditsMin?: number | null;
  moyenneMin?: number | null;
  creneauAge?: number | null;
  priorite: number;
}

export interface Candidat {
  eleveId?: string | null;
  programmeId?: string | null;
  creditsValides?: number | null;
  creditsRequis?: number | null;
  moyenneGenerale?: number | null;
  age?: number | null;
  niveauxValides?: string[];
}

export interface ParcoursAcademique {
  eleveId?: string | null;
  programmeId?: string | null;
  creditsRequis?: number | null;
  inscriptions: InscriptionUE[];
  creditsAcquis: number;
  tauxProgression: number;
  moyennePonderee?: number | null;
  nombreValidees: number;
}

// =====================================================================
// Pédagogie — lot 4d : academic-attendance-service (8114)
// =====================================================================

export type TypeSeance = "COURS" | "TD" | "TP" | "EXAMEN" | "RATTRAPAGE" | "VISITE" | "SORTIE";
export type StatutSeance = "PLANIFIEE" | "EN_COURS" | "TERMINEE" | "ANNULEE" | "REPORTEE";
export type StatutPresence =
  | "PRESENT"
  | "ABSENT"
  | "RETARD"
  | "ABSENT_EXCUSE"
  | "ABSENT_NON_EXCUSE"
  | "CONGE"
  | "MALADE"
  | "SORTIE_AUTORISEE";
export type TypeAbsence =
  | "INJUSTIFIEE"
  | "JUSTIFIEE"
  | "MEDICALE"
  | "FAMILIALE"
  | "SCOLAIRE"
  | "DISCIPLINAIRE"
  | "RELIGIEUSE"
  | "AUTRE";
export type StatutFeuille = "BROUILLON" | "SOUMISE" | "VALIDEE" | "CORRIGEE" | "CLOTUREE";
export type MethodeSaisie = "MANUELLE" | "CODE_BARRE" | "BIOMETRIE" | "VOCAL" | "IMPORT" | "AUTOMATIQUE";
export type StatutJustificatif = "EN_ATTENTE" | "ACCEPTE" | "REJETE" | "EXPIRE";

export interface Seance {
  idSeance: string;
  classeId?: string | null;
  coursId?: string | null;
  enseignantId?: string | null;
  salleId?: string | null;
  anneeId?: string | null;
  periodeId?: string | null;
  date?: number[] | null;
  heureDebut?: number[] | null;
  heureFin?: number[] | null;
  volumeHoraireReel?: number | null;
  type?: TypeSeance | null;
  statut?: StatutSeance | null;
  annulee: boolean;
  motifAnnulation?: string | null;
  dureeReelle?: number;
}

export interface PresenceEleve {
  idPresence: string;
  feuilleId?: string | null;
  eleveId?: string | null;
  statut?: StatutPresence | null;
  heureArrivee?: number[] | null;
  minutesRetard: number;
  heureDepart?: number[] | null;
  departAnticipe: boolean;
  typeAbsence?: TypeAbsence | null;
  justificatifId?: string | null;
  saisiePar?: string | null;
  saisieLe?: number | null;
  justifiee: boolean;
  justifieeLe?: number | null;
  modifieeApresValidation: boolean;
}

export interface FeuillePresence {
  idFeuille: string;
  seanceId?: string | null;
  classeId?: string | null;
  coursId?: string | null;
  enseignantId?: string | null;
  date?: number[] | null;
  statut: StatutFeuille;
  methode?: MethodeSaisie | null;
  ouvertePar?: string | null;
  ouverteLe?: number | null;
  soumiseLe?: number | null;
  valideePar?: string | null;
  valideeLe?: number | null;
  effectifTheorique?: number | null;
  effectifPresent?: number | null;
  observations?: string | null;
  presences: PresenceEleve[];
  tauxPresence: number;
  nbAbsents: number;
}

export interface Justificatif {
  idJustificatif: string;
  eleveId?: string | null;
  type?: TypeAbsence | null;
  dateReference?: number[] | null;
  dateDepot?: number[] | null;
  motif?: string | null;
  fichierUri?: string | null;
  statut: StatutJustificatif;
  traitePar?: string | null;
  traiteLe?: number | null;
  motifRejet?: string | null;
}

export interface SuiviAssiduite {
  eleveId?: string | null;
  anneeId?: string | null;
  periodeId?: string | null;
  nbSeancesPrevues?: number | null;
  nbSeancesTenues?: number | null;
  nbHeuresPrevues: number;
  nbHeuresAbsentes: number;
  nbAbsencesJustifiees?: number | null;
  nbAbsencesInjustifiees?: number | null;
  nbRetards?: number | null;
  minutesRetardTotal?: number | null;
  tauxPresence: number;
  tauxAbsenceJustifiee: number;
  respecteRegle?: boolean | null;
  regleApplicable?: string | null;
  dernierCalcul?: number | null;
}

export interface MotifRequest {
  motif?: string | null;
}

export interface PointerRequest {
  eleveId?: string | null;
  statut?: StatutPresence | null;
}

export interface AppelGlobalRequest {
  statut?: StatutPresence | null;
}

export interface AbsenceRequest {
  eleveId?: string | null;
  typeAbsence?: TypeAbsence | null;
}

export interface RetardRequest {
  eleveId?: string | null;
  minutes?: number | null;
}

// =====================================================================
// Pédagogie — lot 4e : academic-assessment-service (8115)
// =====================================================================

export type StatutEvaluation = "BROUILLON" | "PLANIFIEE" | "OUVERTE" | "CLOTUREE" | "PUBLIEE" | "ANNULEE";
export type TypeBulletin = "TRIMESTRIEL" | "SEMESTRIEL" | "ANNUEL" | "PROVISOIRE" | "RATTRAPAGE" | "CERTIFICAT";
export type StatutBulletin = "BROUILLON" | "CALCULE" | "PUBLIE" | "GENERE" | "SIGNE" | "RECTIFIE" | "ANNULE";
export type StatutNote = "PROVISOIRE" | "DEFINITIVE" | "CONTESTEE" | "ANNULEE" | "EXCLUE";
export type TypeSourceNote = "EVALUATION_CONTINUE" | "SESSION_EXAMEN" | "RATTRAPAGE" | "CONTROLE_CONTINU" | "EXCEPTION";
export type StatutCoupon = "A_TIRER" | "TIRE" | "DETACHE" | "SIGNE_PAR_TUTEUR" | "RENDU" | "NON_RENDU" | "PERDU" | "INVALIDE";

export interface Evaluation {
  idEvaluation: string;
  reference: string | null;
  libelle: string | null;
  type: TypeEvaluation | null;
  classeId?: string | null;
  coursId?: string | null;
  ueId?: string | null;
  semestreId?: string | null;
  matiereId?: string | null;
  enseignantId?: string | null;
  anneeId?: string | null;
  periodeId?: string | null;
  date?: number[] | null;
  heureDebut?: number[] | null;
  duree?: number | null;
  salleId?: string | null;
  statut: StatutEvaluation | null;
  grilleId?: string | null;
  grilleVersion?: number | null;
  baremeType?: string | null;
  noteMax?: number | null;
  coefficientDeclare?: number | null;
  consignes?: string | null;
  anonymise: boolean;
  compteeDansMoyenne: boolean;
  declencheSession: boolean;
  annuleLe?: number | null;
  motifAnnulation?: string | null;
  creeLe?: number | null;
  feuille?: FeuilleNote | null;
  notes: Note[];
  maxNoteAtteignable?: number | null;
}

export interface FeuilleNote {
  id?: string | null;
  evaluationId?: string | null;
  statut?: StatutBulletin | null;
  nbNotesAttendues: number;
  nbNotesSaisies: number;
  nbAbsents: number;
  ouverteLe?: number | null;
  clotureeLe?: number | null;
  verrouillee: boolean;
  progression: number;
}

export interface Note {
  id?: string | null;
  evaluationId?: string | null;
  feuilleId?: string | null;
  eleveId?: string | null;
  valeur?: number | null;
  statut: StatutNote;
  absent: boolean;
  exclue: boolean;
  motifExclusion?: string | null;
  coefficientReel?: number | null;
  source: TypeSourceNote;
  saisiePar?: string | null;
  saisieLe?: number | null;
  approuvee: boolean;
  motifContestation?: string | null;
  dateContestation?: number | null;
  remplaceePar?: string | null;
  valeurEffective?: number | null;
}

export interface LigneBulletin {
  id?: string | null;
  bulletinId?: string | null;
  niveau?: string | null;
  code?: string | null;
  libelle?: string | null;
  parentCode?: string | null;
  coefficient: number;
  poidsNormalise?: number | null;
  moyennePeriode?: number | null;
  moyenneAnnuelle?: number | null;
  moyenneUe?: number | null;
  moyenneCours?: number | null;
  rang?: number | null;
  effectif?: number | null;
  mention?: string | null;
  appreciation?: string | null;
  absent: boolean;
  casParticulier?: string | null;
  ueId?: string | null;
  coursId?: string | null;
  matiereId?: string | null;
  contribution: number;
}

export interface SyntheseAbsences {
  seancesJustifiees: number;
  seancesInjustifiees: number;
  retardJours: number;
  retardHeures: number;
  regleApplicable?: string | null;
  respecteRegle: boolean;
  avertissement?: string | null;
  totalAbsences: number;
  compteRendu: string;
}

export interface CouponNotes {
  id?: string | null;
  numero?: string | null;
  bulletinId?: string | null;
  eleveId?: string | null;
  anneeId?: string | null;
  periodeId?: string | null;
  classe?: string | null;
  dateEdition?: number[] | null;
  statut: StatutCoupon;
  noteMoyenne?: number | null;
  mention?: string | null;
  urlQr?: string | null;
  codeBarre?: string | null;
  empreinte?: string | null;
  detacheLe?: number | null;
  detachePar?: string | null;
  signePar?: string | null;
  signeLe?: number | null;
  observation?: string | null;
  dateRendu?: number | null;
  texte: string;
}

export interface Bulletin {
  idBulletin: string;
  reference: string | null;
  eleveId?: string | null;
  anneeId?: string | null;
  periodeId?: string | null;
  classeId?: string | null;
  type?: TypeBulletin | null;
  statut: StatutBulletin | null;
  version: number;
  grilleId?: string | null;
  grilleVersion?: number | null;
  grilleDateActivation?: number[] | null;
  moyenneGenerale?: number | null;
  moyennePeriode?: number | null;
  moyenneAnnuelle?: number | null;
  rang?: number | null;
  effectif?: number | null;
  appreciationGenerale?: string | null;
  mentionGenerale?: string | null;
  decision?: string | null;
  nbAbsencesJustifiees: number;
  nbAbsencesInjustifiees: number;
  nbRetards: number;
  tauxPresence?: number | null;
  regleAssiduite?: string | null;
  respecteRegleAssiduite?: boolean | null;
  avertissementAssiduite?: string | null;
  retardJours?: number | null;
  retardHeures?: number | null;
  genereLe?: number | null;
  publieLe?: number | null;
  generePar?: string | null;
  rectifieLe?: number | null;
  motifRectification?: string | null;
  creePar?: string | null;
  lignes: LigneBulletin[];
  coupons: CouponNotes[];
  syntheseAbsences: SyntheseAbsences;
}

export interface ResultatMoyenne {
  moyenne?: number | null;
  noteMin?: number | null;
  noteMax?: number | null;
  nbNotes: number;
  effectif: number;
}

export interface Classement {
  rang: number;
  eleveId: string;
  moyenne: number;
}

export interface SaisirNoteRequest {
  eleveId: string;
  valeur: number;
}

export interface EleveRequest {
  eleveId: string;
}

export interface MotifEleveRequest {
  eleveId: string;
  motif: string;
}

export interface ObservationRequest {
  observation: string;
}

// =====================================================================
// Pédagogie — lot 4f : academic-exam-service (8116)
// =====================================================================

export type TypeSession =
  | "SESSION_TRIMESTRIELLE"
  | "SESSION_SEMESTRIELLE"
  | "SESSION_RATTRAPAGE"
  | "RESESSION_ANNUELLE"
  | "EXAMEN_BLANC"
  | "SESSION_EXTRAORDINAIRE";
export type StatutSession = "BROUILLON" | "PROGRAMMEE" | "EN_COURS" | "CORRECTION" | "DELIBERATION" | "CLOTUREE" | "ANNULEE";
export type TypeEpreuve = "ECRIT" | "ORAL" | "QCM" | "PRATIQUE" | "TP_NOTE" | "DISSERTATION" | "PROBLEME" | "MIXTE";
export type StatutEpreuve = "PROJETEE" | "VALIDEE" | "PROGRAMMEE" | "TENUE" | "ANNULEE";
export type StatutConvocation = "EMISE" | "NOTIFIEE" | "PRESENTE" | "ANNULEE";
export type StatutNoteExamen = "PROVISOIRE" | "DEFINITIVE" | "CONTESTEE" | "ANNULEE" | "RATTRAPAGE";
export type StatutCorrection = "OUVERTE" | "EN_COURS" | "CLOTUREE" | "REPRISE" | "HOMOLOGUEE";
export type StatutDeliberation = "PLANIFIEE" | "EN_COURS" | "CLOTUREE" | "REPRISE";
export type DecisionType = "ADMIS" | "ADMIS_MENTION" | "RATTAPAGE" | "REDOUBLANT" | "REJETE";
export type DecisionCode = "P" | "R" | "V" | "M" | "E" | "A" | "B" | "C" | "D";
export type StatutPV = "BROUILLON" | "SOUMIS" | "SIGNE" | "CONTESTE" | "HOMOLOGUE";
export type TypeRetardExam = "MATIN" | "APRES_MIDI" | "SOIR" | "MATIN_APRES_MIDI";

export interface SessionExamen {
  idSession: string;
  reference: string;
  libelle?: string | null;
  type?: TypeSession | null;
  anneeId?: string | null;
  periodeId?: string | null;
  classeId?: string | null;
  dateOuverture?: number[] | null;
  dateCloture?: number[] | null;
  statut: StatutSession;
  anneeUniversitaire: boolean;
  convocationEmise: boolean;
  procesVerbalId?: string | null;
  grilleDeliberation?: string | null;
  nbCandidatsAttendus?: number | null;
  nbCandidatsConvoques?: number | null;
  motifAnnulation?: string | null;
  annuleLe?: number | null;
  epreuves: Epreuve[];
  creneaux: CreneauExamen[];
  convocations: Convocation[];
  totalPointsPrevus?: number | null;
}

export interface Epreuve {
  idEpreuve: string;
  sessionId?: string | null;
  reference: string;
  libelle?: string | null;
  matiereId?: string | null;
  ueId?: string | null;
  type?: TypeEpreuve | null;
  date?: number[] | null;
  heureDebut?: number[] | null;
  dureeMinutes?: number | null;
  bareme?: string | null;
  coefficient?: number | null;
  statut: StatutEpreuve;
  comptabiliseDansMoyenne: boolean;
  periodeRattachement?: string | null;
  evaluationOrigine?: string | null;
  salleParDefaut?: string | null;
  consignes?: string | null;
  anonymise: boolean;
  noteEliminatoire?: number | null;
  declareeHorsMoyenne: boolean;
  pointsMax?: number | null;
}

export interface CreneauExamen {
  id?: string | null;
  sessionId?: string | null;
  date?: number[] | null;
  type?: TypeRetardExam | null;
  heureDebut?: number[] | null;
  heureFin?: number[] | null;
  ordre?: number | null;
  dureeMinutes?: number | null;
  libelle?: string | null;
}

export interface LigneConvocation {
  id?: string | null;
  convocationId?: string | null;
  epreuveId?: string | null;
  date?: number[] | null;
  heureDebut?: number[] | null;
  salle?: string | null;
  numeroPlace?: string | null;
  libelle?: string | null;
}

export interface Convocation {
  id?: string | null;
  sessionId?: string | null;
  eleveId?: string | null;
  numero?: string | null;
  date?: number[] | null;
  statut: StatutConvocation;
  genereeLe?: number | null;
  notifieeLe?: number | null;
  canal?: string | null;
  accuse: boolean;
  qrCode?: string | null;
  emplacement?: string | null;
  urlTelechargement?: string | null;
  signee: boolean;
  lignes: LigneConvocation[];
  planningDetaille?: LigneConvocation[];
}

export interface NoteExamen {
  id?: string | null;
  epreuveId?: string | null;
  eleveId?: string | null;
  valeur?: number | null;
  statut: StatutNoteExamen;
  absent: boolean;
  horsMoyenne: boolean;
  exclue: boolean;
  motifExclusion?: string | null;
  periodeImputation?: string | null;
  correcteur?: string | null;
  secondeCorrection: boolean;
  coefficient?: number | null;
  saisiLe?: number | null;
  valeurNormalisee?: number | null;
}

export interface Correction {
  id?: string | null;
  epreuveId?: string | null;
  correcteur?: string | null;
  date?: number[] | null;
  statut: StatutCorrection;
  nbNotesCorrigees: number;
  moyenneBrute?: number | null;
  moyenneRetenue?: number | null;
  plusForteMoyenne?: number | null;
  plusFaibleMoyenne?: number | null;
  ecartSignificatif: boolean;
}

export interface DecisionExamen {
  id?: string | null;
  deliberationId?: string | null;
  eleveId?: string | null;
  classeId?: string | null;
  moyenneGenerale?: number | null;
  moyennePeriode?: number | null;
  rang?: number | null;
  effectif?: number | null;
  tauxReussite?: number | null;
  nbAbsences?: number | null;
  decision?: DecisionType | null;
  mention?: string | null;
  seuilApplique?: string | null;
  cr?: DecisionCode | null;
  motif?: string | null;
  dateDecision?: number[] | null;
  notifie: boolean;
  dateNotification?: number | null;
  libelleDecision?: string | null;
  consequenceInscription?: string | null;
}

export interface DecisionUE {
  id?: string | null;
  deliberationId?: string | null;
  eleveId?: string | null;
  ueId?: string | null;
  moyenneUE?: number | null;
  credits?: number | null;
  decision?: DecisionType | null;
  details?: DetailUE[];
  creditsAcquis: number;
}

export interface DetailUE {
  id?: string | null;
  decisionUeId?: string | null;
  coursId?: string | null;
  moyenneCours?: number | null;
  coefficient?: number | null;
  admis: boolean;
}

export interface Deliberation {
  id?: string | null;
  sessionId?: string | null;
  reference: string;
  date?: number[] | null;
  statut: StatutDeliberation;
  president?: string | null;
  membres: string[];
  quorumAtteint: boolean;
  grilleVersion?: number | null;
  dateCloture?: number | null;
  decisions: DecisionExamen[];
  decisionsUE: DecisionUE[];
  tauxReussite: number;
}

export interface ResultatOfficiel {
  id?: string | null;
  decisionId?: string | null;
  reference: string;
  publie: boolean;
  datePublication?: number | null;
  generateur?: string | null;
  urlDocument?: string | null;
}

export interface Rattrapage {
  id?: string | null;
  sessionOrigineId?: string | null;
  sessionRattrapageId?: string | null;
  eleveId?: string | null;
  epreuvesConcernees: string[];
  convocationId?: string | null;
  nePasPresenter: boolean;
  motif?: string | null;
}

export interface Signature {
  id?: string | null;
  procesVerbalId?: string | null;
  signataire?: string | null;
  role?: string | null;
  dateSignature?: number | null;
  empreinte?: string | null;
  validee: boolean;
}

export interface ProcesVerbal {
  id?: string | null;
  sessionId?: string | null;
  reference: string;
  statut: StatutPV;
  dateRedaction?: number[] | null;
  redigePar?: string | null;
  presenceDirecteur: boolean;
  observations?: string | null;
  nbAbsences: number;
  nbIncidents: number;
  dateCloture?: number | null;
  motifContestation?: string | null;
  signatures: Signature[];
}

export interface SaisirNoteExamenRequest {
  eleveId: string;
  valeur: number;
  correcteur?: string | null;
}

export interface DeciderRequest {
  classeId?: string | null;
  eleveId?: string | null;
}

export interface CanalRequest {
  canal?: string | null;
}

export interface RoleRequest {
  role?: string | null;
}

// =====================================================================
// Pédagogie — lot 4g : academic-fees-service (8117)
// =====================================================================

export type StatutGrille = "BROUILLON" | "ACTIVE" | "ARCHIVEE";
export type TypeReduction = "POURCENTAGE" | "MONTANT_FIXE" | "TRANCHE" | "TARIF_REDUIT";
export type StatutAvis =
  | "BROUILLON"
  | "EMIS"
  | "PARTIELLEMENT_REGLE"
  | "REGLE"
  | "ANNULE"
  | "EN_RETARD"
  | "TRANSFERE";
export type StatutEcheance = "A_VENIR" | "ECHUE" | "REGLEE" | "PARTIELLEMENT_REGLEE" | "ANNULEE" | "REPORTEE";
export type StatutPaiement = "EN_ATTENTE" | "VALIDE" | "REJETE" | "ANNULE" | "REMBOURSE" | "A_VERIFIER";
export type MoyenPaiement =
  | "ESPECES"
  | "MOBILE_MONEY"
  | "VIREMENT"
  | "CHEQUE"
  | "CARTE"
  | "PRELEVEMENT"
  | "COMPENSATION"
  | "DEVIS_ELEVE";
export type StatutCouponPaiement = "A_TIRER" | "TIRE" | "REMIS" | "ARCHIVE" | "ANNULE";
export type TypeFrais =
  | "INSCRIPTION"
  | "SCOLARITE"
  | "CANTINE"
  | "TRANSPORT"
  | "FOURNITURES"
  | "EXAMEN"
  | "TENUE"
  | "GARDERIE"
  | "EXCURSION"
  | "LABORATOIRE"
  | "BIBLIOTHEQUE"
  | "DIPLOME"
  | "RATTRAPAGE"
  | "AUTRE";
export type CategorieFrais = "OBLIGATOIRE" | "OPTIONNEL" | "FACULTATIF" | "SANCTION";
export type TypeBeneficiaire = "ELEVE" | "TIERS_PAYEUR" | "EMPLOYEUR" | "ANCIENNE" | "AUTRE";
export type Periodicite = "PONCTUELLE" | "MENSUELLE" | "TRIMESTRIELLE" | "SEMESTRIELLE" | "ANNUELLE";

export interface ConditionReduction {
  critere: string;
  operateur: string;
  valeur: string;
  cumulable: boolean;
}

export interface Reduction {
  id?: string | null;
  grilleId?: string | null;
  code: string;
  libelle: string;
  type: TypeReduction;
  valeur: number;
  cumulable: boolean;
  conditions: ConditionReduction[];
}

export interface LigneGrille {
  id?: string | null;
  grilleId?: string | null;
  typeFrais: TypeFrais;
  categorie?: CategorieFrais | null;
  libelle: string;
  montant: number;
  montantPromo?: number | null;
  devise?: string | null;
  obligatoire: boolean;
  periodicite?: Periodicite | null;
  jour?: number | null;
  nbEcheances?: number | null;
  ordre?: number | null;
  montantEffectif?: number | null;
}

export interface GrilleTarifaire {
  id: string;
  code: string;
  libelle?: string | null;
  annee?: string | null;
  profil?: string | null;
  statut: StatutGrille;
  devise?: string | null;
  dateActivation?: number[] | null;
  active: boolean;
  version: number;
  modifiable: boolean;
  lignes: LigneGrille[];
  reductions: Reduction[];
}

export interface Echeance {
  id?: string | null;
  ligneId?: string | null;
  numero?: number | null;
  dateEcheance?: number[] | null;
  montant?: number | null;
  montantRegle: number;
  statut?: StatutEcheance | null;
  dateReglement?: number[] | null;
  solde?: number | null;
}

export interface LigneFrais {
  id?: string | null;
  avisId?: string | null;
  typeFrais?: TypeFrais | null;
  libelle?: string | null;
  montantBrut?: number | null;
  tauxReduction?: number | null;
  montantReduction?: number | null;
  montantNet?: number | null;
  nbEcheances?: number | null;
  anneeReference?: string | null;
  echeances: Echeance[];
  montantDu?: number | null;
}

export interface AvisPaiement {
  idAvis: string;
  reference: string;
  eleve?: string | null;
  annee?: string | null;
  classe?: string | null;
  grilleId?: string | null;
  dateEmission?: number[] | null;
  dateEcheanceGlobale?: number[] | null;
  statut: StatutAvis;
  montantBrut?: number | null;
  montantReduction?: number | null;
  montantNet?: number | null;
  montantRegle?: number | null;
  soldeDu?: number | null;
  devise?: string | null;
  tauxChange?: number | null;
  journalier?: string | null;
  ecritureGeneree?: string | null;
  motifAnnulation?: string | null;
  lignes: LigneFrais[];
}

export interface Imputation {
  paiementId?: string | null;
  echeanceId?: string | null;
  montant?: number | null;
  dateImputation?: number[] | null;
  ordre?: number | null;
}

export interface PayementMobileMoney {
  operateur?: string | null;
  numeroTelephone?: string | null;
  referenceTransaction?: string | null;
  dateTransaction?: number | null;
  statutTransaction?: string | null;
  frais?: number | null;
  montantRecu?: number | null;
}

export interface PayementVirement {
  banque?: string | null;
  numeroCompte?: string | null;
  referenceVirement?: string | null;
  dateValeur?: number[] | null;
  commission?: number | null;
}

export interface PayementEspeces {
  caja?: string | null;
  numeroRecepisse?: string | null;
  agent?: string | null;
  contresigne: boolean;
}

export interface Remboursement {
  id?: string | null;
  paiementId?: string | null;
  montant?: number | null;
  motif?: string | null;
  date?: number[] | null;
  moyen?: MoyenPaiement | null;
  referenceTransaction?: string | null;
}

export interface Paiement {
  idPaiement: string;
  reference: string;
  avis?: string | null;
  eleve?: string | null;
  date?: number[] | null;
  montant: number;
  moyen?: MoyenPaiement | null;
  compteDebit?: string | null;
  referenceTransaction?: string | null;
  idEvenement?: string | null;
  beneficiaire?: TypeBeneficiaire | null;
  referenceBeneficiaire?: string | null;
  statut: StatutPaiement;
  encaissePar?: string | null;
  resteARembourser: number;
  couponGenere?: string | null;
  ecritureGeneree?: string | null;
  motifAnnulation?: string | null;
  motifRejet?: string | null;
  dateValidation?: number | null;
  imputations: Imputation[];
  mobileMoney?: PayementMobileMoney | null;
  virement?: PayementVirement | null;
  especes?: PayementEspeces | null;
  remboursement?: Remboursement | null;
}

export interface CouponPaiement {
  id?: string | null;
  numero: string;
  sequence?: number | null;
  annee?: string | null;
  eleve?: string | null;
  paiement?: string | null;
  avis?: string | null;
  ligneFrais?: string | null;
  dateEdition?: number[] | null;
  montant?: number | null;
  libelle?: string | null;
  moyen?: MoyenPaiement | null;
  statut: StatutCouponPaiement;
  devise?: string | null;
  mention?: string | null;
  urlQr?: string | null;
  codeBarre?: string | null;
  empreinte?: string | null;
  remisLe?: number | null;
  remisA?: string | null;
  referenceSignature?: string | null;
  annule: boolean;
  motifAnnulation?: string | null;
  texte: string;
}

export interface Journalier {
  id: string;
  date?: number[] | null;
  site?: string | null;
  statut: string;
  fond?: number | null;
}

export interface ContexteReduction {
  niveau?: string | null;
  classe?: string | null;
  nombreEnfantsScolaires?: number | null;
  statutSocial?: string | null;
  bourse: boolean;
  inscriptionTardive: boolean;
  eleveRedoublant: boolean;
}

export interface RemiseRequest {
  reductionId?: string | null;
  contexte?: ContexteReduction | null;
}

export interface DestinataireRequest {
  destinataire?: string | null;
}

// =====================================================================
// BOULANGERIE — lot 1 (produit-service)
// =====================================================================

export type UniteVente = "PIECE" | "LOT" | "KG" | "GRAMME" | "TRANCHE" | "SACHET" | "PLATEAU";

export type Allergene =
  | "GLUTEN"
  | "LACTOSE"
  | "OEUF"
  | "ARACHIDE"
  | "SOJA"
  | "NOIX"
  | "POISSON"
  | "CRUSTACE"
  | "SESAME"
  | "SULFITE";

export type NatureCout =
  | "MATIERE"
  | "MAIN_OEUVRE"
  | "ENERGIE"
  | "EMBALLAGE"
  | "AMORTISSEMENT"
  | "AUTRE";

export interface Gamme {
  id?: string | null;
  code: string;
  libelle?: string | null;
  ordre?: number | null;
  actif?: boolean;
}

export interface CategorieProduit {
  id?: string | null;
  code: string;
  libelle?: string | null;
  ordre?: number | null;
  actif?: boolean;
}

export interface Produit {
  idProduit?: string | null;
  code: string;
  designation: string;
  gammeCode?: string | null;
  categorieCode?: string | null;
  articleCode?: string | null;
  uniteVente?: UniteVente | null;
  poidsUnitaireGrammes?: number | null;
  prixVenteHT?: number | null;
  tauxTva?: number | null;
  codeBarre?: string | null;
  dlcHeures?: number | null;
  allergenes?: Allergene[] | null;
  fabricable?: boolean;
  vendableAuDetail?: boolean;
  actif?: boolean;
  prixTTC?: number | null;
}

export interface IngredientRecette {
  id?: string | null;
  recetteId?: string | null;
  articleCode: string;
  libelle?: string | null;
  quantite?: number | null;
  unite?: string | null;
  tauxPerte?: number | null;
  ordre?: number | null;
  comptabiliseCout?: boolean;
  quantiteNette?: number | null;
  quantiteBrute?: number | null;
}

export interface LigneCout {
  id?: string | null;
  recetteId?: string | null;
  nature?: NatureCout | null;
  libelle?: string | null;
  montant?: number | null;
}

export interface FicheRecette {
  idRecette?: string | null;
  code: string;
  produitCode: string;
  libelle?: string | null;
  version?: number;
  rendementQuantite?: number | null;
  rendementUnite?: UniteVente | null;
  tempsPreparationMin?: number | null;
  tempsCuissonMin?: number | null;
  temperatureCuissonC?: number | null;
  instructions?: string | null;
  active?: boolean;
  modifiable?: boolean;
  ingredients?: IngredientRecette[] | null;
  lignesCout?: LigneCout[] | null;
}

export interface BesoinMatiere {
  articleCode: string;
  libelle?: string | null;
  quantiteNette: number;
  quantiteBrute: number;
  unite?: string | null;
}

export interface CoutRecette {
  coutMatiere: number;
  coutRevient: number;
  coutMatiereDisponible: boolean;
}

// =====================================================================
// BOULANGERIE — lot 2 (production-service)
// =====================================================================

export type StatutPlan = "BROUILLON" | "VALIDE" | "LANCE" | "CLOTURE";

export type StatutFournee =
  | "PLANIFIEE"
  | "EN_COURS"
  | "CUITE"
  | "VALIDEE"
  | "ANNULEE";

export type TypePerte =
  | "PATE_PERDUE"
  | "CUISSON_RATEE"
  | "CASSE"
  | "INVENDU_DESTRUCTION"
  | "DEMARQUE"
  | "ECHANTILLON";

export interface LignePlan {
  id?: string | null;
  planId?: string | null;
  recetteCode: string;
  versionRecette?: number | null;
  quantitePrevue: number;
  produitCode?: string | null;
  fourCode?: string | null;
}

export interface PlanProduction {
  idPlan?: string | null;
  date?: string | null;
  atelierCode?: string | null;
  statut?: StatutPlan;
  lignes?: LignePlan[] | null;
  besoinsMatiere?: BesoinMatiere[] | null;
}

export interface LigneConsommation {
  id?: string | null;
  fourneeId?: string | null;
  articleCode: string;
  lotNumero?: string | null;
  quantiteTheorique?: number | null;
  quantiteReelle?: number | null;
  prixMoyen?: number | null;
  ecart?: number | null;
}

export interface PerteProduction {
  idPerte?: string | null;
  fourneeId?: string | null;
  articleCode?: string | null;
  quantite?: number | null;
  typePerte?: TypePerte | null;
  motif?: string | null;
  valorisation?: number | null;
}

export interface Fournee {
  idFournee?: string | null;
  numero?: string | null;
  recetteCode: string;
  versionRecette?: number | null;
  produitCode?: string | null;
  fourCode?: string | null;
  operateurId?: string | null;
  dateDebut?: string | null;
  dateFin?: string | null;
  quantitePrevue?: number | null;
  quantiteRealisee?: number | null;
  statut?: StatutFournee;
  motifAnnulation?: string | null;
  consommations?: LigneConsommation[] | null;
  pertes?: PerteProduction[] | null;
  rendement?: number | null;
  ecartMatiere?: number | null;
}