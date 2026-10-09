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