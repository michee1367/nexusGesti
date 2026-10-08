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
  type SeuilDecision,
  type Site,
  type StatutEtablissement,
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

export { rawFetch as apiFetch };
export { tryRefresh };