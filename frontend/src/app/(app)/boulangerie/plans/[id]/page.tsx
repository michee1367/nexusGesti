"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import { ArrowLeft, CheckCircle2, CircleDot, Lock } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { PageHeader, LoadingView, ErrorState } from "@/components/common/async-state";
import { formatDate } from "@/components/pedagogie/format";
import { lookupLabel } from "@/components/pedagogie/labels";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { useProduitsMap, useRecettesCodeMap } from "@/hooks/use-lookup";
import { boulangerieApi } from "@/services/api";

const STATUT_BADGE: Record<string, "outline" | "success" | "warning" | "default"> = {
  BROUILLON: "outline",
  VALIDE: "success",
  LANCE: "warning",
  CLOTURE: "default",
};

export default function PlanDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const [busy, setBusy] = useState(false);

  const plan = useApi(() => boulangerieApi.plans.get(id), [id, tenantId]);
  const recettes = useRecettesCodeMap([tenantId]);
  const produits = useProduitsMap([tenantId]);
  const data = plan.data;
  const reload = () => plan.reload();

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

  if (plan.loading) return <LoadingView label="Chargement du plan…" />;
  if (plan.error) return <ErrorState error={plan.error} onRetry={reload} />;

  const lignes = data?.lignes ?? [];
  const besoins = data?.besoinsMatiere ?? [];

  return (
    <div>
      <PageHeader
        title={`Plan — ${formatDate(data?.date)}`}
        description={data?.atelierCode ? `Atelier ${data.atelierCode}` : "Plan de production"}
      >
        <Link href="/boulangerie/plans">
          <Button variant="outline" size="sm">
            <ArrowLeft className="h-4 w-4" />
            Retour
          </Button>
        </Link>
        {isAdmin && data?.statut === "BROUILLON" && (
          <Button size="sm" disabled={busy} onClick={() => run(() => boulangerieApi.plans.valider(id), "Plan validé")}>
            <CheckCircle2 className="h-4 w-4" />
            Valider
          </Button>
        )}
        {isAdmin && data?.statut === "VALIDE" && (
          <Button size="sm" disabled={busy} onClick={() => run(() => boulangerieApi.plans.lancer(id), "Plan lancé")}>
            <CircleDot className="h-4 w-4" />
            Lancer
          </Button>
        )}
        {isAdmin && data?.statut === "LANCE" && (
          <Button size="sm" disabled={busy} onClick={() => run(() => boulangerieApi.plans.cloturer(id), "Plan clôturé")}>
            <Lock className="h-4 w-4" />
            Clôturer
          </Button>
        )}
      </PageHeader>

      <div className="mb-4 flex flex-wrap items-center gap-2">
        <Badge variant={STATUT_BADGE[data?.statut ?? ""] ?? "outline"}>{data?.statut ?? "—"}</Badge>
        <Badge variant="outline">{lignes.length} ligne(s)</Badge>
        <Badge variant="outline">{besoins.length} besoin(s) matière(s)</Badge>
      </div>

      <Card className="mb-4">
        <CardHeader className="p-4 pb-2">
          <CardTitle className="text-base">Lignes du plan</CardTitle>
        </CardHeader>
        <CardContent className="overflow-x-auto p-0">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                <th className="px-4 py-3">Recette</th>
                <th className="px-4 py-3">Version</th>
                <th className="px-4 py-3">Produit</th>
                <th className="px-4 py-3">Four</th>
                <th className="px-4 py-3 text-right">Quantité prévue</th>
              </tr>
            </thead>
            <tbody>
              {lignes.map((ligne, index) => (
                <tr key={ligne.id ?? index} className="border-b border-border last:border-0">
                  <td className="px-4 py-3 font-medium text-slate-900">
                    {lookupLabel(recettes, ligne.recetteCode)}
                  </td>
                  <td className="px-4 py-3">{ligne.versionRecette ? `v${ligne.versionRecette}` : "—"}</td>
                  <td className="px-4 py-3">{lookupLabel(produits, ligne.produitCode)}</td>
                  <td className="px-4 py-3 font-mono text-xs">{ligne.fourCode ?? "—"}</td>
                  <td className="px-4 py-3 text-right font-medium">{ligne.quantitePrevue}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </CardContent>
      </Card>

      <Card>
        <CardHeader className="p-4 pb-2">
          <CardTitle className="text-base">Besoins matières agrégés</CardTitle>
        </CardHeader>
        <CardContent className="overflow-x-auto p-0">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                <th className="px-4 py-3">Article</th>
                <th className="px-4 py-3">Libellé</th>
                <th className="px-4 py-3 text-right">Quantité nette</th>
                <th className="px-4 py-3 text-right">Quantité brute</th>
                <th className="px-4 py-3">Unité</th>
              </tr>
            </thead>
            <tbody>
              {besoins.map((besoin) => (
                <tr key={besoin.articleCode} className="border-b border-border last:border-0">
                  <td className="px-4 py-3 font-mono text-xs">{besoin.articleCode}</td>
                  <td className="px-4 py-3">{besoin.libelle ?? "—"}</td>
                  <td className="px-4 py-3 text-right">{besoin.quantiteNette}</td>
                  <td className="px-4 py-3 text-right">{besoin.quantiteBrute}</td>
                  <td className="px-4 py-3">{besoin.unite ?? "—"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </CardContent>
      </Card>
    </div>
  );
}
