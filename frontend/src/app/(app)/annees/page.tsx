"use client";

import { useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import {
  PageHeader,
  LoadingView,
  ErrorState,
  EmptyState,
} from "@/components/common/async-state";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { anneeApi } from "@/services/api";
import { cn } from "@/lib/utils";

const statutStyles: Record<string, string> = {
  OUVERTE: "bg-success/10 text-success border-success/30",
  PREVUE: "bg-slate-100 text-slate-500 border-slate-300",
  CLOTUREE: "bg-danger/10 text-danger border-danger/30",
};

const typeLabels: Record<string, string> = {
  SCOLAIRE: "Scolaire",
  UNIVERSITAIRE: "Universitaire",
  CIVILE: "Civile",
  BUDGETAIRE: "Budgétaire",
  EXERCICE: "Exercice",
};

function toDateInput(value?: string): string {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  return date.toISOString().slice(0, 10);
}

export default function AnneesPage() {
  const { tenantId } = useTenant();
  const { isAdmin } = useAuth();
  const annees = useApi(anneeApi.list, [tenantId]);
  const [expanded, setExpanded] = useState<string | null>(null);
  const [newAnnee, setNewAnnee] = useState({
    code: "",
    libelle: "",
    type: "SCOLAIRE",
    dateDebut: "",
    dateFin: "",
    anneeReference: new Date().getFullYear(),
  });
  const [newPeriode, setNewPeriode] = useState({
    code: "",
    libelle: "",
    type: "TRIMESTRE",
    dateDebut: "",
    dateFin: "",
    rang: 1,
  });
  const [busy, setBusy] = useState(false);

  const periodes = useApi(
    () => (expanded ? anneeApi.listPeriodes(expanded) : Promise.resolve([])),
    [expanded]
  );

  if (annees.loading) return <LoadingView label="Chargement des années…" />;
  if (annees.error) return <ErrorState error={annees.error} onRetry={annees.reload} />;

  const submit = async (action: () => Promise<unknown>, success: string) => {
    setBusy(true);
    try {
      await action();
      toast.success(success);
      annees.reload();
      if (expanded) periodes.reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  return (
    <div>
      <PageHeader
        title="Années scolaires"
        description="Cycle de vie des années du tenant (prévue → ouverte → clôturée)."
      />

      {isAdmin && (
        <Card className="mb-6">
          <CardHeader>
            <CardTitle className="text-base">Nouvelle année</CardTitle>
          </CardHeader>
          <CardContent>
            <form
              className="grid gap-2 sm:grid-cols-3 lg:grid-cols-6"
              onSubmit={(e) => {
                e.preventDefault();
                submit(
                  () =>
                    anneeApi.create({
                      code: newAnnee.code,
                      libelle: newAnnee.libelle,
                      type: newAnnee.type as never,
                      dateDebut: newAnnee.dateDebut || undefined,
                      dateFin: newAnnee.dateFin || undefined,
                      anneeReference: Number(newAnnee.anneeReference),
                    }),
                  "Année créée"
                ).then(() =>
                  setNewAnnee({
                    code: "",
                    libelle: "",
                    type: "SCOLAIRE",
                    dateDebut: "",
                    dateFin: "",
                    anneeReference: new Date().getFullYear(),
                  })
                );
              }}
            >
              <Input
                placeholder="Code"
                value={newAnnee.code}
                onChange={(e) => setNewAnnee({ ...newAnnee, code: e.target.value })}
              />
              <Input
                placeholder="Libellé"
                value={newAnnee.libelle}
                onChange={(e) => setNewAnnee({ ...newAnnee, libelle: e.target.value })}
              />
              <select
                className="h-10 rounded-lg border border-border bg-white px-3 py-2 text-sm"
                value={newAnnee.type}
                onChange={(e) => setNewAnnee({ ...newAnnee, type: e.target.value })}
              >
                {Object.entries(typeLabels).map(([value, label]) => (
                  <option key={value} value={value}>
                    {label}
                  </option>
                ))}
              </select>
              <Input
                type="date"
                value={newAnnee.dateDebut}
                onChange={(e) => setNewAnnee({ ...newAnnee, dateDebut: e.target.value })}
              />
              <Input
                type="date"
                value={newAnnee.dateFin}
                onChange={(e) => setNewAnnee({ ...newAnnee, dateFin: e.target.value })}
              />
              <Button
                type="submit"
                disabled={busy || !newAnnee.code || !newAnnee.libelle}
              >
                Créer
              </Button>
            </form>
          </CardContent>
        </Card>
      )}

      {annees.data?.length ? (
        <div className="space-y-3">
          {annees.data.map((annee) => (
            <Card key={annee.idAnnee}>
              <CardContent className="flex flex-wrap items-center justify-between gap-3 p-4">
                <div>
                  <div className="flex items-center gap-2">
                    <p className="font-semibold text-slate-900">{annee.libelle}</p>
                    <Badge
                      className={cn("border", statutStyles[annee.statut] ?? "")}
                    >
                      {annee.statut}
                    </Badge>
                    <Badge variant="secondary" className="text-xs">
                      {typeLabels[annee.type] ?? annee.type}
                    </Badge>
                  </div>
                  <p className="mt-0.5 font-mono text-xs text-slate-500">
                    {annee.code} · {toDateInput(annee.dateDebut) || "?"} →{" "}
                    {toDateInput(annee.dateFin) || "?"} · {annee.periodes.length} période(s)
                  </p>
                </div>
                <div className="flex items-center gap-2">
                  {isAdmin && annee.statut === "PREVUE" && (
                    <Button
                      variant="outline"
                      size="sm"
                      disabled={busy}
                      onClick={() =>
                        submit(() => anneeApi.ouvrir(annee.idAnnee), "Année ouverte")
                      }
                    >
                      Ouvrir
                    </Button>
                  )}
                  {isAdmin && annee.statut === "OUVERTE" && (
                    <Button
                      variant="outline"
                      size="sm"
                      disabled={busy}
                      onClick={() =>
                        submit(() => anneeApi.cloturer(annee.idAnnee), "Année clôturée")
                      }
                    >
                      Clôturer
                    </Button>
                  )}
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => {
                      setExpanded(expanded === annee.idAnnee ? null : annee.idAnnee);
                      periodes.reload();
                    }}
                  >
                    {expanded === annee.idAnnee ? "Masquer" : "Périodes"}
                  </Button>
                </div>
              </CardContent>

              {expanded === annee.idAnnee && (
                <div className="border-t border-border p-4">
                  {periodes.loading ? (
                    <EmptyState label="Chargement…" />
                  ) : periodes.data?.length ? (
                    <div className="grid gap-2 sm:grid-cols-2">
                      {periodes.data.map((periode) => (
                        <div key={periode.idPeriode} className="rounded-lg border border-border p-3">
                          <div className="flex items-center justify-between">
                            <p className="font-medium text-slate-900">{periode.libelle}</p>
                            {periode.cloturee ? (
                              <Badge className="border border-danger/30 bg-danger/10 text-danger">
                                Clôturée
                              </Badge>
                            ) : (
                              <Badge variant="secondary" className="text-xs">
                                {typeLabels[periode.type] ?? periode.type}
                              </Badge>
                            )}
                          </div>
                          <p className="mt-0.5 text-xs text-slate-500">
                            Rang {periode.rang} · {toDateInput(periode.dateDebut)} →{" "}
                            {toDateInput(periode.dateFin)}
                          </p>
                          {isAdmin && !periode.cloturee && (
                            <Button
                              variant="outline"
                              size="sm"
                              className="mt-2"
                              disabled={busy}
                              onClick={() =>
                                submit(
                                  () =>
                                    anneeApi.cloturerPeriode(annee.idAnnee, periode.idPeriode),
                                  "Période clôturée"
                                )
                              }
                            >
                              Clôturer la période
                            </Button>
                          )}
                        </div>
                      ))}
                    </div>
                  ) : (
                    <EmptyState label="Aucune période" />
                  )}

                  {isAdmin && (
                    <form
                      className="mt-4 grid gap-2 border-t border-border pt-4 sm:grid-cols-3 lg:grid-cols-6"
                      onSubmit={(e) => {
                        e.preventDefault();
                        submit(
                          () =>
                            anneeApi.addPeriode(annee.idAnnee, {
                              code: newPeriode.code,
                              libelle: newPeriode.libelle,
                              type: newPeriode.type as never,
                              dateDebut: newPeriode.dateDebut || undefined,
                              dateFin: newPeriode.dateFin || undefined,
                              rang: Number(newPeriode.rang),
                            }),
                          "Période créée"
                        ).then(() =>
                          setNewPeriode({
                            code: "",
                            libelle: "",
                            type: "TRIMESTRE",
                            dateDebut: "",
                            dateFin: "",
                            rang: (periodes.data?.length ?? 0) + 1,
                          })
                        );
                      }}
                    >
                      <Input
                        placeholder="Code"
                        value={newPeriode.code}
                        onChange={(e) => setNewPeriode({ ...newPeriode, code: e.target.value })}
                      />
                      <Input
                        placeholder="Libellé"
                        value={newPeriode.libelle}
                        onChange={(e) =>
                          setNewPeriode({ ...newPeriode, libelle: e.target.value })
                        }
                      />
                      <Input
                        type="number"
                        placeholder="Rang"
                        value={newPeriode.rang}
                        onChange={(e) =>
                          setNewPeriode({ ...newPeriode, rang: Number(e.target.value) })
                        }
                      />
                      <Input
                        type="date"
                        value={newPeriode.dateDebut}
                        onChange={(e) =>
                          setNewPeriode({ ...newPeriode, dateDebut: e.target.value })
                        }
                      />
                      <Input
                        type="date"
                        value={newPeriode.dateFin}
                        onChange={(e) =>
                          setNewPeriode({ ...newPeriode, dateFin: e.target.value })
                        }
                      />
                      <Button type="submit" disabled={busy || !newPeriode.code || !newPeriode.libelle}>
                        Ajouter
                      </Button>
                    </form>
                  )}
                </div>
              )}
            </Card>
          ))}
        </div>
      ) : (
        <EmptyState label="Aucune année scolaire" />
      )}
    </div>
  );
}