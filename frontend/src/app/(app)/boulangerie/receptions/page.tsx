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
import { commandeLabel } from "@/components/pedagogie/labels";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { boulangerieApi } from "@/services/api";
import type { CommandeAchat, LigneReception, Reception } from "@/types";

const STATUT_BADGE: Record<string, "outline" | "success" | "warning" | "danger" | "default"> = {
  ATTENDUE: "outline",
  EN_CONTROLE: "warning",
  ACCEPTEE: "success",
  REFUSEE: "danger",
  PARTIELLE: "warning",
};

interface LigneDraft {
  articleCode: string;
  quantiteRecue: string;
  prixAchat: string;
  lotNumero: string;
  conforme: boolean;
  motifNonConformite: string;
}

const emptyLigne: LigneDraft = {
  articleCode: "",
  quantiteRecue: "0",
  prixAchat: "0",
  lotNumero: "",
  conforme: true,
  motifNonConformite: "",
};

export default function ReceptionsPage() {
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const receptions = useApi(() => boulangerieApi.receptions.list(), [tenantId]);
  const [busy, setBusy] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [commandeId, setCommandeId] = useState("");
  const [date, setDate] = useState("");
  const [bonLivraison, setBonLivraison] = useState("");
  const [lignes, setLignes] = useState<LigneDraft[]>([{ ...emptyLigne }]);

  const reload = () => receptions.reload();

  const updateLigne = (index: number, patch: Partial<LigneDraft>) =>
    setLignes((prev) => prev.map((ligne, i) => (i === index ? { ...ligne, ...patch } : ligne)));

  const submit = async () => {
    const valides = lignes.filter((l) => l.articleCode && Number(l.quantiteRecue) > 0);
    if (!commandeId || !date || !valides.length) {
      toast.error("Commande, date et au moins une ligne (article + quantité reçue) sont obligatoires");
      return;
    }
    if (valides.some((l) => !l.conforme && !l.motifNonConformite.trim())) {
      toast.error("Chaque ligne non conforme exige un motif de non-conformité");
      return;
    }
    setBusy(true);
    try {
      const payload: Partial<Reception> = {
        commandeId,
        date,
        bonLivraison: bonLivraison || null,
        lignes: valides.map(
          (ligne): LigneReception => ({
            articleCode: ligne.articleCode,
            quantiteRecue: Number(ligne.quantiteRecue),
            prixAchat: Number(ligne.prixAchat) || 0,
            lotNumero: ligne.lotNumero || null,
            conforme: ligne.conforme,
            motifNonConformite: ligne.conforme ? null : ligne.motifNonConformite.trim(),
          }),
        ),
      };
      await boulangerieApi.receptions.create(payload);
      toast.success("Réception créée (à contrôler)");
      setShowForm(false);
      setCommandeId("");
      setDate("");
      setBonLivraison("");
      setLignes([{ ...emptyLigne }]);
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  if (receptions.loading) return <LoadingView label="Chargement des réceptions…" />;
  if (receptions.error) return <ErrorState error={receptions.error} onRetry={reload} />;

  return (
    <div>
      <PageHeader
        title="Réceptions"
        description="Réceptions de marchandises : contrôle, acceptation (totale ou partielle) ou refus motivé (lot 3)."
      >
        {isAdmin && !showForm && (
          <Button onClick={() => setShowForm(true)}>
            <Plus className="h-4 w-4" />
            Nouvelle réception
          </Button>
        )}
      </PageHeader>

      {showForm && (
        <Card className="mb-4">
          <CardContent className="p-4">
            <div className="mb-3 flex items-center justify-between">
              <p className="font-semibold text-slate-900">Créer — réception (statut ATTENDUE)</p>
              <Button variant="ghost" size="sm" onClick={() => setShowForm(false)}>
                <X className="h-4 w-4" />
              </Button>
            </div>
            <div className="mb-3 grid gap-3 sm:grid-cols-3">
              <div>
                <Label className="mb-1 block">Commande *</Label>
                <EntitySelect<CommandeAchat>
                  fetcher={() => boulangerieApi.commandes.list()}
                  keyOf={(commande) => commande.idCommande ?? ""}
                  labelOf={commandeLabel}
                  value={commandeId}
                  onChange={setCommandeId}
                  deps={[tenantId]}
                />
              </div>
              <div>
                <Label className="mb-1 block">Date *</Label>
                <Input type="date" value={date} onChange={(e) => setDate(e.target.value)} />
              </div>
              <div>
                <Label className="mb-1 block">Bon de livraison</Label>
                <Input value={bonLivraison} placeholder="BL-2026-…" onChange={(e) => setBonLivraison(e.target.value)} />
              </div>
            </div>
            <p className="mb-2 text-xs font-medium uppercase text-slate-500">Lignes reçues</p>
            <div className="space-y-2">
              {lignes.map((ligne, index) => (
                <div key={index} className="grid items-end gap-2 sm:grid-cols-[1.3fr_1fr_1fr_1.3fr_1fr_1.3fr_auto]">
                  <div>
                    <Label className="mb-1 block text-xs">Article *</Label>
                    <Input
                      value={ligne.articleCode}
                      placeholder="ART-FARINE-001"
                      onChange={(e) => updateLigne(index, { articleCode: e.target.value })}
                    />
                  </div>
                  <div>
                    <Label className="mb-1 block text-xs">Qté reçue *</Label>
                    <Input
                      type="number"
                      min={0}
                      value={ligne.quantiteRecue}
                      onChange={(e) => updateLigne(index, { quantiteRecue: e.target.value })}
                    />
                  </div>
                  <div>
                    <Label className="mb-1 block text-xs">Prix achat</Label>
                    <Input
                      type="number"
                      min={0}
                      step="0.01"
                      value={ligne.prixAchat}
                      onChange={(e) => updateLigne(index, { prixAchat: e.target.value })}
                    />
                  </div>
                  <div>
                    <Label className="mb-1 block text-xs">Lot</Label>
                    <Input
                      value={ligne.lotNumero}
                      placeholder="LOT-FAR-001"
                      onChange={(e) => updateLigne(index, { lotNumero: e.target.value })}
                    />
                  </div>
                  <div className="flex items-center gap-2 pb-2">
                    <input
                      id={`conforme-${index}`}
                      type="checkbox"
                      checked={ligne.conforme}
                      onChange={(e) => updateLigne(index, { conforme: e.target.checked })}
                    />
                    <Label htmlFor={`conforme-${index}`} className="text-xs">
                      Conforme
                    </Label>
                  </div>
                  <div>
                    <Label className="mb-1 block text-xs">Motif non-conformité</Label>
                    <Input
                      value={ligne.motifNonConformite}
                      placeholder={ligne.conforme ? "—" : "DLC trop courte…"}
                      disabled={ligne.conforme}
                      onChange={(e) => updateLigne(index, { motifNonConformite: e.target.value })}
                    />
                  </div>
                  <Button
                    variant="ghost"
                    size="sm"
                    title="Retirer la ligne"
                    disabled={lignes.length === 1}
                    onClick={() => setLignes((prev) => prev.filter((_, i) => i !== index))}
                  >
                    <Trash2 className="h-4 w-4 text-danger" />
                  </Button>
                </div>
              ))}
            </div>
            <div className="mt-3 flex items-center gap-2">
              <Button variant="outline" size="sm" onClick={() => setLignes((prev) => [...prev, { ...emptyLigne }])}>
                <Plus className="h-4 w-4" />
                Ajouter une ligne
              </Button>
              <Button size="sm" disabled={busy} onClick={submit}>
                Créer la réception
              </Button>
            </div>
          </CardContent>
        </Card>
      )}

      {(receptions.data ?? []).length ? (
        <Card>
          <CardContent className="overflow-x-auto p-0">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-4 py-3">Numéro</th>
                  <th className="px-4 py-3">Commande</th>
                  <th className="px-4 py-3">Date</th>
                  <th className="px-4 py-3">Bon livraison</th>
                  <th className="px-4 py-3">Lignes</th>
                  <th className="px-4 py-3">Statut</th>
                  <th className="px-4 py-3 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {(receptions.data ?? []).map((reception) => (
                  <tr key={reception.idReception} className="border-b border-border last:border-0">
                    <td className="px-4 py-3 font-mono font-medium">{reception.numero ?? "—"}</td>
                    <td className="px-4 py-3 font-mono text-xs">{reception.commandeNumero ?? "—"}</td>
                    <td className="px-4 py-3">{formatDate(reception.date)}</td>
                    <td className="px-4 py-3 font-mono text-xs">{reception.bonLivraison ?? "—"}</td>
                    <td className="px-4 py-3">{reception.lignes?.length ?? 0}</td>
                    <td className="px-4 py-3">
                      <Badge variant={STATUT_BADGE[reception.statut ?? ""] ?? "outline"}>
                        {reception.statut ?? "—"}
                      </Badge>
                    </td>
                    <td className="px-4 py-3 text-right">
                      <Link href={`/boulangerie/receptions/${reception.idReception}`}>
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
        <EmptyState label="Aucune réception" />
      )}

      <p className="mt-3 text-xs text-slate-400">{(receptions.data ?? []).length} réception(s)</p>
    </div>
  );
}
