"use client";

import Link from "next/link";
import { useState } from "react";
import { toast } from "sonner";
import { Plus, Trash2, X } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { PageHeader, LoadingView, ErrorState, EmptyState } from "@/components/common/async-state";
import { EntitySelect } from "@/components/common/entity-select";
import { formatDate } from "@/components/pedagogie/format";
import { produitLabel, recetteLabel } from "@/components/pedagogie/labels";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { boulangerieApi } from "@/services/api";
import type { FicheRecette, LignePlan, PlanProduction, Produit } from "@/types";

const STATUT_BADGE: Record<string, "outline" | "success" | "warning" | "default"> = {
  BROUILLON: "outline",
  VALIDE: "success",
  LANCE: "warning",
  CLOTURE: "default",
};

interface LigneDraft {
  recetteCode: string;
  produitCode: string;
  fourCode: string;
  quantitePrevue: string;
}

export default function PlansPage() {
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const plans = useApi(() => boulangerieApi.plans.list(), [tenantId]);
  const [busy, setBusy] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [date, setDate] = useState("");
  const [atelierCode, setAtelierCode] = useState("");
  const [lignes, setLignes] = useState<LigneDraft[]>([
    { recetteCode: "", produitCode: "", fourCode: "", quantitePrevue: "1" },
  ]);

  const reload = () => plans.reload();

  const addLigne = () =>
    setLignes((prev) => [...prev, { recetteCode: "", produitCode: "", fourCode: "", quantitePrevue: "1" }]);

  const updateLigne = (index: number, patch: Partial<LigneDraft>) =>
    setLignes((prev) => prev.map((ligne, i) => (i === index ? { ...ligne, ...patch } : ligne)));

  const removeLigne = (index: number) => setLignes((prev) => prev.filter((_, i) => i !== index));

  const submit = async () => {
    const valides = lignes.filter((l) => l.recetteCode && Number(l.quantitePrevue) > 0);
    if (!date || !valides.length) {
      toast.error("Date du plan et au moins une ligne (recette + quantité) sont obligatoires");
      return;
    }
    setBusy(true);
    try {
      const payload: Partial<PlanProduction> = {
        date,
        atelierCode: atelierCode || null,
        lignes: valides.map(
          (ligne): LignePlan => ({
            recetteCode: ligne.recetteCode,
            produitCode: ligne.produitCode || null,
            fourCode: ligne.fourCode || null,
            quantitePrevue: Number(ligne.quantitePrevue),
          }),
        ),
      };
      await boulangerieApi.plans.create(payload);
      toast.success("Plan de production créé");
      setShowForm(false);
      setDate("");
      setAtelierCode("");
      setLignes([{ recetteCode: "", produitCode: "", fourCode: "", quantitePrevue: "1" }]);
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  if (plans.loading) return <LoadingView label="Chargement des plans…" />;
  if (plans.error) return <ErrorState error={plans.error} onRetry={reload} />;

  return (
    <div>
      <PageHeader
        title="Plans de production"
        description="Planification journalière des fournées par atelier : recettes, quantités prévues et besoins matières agrégés (lot 2)."
      >
        {isAdmin && !showForm && (
          <Button onClick={() => setShowForm(true)}>
            <Plus className="h-4 w-4" />
            Nouveau plan
          </Button>
        )}
      </PageHeader>

      {showForm && (
        <Card className="mb-4">
          <CardContent className="p-4">
            <div className="mb-3 flex items-center justify-between">
              <p className="font-semibold text-slate-900">Créer — plan de production</p>
              <Button variant="ghost" size="sm" onClick={() => setShowForm(false)}>
                <X className="h-4 w-4" />
              </Button>
            </div>
            <div className="mb-3 grid gap-3 sm:grid-cols-2">
              <div>
                <Label className="mb-1 block">Date du plan *</Label>
                <Input type="date" value={date} onChange={(e) => setDate(e.target.value)} />
              </div>
              <div>
                <Label className="mb-1 block">Atelier</Label>
                <Input
                  value={atelierCode}
                  placeholder="ATELIER-1"
                  onChange={(e) => setAtelierCode(e.target.value)}
                />
              </div>
            </div>
            <p className="mb-2 text-xs font-medium uppercase text-slate-500">Lignes du plan</p>
            <div className="space-y-2">
              {lignes.map((ligne, index) => (
                <div key={index} className="grid items-end gap-2 sm:grid-cols-[2fr_2fr_1fr_1fr_auto]">
                  <div>
                    <Label className="mb-1 block text-xs">Recette *</Label>
                    <EntitySelect<FicheRecette>
                      fetcher={() => boulangerieApi.recettes.list()}
                      keyOf={(recette) => recette.code}
                      labelOf={recetteLabel}
                      value={ligne.recetteCode}
                      onChange={(value) => updateLigne(index, { recetteCode: value })}
                      deps={[tenantId]}
                    />
                  </div>
                  <div>
                    <Label className="mb-1 block text-xs">Produit</Label>
                    <EntitySelect<Produit>
                      fetcher={() => boulangerieApi.produits.list()}
                      keyOf={(produit) => produit.code}
                      labelOf={produitLabel}
                      value={ligne.produitCode}
                      onChange={(value) => updateLigne(index, { produitCode: value })}
                      deps={[tenantId]}
                    />
                  </div>
                  <div>
                    <Label className="mb-1 block text-xs">Four</Label>
                    <Input
                      value={ligne.fourCode}
                      placeholder="FOUR-1"
                      onChange={(e) => updateLigne(index, { fourCode: e.target.value })}
                    />
                  </div>
                  <div>
                    <Label className="mb-1 block text-xs">Quantité prévue *</Label>
                    <Input
                      type="number"
                      min={0}
                      value={ligne.quantitePrevue}
                      onChange={(e) => updateLigne(index, { quantitePrevue: e.target.value })}
                    />
                  </div>
                  <Button
                    variant="ghost"
                    size="sm"
                    title="Retirer la ligne"
                    disabled={lignes.length === 1}
                    onClick={() => removeLigne(index)}
                  >
                    <Trash2 className="h-4 w-4 text-danger" />
                  </Button>
                </div>
              ))}
            </div>
            <div className="mt-3 flex items-center gap-2">
              <Button variant="outline" size="sm" onClick={addLigne}>
                <Plus className="h-4 w-4" />
                Ajouter une ligne
              </Button>
              <Button size="sm" disabled={busy} onClick={submit}>
                Créer le plan
              </Button>
            </div>
          </CardContent>
        </Card>
      )}

      {(plans.data ?? []).length ? (
        <Card>
          <CardContent className="overflow-x-auto p-0">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-4 py-3">Date</th>
                  <th className="px-4 py-3">Atelier</th>
                  <th className="px-4 py-3">Lignes</th>
                  <th className="px-4 py-3">Besoins matières</th>
                  <th className="px-4 py-3">Statut</th>
                  <th className="px-4 py-3 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {(plans.data ?? []).map((plan) => (
                  <tr key={plan.idPlan} className="border-b border-border last:border-0">
                    <td className="px-4 py-3 font-medium text-slate-900">{formatDate(plan.date)}</td>
                    <td className="px-4 py-3 font-mono text-xs">{plan.atelierCode ?? "—"}</td>
                    <td className="px-4 py-3">{plan.lignes?.length ?? 0}</td>
                    <td className="px-4 py-3">{plan.besoinsMatiere?.length ?? 0}</td>
                    <td className="px-4 py-3">
                      <Badge variant={STATUT_BADGE[plan.statut ?? ""] ?? "outline"}>
                        {plan.statut ?? "—"}
                      </Badge>
                    </td>
                    <td className="px-4 py-3 text-right">
                      <Link href={`/boulangerie/plans/${plan.idPlan}`}>
                        <Button variant="ghost" size="sm">
                          Détail
                        </Button>
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
      ) : (
        <EmptyState label="Aucun plan de production" />
      )}

      <p className="mt-3 text-xs text-slate-400">
        {(plans.data ?? []).length} plan(s)
      </p>
    </div>
  );
}
