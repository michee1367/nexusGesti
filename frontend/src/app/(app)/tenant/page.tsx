"use client";

import { useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select } from "@/components/ui/select";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import {
  PageHeader,
  LoadingView,
  ErrorState,
  EmptyState,
} from "@/components/common/async-state";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import {
  etablissementApi,
  tenantApi,
} from "@/services/api";
import { cn } from "@/lib/utils";

const statutStyles: Record<string, string> = {
  ACTIF: "bg-success/10 text-success border-success/30",
  PREPARATION: "bg-warning/10 text-warning border-warning/30",
  SUSPENDU: "bg-danger/10 text-danger border-danger/30",
  ARCHIVE: "bg-slate-100 text-slate-500 border-slate-300",
};

function errorMessage(error: unknown): string {
  return error instanceof Error ? error.message : "Une erreur est survenue";
}

export default function TenantPage() {
  const { tenantId } = useTenant();
  const { isAdmin } = useAuth();
  const etablissement = useApi(() => tenantApi.get(tenantId ?? ""), [tenantId]);
  const sites = useApi(etablissementApi.sites.list, [tenantId]);
  const filieres = useApi(etablissementApi.filieres.list, [tenantId]);
  const cycles = useApi(etablissementApi.cycles.list, [tenantId]);
  const parametres = useApi(etablissementApi.parametres.list, [tenantId]);
  const presets = useApi(etablissementApi.presets.list, [tenantId]);

  const [statut, setStatut] = useState("");
  const [newSite, setNewSite] = useState({ code: "", libelle: "" });
  const [newFiliere, setNewFiliere] = useState({ code: "", libelle: "", departement: "" });
  const [newProgramme, setNewProgramme] = useState({ filiereId: "", code: "", libelle: "" });
  const [newCycle, setNewCycle] = useState({ code: "", libelle: "", rang: 1 });
  const [newParametre, setNewParametre] = useState({ cle: "", valeur: "", typeValeur: "CHAINE" });
  const [newPreset, setNewPreset] = useState({ code: "", libelle: "" });
  const [busy, setBusy] = useState(false);

  if (etablissement.loading) return <LoadingView label="Chargement de l'établissement…" />;
  if (etablissement.error)
    return <ErrorState error={etablissement.error} onRetry={etablissement.reload} />;

  const tenant = etablissement.data;

  const submit = async (action: () => Promise<unknown>, success: string) => {
    setBusy(true);
    try {
      await action();
      toast.success(success);
      for (const resource of [sites, filieres, cycles, parametres, presets]) {
        resource.reload();
      }
    } catch (error) {
      toast.error(errorMessage(error));
    } finally {
      setBusy(false);
    }
  };

  return (
    <div>
      <PageHeader
        title="Établissement"
        description="Identité et structure du tenant courant."
      >
        <Badge className={cn("border", statutStyles[tenant?.statut ?? "PREPARATION"])}>
          {tenant?.statut ?? "—"}
        </Badge>
      </PageHeader>

      <Tabs defaultValue="identite">
        <TabsList>
          <TabsTrigger value="identite">Identité</TabsTrigger>
          <TabsTrigger value="sites">Sites</TabsTrigger>
          <TabsTrigger value="filieres">Filières & programmes</TabsTrigger>
          <TabsTrigger value="cycles">Cycles</TabsTrigger>
          <TabsTrigger value="parametres">Paramètres</TabsTrigger>
          <TabsTrigger value="presets">Presets de grilles</TabsTrigger>
        </TabsList>

        <TabsContent value="identite">
          <Card>
            <CardHeader>
              <CardTitle className="text-base">Identité</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid gap-4 sm:grid-cols-2">
                <div>
                  <Label>Dénomination</Label>
                  <p className="mt-1 font-medium text-slate-900">{tenant?.denomination}</p>
                </div>
                <div>
                  <Label>Code</Label>
                  <p className="mt-1 font-mono text-slate-900">{tenant?.code}</p>
                </div>
                <div>
                  <Label>Profil</Label>
                  <p className="mt-1 text-slate-900">{tenant?.profil}</p>
                </div>
                <div>
                  <Label>Sigle</Label>
                  <p className="mt-1 text-slate-900">{tenant?.sigle || "—"}</p>
                </div>
                <div>
                  <Label>Email officiel</Label>
                  <p className="mt-1 text-slate-900">{tenant?.emailOfficiel || "—"}</p>
                </div>
                <div>
                  <Label>Téléphone</Label>
                  <p className="mt-1 text-slate-900">{tenant?.telephone || "—"}</p>
                </div>
                <div>
                  <Label>Devise</Label>
                  <p className="mt-1 text-slate-900">{tenant?.devise || "—"}</p>
                </div>
                <div>
                  <Label>Id tenant</Label>
                  <p className="mt-1 truncate font-mono text-xs text-slate-900">{tenant?.id}</p>
                </div>
              </div>

              {isAdmin && (
                <div className="flex flex-wrap items-end gap-3 border-t border-border pt-4">
                  <div className="w-48">
                    <Label htmlFor="statut">Changer le statut</Label>
                    <Select
                      id="statut"
                      value={statut}
                      onChange={(e) => setStatut(e.target.value)}
                      className="mt-1"
                    >
                      <option value="">—</option>
                      <option value="PREPARATION">Préparation</option>
                      <option value="ACTIF">Actif</option>
                      <option value="SUSPENDU">Suspendu</option>
                      <option value="ARCHIVE">Archivé</option>
                    </Select>
                  </div>
                  <Button
                    variant="outline"
                    disabled={!statut || busy}
                    onClick={() =>
                      submit(
                        () => tenantApi.changeStatut(tenant?.id ?? "", statut as never),
                        "Statut mis à jour"
                      )
                    }
                  >
                    Appliquer
                  </Button>
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="sites">
          <Card>
            <CardHeader>
              <CardTitle className="text-base">Sites</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              {sites.data?.length ? (
                <div className="grid gap-2 sm:grid-cols-2">
                  {sites.data.map((site) => (
                    <div key={site.idSite} className="rounded-lg border border-border p-3">
                      <div className="flex items-center justify-between">
                        <p className="font-medium text-slate-900">{site.libelle}</p>
                        {site.principal && (
                          <Badge variant="secondary" className="text-xs">
                            Principal
                          </Badge>
                        )}
                      </div>
                      <p className="font-mono text-xs text-slate-500">{site.code}</p>
                    </div>
                  ))}
                </div>
              ) : (
                <EmptyState label="Aucun site" />
              )}

              {isAdmin && (
                <form
                  className="grid gap-2 border-t border-border pt-4 sm:grid-cols-[1fr_1fr_auto]"
                  onSubmit={(e) => {
                    e.preventDefault();
                    submit(
                      () => etablissementApi.sites.create(newSite),
                      "Site créé"
                    ).then(() => setNewSite({ code: "", libelle: "" }));
                  }}
                >
                  <Input
                    placeholder="Code (ex. PRINCIPAL)"
                    value={newSite.code}
                    onChange={(e) => setNewSite({ ...newSite, code: e.target.value })}
                  />
                  <Input
                    placeholder="Libellé"
                    value={newSite.libelle}
                    onChange={(e) => setNewSite({ ...newSite, libelle: e.target.value })}
                  />
                  <Button type="submit" disabled={busy || !newSite.code || !newSite.libelle}>
                    Ajouter
                  </Button>
                </form>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="filieres">
          <Card>
            <CardHeader>
              <CardTitle className="text-base">Filières</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              {filieres.data?.length ? (
                <div className="overflow-x-auto">
                  <table className="w-full text-sm">
                    <thead>
                      <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                        <th className="px-3 py-2">Code</th>
                        <th className="px-3 py-2">Libellé</th>
                        <th className="px-3 py-2">Département</th>
                        <th className="px-3 py-2">Programmes</th>
                      </tr>
                    </thead>
                    <tbody>
                      {filieres.data.map((filiere) => (
                        <tr key={filiere.idFiliere} className="border-b border-border">
                          <td className="px-3 py-2 font-mono">{filiere.code}</td>
                          <td className="px-3 py-2 font-medium text-slate-900">{filiere.libelle}</td>
                          <td className="px-3 py-2">{filiere.departement || "—"}</td>
                          <td className="px-3 py-2 text-slate-500">
                            {filiere.programmes.length} programme(s)
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : (
                <EmptyState label="Aucune filière" />
              )}

              {isAdmin && (
                <div className="grid gap-4 border-t border-border pt-4">
                  <form
                    className="grid gap-2 sm:grid-cols-[1fr_1fr_1fr_auto]"
                    onSubmit={(e) => {
                      e.preventDefault();
                      submit(
                        () => etablissementApi.filieres.create(newFiliere),
                        "Filière créée"
                      ).then(() => setNewFiliere({ code: "", libelle: "", departement: "" }));
                    }}
                  >
                    <Input
                      placeholder="Code"
                      value={newFiliere.code}
                      onChange={(e) => setNewFiliere({ ...newFiliere, code: e.target.value })}
                    />
                    <Input
                      placeholder="Libellé"
                      value={newFiliere.libelle}
                      onChange={(e) => setNewFiliere({ ...newFiliere, libelle: e.target.value })}
                    />
                    <Input
                      placeholder="Département"
                      value={newFiliere.departement}
                      onChange={(e) =>
                        setNewFiliere({ ...newFiliere, departement: e.target.value })
                      }
                    />
                    <Button
                      type="submit"
                      disabled={busy || !newFiliere.code || !newFiliere.libelle}
                    >
                      Ajouter
                    </Button>
                  </form>

                  <div>
                    <Label>Ajouter un programme à une filière</Label>
                    <form
                      className="mt-1 grid gap-2 sm:grid-cols-[1fr_1fr_1fr_auto]"
                      onSubmit={(e) => {
                        e.preventDefault();
                        submit(
                          () =>
                            etablissementApi.filieres.addProgramme(newProgramme.filiereId, {
                              code: newProgramme.code,
                              libelle: newProgramme.libelle,
                            }),
                          "Programme créé"
                        ).then(() =>
                          setNewProgramme({ filiereId: "", code: "", libelle: "" })
                        );
                      }}
                    >
                      <Select
                        value={newProgramme.filiereId}
                        onChange={(e) =>
                          setNewProgramme({ ...newProgramme, filiereId: e.target.value })
                        }
                      >
                        <option value="">Filière…</option>
                        {filieres.data?.map((filiere) => (
                          <option key={filiere.idFiliere} value={filiere.idFiliere}>
                            {filiere.libelle}
                          </option>
                        ))}
                      </Select>
                      <Input
                        placeholder="Code"
                        value={newProgramme.code}
                        onChange={(e) =>
                          setNewProgramme({ ...newProgramme, code: e.target.value })
                        }
                      />
                      <Input
                        placeholder="Libellé"
                        value={newProgramme.libelle}
                        onChange={(e) =>
                          setNewProgramme({ ...newProgramme, libelle: e.target.value })
                        }
                      />
                      <Button
                        type="submit"
                        disabled={busy || !newProgramme.filiereId || !newProgramme.code}
                      >
                        Ajouter
                      </Button>
                    </form>
                  </div>
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="cycles">
          <Card>
            <CardHeader>
              <CardTitle className="text-base">Cycles</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              {cycles.data?.length ? (
                <div className="overflow-x-auto">
                  <table className="w-full text-sm">
                    <thead>
                      <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                        <th className="px-3 py-2">Rang</th>
                        <th className="px-3 py-2">Code</th>
                        <th className="px-3 py-2">Libellé</th>
                        <th className="px-3 py-2">Profil</th>
                      </tr>
                    </thead>
                    <tbody>
                      {cycles.data.map((cycle) => (
                        <tr key={cycle.id} className="border-b border-border">
                          <td className="px-3 py-2">{cycle.rang}</td>
                          <td className="px-3 py-2 font-mono">{cycle.code}</td>
                          <td className="px-3 py-2 font-medium text-slate-900">{cycle.libelle}</td>
                          <td className="px-3 py-2">
                            <Badge variant="secondary">{cycle.profil}</Badge>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : (
                <EmptyState label="Aucun cycle" />
              )}

              {isAdmin && (
                <form
                  className="grid gap-2 border-t border-border pt-4 sm:grid-cols-[1fr_1fr_auto]"
                  onSubmit={(e) => {
                    e.preventDefault();
                    submit(
                      () =>
                        etablissementApi.cycles.create({
                          ...newCycle,
                          profil: tenant?.profil ?? "PRIMAIRE",
                        }),
                      "Cycle créé"
                    ).then(() => setNewCycle({ code: "", libelle: "", rang: cycles.data?.length ?? 0 + 1 }));
                  }}
                >
                  <Input
                    placeholder="Code"
                    value={newCycle.code}
                    onChange={(e) => setNewCycle({ ...newCycle, code: e.target.value })}
                  />
                  <Input
                    placeholder="Libellé"
                    value={newCycle.libelle}
                    onChange={(e) => setNewCycle({ ...newCycle, libelle: e.target.value })}
                  />
                  <Button type="submit" disabled={busy || !newCycle.code || !newCycle.libelle}>
                    Ajouter
                  </Button>
                </form>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="parametres">
          <Card>
            <CardHeader>
              <CardTitle className="text-base">Paramètres</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              {parametres.data?.length ? (
                <div className="overflow-x-auto">
                  <table className="w-full text-sm">
                    <thead>
                      <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                        <th className="px-3 py-2">Clé</th>
                        <th className="px-3 py-2">Valeur</th>
                        <th className="px-3 py-2">Type</th>
                        <th className="px-3 py-2">Catégorie</th>
                      </tr>
                    </thead>
                    <tbody>
                      {parametres.data.map((parametre) => (
                        <tr key={parametre.id} className="border-b border-border">
                          <td className="px-3 py-2 font-mono">{parametre.cle}</td>
                          <td className="px-3 py-2">{parametre.valeur || "—"}</td>
                          <td className="px-3 py-2">
                            <Badge variant="secondary">{parametre.typeValeur}</Badge>
                          </td>
                          <td className="px-3 py-2">{parametre.categorie || "—"}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : (
                <EmptyState label="Aucun paramètre" />
              )}

              {isAdmin && (
                <form
                  className="grid gap-2 border-t border-border pt-4 sm:grid-cols-[1fr_1fr_auto]"
                  onSubmit={(e) => {
                    e.preventDefault();
                    submit(
                      () => etablissementApi.parametres.create({
                          cle: newParametre.cle,
                          valeur: newParametre.valeur,
                          typeValeur: newParametre.typeValeur as never,
                        }),
                      "Paramètre créé"
                    ).then(() => setNewParametre({ cle: "", valeur: "", typeValeur: "CHAINE" }));
                  }}
                >
                  <Input
                    placeholder="Clé"
                    value={newParametre.cle}
                    onChange={(e) => setNewParametre({ ...newParametre, cle: e.target.value })}
                  />
                  <Input
                    placeholder="Valeur"
                    value={newParametre.valeur}
                    onChange={(e) => setNewParametre({ ...newParametre, valeur: e.target.value })}
                  />
                  <Button type="submit" disabled={busy || !newParametre.cle}>
                    Ajouter
                  </Button>
                </form>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="presets">
          <Card>
            <CardHeader>
              <CardTitle className="text-base">Presets de grilles</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              {presets.data?.length ? (
                <div className="grid gap-2 sm:grid-cols-2">
                  {presets.data.map((preset) => (
                    <div key={preset.idGrillePreset} className="rounded-lg border border-border p-3">
                      <div className="flex items-center justify-between">
                        <p className="font-medium text-slate-900">{preset.libelle}</p>
                        {preset.systeme && (
                          <Badge variant="secondary" className="text-xs">
                            Système
                          </Badge>
                        )}
                      </div>
                      <p className="font-mono text-xs text-slate-500">
                        {preset.code} · {preset.profil}
                      </p>
                    </div>
                  ))}
                </div>
              ) : (
                <EmptyState label="Aucun preset" />
              )}

              {isAdmin && (
                <form
                  className="grid gap-2 border-t border-border pt-4 sm:grid-cols-[1fr_1fr_auto]"
                  onSubmit={(e) => {
                    e.preventDefault();
                    submit(
                      () =>
                        etablissementApi.presets.create({
                          ...newPreset,
                          profil: tenant?.profil ?? "PRIMAIRE",
                        }),
                      "Preset créé"
                    ).then(() => setNewPreset({ code: "", libelle: "" }));
                  }}
                >
                  <Input
                    placeholder="Code"
                    value={newPreset.code}
                    onChange={(e) => setNewPreset({ ...newPreset, code: e.target.value })}
                  />
                  <Input
                    placeholder="Libellé"
                    value={newPreset.libelle}
                    onChange={(e) => setNewPreset({ ...newPreset, libelle: e.target.value })}
                  />
                  <Button type="submit" disabled={busy || !newPreset.code || !newPreset.libelle}>
                    Ajouter
                  </Button>
                </form>
              )}
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}