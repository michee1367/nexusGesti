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
export { tryRefresh };