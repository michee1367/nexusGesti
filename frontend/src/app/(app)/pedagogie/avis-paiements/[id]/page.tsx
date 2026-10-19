"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import { ArrowLeft, Ban, CheckCircle2, Send } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Checkbox } from "@/components/ui/checkbox";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { PageHeader, LoadingView, ErrorState, EmptyState } from "@/components/common/async-state";
import { EntitySelect } from "@/components/common/entity-select";
import { humanize, formatDate } from "@/components/pedagogie/format";
import { lookupLabel, reductionLabel } from "@/components/pedagogie/labels";
import { useApi } from "@/hooks/use-api";
import { useElevesMap } from "@/hooks/use-lookup";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { feesApi } from "@/services/api";
import type { Reduction } from "@/types";

export default function AvisPaiementDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const eleves = useElevesMap([tenantId]);

  const avis = useApi(() => feesApi.avis.get(id), [id, tenantId]);
  const [busy, setBusy] = useState(false);

  const [remiseForm, setRemiseForm] = useState({ reductionId: "", bourse: false, inscriptionTardive: false, eleveRedoublant: false, nombreEnfantsScolaires: "0" });

  const data = avis.data;
  const reload = () => avis.reload();

  const run = async (fn: () => Promise<unknown>, message: string) => {
    setBusy(true);
    try {
      await fn();
      toast.success(message);
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  if (avis.loading) return <LoadingView label="Chargement de l'avis…" />;
  if (avis.error) return <ErrorState error={avis.error} onRetry={reload} />;

  return (
    <div>
      <PageHeader title={`Avis ${data?.reference ?? ""}`} description={data?.eleve ? `Élève ${lookupLabel(eleves, data.eleve)}` : "—"}>
        <Link href="/pedagogie/avis-paiements">
          <Button variant="outline" size="sm">
            <ArrowLeft className="h-4 w-4" />
            Retour
          </Button>
        </Link>
        {isAdmin && data?.statut === "BROUILLON" && (
          <Button size="sm" disabled={busy} onClick={() => run(() => feesApi.avis.valider(id), "Avis validé")}>
            <CheckCircle2 className="h-4 w-4" />
            Valider
          </Button>
        )}
        {isAdmin && data && !["ANNULE", "REGLE"].includes(data.statut) && (
          <Button variant="outline" size="sm" disabled={busy} onClick={() => run(() => feesApi.avis.emettre(id), "Avis émis")}>
            <Send className="h-4 w-4" />
            Émettre
          </Button>
        )}
        {isAdmin && data && !["ANNULE", "REGLE"].includes(data.statut) && (
          <Button
            variant="outline"
            size="sm"
            disabled={busy}
            onClick={() => {
              const motif = prompt("Motif d'annulation :");
              if (motif == null) return;
              run(() => feesApi.avis.annuler(id, motif), "Avis annulé");
            }}
          >
            <Ban className="h-4 w-4" />
            Annuler
          </Button>
        )}
      </PageHeader>

      <div className="mb-4 grid gap-3 sm:grid-cols-4">
        <Card>
          <CardContent className="p-4">
            <p className="text-xs uppercase text-slate-500">Montant brut</p>
            <p className="text-lg font-semibold">{data?.montantBrut ?? "—"} {data?.devise ?? "USD"}</p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <p className="text-xs uppercase text-slate-500">Réduction</p>
            <p className="text-lg font-semibold">{data?.montantReduction ?? 0}</p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <p className="text-xs uppercase text-slate-500">Net</p>
            <p className="text-lg font-semibold">{data?.montantNet ?? "—"}</p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <p className="text-xs uppercase text-slate-500">Solde dû</p>
            <p className="text-lg font-semibold text-danger">{data?.soldeDu ?? "—"}</p>
          </CardContent>
        </Card>
      </div>

      <div className="mb-4 flex flex-wrap items-center gap-2">
        <Badge variant={data?.statut === "REGLE" ? "success" : data?.statut === "ANNULE" ? "danger" : "outline"}>{humanize(data?.statut ?? "—")}</Badge>
        {data?.annee && <Badge variant="outline">Année {data.annee}</Badge>}
        {data?.classe && <Badge variant="outline">Classe {data.classe}</Badge>}
        {data?.dateEmission && <Badge variant="outline">Émis {formatDate(data.dateEmission)}</Badge>}
        {data?.dateEcheanceGlobale && <Badge variant="outline">Échéance {formatDate(data.dateEcheanceGlobale)}</Badge>}
        <Badge variant="outline">Réglé {data?.montantRegle ?? 0}</Badge>
      </div>

      {isAdmin && data?.grilleId && !["ANNULE", "REGLE"].includes(data.statut) && (
        <Card className="mb-4">
          <CardHeader className="p-4 pb-2">
            <CardTitle className="text-base">Appliquer une remise</CardTitle>
          </CardHeader>
          <CardContent className="p-4 pt-2">
            <form
              className="grid gap-3 sm:grid-cols-4"
              onSubmit={(event) => {
                event.preventDefault();
                if (!remiseForm.reductionId) return;
                run(
                  () =>
                    feesApi.avis.remise(id, {
                      reductionId: remiseForm.reductionId,
                      contexte: {
                        nombreEnfantsScolaires: Number(remiseForm.nombreEnfantsScolaires),
                        bourse: remiseForm.bourse,
                        inscriptionTardive: remiseForm.inscriptionTardive,
                        eleveRedoublant: remiseForm.eleveRedoublant,
                      },
                    }),
                  "Remise appliquée",
                );
              }}
            >
              <div className="sm:col-span-2">
                <Label className="mb-1 block">Réduction</Label>
                <EntitySelect<Reduction>
                  fetcher={async () => (data?.grilleId ? (await feesApi.grilles.get(data.grilleId)).reductions ?? [] : [])}
                  keyOf={(reduction) => reduction.id ?? ""}
                  labelOf={reductionLabel}
                  value={remiseForm.reductionId}
                  onChange={(value) => setRemiseForm({ ...remiseForm, reductionId: value })}
                  deps={[id, data?.grilleId]}
                  placeholder="Sélectionner une réduction…"
                />
              </div>
              <div>
                <Label className="mb-1 block">Enfants scolarisés</Label>
                <Input type="number" value={remiseForm.nombreEnfantsScolaires} onChange={(event) => setRemiseForm({ ...remiseForm, nombreEnfantsScolaires: event.target.value })} />
              </div>
              <div className="flex items-end">
                <Button type="submit" disabled={busy || !remiseForm.reductionId}>
                  Appliquer
                </Button>
              </div>
              <div className="flex items-center gap-2">
                <Checkbox checked={remiseForm.bourse} onChange={(checked) => setRemiseForm({ ...remiseForm, bourse: checked })} />
                <Label>Bourse</Label>
              </div>
              <div className="flex items-center gap-2">
                <Checkbox checked={remiseForm.inscriptionTardive} onChange={(checked) => setRemiseForm({ ...remiseForm, inscriptionTardive: checked })} />
                <Label>Inscription tardive</Label>
              </div>
              <div className="flex items-center gap-2">
                <Checkbox checked={remiseForm.eleveRedoublant} onChange={(checked) => setRemiseForm({ ...remiseForm, eleveRedoublant: checked })} />
                <Label>Élève redoublant</Label>
              </div>
            </form>
          </CardContent>
        </Card>
      )}

      <Card>
        <CardHeader className="p-4 pb-2">
          <CardTitle className="text-base">Lignes et échéances ({data?.lignes?.length ?? 0})</CardTitle>
        </CardHeader>
        <CardContent className="p-4 pt-2">
          {data?.lignes?.length ? (
            <div className="space-y-3">
              {data.lignes.map((ligne, index) => (
                <div key={ligne.id ?? index} className="rounded-md border border-border p-3">
                  <div className="mb-2 flex flex-wrap items-center gap-2">
                    {ligne.typeFrais && <Badge variant="outline">{humanize(ligne.typeFrais)}</Badge>}
                    <span className="font-medium">{ligne.libelle ?? "—"}</span>
                    <span className="text-sm text-slate-500">
                      brut {ligne.montantBrut ?? 0} · réduction {ligne.montantReduction ?? 0} · net {ligne.montantNet ?? 0} · dû {ligne.montantDu ?? 0}
                    </span>
                  </div>
                  {ligne.echeances?.length ? (
                    <table className="w-full text-sm">
                      <thead>
                        <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                          <th className="px-2 py-1">N°</th>
                          <th className="px-2 py-1">Date</th>
                          <th className="px-2 py-1">Montant</th>
                          <th className="px-2 py-1">Réglé</th>
                          <th className="px-2 py-1">Statut</th>
                        </tr>
                      </thead>
                      <tbody>
                        {ligne.echeances.map((echeance, eIndex) => (
                          <tr key={echeance.id ?? eIndex} className="border-b border-border last:border-0">
                            <td className="px-2 py-1">{echeance.numero ?? eIndex + 1}</td>
                            <td className="px-2 py-1">{formatDate(echeance.dateEcheance)}</td>
                            <td className="px-2 py-1">{echeance.montant ?? "—"}</td>
                            <td className="px-2 py-1">{echeance.montantRegle}</td>
                            <td className="px-2 py-1">
                              <Badge variant={echeance.statut === "REGLEE" ? "success" : echeance.statut === "ECHUE" ? "danger" : "outline"}>
                                {humanize(echeance.statut ?? "—")}
                              </Badge>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  ) : (
                    <p className="text-xs text-slate-400">Aucune échéance</p>
                  )}
                </div>
              ))}
            </div>
          ) : (
            <EmptyState label="Aucune ligne" />
          )}
        </CardContent>
      </Card>
    </div>
  );
}