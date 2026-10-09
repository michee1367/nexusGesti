export type ProfilEtablissement = "PRIMAIRE" | "SECONDAIRE" | "UNIVERSITAIRE";
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
  | "EXAMEN_BLANC";
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