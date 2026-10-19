"use client";

import Link from "next/link";
import { useState } from "react";
import { toast } from "sonner";
import { X } from "lucide-react";
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
import type { FicheRecette, Fournee, Produit } from "@/types";

const STATUT_BADGE: Record<string, "outline" | "success" | "warning" | "danger" | "default"> = {
  PLANIFIEE: "outline",
  EN_COURS: "warning",
  CUITE: "success",
  VALIDEE: "default",
  ANNULEE: "danger",
};

export default function FourneesPage() {
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const fournees = useApi(() => boulangerieApi.fournees.list(), [tenantId]);
  const [busy, setBusy] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [recetteCode, setRecetteCode] = useState("");
  const [versionRecette, setVersionRecette] = useState("1");
  const [produitCode, setProduitCode] = useState("");
  const [fourCode, setFourCode] = useState("");
  const [quantitePrevue, setQuantitePrevue] = useState("1");

  const reload = () => fournees.reload();

  const submit = async () => {
    if (!recetteCode) {
      toast.error("La recette est obligatoire");
      return;
    }
    setBusy(true);
    try {
      const payload: Partial<Fournee> = {
        recetteCode,
        versionRecette: Number(versionRecette) || 1,
        produitCode: produitCode || null,
        fourCode: fourCode || null,
        quantitePrevue: Number(quantitePrevue) || 0,
      };
      await boulangerieApi.fournees.create(payload);
      toast.success("Fournée créée");
      setShowForm(false);
      setRecetteCode("");
      setProduitCode("");
      setFourCode("");
      setVersionRecette("1");
      setQuantitePrevue("1");
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  if (fournees.loading) return <LoadingView label="Chargement des fournées…" />;
  if (fournees.error) return <ErrorState error={fournees.error} onRetry={reload} />;

  return (
    <div>
      <PageHeader
        title="Fournées"
        description="Cycle de vie des fournées : lancement, rendu, cuisson, validation, consommations par lot et pertes valorisées (lot 2)."
      >
        {isAdmin && !showForm && (
          <Button onClick={() => setShowForm(true)}>Nouvelle fournée</Button>
        )}
      </PageHeader>

      {showForm && (
        <Card className="mb-4">
          <CardContent className="p-4">
            <div className="mb-3 flex items-center justify-between">
              <p className="font-semibold text-slate-900">Créer — fournée</p>
              <Button variant="ghost" size="sm" onClick={() => setShowForm(false)}>
                <X className="h-4 w-4" />
              </Button>
            </div>
            <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
              <div>
                <Label className="mb-1 block">Recette *</Label>
                <EntitySelect<FicheRecette>
                  fetcher={() => boulangerieApi.recettes.list()}
                  keyOf={(recette) => recette.code}
                  labelOf={recetteLabel}
                  value={recetteCode}
                  onChange={setRecetteCode}
                  deps={[tenantId]}
                />
              </div>
              <div>
                <Label className="mb-1 block">Version recette</Label>
                <Input
                  type="number"
                  min={1}
                  value={versionRecette}
                  onChange={(e) => setVersionRecette(e.target.value)}
                />
              </div>
              <div>
                <Label className="mb-1 block">Produit</Label>
                <EntitySelect<Produit>
                  fetcher={() => boulangerieApi.produits.list()}
                  keyOf={(produit) => produit.code}
                  labelOf={produitLabel}
                  value={produitCode}
                  onChange={setProduitCode}
                  deps={[tenantId]}
                />
              </div>
              <div>
                <Label className="mb-1 block">Four</Label>
                <Input value={fourCode} placeholder="FOUR-1" onChange={(e) => setFourCode(e.target.value)} />
              </div>
              <div>
                <Label className="mb-1 block">Quantité prévue</Label>
                <Input
                  type="number"
                  min={0}
                  value={quantitePrevue}
                  onChange={(e) => setQuantitePrevue(e.target.value)}
                />
              </div>
            </div>
            <div className="mt-3">
              <Button size="sm" disabled={busy} onClick={submit}>
                Créer la fournée
              </Button>
            </div>
          </CardContent>
        </Card>
      )}

      {(fournees.data ?? []).length ? (
        <Card>
          <CardContent className="overflow-x-auto p-0">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-4 py-3">Numéro</th>
                  <th className="px-4 py-3">Recette</th>
                  <th className="px-4 py-3">Produit</th>
                  <th className="px-4 py-3">Four</th>
                  <th className="px-4 py-3">Début</th>
                  <th className="px-4 py-3 text-right">Prévue</th>
                  <th className="px-4 py-3 text-right">Réalisée</th>
                  <th className="px-4 py-3">Statut</th>
                  <th className="px-4 py-3 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {(fournees.data ?? []).map((fournee) => (
                  <tr key={fournee.idFournee} className="border-b border-border last:border-0">
                    <td className="px-4 py-3 font-mono text-xs font-medium text-slate-900">
                      {fournee.numero ?? "—"}
                    </td>
                    <td className="px-4 py-3">
                      {fournee.recetteCode}
                      {fournee.versionRecette ? ` (v${fournee.versionRecette})` : ""}
                    </td>
                    <td className="px-4 py-3">{fournee.produitCode ?? "—"}</td>
                    <td className="px-4 py-3 font-mono text-xs">{fournee.fourCode ?? "—"}</td>
                    <td className="px-4 py-3">{formatDate(fournee.dateDebut)}</td>
                    <td className="px-4 py-3 text-right">{fournee.quantitePrevue ?? "—"}</td>
                    <td className="px-4 py-3 text-right">{fournee.quantiteRealisee ?? "—"}</td>
                    <td className="px-4 py-3">
                      <Badge variant={STATUT_BADGE[fournee.statut ?? ""] ?? "outline"}>
                        {fournee.statut ?? "—"}
                      </Badge>
                    </td>
                    <td className="px-4 py-3 text-right">
                      <Link href={`/boulangerie/fournees/${fournee.idFournee}`}>
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
        <EmptyState label="Aucune fournée" />
      )}

      <p className="mt-3 text-xs text-slate-400">{(fournees.data ?? []).length} fournée(s)</p>
    </div>
  );
}
