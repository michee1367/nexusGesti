"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import { ArrowLeft, CheckCircle2, Gavel, Play } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { PageHeader, LoadingView, ErrorState, EmptyState } from "@/components/common/async-state";
import { humanize } from "@/components/pedagogie/format";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { examApi } from "@/services/api";

export default function DeliberationDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();

  const deliberation = useApi(() => examApi.deliberations.get(id), [id, tenantId]);
  const [busy, setBusy] = useState(false);

  const [decider, setDecider] = useState({ classeId: "", eleveId: "" });
  const [decisionUe, setDecisionUe] = useState({ eleveId: "", ueId: "", credits: "0", moyenneUE: "0" });

  const data = deliberation.data;
  const reload = () => deliberation.reload();

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

  if (deliberation.loading) return <LoadingView label="Chargement de la délibération…" />;
  if (deliberation.error) return <ErrorState error={deliberation.error} onRetry={reload} />;

  const statut = data?.statut ?? "";

  return (
    <div>
      <PageHeader title="Délibération" description={`${data?.reference ?? "—"} · ${humanize(statut || "—")}`}>
        <Link href="/pedagogie/deliberations">
          <Button variant="outline" size="sm">
            <ArrowLeft className="h-4 w-4" />
            Retour
          </Button>
        </Link>
        {isAdmin && ["PLANIFIEE", "REPRISE"].includes(statut) && (
          <Button size="sm" disabled={busy} onClick={() => run(() => examApi.deliberations.ouvrir(id), "Délibération ouverte")}>
            <Play className="h-4 w-4" />
            Ouvrir
          </Button>
        )}
        {isAdmin && statut === "EN_COURS" && (
          <Button size="sm" disabled={busy} onClick={() => run(() => examApi.deliberations.cloturer(id), "Délibération clôturée")}>
            <CheckCircle2 className="h-4 w-4" />
            Clôturer
          </Button>
        )}
      </PageHeader>

      <div className="mb-4 flex flex-wrap items-center gap-2">
        <Badge variant="outline">{humanize(statut || "—")}</Badge>
        <Badge variant={data?.quorumAtteint ? "success" : "warning"}>
          Quorum {data?.quorumAtteint ? "atteint" : "non atteint"}
        </Badge>
        {data?.tauxReussite != null && <Badge variant="outline">Taux réussite {data.tauxReussite} %</Badge>}
        {data?.president && <Badge variant="outline">Président {data.president}</Badge>}
      </div>

      <Card className="mb-4">
        <CardHeader className="p-4 pb-2">
          <CardTitle className="flex items-center gap-2 text-base">
            <Gavel className="h-4 w-4" />
            Décisions individuelles ({data?.decisions?.length ?? 0})
          </CardTitle>
        </CardHeader>
        <CardContent className="overflow-x-auto p-4 pt-2">
          {data?.decisions?.length ? (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-3 py-2">Élève</th>
                  <th className="px-3 py-2">Classe</th>
                  <th className="px-3 py-2">Moyenne</th>
                  <th className="px-3 py-2">Décision</th>
                  <th className="px-3 py-2">CR</th>
                  <th className="px-3 py-2">Mention</th>
                  <th className="px-3 py-2">Rang</th>
                  {isAdmin && <th className="px-3 py-2 text-right">Résultat</th>}
                </tr>
              </thead>
              <tbody>
                {data.decisions.map((decision, index) => (
                  <tr key={decision.id ?? index} className="border-b border-border last:border-0">
                    <td className="px-3 py-2 font-mono text-xs">{decision.eleveId?.slice(0, 8)}…</td>
                    <td className="px-3 py-2 font-mono text-xs">{decision.classeId?.slice(0, 8) ?? "—"}</td>
                    <td className="px-3 py-2">{decision.moyenneGenerale ?? "—"}</td>
                    <td className="px-3 py-2">
                      {decision.decision ? (
                        <Badge variant={decision.decision.startsWith("ADMIS") ? "success" : "outline"}>{humanize(decision.decision)}</Badge>
                      ) : (
                        "—"
                      )}
                    </td>
                    <td className="px-3 py-2">{decision.cr ?? "—"}</td>
                    <td className="px-3 py-2">{decision.mention ?? "—"}</td>
                    <td className="px-3 py-2">{decision.rang != null ? `#${decision.rang}` : "—"}</td>
                    {isAdmin && (
                      <td className="px-3 py-2">
                        <div className="flex justify-end">
                          <Button
                            variant="ghost"
                            size="sm"
                            title="Publier le résultat"
                            disabled={busy || !decision.id}
                            onClick={() => run(() => examApi.resultats.publier(decision.id as string), "Résultat publié")}
                          >
                            Publier
                          </Button>
                        </div>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <EmptyState label="Aucune décision" />
          )}

          {isAdmin && statut === "EN_COURS" && (
            <form
              className="mt-3 flex flex-wrap items-end gap-2 border-t pt-3"
              onSubmit={(event) => {
                event.preventDefault();
                if (!decider.eleveId) return;
                run(
                  () => examApi.deliberations.decider(id, { classeId: decider.classeId || null, eleveId: decider.eleveId }),
                  "Décision enregistrée",
                );
                setDecider({ classeId: "", eleveId: "" });
              }}
            >
              <div className="w-64">
                <Label className="mb-1 block">Classe (UUID)</Label>
                <Input value={decider.classeId} onChange={(event) => setDecider({ ...decider, classeId: event.target.value })} />
              </div>
              <div className="w-64">
                <Label className="mb-1 block">Élève (UUID)</Label>
                <Input value={decider.eleveId} onChange={(event) => setDecider({ ...decider, eleveId: event.target.value })} />
              </div>
              <Button type="submit" disabled={busy || !decider.eleveId}>
                Décider
              </Button>
            </form>
          )}
        </CardContent>
      </Card>

      <Card className="mb-4">
        <CardHeader className="p-4 pb-2">
          <CardTitle className="text-base">Décisions par UE ({data?.decisionsUE?.length ?? 0})</CardTitle>
        </CardHeader>
        <CardContent className="overflow-x-auto p-4 pt-2">
          {data?.decisionsUE?.length ? (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-3 py-2">Élève</th>
                  <th className="px-3 py-2">UE</th>
                  <th className="px-3 py-2">Moyenne UE</th>
                  <th className="px-3 py-2">Crédits</th>
                  <th className="px-3 py-2">Décision</th>
                </tr>
              </thead>
              <tbody>
                {data.decisionsUE.map((du, index) => (
                  <tr key={du.id ?? index} className="border-b border-border last:border-0">
                    <td className="px-3 py-2 font-mono text-xs">{du.eleveId?.slice(0, 8)}…</td>
                    <td className="px-3 py-2 font-mono text-xs">{du.ueId?.slice(0, 8)}…</td>
                    <td className="px-3 py-2">{du.moyenneUE ?? "—"}</td>
                    <td className="px-3 py-2">{du.creditsAcquis}</td>
                    <td className="px-3 py-2">{du.decision ? humanize(du.decision) : "—"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <EmptyState label="Aucune décision par UE" />
          )}

          {isAdmin && statut === "EN_COURS" && (
            <form
              className="mt-3 grid gap-3 border-t pt-3 sm:grid-cols-5"
              onSubmit={(event) => {
                event.preventDefault();
                if (!decisionUe.eleveId || !decisionUe.ueId) return;
                run(
                  () =>
                    examApi.deliberations.ajouterDecisionUE(id, {
                      eleveId: decisionUe.eleveId,
                      ueId: decisionUe.ueId,
                      credits: Number(decisionUe.credits),
                      moyenneUE: Number(decisionUe.moyenneUE),
                    }),
                  "Décision UE ajoutée",
                );
                setDecisionUe({ eleveId: "", ueId: "", credits: "0", moyenneUE: "0" });
              }}
            >
              <div>
                <Label className="mb-1 block">Élève (UUID)</Label>
                <Input value={decisionUe.eleveId} onChange={(event) => setDecisionUe({ ...decisionUe, eleveId: event.target.value })} />
              </div>
              <div>
                <Label className="mb-1 block">UE (UUID)</Label>
                <Input value={decisionUe.ueId} onChange={(event) => setDecisionUe({ ...decisionUe, ueId: event.target.value })} />
              </div>
              <div>
                <Label className="mb-1 block">Crédits</Label>
                <Input type="number" value={decisionUe.credits} onChange={(event) => setDecisionUe({ ...decisionUe, credits: event.target.value })} />
              </div>
              <div>
                <Label className="mb-1 block">Moyenne UE</Label>
                <Input type="number" step="0.01" value={decisionUe.moyenneUE} onChange={(event) => setDecisionUe({ ...decisionUe, moyenneUE: event.target.value })} />
              </div>
              <div className="flex items-end">
                <Button type="submit" disabled={busy || !decisionUe.eleveId || !decisionUe.ueId}>
                  Ajouter
                </Button>
              </div>
            </form>
          )}
        </CardContent>
      </Card>
    </div>
  );
}