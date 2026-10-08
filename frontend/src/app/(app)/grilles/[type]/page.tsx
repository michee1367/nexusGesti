"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { toast } from "sonner";
import { ArrowLeft } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { Checkbox } from "@/components/ui/checkbox";
import {
  PageHeader,
  LoadingView,
  ErrorState,
  EmptyState,
} from "@/components/common/async-state";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { grilleApi } from "@/services/api";
import {
  type Bareme,
  type ConfigCascade,
  type Grille,
  type GrilleConformite,
  type GrilleStock,
  type GrilleType,
  type Mention,
  type RegleAppreciation,
  type RegleAssiduite,
  type SeuilDecision,
} from "@/types";

const PORTEES = [
  "GLOBALE",
  "PAR_ETABLISSEMENT",
  "PAR_SITE",
  "PAR_PROFIL",
  "PAR_CYCLE",
  "PAR_NIVEAU",
  "PAR_PARCOURS",
  "PAR_PROGRAMME",
  "PAR_SEMESTRE",
  "PAR_UE",
  "PAR_COURS",
  "PAR_MATIERE",
  "PAR_ENSEIGNANT",
  "PAR_CLASSE",
  "PAR_TYPE_EVALUATION",
  "PAR_TYPE_SESSION",
];
const DECISIONS = ["ADMIS", "ADMIS_MENTION", "RATTAPAGE", "REDOUBLANT", "REJETE"];
const BAREME_TYPES = ["POINTS", "SUR_5", "SUR_10", "SUR_20", "SUR_100", "LETTRE", "MENTION"];
const ARRONDIS = ["AUCUN", "DECIMALE", "INFERIEUR", "SUPERIEUR", "MI_SUPERIEUR"];
const NIVEAUX_ACALCUL = ["EVALUATION", "COURS_ECUE", "UE", "PERIODE", "ANNEE", "CYCLE"];
const STRATEGIES = [
  "MOYENNE_PONDEREE",
  "MOYENNE_NON_PONDEREE",
  "SOMME_PONDEREE",
  "MEILLEURE_NOTE",
  "MOYENNE_TRONQUEE",
  "DERNIERE_NOTE",
];
const ALERTE_NIVEAUX = ["INFO", "ATTENTION", "CRITIQUE", "BLOCAGE"];

function field(label: string, value: React.ReactNode) {
  return (
    <div>
      <dt className="text-xs text-slate-500">{label}</dt>
      <dd className="text-sm font-medium text-slate-900">{value}</dd>
    </div>
  );
}

function StatusBadges({ grille }: { grille: Grille }) {
  return (
    <div className="flex items-center gap-2">
      {grille.active ? (
        <Badge>Active</Badge>
      ) : grille.modifiable ? (
        <Badge variant="outline">En préparation</Badge>
      ) : (
        <Badge variant="outline">Figée</Badge>
      )}
      <Badge variant="secondary">v{grille.version}</Badge>
      <Badge variant="secondary">{grille.profil}</Badge>
    </div>
  );
}

export default function GrilleDetailPage({
  params,
  searchParams,
}: {
  params: { type: string };
  searchParams: { id?: string };
}) {
  const type = (["notation", "stock", "conformite"].includes(params.type)
    ? params.type
    : "notation") as GrilleType;
  const id = searchParams.id ?? "";
  const { tenantId } = useTenant();
  const { isAdmin } = useAuth();
  const router = useRouter();
  const [busy, setBusy] = useState(false);

  const grille = useApi(
    () => grilleApi.get(type, id),
    [type, id, tenantId]
  );

  const editable = isAdmin && (grille.data?.modifiable ?? false) && !(grille.data?.active ?? false);

  const run = async (action: () => Promise<unknown>, success: string) => {
    setBusy(true);
    try {
      await action();
      toast.success(success);
      grille.reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  if (grille.loading) return <LoadingView label="Chargement de la grille…" />;
  if (grille.error || !grille.data)
    return (
      <div>
        <ErrorState error={grille.error} onRetry={grille.reload} />
        <Button
          variant="ghost"
          size="sm"
          className="mt-4"
          onClick={() => router.push("/grilles")}
        >
          <ArrowLeft className="mr-2 h-4 w-4" />
          Retour aux grilles
        </Button>
      </div>
    );

  const base = grille.data as Grille;

  return (
    <div>
      <PageHeader title={base.libelle} description={`${base.code} — grille de ${type}`}>
        <StatusBadges grille={base} />
      </PageHeader>

      <Button variant="ghost" size="sm" className="mb-4" onClick={() => router.push("/grilles")}>
        <ArrowLeft className="mr-2 h-4 w-4" />
        Toutes les grilles
      </Button>

      {type === "notation" && (
        <NotationSections
          grille={grille.data as Grille & { baremes: Bareme[]; seuilsDecision: SeuilDecision[]; mentions: Mention[]; reglesAssiduite: RegleAssiduite[]; appreciations: RegleAppreciation[]; configCascade?: ConfigCascade | null }}
          editable={editable}
          busy={busy}
          run={run}
        />
      )}
      {type === "stock" && <StockSections grille={grille.data as GrilleStock} editable={editable} busy={busy} run={run} />}
      {type === "conformite" &&
        <ConformiteSection grille={grille.data as GrilleConformite} />}
    </div>
  );
}

type RunFn = (action: () => Promise<unknown>, success: string) => Promise<void>;

function AddForm({
  title,
  children,
  onSubmit,
  busy,
  disabled,
}: {
  title: string;
  children: React.ReactNode;
  onSubmit: () => void;
  busy: boolean;
  disabled: boolean;
}) {
  return (
    <form
      className="grid gap-2 rounded-lg border border-border bg-slate-50 p-3"
      onSubmit={(e) => {
        e.preventDefault();
        onSubmit();
      }}
    >
      <p className="text-sm font-semibold text-slate-700">{title}</p>
      {children}
      <Button type="submit" disabled={busy || disabled} className="justify-self-start">
        Ajouter
      </Button>
    </form>
  );
}

function DataTable<T extends object>({
  columns,
  rows,
  keyOf,
  emptyLabel,
}: {
  columns: { header: string; render: (row: T) => React.ReactNode }[];
  rows: T[];
  keyOf: (row: T) => string;
  emptyLabel: string;
}) {
  if (!rows.length) return <EmptyState label={emptyLabel} />;
  return (
    <div className="overflow-x-auto">
      <table className="w-full text-sm">
        <thead>
          <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
            {columns.map((column) => (
              <th key={column.header} className="px-3 py-2">
                {column.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row) => (
            <tr key={keyOf(row)} className="border-b border-border">
              {columns.map((column) => (
                <td key={column.header} className="px-3 py-2 align-top">
                  {column.render(row)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function NotationSections({
  grille,
  editable,
  busy,
  run,
}: {
  grille: Grille & {
    baremes: Bareme[];
    seuilsDecision: SeuilDecision[];
    mentions: Mention[];
    reglesAssiduite: RegleAssiduite[];
    appreciations: RegleAppreciation[];
    configCascade?: ConfigCascade | null;
  };
  editable: boolean;
  busy: boolean;
  run: RunFn;
}) {
  const id = grille.id ?? "";

  const [bareme, setBareme] = useState<Partial<Bareme>>({
    code: "",
    libelle: "",
    type: "SUR_20",
    baremeMax: 20,
    noteMinimale: 10,
    precision: 1,
    arrondi: "DECIMALE",
    defaut: false,
  });
  const [seuil, setSeuil] = useState<Partial<SeuilDecision>>({
    libelle: "",
    portee: "GLOBALE",
    decisionResultante: "ADMIS",
    priorite: 1,
  });
  const [mention, setMention] = useState<Partial<Mention>>({
    code: "",
    libelle: "",
    ordre: (grille.mentions?.length ?? 0) + 1,
  });
  const [assiduite, setAssiduite] = useState<Partial<RegleAssiduite>>({
    portee: "GLOBALE",
    consequence: "ADMIS",
    absencesJustifieesExonerees: true,
    retardCompteCommeAbsence: false,
    priorite: 1,
  });
  const [appreciation, setAppreciation] = useState<Partial<RegleAppreciation>>({
    portee: "GLOBALE",
    libelle: "",
  });
  const [cascade, setCascade] = useState<ConfigCascade>({
    niveauxActifs: grille.configCascade?.niveauxActifs ?? [],
    strategie: grille.configCascade?.strategie ?? "MOYENNE_PONDEREE",
    arrondi: grille.configCascade?.arrondi ?? "DECIMALE",
    precision: grille.configCascade?.precision ?? 2,
    seuilAbsorption: grille.configCascade?.seuilAbsorption ?? null,
    ordreConversionLettre: grille.configCascade?.ordreConversionLettre ?? false,
  });

  const toggleNiveau = (niveau: string) => {
    setCascade((current) => ({
      ...current,
      niveauxActifs: current.niveauxActifs.includes(niveau as never)
        ? current.niveauxActifs.filter((value) => value !== niveau)
        : [...current.niveauxActifs, niveau as never],
    }));
  };

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle className="text-base">Barèmes</CardTitle>
          <CardDescription>Calcul de la note à partir du barème par défaut.</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <DataTable
            emptyLabel="Aucun barème"
            keyOf={(row) => row.idBareme ?? row.code}
            columns={[
              { header: "Code", render: (row) => <span className="font-mono">{row.code}</span> },
              { header: "Libellé", render: (row) => row.libelle },
              { header: "Type", render: (row) => <Badge variant="secondary">{row.type}</Badge> },
              { header: "Max", render: (row) => row.baremeMax },
              { header: "Note min", render: (row) => row.noteMinimale },
              { header: "Arrondi", render: (row) => row.arrondi },
              { header: "Défaut", render: (row) => (row.defaut ? "✓" : "—") },
            ]}
            rows={grille.baremes ?? []}
          />
          {editable && (
            <AddForm
              title="Nouveau barème"
              busy={busy}
              disabled={!bareme.code || !bareme.libelle}
              onSubmit={() =>
                run(() => grilleApi.notation.addBareme(id, bareme as Bareme), "Barème ajouté").then(
                  () => setBareme({ ...bareme, code: "", libelle: "" })
                )
              }
            >
              <div className="grid gap-2 sm:grid-cols-4">
                <Input placeholder="Code" value={bareme.code ?? ""} onChange={(e) => setBareme({ ...bareme, code: e.target.value })} />
                <Input placeholder="Libellé" value={bareme.libelle ?? ""} onChange={(e) => setBareme({ ...bareme, libelle: e.target.value })} />
                <Select value={bareme.type} onChange={(e) => setBareme({ ...bareme, type: e.target.value as never })}>
                  {BAREME_TYPES.map((value) => (
                    <option key={value} value={value}>{value}</option>
                  ))}
                </Select>
                <Select value={bareme.arrondi} onChange={(e) => setBareme({ ...bareme, arrondi: e.target.value as never })}>
                  {ARRONDIS.map((value) => (
                    <option key={value} value={value}>{value}</option>
                  ))}
                </Select>
                <Input type="number" placeholder="Bareme max" value={bareme.baremeMax ?? ""} onChange={(e) => setBareme({ ...bareme, baremeMax: Number(e.target.value) })} />
                <Input type="number" placeholder="Note minimale" value={bareme.noteMinimale ?? ""} onChange={(e) => setBareme({ ...bareme, noteMinimale: Number(e.target.value) })} />
                <Input type="number" placeholder="Précision" value={bareme.precision ?? ""} onChange={(e) => setBareme({ ...bareme, precision: Number(e.target.value) })} />
                <label className="flex items-center gap-2 text-sm text-slate-700">
                  <Checkbox checked={bareme.defaut} onClick={() => setBareme({ ...bareme, defaut: !bareme.defaut })} />
                  Défaut
                </label>
              </div>
            </AddForm>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Seuils de décision</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <DataTable
            emptyLabel="Aucun seuil"
            keyOf={(row) => row.idSeuil ?? row.libelle}
            columns={[
              { header: "Libellé", render: (row) => <span className="font-medium text-slate-900">{row.libelle}</span> },
              { header: "Portée", render: (row) => row.portee },
              { header: "Moyenne", render: (row) => `${row.moyenneMinimale ?? "?"} → ${row.moyenneMaximale ?? "∞"}` },
              { header: "Décision", render: (row) => <Badge variant="secondary">{row.decisionResultante}</Badge> },
              { header: "Rang", render: (row) => (row.rangMin !== undefined ? `${row.rangMin ?? "?"}-${row.rangMax ?? "∞"}` : "—") },
              { header: "Priorité", render: (row) => row.priorite },
            ]}
            rows={grille.seuilsDecision ?? []}
          />
          {editable && (
            <AddForm
              title="Nouveau seuil"
              busy={busy}
              disabled={!seuil.libelle}
              onSubmit={() =>
                run(() => grilleApi.notation.addSeuil(id, seuil as SeuilDecision), "Seuil ajouté").then(() =>
                  setSeuil({ ...seuil, libelle: "" })
                )
              }
            >
              <div className="grid gap-2 sm:grid-cols-4">
                <Input placeholder="Libellé" value={seuil.libelle ?? ""} onChange={(e) => setSeuil({ ...seuil, libelle: e.target.value })} />
                <Select value={seuil.portee} onChange={(e) => setSeuil({ ...seuil, portee: e.target.value as never })}>
                  {PORTEES.map((value) => (
                    <option key={value} value={value}>{value}</option>
                  ))}
                </Select>
                <Select value={seuil.decisionResultante} onChange={(e) => setSeuil({ ...seuil, decisionResultante: e.target.value as never })}>
                  {DECISIONS.map((value) => (
                    <option key={value} value={value}>{value}</option>
                  ))}
                </Select>
                <Input type="number" placeholder="Priorité" value={seuil.priorite ?? ""} onChange={(e) => setSeuil({ ...seuil, priorite: Number(e.target.value) })} />
                <Input type="number" placeholder="Moyenne min" value={seuil.moyenneMinimale ?? ""} onChange={(e) => setSeuil({ ...seuil, moyenneMinimale: e.target.value === "" ? null : Number(e.target.value) })} />
                <Input type="number" placeholder="Moyenne max" value={seuil.moyenneMaximale ?? ""} onChange={(e) => setSeuil({ ...seuil, moyenneMaximale: e.target.value === "" ? null : Number(e.target.value) })} />
                <Input type="number" placeholder="Taux réussite min" value={seuil.tauxReussiteMinimale ?? ""} onChange={(e) => setSeuil({ ...seuil, tauxReussiteMinimale: e.target.value === "" ? null : Number(e.target.value) })} />
                <Input type="number" placeholder="Absences max" value={seuil.absencesMaximales ?? ""} onChange={(e) => setSeuil({ ...seuil, absencesMaximales: e.target.value === "" ? null : Number(e.target.value) })} />
              </div>
            </AddForm>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Cascade de pondération</CardTitle>
          <CardDescription>
            Évaluation → coursECUE → UE → période → année → cycle.
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex flex-wrap gap-2">
            {NIVEAUX_ACALCUL.map((niveau) => {
              const checked = cascade.niveauxActifs.includes(niveau as never);
              return (
                <label key={niveau} className="flex items-center gap-2 rounded-lg border border-border px-3 py-1.5 text-sm">
                  <Checkbox checked={checked} onClick={() => toggleNiveau(niveau)} />
                  {niveau}
                </label>
              );
            })}
          </div>
          <dl className="grid grid-cols-2 gap-4 sm:grid-cols-4">
            {field("Stratégie", cascade.strategie)}
            {field("Arrondi", cascade.arrondi)}
            {field("Précision", cascade.precision)}
            {field("Seuil d'absorption", cascade.seuilAbsorption ?? "—")}
          </dl>
          {editable && (
            <AddForm
              title="Mettre à jour la cascade"
              busy={busy}
              disabled={cascade.niveauxActifs.length === 0}
              onSubmit={() =>
                run(() => grilleApi.notation.setConfigCascade(id, cascade), "Cascade mise à jour")
              }
            >
              <div className="grid gap-2 sm:grid-cols-4">
                <Select value={cascade.strategie} onChange={(e) => setCascade({ ...cascade, strategie: e.target.value as never })}>
                  {STRATEGIES.map((value) => (
                    <option key={value} value={value}>{value}</option>
                  ))}
                </Select>
                <Select value={cascade.arrondi} onChange={(e) => setCascade({ ...cascade, arrondi: e.target.value as never })}>
                  {ARRONDIS.map((value) => (
                    <option key={value} value={value}>{value}</option>
                  ))}
                </Select>
                <Input type="number" placeholder="Précision" value={cascade.precision} onChange={(e) => setCascade({ ...cascade, precision: Number(e.target.value) })} />
                <Input type="number" placeholder="Seuil absorption (%)" value={cascade.seuilAbsorption ?? ""} onChange={(e) => setCascade({ ...cascade, seuilAbsorption: e.target.value === "" ? null : Number(e.target.value) })} />
              </div>
            </AddForm>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Mentions</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <DataTable
            emptyLabel="Aucune mention"
            keyOf={(row) => row.idMention ?? row.code}
            columns={[
              { header: "Code", render: (row) => <span className="font-mono">{row.code}</span> },
              { header: "Libellé", render: (row) => row.libelle },
              { header: "Symbole", render: (row) => row.symbole || "—" },
              { header: "Seuil", render: (row) => `${row.seuilMinimum} → ${row.seuilMaximum}` },
              { header: "Ordre", render: (row) => row.ordre },
            ]}
            rows={grille.mentions ?? []}
          />
          {editable && (
            <AddForm
              title="Nouvelle mention"
              busy={busy}
              disabled={!mention.code || !mention.libelle}
              onSubmit={() =>
                run(() => grilleApi.notation.addMention(id, mention as Mention), "Mention ajoutée").then(() =>
                  setMention({ code: "", libelle: "", symbole: "", ordre: (grille.mentions?.length ?? 0) + 2 })
                )
              }
            >
              <div className="grid gap-2 sm:grid-cols-4">
                <Input placeholder="Code" value={mention.code ?? ""} onChange={(e) => setMention({ ...mention, code: e.target.value })} />
                <Input placeholder="Libellé" value={mention.libelle ?? ""} onChange={(e) => setMention({ ...mention, libelle: e.target.value })} />
                <Input placeholder="Symbole" value={mention.symbole ?? ""} onChange={(e) => setMention({ ...mention, symbole: e.target.value })} />
                <Input type="number" placeholder="Ordre" value={mention.ordre ?? ""} onChange={(e) => setMention({ ...mention, ordre: Number(e.target.value) })} />
                <Input type="number" placeholder="Seuil min" value={mention.seuilMinimum ?? ""} onChange={(e) => setMention({ ...mention, seuilMinimum: Number(e.target.value) })} />
                <Input type="number" placeholder="Seuil max" value={mention.seuilMaximum ?? ""} onChange={(e) => setMention({ ...mention, seuilMaximum: Number(e.target.value) })} />
                <Input placeholder="Couleur" value={mention.couleur ?? ""} onChange={(e) => setMention({ ...mention, couleur: e.target.value })} />
              </div>
            </AddForm>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Règles d&apos;assiduité</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <DataTable
            emptyLabel="Aucune règle"
            keyOf={(row) => row.idRegle ?? `${row.portee}-${row.priorite}`}
            columns={[
              { header: "Portée", render: (row) => row.portee },
              { header: "Absences max/période", render: (row) => row.absencesMaxParPeriode ?? "—" },
              { header: "Présence minimale", render: (row) => (row.tauxPresenceMinimale != null ? `${row.tauxPresenceMinimale}%` : "—") },
              { header: "Exonération", render: (row) => (row.absencesJustifieesExonerees ? "Oui" : "Non") },
              { header: "Conséquence", render: (row) => <Badge variant="secondary">{row.consequence}</Badge> },
            ]}
            rows={grille.reglesAssiduite ?? []}
          />
          {editable && (
            <AddForm
              title="Nouvelle règle d'assiduité"
              busy={busy}
              disabled={false}
              onSubmit={() =>
                run(() => grilleApi.notation.addRegleAssiduite(id, assiduite as RegleAssiduite), "Règle ajoutée")
              }
            >
              <div className="grid gap-2 sm:grid-cols-4">
                <Select value={assiduite.portee} onChange={(e) => setAssiduite({ ...assiduite, portee: e.target.value as never })}>
                  {PORTEES.map((value) => (
                    <option key={value} value={value}>{value}</option>
                  ))}
                </Select>
                <Select value={assiduite.consequence} onChange={(e) => setAssiduite({ ...assiduite, consequence: e.target.value as never })}>
                  {DECISIONS.map((value) => (
                    <option key={value} value={value}>{value}</option>
                  ))}
                </Select>
                <Input type="number" placeholder="Absences max/période" value={assiduite.absencesMaxParPeriode ?? ""} onChange={(e) => setAssiduite({ ...assiduite, absencesMaxParPeriode: e.target.value === "" ? null : Number(e.target.value) })} />
                <Input type="number" placeholder="Priorité" value={assiduite.priorite ?? ""} onChange={(e) => setAssiduite({ ...assiduite, priorite: Number(e.target.value) })} />
                <Input type="number" placeholder="Présence minimale (%)" value={assiduite.tauxPresenceMinimale ?? ""} onChange={(e) => setAssiduite({ ...assiduite, tauxPresenceMinimale: e.target.value === "" ? null : Number(e.target.value) })} />
              </div>
            </AddForm>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Appréciations</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <DataTable
            emptyLabel="Aucune appréciation"
            keyOf={(row) => row.idRegle ?? row.libelle}
            columns={[
              { header: "Libellé", render: (row) => <span className="font-medium text-slate-900">{row.libelle}</span> },
              { header: "Portée", render: (row) => row.portee },
              { header: "Moyenne", render: (row) => `${row.moyenneMinimale ?? "?"} → ${row.moyenneMaximale ?? "∞"}` },
              { header: "Modèle", render: (row) => row.modeleTexte || "—" },
            ]}
            rows={grille.appreciations ?? []}
          />
          {editable && (
            <AddForm
              title="Nouvelle appréciation"
              busy={busy}
              disabled={!appreciation.libelle}
              onSubmit={() =>
                run(() => grilleApi.notation.addAppreciation(id, appreciation as RegleAppreciation), "Appréciation ajoutée").then(() =>
                  setAppreciation({ ...appreciation, libelle: "" })
                )
              }
            >
              <div className="grid gap-2 sm:grid-cols-3">
                <Input placeholder="Libellé" value={appreciation.libelle ?? ""} onChange={(e) => setAppreciation({ ...appreciation, libelle: e.target.value })} />
                <Select value={appreciation.portee} onChange={(e) => setAppreciation({ ...appreciation, portee: e.target.value as never })}>
                  {PORTEES.map((value) => (
                    <option key={value} value={value}>{value}</option>
                  ))}
                </Select>
                <Input placeholder="Modèle de texte" value={appreciation.modeleTexte ?? ""} onChange={(e) => setAppreciation({ ...appreciation, modeleTexte: e.target.value })} />
                <Input type="number" placeholder="Moyenne min" value={appreciation.moyenneMinimale ?? ""} onChange={(e) => setAppreciation({ ...appreciation, moyenneMinimale: e.target.value === "" ? null : Number(e.target.value) })} />
                <Input type="number" placeholder="Moyenne max" value={appreciation.moyenneMaximale ?? ""} onChange={(e) => setAppreciation({ ...appreciation, moyenneMaximale: e.target.value === "" ? null : Number(e.target.value) })} />
              </div>
            </AddForm>
          )}
        </CardContent>
      </Card>
    </div>
  );
}

function StockSections({
  grille,
  editable,
  busy,
  run,
}: {
  grille: GrilleStock;
  editable: boolean;
  busy: boolean;
  run: RunFn;
}) {
  const id = grille.id ?? "";
  const [alerte, setAlerte] = useState<Partial<{ reference: string; depot: string; seuil: number; niveau: string; active: boolean }>>({
    reference: "",
    depot: "",
    niveau: "ATTENTION",
    active: true,
  });

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle className="text-base">Stratégie & seuils</CardTitle>
        </CardHeader>
        <CardContent>
          <dl className="grid grid-cols-2 gap-4 sm:grid-cols-3">
            {field("Rotation des lots", grille.strategieRotationLot)}
            {field("Quarantaine (j)", grille.dureeQuarantaineJours)}
            {field("Alerte péremption (j)", grille.seuilAlertePeremptionJours)}
            {field("Tolérance inventaire (%)", grille.toleranceEcartInventairePct)}
            {field("Délai réappro (j)", grille.delaiReapproJours ?? "—")}
            {field("Autoriser sortie périmé", grille.autoriserPeremptionEnSortie ? "Oui" : "Non")}
            {field("Contrôle réception", grille.exigerControleReception ? "Oui" : "Non")}
          </dl>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Alertes stock</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <DataTable
            emptyLabel="Aucune alerte"
            keyOf={(row) => row.idAlerte ?? `${row.reference}-${row.depot}`}
            columns={[
              { header: "Référence", render: (row) => <span className="font-mono">{row.reference}</span> },
              { header: "Dépôt", render: (row) => row.depot },
              { header: "Seuil", render: (row) => row.seuil },
              { header: "Niveau", render: (row) => <Badge variant="secondary">{row.niveau}</Badge> },
              { header: "Actif", render: (row) => (row.active ? "Oui" : "Non") },
            ]}
            rows={grille.alertes ?? []}
          />
          {editable && (
            <AddForm
              title="Nouvelle alerte"
              busy={busy}
              disabled={!alerte.reference || !alerte.depot}
              onSubmit={() =>
                run(() => grilleApi.stock.addAlerte(id, alerte as never), "Alerte ajoutée").then(() =>
                  setAlerte({ reference: "", depot: "", seuil: 10, niveau: "ATTENTION", active: true })
                )
              }
            >
              <div className="grid gap-2 sm:grid-cols-4">
                <Input placeholder="Référence" value={alerte.reference} onChange={(e) => setAlerte({ ...alerte, reference: e.target.value })} />
                <Input placeholder="Dépôt" value={alerte.depot} onChange={(e) => setAlerte({ ...alerte, depot: e.target.value })} />
                <Input type="number" placeholder="Seuil" value={alerte.seuil ?? ""} onChange={(e) => setAlerte({ ...alerte, seuil: Number(e.target.value) })} />
                <Select value={alerte.niveau} onChange={(e) => setAlerte({ ...alerte, niveau: e.target.value })}>
                  {ALERTE_NIVEAUX.map((value) => (
                    <option key={value} value={value}>{value}</option>
                  ))}
                </Select>
              </div>
            </AddForm>
          )}
        </CardContent>
      </Card>
    </div>
  );
}

function ConformiteSection({ grille }: { grille: GrilleConformite }) {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Conformité</CardTitle>
      </CardHeader>
      <CardContent>
        <dl className="grid grid-cols-2 gap-4 sm:grid-cols-3">
          {field("Rétention dossier médical (j)", grille.dureeRetentionDossierMedical)}
          {field("Conservation résultats (j)", grille.dureeConservationResultats)}
          {field("Conservation images (j)", grille.dureeConservationImages)}
          {field("Conservation échantillons (j)", grille.dureeConservationEchantillons ?? "—")}
          {field("Chiffrement au repos", grille.chiffrementAuRepos ? "Oui" : "Non")}
          {field("Accès tracé", grille.accesTracable ? "Oui" : "Non")}
          {field("Délai résultat critique (h)", grille.delaiDeclarationResultatCritique ?? "—")}
          {field("Niveau accès critique", grille.niveauAccesResultatCritique ?? "—")}
          {field("Archivable", grille.archivable ? "Oui" : "Non")}
        </dl>
      </CardContent>
    </Card>
  );
}