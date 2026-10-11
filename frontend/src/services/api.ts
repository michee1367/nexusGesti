import {
  ApiError,
  type AlerteStock,
  type AnneeScolaire,
  type AuditEntry,
  type AuditSearchCriteria,
  type Bareme,
  type ConfigCascade,
  type Cycle,
  type Etablissement,
  type Filiere,
  type Grille,
  type GrilleConformite,
  type GrilleNotation,
  type GrillePreset,
  type GrilleStock,
  type GrilleType,
  type LoginResponse,
  type Mention,
  type Notification,
  type NotificationChannel,
  type NotificationPriority,
  type Parametre,
  type Periode,
  type Permission,
  type ProfilEtablissement,
  type Programme,
  type Referentiel,
  type ReferentielItem,
  type RegleAppreciation,
  type RegleAssiduite,
  type ReglePonderation,
  type ReportFormat,
  type ReportRequest,
  type ReportType,
  type Role,
  type SeuilDecision,
  type Site,
  type StatutEtablissement,
  type TypeCompte,
  type Utilisateur,
} from "@/types";
import type {
  Niveau,
  Classe,
  Promotion,
  Semestre,
  UE,
  Matiere,
  CoursECUE,
  Salle,
  Creneau,
  RegimeHoraire,
  EmploiDuTemps,
  PlageEmploi,
  ConflitEmploi,
  Eleve,
  DossierEleve,
  FichierJoint,
  Inscription,
  CongeEtude,
  SanctionDisciplinaire,
  Parent,
  LienParentalite,
  Enseignant,
  PieceIdentiteEnseignant,
  AffectationEnseignant,
  Titularite,
  Agent,
  ServiceEtablissement,
  ResultatValidation,
  StatutEnseignant,
} from "@/types";
import type {
  InscriptionUE,
  MembreGroupe,
  Groupe,
  RegleInscription,
  Candidat,
  ParcoursAcademique,
  Seance,
  FeuillePresence,
  PresenceEleve,
  Justificatif,
  SuiviAssiduite,
  PointerRequest,
  AppelGlobalRequest,
  AbsenceRequest,
  RetardRequest,
  Evaluation,
  FeuilleNote,
  Note,
  LigneBulletin,
  SyntheseAbsences,
  CouponNotes,
  Bulletin,
  ResultatMoyenne,
  Classement,
  SaisirNoteRequest,
  EleveRequest,
  MotifEleveRequest,
  SessionExamen,
  Epreuve,
  CreneauExamen,
  Convocation,
  NoteExamen,
  Correction,
  Deliberation,
  DecisionExamen,
  DecisionUE,
  ResultatOfficiel,
  Rattrapage,
  ProcesVerbal,
  SaisirNoteExamenRequest,
  DeciderRequest,
  Reduction,
  LigneGrille,
  GrilleTarifaire,
  AvisPaiement,
  Paiement,
  CouponPaiement,
  Journalier,
  RemiseRequest,
} from "@/types";

export const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api/v1";
export const TENANT_KEY = "x_tenant_id";

const UUID_PATTERN = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

export function isValidUuid(value: string): boolean {
  return UUID_PATTERN.test(value);
}

export function getAccessToken(): string | null {
  if (typeof window === "undefined") return null;
  return window.localStorage.getItem("token");
}

export function getRefreshToken(): string | null {
  if (typeof window === "undefined") return null;
  return window.localStorage.getItem("refresh_token");
}

function saveTokens(login: LoginResponse): void {
  window.localStorage.setItem("token", login.accessToken);
  window.localStorage.setItem("refresh_token", login.refreshToken);
}

export function clearSession(): void {
  window.localStorage.removeItem("token");
  window.localStorage.removeItem("refresh_token");
}

export function getTenantId(): string | null {
  if (typeof window === "undefined") return null;
  return window.localStorage.getItem(TENANT_KEY);
}

export function setTenantId(id: string): void {
  window.localStorage.setItem(TENANT_KEY, id);
}

const isAuthPath = (path: string): boolean =>
  path.startsWith("/socle/auth/") || path.startsWith("/auth/");

async function parseJson(response: Response): Promise<unknown> {
  const text = await response.text();
  if (!text) return null;
  try {
    return JSON.parse(text);
  } catch {
    return { message: text };
  }
}

function toApiError(response: Response, body: { code?: string; message?: string }): ApiError {
  const details = Array.isArray((body as { details?: string[] }).details)
    ? (body as { details: string[] }).details.join(" · ")
    : null;
  const message = [body.message, details].filter(Boolean).join(" · ");
  return new ApiError(response.status, message || `Erreur ${response.status}`, body.code);
}

async function rawFetch<T>(
  path: string,
  init: RequestInit = {},
  retried = false,
): Promise<T> {
  const headers = new Headers(init.headers);
  headers.set("Accept", "application/json");
  const token = getAccessToken();
  if (token) headers.set("Authorization", `Bearer ${token}`);
  const tenantId = getTenantId();
  if (tenantId) headers.set("X-Tenant-Id", tenantId);

  let response: Response;
  try {
    response = await fetch(`${BASE_URL}${path}`, { ...init, headers });
  } catch {
    throw new ApiError(0, "Impossible de joindre l'API", "NETWORK_ERROR");
  }

  if (response.status === 401 && !retried && !isAuthPath(path)) {
    const refreshed = await tryRefresh();
    if (refreshed) return rawFetch<T>(path, init, true);
  }

  const body = await parseJson(response);
  if (!response.ok) throw toApiError(response, (body as { code?: string; message?: string }) ?? {});

  return body as T;
}

async function tryRefresh(): Promise<boolean> {
  const refreshToken = getRefreshToken();
  if (!refreshToken) {
    clearSession();
    return false;
  }
  try {
    const res = await fetch(`${BASE_URL}/socle/auth/refresh`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
        "X-Tenant-Id": getTenantId() ?? "",
      },
      body: JSON.stringify({ refreshToken }),
    });
    if (!res.ok) {
      clearSession();
      return false;
    }
    const data = (await res.json()) as LoginResponse;
    saveTokens(data);
    return true;
  } catch {
    clearSession();
    return false;
  }
}

function jsonInit(method: string, body?: unknown): RequestInit {
  return {
    method,
    headers: { "Content-Type": "application/json" },
    body: body === undefined ? undefined : JSON.stringify(body),
  };
}

export const authApi = {
  async login(email: string, password: string, deviceFingerprint?: string): Promise<LoginResponse> {
    const res = await rawFetch<LoginResponse>("/socle/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password, deviceFingerprint }),
    });
    saveTokens(res);
    return res;
  },

  async logout(accessToken?: string, refreshToken?: string): Promise<void> {
    try {
      await rawFetch<void>("/socle/auth/logout", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ accessToken, refreshToken }),
      });
    } finally {
      clearSession();
    }
  },

  async register(payload: {
    email: string;
    phone: string;
    password: string;
    firstName: string;
    lastName: string;
    countryCode: string;
    acceptedTerms: boolean;
  }): Promise<{ userId: string; status: string }> {
    return rawFetch("/socle/auth/register", jsonInit("POST", payload));
  },
};

export const tenantApi = {
  create(payload: Partial<Etablissement> & { code: string; denomination: string; profil: ProfilEtablissement }): Promise<Etablissement> {
    return rawFetch("/socle/tenants", jsonInit("POST", payload));
  },

  get(id: string): Promise<Etablissement> {
    return rawFetch(`/socle/tenants/${id}`);
  },

  changeStatut(id: string, statut: StatutEtablissement, motif?: string): Promise<Etablissement> {
    return rawFetch(`/socle/tenants/${id}/statut`, jsonInit("PATCH", { statut, motif }));
  },
};

export const etablissementApi = {
  sites: {
    list: () => rawFetch<Site[]>("/socle/etablissements/sites"),
    create: (body: Partial<Site>) => rawFetch<Site>("/socle/etablissements/sites", jsonInit("POST", body)),
    update: (siteId: string, body: Partial<Site>) =>
      rawFetch<Site>(`/socle/etablissements/sites/${siteId}`, jsonInit("PUT", body)),
    remove: (siteId: string) => rawFetch<void>(`/socle/etablissements/sites/${siteId}`, { method: "DELETE" }),
  },

  filieres: {
    list: () => rawFetch<Filiere[]>("/socle/etablissements/filieres"),
    create: (body: Partial<Filiere>) =>
      rawFetch<Filiere>("/socle/etablissements/filieres", jsonInit("POST", body)),
    update: (filiereId: string, body: Partial<Filiere>) =>
      rawFetch<Filiere>(`/socle/etablissements/filieres/${filiereId}`, jsonInit("PUT", body)),
    remove: (filiereId: string) =>
      rawFetch<void>(`/socle/etablissements/filieres/${filiereId}`, { method: "DELETE" }),
    addProgramme: (filiereId: string, body: Partial<Programme>) =>
      rawFetch<Programme>(
        `/socle/etablissements/filieres/${filiereId}/programmes`,
        jsonInit("POST", body),
      ),
  },

  programmes: {
    list: () => rawFetch<Programme[]>("/socle/etablissements/programmes"),
    update: (programmeId: string, body: Partial<Programme>) =>
      rawFetch<Programme>(`/socle/etablissements/programmes/${programmeId}`, jsonInit("PUT", body)),
    remove: (programmeId: string) =>
      rawFetch<void>(`/socle/etablissements/programmes/${programmeId}`, { method: "DELETE" }),
  },

  cycles: {
    list: () => rawFetch<Cycle[]>("/socle/etablissements/cycles"),
    create: (body: Partial<Cycle>) => rawFetch<Cycle>("/socle/etablissements/cycles", jsonInit("POST", body)),
    update: (cycleId: string, body: Partial<Cycle>) =>
      rawFetch<Cycle>(`/socle/etablissements/cycles/${cycleId}`, jsonInit("PUT", body)),
    remove: (cycleId: string) =>
      rawFetch<void>(`/socle/etablissements/cycles/${cycleId}`, { method: "DELETE" }),
  },

  parametres: {
    list: () => rawFetch<Parametre[]>("/socle/etablissements/parametres"),
    create: (body: Partial<Parametre>) =>
      rawFetch<Parametre>("/socle/etablissements/parametres", jsonInit("POST", body)),
    update: (parametreId: string, body: Partial<Parametre>) =>
      rawFetch<Parametre>(
        `/socle/etablissements/parametres/${parametreId}`,
        jsonInit("PUT", body),
      ),
    remove: (parametreId: string) =>
      rawFetch<void>(`/socle/etablissements/parametres/${parametreId}`, { method: "DELETE" }),
  },

  presets: {
    list: () => rawFetch<GrillePreset[]>("/socle/etablissements/grille-presets"),
    create: (body: Partial<GrillePreset>) =>
      rawFetch<GrillePreset>("/socle/etablissements/grille-presets", jsonInit("POST", body)),
    dupliquer: (presetId: string, libelle: string) =>
      rawFetch<GrillePreset>(
        `/socle/etablissements/grille-presets/${presetId}/dupliquer`,
        jsonInit("POST", { libelle }),
      ),
    remove: (presetId: string) =>
      rawFetch<void>(`/socle/etablissements/grille-presets/${presetId}`, { method: "DELETE" }),
  },
};

export const anneeApi = {
  list: () => rawFetch<AnneeScolaire[]>("/socle/annees"),
  courante: () => rawFetch<AnneeScolaire>("/socle/annees/courante"),
  get: (anneeId: string) => rawFetch<AnneeScolaire>(`/socle/annees/${anneeId}`),
  create: (body: Partial<AnneeScolaire>) => rawFetch<AnneeScolaire>("/socle/annees", jsonInit("POST", body)),
  update: (anneeId: string, body: Partial<AnneeScolaire>) =>
    rawFetch<AnneeScolaire>(`/socle/annees/${anneeId}`, jsonInit("PUT", body)),
  ouvrir: (anneeId: string) => rawFetch<AnneeScolaire>(`/socle/annees/${anneeId}/ouvrir`, { method: "POST" }),
  cloturer: (anneeId: string) =>
    rawFetch<AnneeScolaire>(`/socle/annees/${anneeId}/cloturer`, { method: "POST" }),
  listPeriodes: (anneeId: string) => rawFetch<Periode[]>(`/socle/annees/${anneeId}/periodes`),
  addPeriode: (anneeId: string, body: Partial<Periode>) =>
    rawFetch<Periode>(`/socle/annees/${anneeId}/periodes`, jsonInit("POST", body)),
  cloturerPeriode: (anneeId: string, periodeId: string) =>
    rawFetch<Periode>(`/socle/annees/${anneeId}/periodes/${periodeId}/cloturer`, { method: "POST" }),
};

export const grilleApi = {
  list: (type: GrilleType) => rawFetch<Grille[]>(`/socle/grilles/${type}`),
  get: <T extends Grille>(type: GrilleType, id: string) => rawFetch<T>(`/socle/grilles/${type}/${id}`),
  applicable: (type: GrilleType, profil: ProfilEtablissement) =>
    rawFetch<Grille>(`/socle/grilles/${type}/applicable?profil=${profil}`),
  create: <T extends Grille>(type: GrilleType, body: Partial<Grille>) =>
    rawFetch<T>(`/socle/grilles/${type}`, jsonInit("POST", body)),

  activer: (type: GrilleType, id: string) =>
    rawFetch<Grille>(`/socle/grilles/${type}/${id}/activer`, { method: "POST" }),
  figer: (type: GrilleType, id: string) =>
    rawFetch<Grille>(`/socle/grilles/${type}/${id}/figer`, { method: "POST" }),
  dupliquer: (type: GrilleType, id: string, libelle?: string) =>
    rawFetch<Grille>(`/socle/grilles/${type}/${id}/dupliquer`, jsonInit("POST", { libelle })),

  notation: {
    get: (id: string) => rawFetch<GrilleNotation>(`/socle/grilles/notation/${id}`),
    addBareme: (id: string, body: Bareme) =>
      rawFetch<Bareme>(`/socle/grilles/notation/${id}/baremes`, jsonInit("POST", body)),
    setConfigCascade: (id: string, body: ConfigCascade) =>
      rawFetch<ConfigCascade>(`/socle/grilles/notation/${id}/config-cascade`, jsonInit("PUT", body)),
    addSeuil: (id: string, body: SeuilDecision) =>
      rawFetch<SeuilDecision>(`/socle/grilles/notation/${id}/seuils-decision`, jsonInit("POST", body)),
    addRegleAssiduite: (id: string, body: RegleAssiduite) =>
      rawFetch<RegleAssiduite>(`/socle/grilles/notation/${id}/regles-assiduite`, jsonInit("POST", body)),
    addReglePonderation: (id: string, body: ReglePonderation) =>
      rawFetch<ReglePonderation>(`/socle/grilles/notation/${id}/regles-ponderation`, jsonInit("POST", body)),
    addMention: (id: string, body: Mention) =>
      rawFetch<Mention>(`/socle/grilles/notation/${id}/mentions`, jsonInit("POST", body)),
    addAppreciation: (id: string, body: RegleAppreciation) =>
      rawFetch<RegleAppreciation>(`/socle/grilles/notation/${id}/appreciations`, jsonInit("POST", body)),
  },

  stock: {
    get: (id: string) => rawFetch<GrilleStock>(`/socle/grilles/stock/${id}`),
    addAlerte: (id: string, body: Partial<AlerteStock>) =>
      rawFetch<AlerteStock>(`/socle/grilles/stock/${id}/alertes`, jsonInit("POST", body)),
    acquitterAlerte: (id: string, alerteId: string) =>
      rawFetch<AlerteStock>(`/socle/grilles/stock/${id}/alertes/${alerteId}/acquitter`, {
        method: "POST",
      }),
  },

  conformite: {
    get: (id: string) => rawFetch<GrilleConformite>(`/socle/grilles/conformite/${id}`),
  },
};

export const referentielApi = {
  list: () => rawFetch<Referentiel[]>("/socle/referentiels"),
  get: (id: string) => rawFetch<Referentiel>(`/socle/referentiels/${id}`),
  getByCode: (code: string) => rawFetch<Referentiel>(`/socle/referentiels/code/${code}`),
  addItem: (referentielId: string, body: Partial<ReferentielItem>) =>
    rawFetch<ReferentielItem>(`/socle/referentiels/${referentielId}/items`, jsonInit("POST", body)),
  desactiverItem: (referentielId: string, itemId: string) =>
    rawFetch<void>(`/socle/referentiels/${referentielId}/items/${itemId}/desactiver`, { method: "POST" }),
};

export const notificationApi = {
  list: () => rawFetch<Notification[]>("/socle/notifications"),
  get: (id: string) => rawFetch<Notification>(`/socle/notifications/${id}`),
  send: (body: {
    userId: string;
    channel: NotificationChannel;
    templateCode?: string;
    recipientAddress: string;
    subject?: string;
    variables?: Record<string, string>;
    priority?: NotificationPriority;
  }) => rawFetch<Notification>("/socle/notifications/send", jsonInit("POST", body)),
};

export const auditApi = {
  search: (criteria: AuditSearchCriteria = {}) => {
    const params = new URLSearchParams();
    for (const [key, value] of Object.entries(criteria)) {
      if (value !== undefined && value !== null && value !== "") params.set(key, String(value));
    }
    const qs = params.toString();
    return rawFetch<AuditEntry[]>(`/socle/audit/search${qs ? `?${qs}` : ""}`);
  },
  get: (id: string) => rawFetch<AuditEntry>(`/socle/audit/${id}`),
};

export const reportApi = {
  request: (body: { reportType: ReportType; format: ReportFormat; parameters?: Record<string, string> }) =>
    rawFetch<ReportRequest>("/socle/reports/request", jsonInit("POST", body)),
  list: () => rawFetch<ReportRequest[]>("/socle/reports"),
  download: (id: string) => rawFetch<{ downloadUrl: string }>(`/socle/reports/${id}/download`),
};

export interface CreerUtilisateurBody {
  email: string;
  telephone?: string;
  nom?: string;
  postNom?: string;
  prenom?: string;
  sexe?: string;
  dateNaissance?: string;
  nationalite?: string;
  motDePasse?: string;
  roles?: string[];
  typeCompte?: TypeCompte;
}

export interface ModifierUtilisateurBody {
  telephone?: string;
  nom?: string;
  postNom?: string;
  prenom?: string;
  sexe?: string;
  dateNaissance?: string;
  nationalite?: string;
  photoUrl?: string;
  languePreferee?: string;
}

const queryString = (q?: string): string =>
  q && q.trim() ? `?q=${encodeURIComponent(q.trim())}` : "";

export const utilisateurApi = {
  list: (q?: string) => rawFetch<Utilisateur[]>(`/socle/utilisateurs${queryString(q)}`),
  get: (id: string) => rawFetch<Utilisateur>(`/socle/utilisateurs/${id}`),
  create: (body: CreerUtilisateurBody) => rawFetch<Utilisateur>("/socle/utilisateurs", jsonInit("POST", body)),
  inviter: (body: { email: string; nom?: string; prenom?: string; roles?: string[] }) =>
    rawFetch<Utilisateur>("/socle/utilisateurs/invitations", jsonInit("POST", body)),
  modifier: (id: string, body: ModifierUtilisateurBody) =>
    rawFetch<Utilisateur>(`/socle/utilisateurs/${id}`, jsonInit("PATCH", body)),
  activer: (id: string) => rawFetch<Utilisateur>(`/socle/utilisateurs/${id}/activer`, { method: "POST" }),
  desactiver: (id: string, motif: string) =>
    rawFetch<Utilisateur>(`/socle/utilisateurs/${id}/desactiver`, jsonInit("POST", { motif })),
  suspendre: (id: string, jusquau?: string) =>
    rawFetch<Utilisateur>(`/socle/utilisateurs/${id}/suspendre`, jsonInit("POST", { jusquau })),
  reinitialiserMotDePasse: (id: string, motDePasse: string) =>
    rawFetch<void>(`/socle/utilisateurs/${id}/reinitialiser-mot-de-passe`, jsonInit("POST", { motDePasse })),
  attribuerRole: (id: string, roleCode: string) =>
    rawFetch<Utilisateur>(`/socle/utilisateurs/${id}/roles`, jsonInit("POST", { roleCode })),
  retirerRole: (id: string, code: string) =>
    rawFetch<Utilisateur>(`/socle/utilisateurs/${id}/roles/${encodeURIComponent(code)}`, { method: "DELETE" }),
  definirPreferences: (id: string, preferences: Record<string, string>) =>
    rawFetch<Utilisateur>(`/socle/utilisateurs/${id}/preferences`, jsonInit("PUT", preferences)),
};

export const identiteApi = {
  annuaire: (q?: string) => rawFetch<Utilisateur[]>(`/socle/identites/annuaire${queryString(q)}`),
  roles: {
    list: () => rawFetch<Role[]>("/socle/identites/roles"),
    create: (body: { code: string; libelle?: string; categorie?: string; systeme?: boolean; hierarchique?: number }) =>
      rawFetch<Role>("/socle/identites/roles", jsonInit("POST", body)),
    definirPermissions: (code: string, permissionIds: string[]) =>
      rawFetch<Role>(`/socle/identites/roles/${encodeURIComponent(code)}/permissions`, jsonInit("PUT", { permissionIds })),
  },
  permissions: {
    list: () => rawFetch<Permission[]>("/socle/identites/permissions"),
    create: (body: { code: string; libelle?: string; service?: string; ressource?: string; action?: string }) =>
      rawFetch<Permission>("/socle/identites/permissions", jsonInit("POST", body)),
    verifier: (role: string, verbe: string, ressource: string) =>
      rawFetch<{ autorise: boolean }>(
        `/socle/identites/permissions/verifier?role=${encodeURIComponent(role)}&verbe=${encodeURIComponent(verbe)}&ressource=${encodeURIComponent(ressource)}`,
      ),
  },
};

export const structureApi = {
  niveaux: {
    list: () => rawFetch<Niveau[]>("/ped/niveaux"),
    create: (body: Partial<Niveau>) => rawFetch<Niveau>("/ped/niveaux", jsonInit("POST", body)),
    update: (id: string, body: Partial<Niveau>) => rawFetch<Niveau>(`/ped/niveaux/${id}`, jsonInit("PUT", body)),
    remove: (id: string) => rawFetch<void>(`/ped/niveaux/${id}`, { method: "DELETE" }),
  },
  classes: {
    list: () => rawFetch<Classe[]>("/ped/classes"),
    create: (body: Partial<Classe>) => rawFetch<Classe>("/ped/classes", jsonInit("POST", body)),
    update: (id: string, body: Partial<Classe>) => rawFetch<Classe>(`/ped/classes/${id}`, jsonInit("PUT", body)),
    ouvrir: (id: string) => rawFetch<Classe>(`/ped/classes/${id}/ouvrir`, { method: "POST" }),
    cloturer: (id: string) => rawFetch<Classe>(`/ped/classes/${id}/cloturer`, { method: "POST" }),
  },
  promotions: {
    list: () => rawFetch<Promotion[]>("/ped/promotions"),
    create: (body: Partial<Promotion>) => rawFetch<Promotion>("/ped/promotions", jsonInit("POST", body)),
    update: (id: string, body: Partial<Promotion>) => rawFetch<Promotion>(`/ped/promotions/${id}`, jsonInit("PUT", body)),
  },
  semestres: {
    list: () => rawFetch<Semestre[]>("/ped/semestres"),
    create: (body: Partial<Semestre>) => rawFetch<Semestre>("/ped/semestres", jsonInit("POST", body)),
    update: (id: string, body: Partial<Semestre>) => rawFetch<Semestre>(`/ped/semestres/${id}`, jsonInit("PUT", body)),
  },
  ues: {
    list: () => rawFetch<UE[]>("/ped/ues"),
    create: (body: Partial<UE>) => rawFetch<UE>("/ped/ues", jsonInit("POST", body)),
    update: (id: string, body: Partial<UE>) => rawFetch<UE>(`/ped/ues/${id}`, jsonInit("PUT", body)),
  },
  matieres: {
    list: () => rawFetch<Matiere[]>("/ped/matieres"),
    create: (body: Partial<Matiere>) => rawFetch<Matiere>("/ped/matieres", jsonInit("POST", body)),
    update: (id: string, body: Partial<Matiere>) => rawFetch<Matiere>(`/ped/matieres/${id}`, jsonInit("PUT", body)),
    remove: (id: string) => rawFetch<void>(`/ped/matieres/${id}`, { method: "DELETE" }),
  },
  cours: {
    list: () => rawFetch<CoursECUE[]>("/ped/cours"),
    create: (body: Partial<CoursECUE>) => rawFetch<CoursECUE>("/ped/cours", jsonInit("POST", body)),
    update: (id: string, body: Partial<CoursECUE>) => rawFetch<CoursECUE>(`/ped/cours/${id}`, jsonInit("PUT", body)),
  },
  salles: {
    list: () => rawFetch<Salle[]>("/ped/salles"),
    create: (body: Partial<Salle>) => rawFetch<Salle>("/ped/salles", jsonInit("POST", body)),
    update: (id: string, body: Partial<Salle>) => rawFetch<Salle>(`/ped/salles/${id}`, jsonInit("PUT", body)),
  },
  creneaux: {
    list: () => rawFetch<Creneau[]>("/ped/creneaux"),
    create: (body: Partial<Creneau>) => rawFetch<Creneau>("/ped/creneaux", jsonInit("POST", body)),
    update: (id: string, body: Partial<Creneau>) => rawFetch<Creneau>(`/ped/creneaux/${id}`, jsonInit("PUT", body)),
  },
  regimesHoraires: {
    list: () => rawFetch<RegimeHoraire[]>("/ped/regimes-horaires"),
    get: (code: string) => rawFetch<RegimeHoraire>(`/ped/regimes-horaires/${encodeURIComponent(code)}`),
    create: (body: Partial<RegimeHoraire>) => rawFetch<RegimeHoraire>("/ped/regimes-horaires", jsonInit("POST", body)),
    update: (code: string, body: Partial<RegimeHoraire>) =>
      rawFetch<RegimeHoraire>(`/ped/regimes-horaires/${encodeURIComponent(code)}`, jsonInit("PUT", body)),
  },
  emploisDuTemps: {
    list: () => rawFetch<EmploiDuTemps[]>("/ped/emplois-du-temps"),
    get: (id: string) => rawFetch<EmploiDuTemps>(`/ped/emplois-du-temps/${id}`),
    create: (body: Partial<EmploiDuTemps>) => rawFetch<EmploiDuTemps>("/ped/emplois-du-temps", jsonInit("POST", body)),
    ajouterPlage: (id: string, body: Partial<PlageEmploi>) =>
      rawFetch<EmploiDuTemps>(`/ped/emplois-du-temps/${id}/plages`, jsonInit("POST", body)),
    publier: (id: string) => rawFetch<EmploiDuTemps>(`/ped/emplois-du-temps/${id}/publier`, { method: "POST" }),
    conflits: (id: string) => rawFetch<ConflitEmploi[]>(`/ped/emplois-du-temps/${id}/conflits`),
  },
};

export const peopleApi = {
  eleves: {
    list: () => rawFetch<Eleve[]>("/ped/eleves"),
    get: (id: string) => rawFetch<Eleve>(`/ped/eleves/${id}`),
    create: (body: Partial<Eleve>) => rawFetch<Eleve>("/ped/eleves", jsonInit("POST", body)),
    update: (id: string, body: Partial<Eleve>) => rawFetch<Eleve>(`/ped/eleves/${id}`, jsonInit("PUT", body)),
    transferer: (id: string, destination: string, motif?: string) =>
      rawFetch<Eleve>(`/ped/eleves/${id}/transferer`, jsonInit("POST", { destination, motif })),
    radier: (id: string, motif: string) => rawFetch<Eleve>(`/ped/eleves/${id}/radier`, jsonInit("POST", { motif })),
    dossier: (id: string) => rawFetch<DossierEleve>(`/ped/eleves/${id}/dossier`),
    ajouterPiece: (id: string, body: Partial<FichierJoint>) =>
      rawFetch<DossierEleve>(`/ped/eleves/${id}/dossier/pieces`, jsonInit("POST", body)),
    archiverDossier: (id: string) =>
      rawFetch<DossierEleve>(`/ped/eleves/${id}/dossier/archiver`, { method: "POST" }),
    inscriptions: (id: string) => rawFetch<Inscription[]>(`/ped/eleves/${id}/inscriptions`),
    inscrire: (id: string, body: Partial<Inscription>) =>
      rawFetch<Inscription>(`/ped/eleves/${id}/inscriptions`, jsonInit("POST", body)),
    validerInscription: (id: string, inscriptionId: string) =>
      rawFetch<Inscription>(`/ped/eleves/${id}/inscriptions/${inscriptionId}/valider`, { method: "POST" }),
    annulerInscription: (id: string, inscriptionId: string, motif: string) =>
      rawFetch<Inscription>(
        `/ped/eleves/${id}/inscriptions/${inscriptionId}/annuler`,
        jsonInit("POST", { motif }),
      ),
    conges: (id: string) => rawFetch<CongeEtude[]>(`/ped/eleves/${id}/conges`),
    ouvrirConge: (id: string, body: Partial<CongeEtude>) =>
      rawFetch<CongeEtude>(`/ped/eleves/${id}/conges`, jsonInit("POST", body)),
    sanctions: (id: string) => rawFetch<SanctionDisciplinaire[]>(`/ped/eleves/${id}/sanctions`),
    prononcerSanction: (id: string, body: Partial<SanctionDisciplinaire>) =>
      rawFetch<SanctionDisciplinaire>(`/ped/eleves/${id}/sanctions`, jsonInit("POST", body)),
    liens: (id: string) => rawFetch<LienParentalite[]>(`/ped/eleves/${id}/liens`),
    lierParent: (id: string, body: Partial<LienParentalite>) =>
      rawFetch<LienParentalite>(`/ped/eleves/${id}/liens`, jsonInit("POST", body)),
  },
  parents: {
    list: () => rawFetch<Parent[]>("/ped/parents"),
    get: (id: string) => rawFetch<Parent>(`/ped/parents/${id}`),
    create: (body: Partial<Parent>) => rawFetch<Parent>("/ped/parents", jsonInit("POST", body)),
    update: (id: string, body: Partial<Parent>) => rawFetch<Parent>(`/ped/parents/${id}`, jsonInit("PUT", body)),
  },
  enseignants: {
    list: () => rawFetch<Enseignant[]>("/ped/enseignants"),
    get: (id: string) => rawFetch<Enseignant>(`/ped/enseignants/${id}`),
    create: (body: Partial<Enseignant>) => rawFetch<Enseignant>("/ped/enseignants", jsonInit("POST", body)),
    update: (id: string, body: Partial<Enseignant>) => rawFetch<Enseignant>(`/ped/enseignants/${id}`, jsonInit("PUT", body)),
    changerStatut: (id: string, statut: StatutEnseignant) =>
      rawFetch<Enseignant>(`/ped/enseignants/${id}/statut`, jsonInit("POST", { statut })),
    ajouterPiece: (id: string, body: Partial<PieceIdentiteEnseignant>) =>
      rawFetch<PieceIdentiteEnseignant>(`/ped/enseignants/${id}/pieces`, jsonInit("POST", body)),
    affectations: (id: string) => rawFetch<AffectationEnseignant[]>(`/ped/enseignants/${id}/affectations`),
    affecter: (id: string, body: Partial<AffectationEnseignant>) =>
      rawFetch<AffectationEnseignant>(`/ped/enseignants/${id}/affectations`, jsonInit("POST", body)),
  },
  personnel: {
    services: {
      list: () => rawFetch<ServiceEtablissement[]>("/ped/personnel/services"),
      create: (body: Partial<ServiceEtablissement>) =>
        rawFetch<ServiceEtablissement>("/ped/personnel/services", jsonInit("POST", body)),
      update: (id: string, body: Partial<ServiceEtablissement>) =>
        rawFetch<ServiceEtablissement>(`/ped/personnel/services/${id}`, jsonInit("PUT", body)),
    },
    agents: {
      list: () => rawFetch<Agent[]>("/ped/personnel/agents"),
      create: (body: Partial<Agent>) => rawFetch<Agent>("/ped/personnel/agents", jsonInit("POST", body)),
      update: (id: string, body: Partial<Agent>) => rawFetch<Agent>(`/ped/personnel/agents/${id}`, jsonInit("PUT", body)),
    },
    rendreTitulaire: (affectationId: string) =>
      rawFetch<AffectationEnseignant>(`/ped/personnel/affectations/${affectationId}/titulaire`, { method: "POST" }),
    cederTitularite: (affectationId: string) =>
      rawFetch<AffectationEnseignant>(`/ped/personnel/affectations/${affectationId}/ceder`, { method: "POST" }),
    affectationsClasse: (classeId: string) =>
      rawFetch<AffectationEnseignant[]>(`/ped/personnel/affectations/classe/${classeId}`),
    validerAffectation: (profil: string, classeId: string) =>
      rawFetch<ResultatValidation>(
        `/ped/personnel/affectations/valider?profil=${encodeURIComponent(profil)}&classeId=${encodeURIComponent(classeId)}`,
      ),
    titularites: (classeId: string) => rawFetch<Titularite[]>(`/ped/personnel/titularites/classe/${classeId}`),
    designerTitulaire: (body: Partial<Titularite>) =>
      rawFetch<Titularite>("/ped/personnel/titularites", jsonInit("POST", body)),
  },
};

export { rawFetch as apiFetch };

const qp = (params: Record<string, string | number | null | undefined>): string => {
  const usp = new URLSearchParams();
  for (const [key, value] of Object.entries(params)) {
    if (value !== undefined && value !== null && value !== "") usp.set(key, String(value));
  }
  const qs = usp.toString();
  return qs ? `?${qs}` : "";
};

export const enrollmentApi = {
  inscriptions: {
    list: () => rawFetch<InscriptionUE[]>("/ped/inscriptions"),
    get: (id: string) => rawFetch<InscriptionUE>(`/ped/inscriptions/${id}`),
    create: (body: Partial<InscriptionUE>) =>
      rawFetch<InscriptionUE>("/ped/inscriptions", jsonInit("POST", body)),
    update: (id: string, body: Partial<InscriptionUE>) =>
      rawFetch<InscriptionUE>(`/ped/inscriptions/${id}`, jsonInit("PUT", body)),
    valider: (id: string) => rawFetch<InscriptionUE>(`/ped/inscriptions/${id}/valider`, { method: "POST" }),
    refuser: (id: string, motif: string) =>
      rawFetch<InscriptionUE>(`/ped/inscriptions/${id}/refuser`, jsonInit("POST", { motif })),
    listeAttente: (id: string, rang?: number) =>
      rawFetch<InscriptionUE>(`/ped/inscriptions/${id}/liste-attente`, jsonInit("POST", { rang })),
    annuler: (id: string) => rawFetch<InscriptionUE>(`/ped/inscriptions/${id}/annuler`, { method: "POST" }),
  },
  groupes: {
    list: () => rawFetch<Groupe[]>("/ped/inscriptions/groupes"),
    get: (id: string) => rawFetch<Groupe>(`/ped/inscriptions/groupes/${id}`),
    create: (body: Partial<Groupe>) =>
      rawFetch<Groupe>("/ped/inscriptions/groupes", jsonInit("POST", body)),
    update: (id: string, body: Partial<Groupe>) =>
      rawFetch<Groupe>(`/ped/inscriptions/groupes/${id}`, jsonInit("PUT", body)),
    ouvrir: (id: string) => rawFetch<Groupe>(`/ped/inscriptions/groupes/${id}/ouvrir`, { method: "POST" }),
    clore: (id: string) => rawFetch<Groupe>(`/ped/inscriptions/groupes/${id}/clore`, { method: "POST" }),
    membres: (id: string) => rawFetch<MembreGroupe[]>(`/ped/inscriptions/groupes/${id}/membres`),
    ajouterMembre: (id: string, body: Partial<MembreGroupe>) =>
      rawFetch<Groupe>(`/ped/inscriptions/groupes/${id}/membres`, jsonInit("POST", body)),
    exclureMembre: (id: string, membreId: string) =>
      rawFetch<Groupe>(`/ped/inscriptions/groupes/${id}/membres/${membreId}`, { method: "DELETE" }),
  },
  regles: {
    list: () => rawFetch<RegleInscription[]>("/ped/admissions/regles"),
    get: (id: string) => rawFetch<RegleInscription>(`/ped/admissions/regles/${id}`),
    create: (body: Partial<RegleInscription>) =>
      rawFetch<RegleInscription>("/ped/admissions/regles", jsonInit("POST", body)),
    update: (id: string, body: Partial<RegleInscription>) =>
      rawFetch<RegleInscription>(`/ped/admissions/regles/${id}`, jsonInit("PUT", body)),
    remove: (id: string) => rawFetch<void>(`/ped/admissions/regles/${id}`, { method: "DELETE" }),
  },
  evaluer: (candidat: Candidat) =>
    rawFetch<{ decision: string }>("/ped/admissions/evaluer", jsonInit("POST", candidat)),
  parcours: (eleveId: string, params?: { programmeId?: string; creditsRequis?: number }) =>
    rawFetch<ParcoursAcademique>(`/ped/admissions/parcours/${eleveId}${qp(params ?? {})}`),
};

export const attendanceApi = {
  seances: {
    list: (params?: { date?: string }) => rawFetch<Seance[]>(`/ped/seances${qp(params ?? {})}`),
    get: (id: string) => rawFetch<Seance>(`/ped/seances/${id}`),
    create: (body: Partial<Seance>) => rawFetch<Seance>("/ped/seances", jsonInit("POST", body)),
    update: (id: string, body: Partial<Seance>) => rawFetch<Seance>(`/ped/seances/${id}`, jsonInit("PUT", body)),
    demarrer: (id: string) => rawFetch<Seance>(`/ped/seances/${id}/demarrer`, { method: "POST" }),
    terminer: (id: string) => rawFetch<Seance>(`/ped/seances/${id}/terminer`, { method: "POST" }),
    reporter: (id: string) => rawFetch<Seance>(`/ped/seances/${id}/reporter`, { method: "POST" }),
    annuler: (id: string, motif: string) =>
      rawFetch<Seance>(`/ped/seances/${id}/annuler`, jsonInit("POST", { motif })),
  },
  appels: {
    list: () => rawFetch<FeuillePresence[]>("/ped/appels"),
    get: (id: string) => rawFetch<FeuillePresence>(`/ped/appels/${id}`),
    create: (body: Partial<FeuillePresence>) => rawFetch<FeuillePresence>("/ped/appels", jsonInit("POST", body)),
    presences: (id: string) => rawFetch<PresenceEleve[]>(`/ped/appels/${id}/presences`),
    pointer: (id: string, body: PointerRequest) =>
      rawFetch<FeuillePresence>(`/ped/appels/${id}/pointer`, jsonInit("POST", body)),
    appelGlobal: (id: string, body: AppelGlobalRequest) =>
      rawFetch<FeuillePresence>(`/ped/appels/${id}/appel-global`, jsonInit("POST", body)),
    absence: (id: string, body: AbsenceRequest) =>
      rawFetch<FeuillePresence>(`/ped/appels/${id}/absence`, jsonInit("POST", body)),
    retard: (id: string, body: RetardRequest) =>
      rawFetch<FeuillePresence>(`/ped/appels/${id}/retard`, jsonInit("POST", body)),
    soumettre: (id: string) => rawFetch<FeuillePresence>(`/ped/appels/${id}/soumettre`, { method: "POST" }),
    valider: (id: string) => rawFetch<FeuillePresence>(`/ped/appels/${id}/valider`, { method: "POST" }),
    cloturer: (id: string) => rawFetch<FeuillePresence>(`/ped/appels/${id}/cloturer`, { method: "POST" }),
  },
  justificatifs: {
    list: () => rawFetch<Justificatif[]>("/ped/absences/justificatifs"),
    get: (id: string) => rawFetch<Justificatif>(`/ped/absences/justificatifs/${id}`),
    create: (body: Partial<Justificatif>) =>
      rawFetch<Justificatif>("/ped/absences/justificatifs", jsonInit("POST", body)),
    accepter: (id: string) =>
      rawFetch<Justificatif>(`/ped/absences/justificatifs/${id}/accepter`, { method: "POST" }),
    rejeter: (id: string, motif: string) =>
      rawFetch<Justificatif>(`/ped/absences/justificatifs/${id}/rejeter`, jsonInit("POST", { motif })),
  },
  assiduite: {
    eleve: (eleveId: string, params?: { anneeId?: string; periodeId?: string }) =>
      rawFetch<SuiviAssiduite>(`/ped/assiduite/${eleveId}${qp(params ?? {})}`),
    classeTaux: (classeId: string) => rawFetch<number>(`/ped/assiduite/classe/${classeId}/taux`),
    registre: (params?: { classeId?: string; date?: string }) =>
      rawFetch<FeuillePresence[]>(`/ped/assiduite/registre${qp(params ?? {})}`),
  },
};

export const assessmentApi = {
  evaluations: {
    list: (params?: { classeId?: string; periodeId?: string }) =>
      rawFetch<Evaluation[]>(`/ped/evaluations${qp(params ?? {})}`),
    get: (id: string) => rawFetch<Evaluation>(`/ped/evaluations/${id}`),
    create: (body: Partial<Evaluation>) =>
      rawFetch<Evaluation>("/ped/evaluations", jsonInit("POST", body)),
    update: (id: string, body: Partial<Evaluation>) =>
      rawFetch<Evaluation>(`/ped/evaluations/${id}`, jsonInit("PUT", body)),
    ouvrir: (id: string) => rawFetch<Evaluation>(`/ped/evaluations/${id}/ouvrir`, { method: "POST" }),
    cloturer: (id: string) => rawFetch<Evaluation>(`/ped/evaluations/${id}/cloturer`, { method: "POST" }),
    publier: (id: string) => rawFetch<Evaluation>(`/ped/evaluations/${id}/publier`, { method: "POST" }),
    annuler: (id: string, motif?: string) =>
      rawFetch<Evaluation>(`/ped/evaluations/${id}/annuler`, jsonInit("POST", { motif })),
    feuille: (id: string) => rawFetch<FeuilleNote>(`/ped/evaluations/${id}/feuille`),
    notes: (id: string) => rawFetch<Note[]>(`/ped/evaluations/${id}/notes`),
    saisirNote: (id: string, body: SaisirNoteRequest) =>
      rawFetch<Evaluation>(`/ped/evaluations/${id}/notes`, jsonInit("POST", body)),
    marquerAbsent: (id: string, body: EleveRequest) =>
      rawFetch<Evaluation>(`/ped/evaluations/${id}/notes/absence`, jsonInit("POST", body)),
    exclureNote: (id: string, body: MotifEleveRequest) =>
      rawFetch<Evaluation>(`/ped/evaluations/${id}/notes/exclusion`, jsonInit("POST", body)),
    contesterNote: (id: string, body: MotifEleveRequest) =>
      rawFetch<Evaluation>(`/ped/evaluations/${id}/notes/contestation`, jsonInit("POST", body)),
  },
  notes: {
    list: (params?: { evaluationId?: string; eleveId?: string }) =>
      rawFetch<Note[]>(`/ped/notes${qp(params ?? {})}`),
  },
  bulletins: {
    list: (params?: { eleveId?: string; classeId?: string; periodeId?: string }) =>
      rawFetch<Bulletin[]>(`/ped/bulletins${qp(params ?? {})}`),
    get: (id: string) => rawFetch<Bulletin>(`/ped/bulletins/${id}`),
    create: (body: Partial<Bulletin>) =>
      rawFetch<Bulletin>("/ped/bulletins", jsonInit("POST", body)),
    ajouterLigne: (id: string, body: Partial<LigneBulletin>) =>
      rawFetch<Bulletin>(`/ped/bulletins/${id}/lignes`, jsonInit("POST", body)),
    definirAbsences: (id: string, body: Partial<SyntheseAbsences>) =>
      rawFetch<Bulletin>(`/ped/bulletins/${id}/absences`, jsonInit("POST", body)),
    calculer: (id: string) => rawFetch<Bulletin>(`/ped/bulletins/${id}/calculer`, { method: "POST" }),
    publier: (id: string) => rawFetch<Bulletin>(`/ped/bulletins/${id}/publier`, { method: "POST" }),
    generer: (id: string) => rawFetch<Bulletin>(`/ped/bulletins/${id}/generer`, { method: "POST" }),
    signer: (id: string) => rawFetch<Bulletin>(`/ped/bulletins/${id}/signer`, { method: "POST" }),
    rectifier: (id: string, motif?: string) =>
      rawFetch<Bulletin>(`/ped/bulletins/${id}/rectifier`, jsonInit("POST", { motif })),
    coupons: (id: string) => rawFetch<CouponNotes[]>(`/ped/bulletins/${id}/coupons`),
    creerCoupon: (id: string, observation?: string) =>
      rawFetch<CouponNotes>(`/ped/bulletins/${id}/coupons`, jsonInit("POST", { observation })),
    tirerCoupon: (id: string, couponId: string) =>
      rawFetch<CouponNotes>(`/ped/bulletins/${id}/coupons/${couponId}/tirer`, { method: "POST" }),
    detacherCoupon: (id: string, couponId: string) =>
      rawFetch<CouponNotes>(`/ped/bulletins/${id}/coupons/${couponId}/detacher`, { method: "POST" }),
    signerCoupon: (id: string, couponId: string) =>
      rawFetch<CouponNotes>(`/ped/bulletins/${id}/coupons/${couponId}/signer`, { method: "POST" }),
    rendreCoupon: (id: string, couponId: string) =>
      rawFetch<CouponNotes>(`/ped/bulletins/${id}/coupons/${couponId}/rendre`, { method: "POST" }),
  },
  moyennes: {
    evaluation: (evaluationId: string) =>
      rawFetch<ResultatMoyenne>(`/ped/moyennes/evaluation/${evaluationId}`),
    eleve: (eleveId: string, periodeId?: string) =>
      rawFetch<ResultatMoyenne>(`/ped/moyennes/eleve/${eleveId}${qp({ periodeId })}`),
    classe: (classeId: string) => rawFetch<Classement[]>(`/ped/moyennes/classe/${classeId}`),
  },
};

export const examApi = {
  sessions: {
    list: (params?: { anneeId?: string; classeId?: string }) =>
      rawFetch<SessionExamen[]>(`/ped/sessions${qp(params ?? {})}`),
    get: (id: string) => rawFetch<SessionExamen>(`/ped/sessions/${id}`),
    create: (body: Partial<SessionExamen>) => rawFetch<SessionExamen>("/ped/sessions", jsonInit("POST", body)),
    programmer: (id: string) => rawFetch<SessionExamen>(`/ped/sessions/${id}/programmer`, { method: "POST" }),
    ouvrir: (id: string) => rawFetch<SessionExamen>(`/ped/sessions/${id}/ouvrir`, { method: "POST" }),
    cloturer: (id: string) => rawFetch<SessionExamen>(`/ped/sessions/${id}/cloturer`, { method: "POST" }),
    annuler: (id: string, motif?: string) =>
      rawFetch<SessionExamen>(`/ped/sessions/${id}/annuler`, jsonInit("POST", { motif })),
    epreuves: (sessionId: string) => rawFetch<Epreuve[]>(`/ped/sessions/${sessionId}/epreuves`),
    ajouterEpreuve: (sessionId: string, body: Partial<Epreuve>) =>
      rawFetch<Epreuve>(`/ped/sessions/${sessionId}/epreuves`, jsonInit("POST", body)),
    creneaux: (sessionId: string) => rawFetch<CreneauExamen[]>(`/ped/sessions/${sessionId}/creneaux`),
    ajouterCreneau: (sessionId: string, body: Partial<CreneauExamen>) =>
      rawFetch<CreneauExamen>(`/ped/sessions/${sessionId}/creneaux`, jsonInit("POST", body)),
    convocations: (sessionId: string) => rawFetch<Convocation[]>(`/ped/sessions/${sessionId}/convocations`),
    creerConvocation: (sessionId: string, body: Partial<Convocation>) =>
      rawFetch<Convocation>(`/ped/sessions/${sessionId}/convocations`, jsonInit("POST", body)),
  },
  epreuves: {
    get: (id: string) => rawFetch<Epreuve>(`/ped/epreuves/${id}`),
    valider: (id: string) => rawFetch<Epreuve>(`/ped/epreuves/${id}/valider`, { method: "POST" }),
    horsMoyenne: (id: string) => rawFetch<Epreuve>(`/ped/epreuves/${id}/hors-moyenne`, { method: "POST" }),
    rattacherPeriode: (id: string, periodeId: string) =>
      rawFetch<Epreuve>(`/ped/epreuves/${id}/periode`, jsonInit("POST", { eleveId: periodeId })),
  },
  convocations: {
    get: (id: string) => rawFetch<Convocation>(`/ped/convocations/${id}`),
    emettre: (id: string) => rawFetch<Convocation>(`/ped/convocations/${id}/emettre`, { method: "POST" }),
    notifier: (id: string, canal?: string) =>
      rawFetch<Convocation>(`/ped/convocations/${id}/notifier`, jsonInit("POST", { canal })),
  },
  notes: {
    list: (params?: { epreuveId?: string; eleveId?: string }) =>
      rawFetch<NoteExamen[]>(`/ped/notes-examen${qp(params ?? {})}`),
    saisir: (epreuveId: string, body: SaisirNoteExamenRequest) =>
      rawFetch<NoteExamen>(`/ped/epreuves/${epreuveId}/notes`, jsonInit("POST", body)),
    marquerAbsent: (epreuveId: string, body: EleveRequest) =>
      rawFetch<NoteExamen>(`/ped/epreuves/${epreuveId}/notes/absence`, jsonInit("POST", body)),
    exclure: (id: string, motif?: string) =>
      rawFetch<NoteExamen>(`/ped/notes-examen/${id}/exclure`, jsonInit("POST", { motif })),
    contester: (id: string, motif?: string) =>
      rawFetch<NoteExamen>(`/ped/notes-examen/${id}/contester`, jsonInit("POST", { motif })),
    valider: (id: string) => rawFetch<NoteExamen>(`/ped/notes-examen/${id}/valider`, { method: "POST" }),
  },
  corrections: {
    ouvrir: (epreuveId: string) =>
      rawFetch<Correction>(`/ped/epreuves/${epreuveId}/corrections`, jsonInit("POST", {})),
    get: (id: string) => rawFetch<Correction>(`/ped/corrections/${id}`),
    calculer: (id: string) => rawFetch<Correction>(`/ped/corrections/${id}/calculer`, { method: "POST" }),
    cloturer: (id: string) => rawFetch<Correction>(`/ped/corrections/${id}/cloturer`, { method: "POST" }),
  },
  deliberations: {
    list: (sessionId?: string) => rawFetch<Deliberation[]>(`/ped/deliberations${qp({ sessionId })}`),
    get: (id: string) => rawFetch<Deliberation>(`/ped/deliberations/${id}`),
    create: (body: Partial<Deliberation>) =>
      rawFetch<Deliberation>("/ped/deliberations", jsonInit("POST", body)),
    planifier: (id: string) => rawFetch<Deliberation>(`/ped/deliberations/${id}/planifier`, { method: "POST" }),
    ouvrir: (id: string) => rawFetch<Deliberation>(`/ped/deliberations/${id}/ouvrir`, { method: "POST" }),
    cloturer: (id: string) => rawFetch<Deliberation>(`/ped/deliberations/${id}/cloturer`, { method: "POST" }),
    decider: (id: string, body: DeciderRequest) =>
      rawFetch<DecisionExamen>(`/ped/deliberations/${id}/decisions`, jsonInit("POST", body)),
    ajouterDecisionUE: (id: string, body: Partial<DecisionUE>) =>
      rawFetch<DecisionUE>(`/ped/deliberations/${id}/decisions-ue`, jsonInit("POST", body)),
  },
  resultats: {
    publier: (decisionId: string) =>
      rawFetch<ResultatOfficiel>(`/ped/decisions/${decisionId}/resultats`, { method: "POST" }),
    get: (id: string) => rawFetch<ResultatOfficiel>(`/ped/resultats/${id}`),
  },
  rattrapages: {
    list: (eleveId?: string) => rawFetch<Rattrapage[]>(`/ped/rattrapages${qp({ eleveId })}`),
    get: (id: string) => rawFetch<Rattrapage>(`/ped/rattrapages/${id}`),
    create: (body: Partial<Rattrapage>) =>
      rawFetch<Rattrapage>("/ped/rattrapages", jsonInit("POST", body)),
    exempt: (id: string, motif?: string) =>
      rawFetch<Rattrapage>(`/ped/rattrapages/${id}/exempt`, jsonInit("POST", { motif })),
  },
  procesVerbaux: {
    list: (sessionId?: string) => rawFetch<ProcesVerbal[]>(`/ped/proces-verbaux${qp({ sessionId })}`),
    get: (id: string) => rawFetch<ProcesVerbal>(`/ped/proces-verbaux/${id}`),
    create: (body: Partial<ProcesVerbal>) =>
      rawFetch<ProcesVerbal>("/ped/proces-verbaux", jsonInit("POST", body)),
    soumettre: (id: string) => rawFetch<ProcesVerbal>(`/ped/proces-verbaux/${id}/soumettre`, { method: "POST" }),
    signer: (id: string, role?: string) =>
      rawFetch<ProcesVerbal>(`/ped/proces-verbaux/${id}/signer`, jsonInit("POST", { role })),
    homologuer: (id: string) => rawFetch<ProcesVerbal>(`/ped/proces-verbaux/${id}/homologuer`, { method: "POST" }),
    contester: (id: string, motif?: string) =>
      rawFetch<ProcesVerbal>(`/ped/proces-verbaux/${id}/contester`, jsonInit("POST", { motif })),
  },
};

export const feesApi = {
  grilles: {
    list: (params?: { anneeId?: string; profil?: string }) =>
      rawFetch<GrilleTarifaire[]>(`/ped/frais/grilles${qp(params ?? {})}`),
    applicable: (params?: { anneeId?: string; profil?: string }) =>
      rawFetch<GrilleTarifaire>(`/ped/frais/grilles/applicable${qp(params ?? {})}`),
    get: (id: string) => rawFetch<GrilleTarifaire>(`/ped/frais/grilles/${id}`),
    create: (body: Partial<GrilleTarifaire>) =>
      rawFetch<GrilleTarifaire>("/ped/frais/grilles", jsonInit("POST", body)),
    activer: (id: string) => rawFetch<GrilleTarifaire>(`/ped/frais/grilles/${id}/activer`, { method: "POST" }),
    figer: (id: string) => rawFetch<GrilleTarifaire>(`/ped/frais/grilles/${id}/figer`, { method: "POST" }),
    archiver: (id: string) => rawFetch<GrilleTarifaire>(`/ped/frais/grilles/${id}/archiver`, { method: "POST" }),
    ajouterLigne: (id: string, body: Partial<LigneGrille>) =>
      rawFetch<GrilleTarifaire>(`/ped/frais/grilles/${id}/lignes`, jsonInit("POST", body)),
    ajouterReduction: (id: string, body: Partial<Reduction>) =>
      rawFetch<GrilleTarifaire>(`/ped/frais/grilles/${id}/reductions`, jsonInit("POST", body)),
  },
  avis: {
    list: (params?: { eleveId?: string; anneeId?: string }) =>
      rawFetch<AvisPaiement[]>(`/ped/frais/avis${qp(params ?? {})}`),
    get: (id: string) => rawFetch<AvisPaiement>(`/ped/frais/avis/${id}`),
    create: (body: Partial<AvisPaiement>) =>
      rawFetch<AvisPaiement>("/ped/frais/avis", jsonInit("POST", body)),
    valider: (id: string) => rawFetch<AvisPaiement>(`/ped/frais/avis/${id}/valider`, { method: "POST" }),
    emettre: (id: string) => rawFetch<AvisPaiement>(`/ped/frais/avis/${id}/emettre`, { method: "POST" }),
    annuler: (id: string, motif?: string) =>
      rawFetch<AvisPaiement>(`/ped/frais/avis/${id}/annuler`, jsonInit("POST", { motif })),
    remise: (id: string, body: RemiseRequest) =>
      rawFetch<AvisPaiement>(`/ped/frais/avis/${id}/remise`, jsonInit("POST", body)),
  },
  paiements: {
    list: (params?: { eleveId?: string; avisId?: string }) =>
      rawFetch<Paiement[]>(`/ped/paiements${qp(params ?? {})}`),
    get: (id: string) => rawFetch<Paiement>(`/ped/paiements/${id}`),
    create: (body: Partial<Paiement>, idempotencyKey: string) =>
      rawFetch<Paiement>("/ped/paiements", {
        ...jsonInit("POST", body),
        headers: { ...jsonInit("POST", body).headers, "Idempotency-Key": idempotencyKey },
      }),
    valider: (id: string) => rawFetch<Paiement>(`/ped/paiements/${id}/valider`, { method: "POST" }),
    rejeter: (id: string, motif: string) =>
      rawFetch<Paiement>(`/ped/paiements/${id}/rejeter`, jsonInit("POST", { motif })),
    annuler: (id: string, motif: string) =>
      rawFetch<Paiement>(`/ped/paiements/${id}/annuler`, jsonInit("POST", { motif })),
  },
  coupons: {
    list: (params?: { eleveId?: string; anneeId?: string }) =>
      rawFetch<CouponPaiement[]>(`/ped/coupons${qp(params ?? {})}`),
    get: (id: string) => rawFetch<CouponPaiement>(`/ped/coupons/${id}`),
    tirer: (id: string) => rawFetch<CouponPaiement>(`/ped/coupons/${id}/tirer`, { method: "POST" }),
    remettre: (id: string, destinataire?: string) =>
      rawFetch<CouponPaiement>(`/ped/coupons/${id}/remettre`, jsonInit("POST", { destinataire })),
    annuler: (id: string, motif?: string) =>
      rawFetch<CouponPaiement>(`/ped/coupons/${id}/annuler`, jsonInit("POST", { motif })),
    verifier: (id: string) => rawFetch<boolean>(`/ped/coupons/${id}/verifier`, { method: "POST" }),
  },
  journaliers: {
    list: (params?: { date?: string; site?: string }) =>
      rawFetch<Journalier[]>(`/ped/frais/journaliers${qp(params ?? {})}`),
    get: (id: string) => rawFetch<Journalier>(`/ped/frais/journaliers/${id}`),
    ouvrir: (body: Partial<Journalier>) =>
      rawFetch<Journalier>("/ped/frais/journaliers", jsonInit("POST", body)),
    cloturer: (id: string) => rawFetch<Journalier>(`/ped/frais/journaliers/${id}/cloturer`, { method: "POST" }),
  },
};
export { tryRefresh };