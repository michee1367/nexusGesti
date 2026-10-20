"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import { ArrowLeft, Power, PowerOff, Truck } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { PageHeader, LoadingView, ErrorState } from "@/components/common/async-state";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { boulangerieApi } from "@/services/api";
import { ApiError } from "@/types";

export default function FournisseurDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const fournisseur = useApi(() => boulangerieApi.fournisseurs.get(id), [id, tenantId]);
  const evaluation = useApi(() => boulangerieApi.fournisseurs.evaluation(id), [id, tenantId]);
  const [busy, setBusy] = useState(false);

  const reload = () => {
    fournisseur.reload();
    evaluation.reload();
  };

  const basculer = async () => {
    const courant = fournisseur.data;
    if (!courant) return;
    setBusy(true);
    try {
      if (courant.actif === false) await boulangerieApi.fournisseurs.activer(id);
      else await boulangerieApi.fournisseurs.desactiver(id);
      toast.success(courant.actif === false ? "Fournisseur réactivé" : "Fournisseur désactivé");
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  if (fournisseur.loading) return <LoadingView label="Chargement du fournisseur…" />;
  if (fournisseur.error) return <ErrorState error={fournisseur.error} onRetry={reload} />;

  const f = fournisseur.data;
  if (!f) return <ErrorState error={new ApiError(404, "Fournisseur introuvable", "NOT_FOUND")} onRetry={reload} />;
  const e = evaluation.data;

  return (
    <div>
      <PageHeader
        title={`${f.code}${f.raisonSociale ? ` — ${f.raisonSociale}` : ""}`}
        description="Fiche fournisseur et évaluation des réceptions (lot 3 approvisionnement)."
      >
        <Link href="/boulangerie/fournisseurs">
          <Button variant="ghost" size="sm">
            <ArrowLeft className="h-4 w-4" />
            Retour
          </Button>
        </Link>
        {isAdmin && (
          <Button variant="outline" size="sm" disabled={busy} onClick={basculer}>
            {f.actif === false ? <Power className="h-4 w-4" /> : <PowerOff className="h-4 w-4" />}
            {f.actif === false ? "Réactiver" : "Désactiver"}
          </Button>
        )}
      </PageHeader>

      <div className="grid gap-4 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Truck className="h-4 w-4" /> Identité
            </CardTitle>
          </CardHeader>
          <CardContent className="grid gap-3 text-sm sm:grid-cols-2">
            <div>
              <p className="text-xs uppercase text-slate-500">Code</p>
              <p className="font-mono font-medium">{f.code}</p>
            </div>
            <div>
              <p className="text-xs uppercase text-slate-500">Raison sociale</p>
              <p>{f.raisonSociale ?? "—"}</p>
            </div>
            <div>
              <p className="text-xs uppercase text-slate-500">Tiers</p>
              <p className="font-mono text-xs">{f.tiersCode ?? "—"}</p>
            </div>
            <div>
              <p className="text-xs uppercase text-slate-500">Catégorie produits</p>
              <p>{f.categorieProduits ?? "—"}</p>
            </div>
            <div>
              <p className="text-xs uppercase text-slate-500">Délai de livraison</p>
              <p>{f.delaiLivraisonJours ?? 0} jour(s)</p>
            </div>
            <div>
              <p className="text-xs uppercase text-slate-500">Montant mini commande</p>
              <p>{f.montantMiniCommande != null ? `${f.montantMiniCommande} USD` : "—"}</p>
            </div>
            <div className="sm:col-span-2">
              <p className="text-xs uppercase text-slate-500">Conditions de règlement</p>
              <p>{f.conditionsReglement ?? "—"}</p>
            </div>
            <div>
              <p className="text-xs uppercase text-slate-500">Statut</p>
              <Badge variant={f.actif === false ? "outline" : "success"}>
                {f.actif === false ? "Inactif" : "Actif"}
              </Badge>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Évaluation</CardTitle>
          </CardHeader>
          <CardContent>
            {evaluation.loading ? (
              <p className="text-sm text-slate-500">Calcul en cours…</p>
            ) : evaluation.error ? (
              <ErrorState error={evaluation.error} onRetry={evaluation.reload} />
            ) : e ? (
              <div className="grid gap-3 text-sm sm:grid-cols-2">
                <div>
                  <p className="text-xs uppercase text-slate-500">Fiabilité</p>
                  <Badge variant={e.fiable ? "success" : "warning"}>
                    {e.fiable ? "Fiable" : "À surveiller"}
                  </Badge>
                </div>
                <div>
                  <p className="text-xs uppercase text-slate-500">Délai moyen constaté</p>
                  <p>{e.delaiLivraisonJours ?? 0} j</p>
                </div>
                <div>
                  <p className="text-xs uppercase text-slate-500">Commandes</p>
                  <p className="text-lg font-semibold">{e.nbCommandes ?? 0}</p>
                </div>
                <div>
                  <p className="text-xs uppercase text-slate-500">Réceptions honorées / refusées</p>
                  <p className="text-lg font-semibold">
                    {e.nbReceptionsHonorees ?? 0} / {e.nbReceptionsRefusees ?? 0}
                  </p>
                </div>
              </div>
            ) : (
              <p className="text-sm text-slate-500">Aucune donnée d’évaluation.</p>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
