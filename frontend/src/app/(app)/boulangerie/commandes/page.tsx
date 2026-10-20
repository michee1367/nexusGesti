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
import { fournisseurLabel } from "@/components/pedagogie/labels";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { boulangerieApi } from "@/services/api";
import type { CommandeAchat, FournisseurBoulangerie, LigneCommandeAchat } from "@/types";

const STATUT_BADGE: Record<string, "outline" | "success" | "warning" | "danger" | "default"> = {
  BROUILLON: "outline",
  VALIDEE: "warning",
  ENVOYEE: "warning",
  PARTIELLE: "warning",
  RECEPTIONNEE: "success",
  FACTUREE: "default",
  ANNULEE: "danger",
};

interface LigneDraft {
  articleCode: string;
  designation: string;
  quantiteCommandee: string;
  prixUnitaire: string;
  tauxTva: string;
}

const emptyLigne: LigneDraft = {
  articleCode: "",
  designation: "",
  quantiteCommandee: "1",
  prixUnitaire: "0",
  tauxTva: "16",
};

export default function CommandesPage() {
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const commandes = useApi(() => boulangerieApi.commandes.list(), [tenantId]);
  const [busy, setBusy] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [fournisseurCode, setFournisseurCode] = useState("");
  const [date, setDate] = useState("");
  const [dateLivraison, setDateLivraison] = useState("");
  const [lignes, setLignes] = useState<LigneDraft[]>([{ ...emptyLigne }]);

  const reload = () => commandes.reload();

  const updateLigne = (index: number, patch: Partial<LigneDraft>) =>
    setLignes((prev) => prev.map((ligne, i) => (i === index ? { ...ligne, ...patch } : ligne)));

  const submit = async () => {
    const valides = lignes.filter((l) => l.articleCode && Number(l.quantiteCommandee) > 0);
    if (!fournisseurCode || !date || !valides.length) {
      toast.error("Fournisseur, date et au moins une ligne (article + quantité) sont obligatoires");
      return;
    }
    setBusy(true);
    try {
      const payload: Partial<CommandeAchat> = {
        fournisseurTiersCode: fournisseurCode,
        date,
        dateLivraisonPrevue: dateLivraison || null,
        lignes: valides.map(
          (ligne): LigneCommandeAchat => ({
            articleCode: ligne.articleCode,
            designation: ligne.designation || null,
            quantiteCommandee: Number(ligne.quantiteCommandee),
            prixUnitaire: Number(ligne.prixUnitaire) || 0,
            tauxTva: Number(ligne.tauxTva) || 0,
          }),
        ),
      };
      await boulangerieApi.commandes.create(payload);
      toast.success("Commande créée (brouillon)");
      setShowForm(false);
      setFournisseurCode("");
      setDate("");
      setDateLivraison("");
      setLignes([{ ...emptyLigne }]);
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  if (commandes.loading) return <LoadingView label="Chargement des commandes…" />;
  if (commandes.error) return <ErrorState error={commandes.error} onRetry={reload} />;

  return (
    <div>
      <PageHeader
        title="Commandes d’achat"
        description="Commandes fournisseurs : validation, envoi, réception partielle ou totale et rapprochement (lot 3)."
      >
        {isAdmin && !showForm && (
          <Button onClick={() => setShowForm(true)}>
            <Plus className="h-4 w-4" />
            Nouvelle commande
          </Button>
        )}
      </PageHeader>

      {showForm && (
        <Card className="mb-4">
          <CardContent className="p-4">
            <div className="mb-3 flex items-center justify-between">
              <p className="font-semibold text-slate-900">Créer — commande d’achat (brouillon)</p>
              <Button variant="ghost" size="sm" onClick={() => setShowForm(false)}>
                <X className="h-4 w-4" />
              </Button>
            </div>
            <div className="mb-3 grid gap-3 sm:grid-cols-3">
              <div>
                <Label className="mb-1 block">Fournisseur *</Label>
                <EntitySelect<FournisseurBoulangerie>
                  fetcher={() => boulangerieApi.fournisseurs.list()}
                  keyOf={(f) => f.code}
                  labelOf={fournisseurLabel}
                  value={fournisseurCode}
                  onChange={setFournisseurCode}
                  deps={[tenantId]}
                />
              </div>
              <div>
                <Label className="mb-1 block">Date *</Label>
                <Input type="date" value={date} onChange={(e) => setDate(e.target.value)} />
              </div>
              <div>
                <Label className="mb-1 block">Livraison prévue</Label>
                <Input type="date" value={dateLivraison} onChange={(e) => setDateLivraison(e.target.value)} />
              </div>
            </div>
            <p className="mb-2 text-xs font-medium uppercase text-slate-500">Lignes de la commande</p>
            <div className="space-y-2">
              {lignes.map((ligne, index) => (
                <div key={index} className="grid items-end gap-2 sm:grid-cols-[1.5fr_2fr_1fr_1fr_1fr_auto]">
                  <div>
                    <Label className="mb-1 block text-xs">Article *</Label>
                    <Input
                      value={ligne.articleCode}
                      placeholder="ART-FARINE-001"
                      onChange={(e) => updateLigne(index, { articleCode: e.target.value })}
                    />
                  </div>
                  <div>
                    <Label className="mb-1 block text-xs">Désignation</Label>
                    <Input
                      value={ligne.designation}
                      placeholder="Farine T55"
                      onChange={(e) => updateLigne(index, { designation: e.target.value })}
                    />
                  </div>
                  <div>
                    <Label className="mb-1 block text-xs">Quantité *</Label>
                    <Input
                      type="number"
                      min={0}
                      value={ligne.quantiteCommandee}
                      onChange={(e) => updateLigne(index, { quantiteCommandee: e.target.value })}
                    />
                  </div>
                  <div>
                    <Label className="mb-1 block text-xs">Prix unitaire HT</Label>
                    <Input
                      type="number"
                      min={0}
                      step="0.01"
                      value={ligne.prixUnitaire}
                      onChange={(e) => updateLigne(index, { prixUnitaire: e.target.value })}
                    />
                  </div>
                  <div>
                    <Label className="mb-1 block text-xs">TVA %</Label>
                    <Input
                      type="number"
                      min={0}
                      value={ligne.tauxTva}
                      onChange={(e) => updateLigne(index, { tauxTva: e.target.value })}
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
                Créer la commande
              </Button>
            </div>
          </CardContent>
        </Card>
      )}

      {(commandes.data ?? []).length ? (
        <Card>
          <CardContent className="overflow-x-auto p-0">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-4 py-3">Numéro</th>
                  <th className="px-4 py-3">Fournisseur</th>
                  <th className="px-4 py-3">Date</th>
                  <th className="px-4 py-3">Montant TTC</th>
                  <th className="px-4 py-3">Reste à recevoir</th>
                  <th className="px-4 py-3">Statut</th>
                  <th className="px-4 py-3 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {(commandes.data ?? []).map((commande) => (
                  <tr key={commande.idCommande} className="border-b border-border last:border-0">
                    <td className="px-4 py-3 font-mono font-medium">{commande.numero ?? "—"}</td>
                    <td className="px-4 py-3 font-mono text-xs">{commande.fournisseurTiersCode}</td>
                    <td className="px-4 py-3">{formatDate(commande.date)}</td>
                    <td className="px-4 py-3 font-medium">
                      {commande.montantTTC ?? 0} {commande.devise ?? "USD"}
                    </td>
                    <td className="px-4 py-3">{commande.resteARecevoir ?? "—"}</td>
                    <td className="px-4 py-3">
                      <Badge variant={STATUT_BADGE[commande.statut ?? ""] ?? "outline"}>
                        {commande.statut ?? "—"}
                      </Badge>
                    </td>
                    <td className="px-4 py-3 text-right">
                      <Link href={`/boulangerie/commandes/${commande.idCommande}`}>
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
        <EmptyState label="Aucune commande d’achat" />
      )}

      <p className="mt-3 text-xs text-slate-400">{(commandes.data ?? []).length} commande(s)</p>
    </div>
  );
}
