"use client";

import { useState } from "react";
import { toast } from "sonner";
import { Plus, Search, Trash2 } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { PageHeader } from "@/components/common/async-state";
import { boulangerieApi } from "@/services/api";
import type { BesoinApprovisionnement, SaisieBesoin } from "@/types";

const URGENCE_BADGE: Record<string, "danger" | "warning" | "success" | "outline"> = {
  RUPTURE: "danger",
  HAUTE: "warning",
  NORMALE: "success",
};

interface SaisieDraft {
  articleCode: string;
  libelle: string;
  quantiteDisponible: string;
  seuil: string;
  consommationPrevue: string;
  delaiReapproJours: string;
}

const emptySaisie: SaisieDraft = {
  articleCode: "",
  libelle: "",
  quantiteDisponible: "0",
  seuil: "0",
  consommationPrevue: "0",
  delaiReapproJours: "",
};

export default function BesoinsPage() {
  const [saisies, setSaisies] = useState<SaisieDraft[]>([{ ...emptySaisie }]);
  const [resultats, setResultats] = useState<BesoinApprovisionnement[]>([]);
  const [alertes, setAlertes] = useState<BesoinApprovisionnement[]>([]);
  const [busy, setBusy] = useState(false);

  const update = (index: number, patch: Partial<SaisieDraft>) =>
    setSaisies((prev) => prev.map((s, i) => (i === index ? { ...s, ...patch } : s)));

  const toPayload = (): SaisieBesoin[] =>
    saisies
      .filter((s) => s.articleCode)
      .map((s) => ({
        articleCode: s.articleCode,
        libelle: s.libelle || null,
        quantiteDisponible: Number(s.quantiteDisponible) || 0,
        seuil: Number(s.seuil) || 0,
        consommationPrevue: Number(s.consommationPrevue) || 0,
        delaiReapproJours: s.delaiReapproJours ? Number(s.delaiReapproJours) : null,
      }));

  const calculer = async () => {
    const payload = toPayload();
    if (!payload.length) {
      toast.error("Au moins un article avec un code est obligatoire");
      return;
    }
    setBusy(true);
    try {
      setResultats(await boulangerieApi.besoins.calcule(payload));
      setAlertes([]);
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  const rechercherAlertes = async () => {
    const payload = toPayload();
    if (!payload.length) {
      toast.error("Au moins un article avec un code est obligatoire");
      return;
    }
    setBusy(true);
    try {
      setAlertes(
        await boulangerieApi.besoins.sousSeuil(
          payload.map((s) => ({
            articleCode: s.articleCode,
            quantiteDisponible: s.quantiteDisponible,
            seuil: s.seuil,
          })),
        ),
      );
      setResultats([]);
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  return (
    <div>
      <PageHeader
        title="Besoins d’approvisionnement"
        description="Calcul des quantités à recommander (seuil + consommation prévue − disponible) et alertes de sous-seuil (lot 3)."
      />

      <Card className="mb-4">
        <CardContent className="p-4">
          <p className="mb-2 text-xs font-medium uppercase text-slate-500">Articles suivis</p>
          <div className="space-y-2">
            {saisies.map((saisie, index) => (
              <div key={index} className="grid items-end gap-2 sm:grid-cols-[1.5fr_1.5fr_1fr_1fr_1fr_1fr_auto]">
                <div>
                  <Label className="mb-1 block text-xs">Article *</Label>
                  <Input
                    value={saisie.articleCode}
                    placeholder="ART-FARINE-001"
                    onChange={(e) => update(index, { articleCode: e.target.value })}
                  />
                </div>
                <div>
                  <Label className="mb-1 block text-xs">Libellé</Label>
                  <Input
                    value={saisie.libelle}
                    placeholder="Farine T55"
                    onChange={(e) => update(index, { libelle: e.target.value })}
                  />
                </div>
                <div>
                  <Label className="mb-1 block text-xs">Disponible</Label>
                  <Input
                    type="number"
                    min={0}
                    value={saisie.quantiteDisponible}
                    onChange={(e) => update(index, { quantiteDisponible: e.target.value })}
                  />
                </div>
                <div>
                  <Label className="mb-1 block text-xs">Seuil</Label>
                  <Input
                    type="number"
                    min={0}
                    value={saisie.seuil}
                    onChange={(e) => update(index, { seuil: e.target.value })}
                  />
                </div>
                <div>
                  <Label className="mb-1 block text-xs">Conso. prévue</Label>
                  <Input
                    type="number"
                    min={0}
                    value={saisie.consommationPrevue}
                    onChange={(e) => update(index, { consommationPrevue: e.target.value })}
                  />
                </div>
                <div>
                  <Label className="mb-1 block text-xs">Délai réappro (j)</Label>
                  <Input
                    type="number"
                    min={0}
                    value={saisie.delaiReapproJours}
                    onChange={(e) => update(index, { delaiReapproJours: e.target.value })}
                  />
                </div>
                <Button
                  variant="ghost"
                  size="sm"
                  title="Retirer la ligne"
                  disabled={saisies.length === 1}
                  onClick={() => setSaisies((prev) => prev.filter((_, i) => i !== index))}
                >
                  <Trash2 className="h-4 w-4 text-danger" />
                </Button>
              </div>
            ))}
          </div>
          <div className="mt-3 flex items-center gap-2">
            <Button variant="outline" size="sm" onClick={() => setSaisies((prev) => [...prev, { ...emptySaisie }])}>
              <Plus className="h-4 w-4" />
              Ajouter un article
            </Button>
            <Button size="sm" disabled={busy} onClick={calculer}>
              <Search className="h-4 w-4" />
              Calculer les besoins
            </Button>
            <Button variant="outline" size="sm" disabled={busy} onClick={rechercherAlertes}>
              Alertes sous-seuil
            </Button>
          </div>
        </CardContent>
      </Card>

      {resultats.length > 0 && (
        <Card className="mb-4">
          <CardContent className="overflow-x-auto p-0">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-4 py-3">Article</th>
                  <th className="px-4 py-3">Disponible</th>
                  <th className="px-4 py-3">Seuil</th>
                  <th className="px-4 py-3">Quantité suggérée</th>
                  <th className="px-4 py-3">Délai</th>
                  <th className="px-4 py-3">Urgence</th>
                </tr>
              </thead>
              <tbody>
                {resultats.map((r) => (
                  <tr key={r.articleCode} className="border-b border-border last:border-0">
                    <td className="px-4 py-3 font-mono font-medium">
                      {r.articleCode}
                      {r.libelle ? <span className="ml-2 font-sans text-slate-500">{r.libelle}</span> : null}
                    </td>
                    <td className="px-4 py-3">{r.quantiteDisponible ?? 0}</td>
                    <td className="px-4 py-3">{r.seuil ?? 0}</td>
                    <td className="px-4 py-3 font-semibold">{r.quantiteSuggeree ?? 0}</td>
                    <td className="px-4 py-3">{r.delaiReapproJours != null ? `${r.delaiReapproJours} j` : "—"}</td>
                    <td className="px-4 py-3">
                      <Badge variant={URGENCE_BADGE[r.urgence ?? ""] ?? "outline"}>{r.urgence ?? "—"}</Badge>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

      {alertes.length > 0 && (
        <Card>
          <CardContent className="overflow-x-auto p-0">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-4 py-3">Article</th>
                  <th className="px-4 py-3">Disponible</th>
                  <th className="px-4 py-3">Seuil</th>
                  <th className="px-4 py-3">Urgence</th>
                </tr>
              </thead>
              <tbody>
                {alertes.map((r) => (
                  <tr key={r.articleCode} className="border-b border-border last:border-0">
                    <td className="px-4 py-3 font-mono font-medium">{r.articleCode}</td>
                    <td className="px-4 py-3">{r.quantiteDisponible ?? 0}</td>
                    <td className="px-4 py-3">{r.seuil ?? 0}</td>
                    <td className="px-4 py-3">
                      <Badge variant={URGENCE_BADGE[r.urgence ?? ""] ?? "outline"}>{r.urgence ?? "—"}</Badge>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

      {resultats.length === 0 && alertes.length === 0 && (
        <p className="text-sm text-slate-500">
          Renseignez vos articles puis lancez un calcul — les suggestions sont recalculées à la volée (aucune
          donnée stockée).
        </p>
      )}

      <p className="mt-3 text-xs text-slate-400">{saisies.length} article(s) suivi(s)</p>
    </div>
  );
}
