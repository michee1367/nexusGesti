"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import {
  ArrowLeft,
  Ban,
  CheckCircle2,
  Send,
} from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { PageHeader, LoadingView, ErrorState } from "@/components/common/async-state";
import { formatDate } from "@/components/pedagogie/format";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { boulangerieApi } from "@/services/api";
import { ApiError } from "@/types";

const STATUT_BADGE: Record<string, "outline" | "success" | "warning" | "danger" | "default"> = {
  BROUILLON: "outline",
  VALIDEE: "warning",
  ENVOYEE: "warning",
  PARTIELLE: "warning",
  RECEPTIONNEE: "success",
  FACTUREE: "default",
  ANNULEE: "danger",
};

export default function CommandeDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const commande = useApi(() => boulangerieApi.commandes.get(id), [id, tenantId]);
  const [busy, setBusy] = useState(false);
  const [motif, setMotif] = useState("");
  const [showAnnulation, setShowAnnulation] = useState(false);
  const [rapprochement, setRapprochement] = useState<Awaited<
    ReturnType<typeof boulangerieApi.commandes.rapprochement>
  > | null>(null);
  const [rapproLoading, setRapproLoading] = useState(false);

  const reload = () => {
    commande.reload();
    setRapprochement(null);
  };

  const transition = async (
    action: () => Promise<unknown>,
    message: string,
  ) => {
    setBusy(true);
    try {
      await action();
      toast.success(message);
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  const annuler = async () => {
    if (!motif.trim()) {
      toast.error("Un motif d’annulation est obligatoire");
      return;
    }
    setBusy(true);
    try {
      await boulangerieApi.commandes.annuler(id, motif.trim());
      toast.success("Commande annulée");
      setMotif("");
      setShowAnnulation(false);
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  const chargerRapprochement = async () => {
    setRapproLoading(true);
    try {
      setRapprochement(await boulangerieApi.commandes.rapprochement(id));
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setRapproLoading(false);
    }
  };

  if (commande.loading) return <LoadingView label="Chargement de la commande…" />;
  if (commande.error) return <ErrorState error={commande.error} onRetry={reload} />;

  const c = commande.data;
  if (!c) return <ErrorState error={new ApiError(404, "Commande introuvable", "NOT_FOUND")} onRetry={reload} />;
  const statut = c.statut ?? "BROUILLON";

  return (
    <div>
      <PageHeader
        title={`Commande ${c.numero ?? ""}`}
        description={`${c.fournisseurTiersCode} — cycle BROUILLON → VALIDEE → ENVOYEE → réceptions (lot 3).`}
      >
        <Link href="/boulangerie/commandes">
          <Button variant="ghost" size="sm">
            <ArrowLeft className="h-4 w-4" />
            Retour
          </Button>
        </Link>
        {isAdmin && (
          <>
            {statut === "BROUILLON" && (
              <Button
                size="sm"
                disabled={busy}
                onClick={() => transition(() => boulangerieApi.commandes.valider(id), "Commande validée")}
              >
                <CheckCircle2 className="h-4 w-4" />
                Valider
              </Button>
            )}
            {statut === "VALIDEE" && (
              <Button
                size="sm"
                disabled={busy}
                onClick={() => transition(() => boulangerieApi.commandes.envoyer(id), "Commande envoyée")}
              >
                <Send className="h-4 w-4" />
                Envoyer
              </Button>
            )}
            {statut !== "ANNULEE" && statut !== "RECEPTIONNEE" && statut !== "FACTUREE" && (
              <Button variant="outline" size="sm" onClick={() => setShowAnnulation((v) => !v)}>
                <Ban className="h-4 w-4" />
                Annuler
              </Button>
            )}
          </>
        )}
      </PageHeader>

      {showAnnulation && (
        <Card className="mb-4">
          <CardContent className="flex flex-wrap items-end gap-3 p-4">
            <div className="min-w-64 flex-1">
              <Label className="mb-1 block">Motif d’annulation *</Label>
              <Input
                value={motif}
                placeholder="Doublon, rupture de stock fournisseur…"
                onChange={(e) => setMotif(e.target.value)}
              />
            </div>
            <Button size="sm" variant="destructive" disabled={busy} onClick={annuler}>
              Confirmer l’annulation
            </Button>
            <Button size="sm" variant="ghost" onClick={() => setShowAnnulation(false)}>
              Fermer
            </Button>
          </CardContent>
        </Card>
      )}

      <div className="mb-4 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardContent className="p-4 text-sm">
            <p className="text-xs uppercase text-slate-500">Statut</p>
            <Badge variant={STATUT_BADGE[statut] ?? "outline"}>{statut}</Badge>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 text-sm">
            <p className="text-xs uppercase text-slate-500">Date / livraison prévue</p>
            <p className="font-medium">
              {formatDate(c.date)} → {formatDate(c.dateLivraisonPrevue)}
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 text-sm">
            <p className="text-xs uppercase text-slate-500">Montants (HT / TVA / TTC)</p>
            <p className="font-medium">
              {c.montantHT ?? 0} / {c.montantTVA ?? 0} / {c.montantTTC ?? 0} {c.devise ?? "USD"}
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 text-sm">
            <p className="text-xs uppercase text-slate-500">Reste à recevoir</p>
            <p className="text-lg font-semibold">{c.resteARecevoir ?? 0}</p>
          </CardContent>
        </Card>
      </div>

      {c.motifAnnulation && (
        <Card className="mb-4">
          <CardContent className="p-4 text-sm">
            <p className="text-xs uppercase text-slate-500">Motif d’annulation</p>
            <p>{c.motifAnnulation}</p>
          </CardContent>
        </Card>
      )}

      <Card className="mb-4">
        <CardHeader>
          <CardTitle>Lignes de la commande</CardTitle>
        </CardHeader>
        <CardContent className="overflow-x-auto p-0">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                <th className="px-4 py-3">Article</th>
                <th className="px-4 py-3">Désignation</th>
                <th className="px-4 py-3">Qté commandée</th>
                <th className="px-4 py-3">Qté reçue</th>
                <th className="px-4 py-3">Reste</th>
                <th className="px-4 py-3">Prix unit.</th>
                <th className="px-4 py-3">Montant HT</th>
              </tr>
            </thead>
            <tbody>
              {(c.lignes ?? []).map((ligne) => (
                <tr key={ligne.id} className="border-b border-border last:border-0">
                  <td className="px-4 py-3 font-mono font-medium">{ligne.articleCode}</td>
                  <td className="px-4 py-3">{ligne.designation ?? "—"}</td>
                  <td className="px-4 py-3">{ligne.quantiteCommandee ?? 0}</td>
                  <td className="px-4 py-3">{ligne.quantiteRecue ?? 0}</td>
                  <td className="px-4 py-3 font-medium">{ligne.resteARecevoir ?? 0}</td>
                  <td className="px-4 py-3">{ligne.prixUnitaire ?? 0}</td>
                  <td className="px-4 py-3">{ligne.montantHT ?? 0}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </CardContent>
      </Card>

      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <CardTitle>Rapprochement commande ↔ réceptions</CardTitle>
          <Button size="sm" variant="outline" disabled={rapproLoading} onClick={chargerRapprochement}>
            {rapproLoading ? "Vérification…" : "Vérifier"}
          </Button>
        </CardHeader>
        <CardContent>
          {!rapprochement ? (
            <p className="text-sm text-slate-500">
              Compare les montants commandés et reçus ainsi que les prix d’achat (la facture relève de la phase
              finance).
            </p>
          ) : (
            <div className="space-y-3 text-sm">
              <div className="flex flex-wrap items-center gap-3">
                <Badge variant={rapprochement.concordant ? "success" : "danger"}>
                  {rapprochement.concordant ? "Concordant" : "Écart détecté"}
                </Badge>
                <span>
                  Commande {rapprochement.montantCommandeTTC ?? 0} / Réceptions{" "}
                  {rapprochement.montantReceptionsTTC ?? 0}
                </span>
              </div>
              {(rapprochement.ecarts ?? []).length > 0 && (
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                      <th className="px-2 py-2">Article</th>
                      <th className="px-2 py-2">Qté cmd.</th>
                      <th className="px-2 py-2">Qté reçue</th>
                      <th className="px-2 py-2">Prix commande</th>
                      <th className="px-2 py-2">Prix achat</th>
                      <th className="px-2 py-2">Écart</th>
                    </tr>
                  </thead>
                  <tbody>
                    {(rapprochement.ecarts ?? []).map((ecart, index) => (
                      <tr key={`${ecart.articleCode}-${index}`} className="border-b border-border last:border-0">
                        <td className="px-2 py-2 font-mono">{ecart.articleCode}</td>
                        <td className="px-2 py-2">{ecart.quantiteCommandee ?? 0}</td>
                        <td className="px-2 py-2">{ecart.quantiteRecue ?? 0}</td>
                        <td className="px-2 py-2">{ecart.prixCommande ?? 0}</td>
                        <td className="px-2 py-2">{ecart.prixAchat ?? 0}</td>
                        <td className="px-2 py-2 font-semibold">{ecart.ecartPrixAchat ?? 0}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
