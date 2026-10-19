"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useState, type ReactNode } from "react";
import { toast } from "sonner";
import { ArrowLeft, Ban, CheckCircle2 } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { PageHeader, LoadingView, ErrorState, EmptyState } from "@/components/common/async-state";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { boulangerieApi } from "@/services/api";

function Info({ label, value }: { label: string; value: ReactNode }) {
  return (
    <div>
      <p className="text-xs uppercase tracking-wide text-slate-400">{label}</p>
      <p className="text-sm font-medium text-slate-900">{value ?? "—"}</p>
    </div>
  );
}

export default function ProduitDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const [busy, setBusy] = useState(false);

  const produit = useApi(() => boulangerieApi.produits.get(id), [id, tenantId]);
  const data = produit.data;
  const reload = () => produit.reload();

  const recettes = useApi(
    () => (data ? boulangerieApi.recettes.list({ produitCode: data.code }) : Promise.resolve([])),
    [data?.code, tenantId],
  );

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

  if (produit.loading) return <LoadingView label="Chargement du produit…" />;
  if (produit.error) return <ErrorState error={produit.error} onRetry={reload} />;

  return (
    <div>
      <PageHeader title={data?.designation ?? "Produit"} description={data?.code ?? ""}>
        <Link href="/boulangerie/produits">
          <Button variant="outline" size="sm">
            <ArrowLeft className="h-4 w-4" />
            Retour
          </Button>
        </Link>
        {isAdmin && data?.actif === false && (
          <Button size="sm" disabled={busy} onClick={() => run(() => boulangerieApi.produits.activer(id), "Produit activé")}>
            <CheckCircle2 className="h-4 w-4" />
            Activer
          </Button>
        )}
        {isAdmin && data?.actif !== false && (
          <Button variant="outline" size="sm" disabled={busy} onClick={() => run(() => boulangerieApi.produits.desactiver(id), "Produit désactivé")}>
            <Ban className="h-4 w-4" />
            Désactiver
          </Button>
        )}
      </PageHeader>

      <div className="mb-4 flex flex-wrap items-center gap-2">
        <Badge variant={data?.actif === false ? "danger" : "success"}>
          {data?.actif === false ? "Inactif" : "Actif"}
        </Badge>
        <Badge variant={data?.fabricable ? "success" : "outline"}>
          {data?.fabricable ? "Fabricable" : "Non fabricable"}
        </Badge>
        {data?.uniteVente && <Badge variant="outline">{data.uniteVente}</Badge>}
        {data?.vendableAuDetail === false && <Badge variant="outline">Vente au détail désactivée</Badge>}
      </div>

      <Card className="mb-4">
        <CardHeader className="p-4 pb-2">
          <CardTitle className="text-base">Caractéristiques</CardTitle>
        </CardHeader>
        <CardContent className="grid gap-4 p-4 pt-2 sm:grid-cols-3 lg:grid-cols-4">
          <Info label="Code" value={data?.code} />
          <Info label="Gamme" value={data?.gammeCode} />
          <Info label="Catégorie" value={data?.categorieCode} />
          <Info label="Article (stock)" value={data?.articleCode} />
          <Info label="Prix HT" value={data?.prixVenteHT} />
          <Info label="TVA (%)" value={data?.tauxTva} />
          <Info label="Prix TTC" value={data?.prixTTC} />
          <Info label="Poids (g)" value={data?.poidsUnitaireGrammes} />
          <Info label="DLC (heures)" value={data?.dlcHeures} />
          <Info label="Code-barres" value={data?.codeBarre} />
          <Info
            label="Allergènes"
            value={data?.allergenes?.length ? data.allergenes.join(", ") : "Aucun"}
          />
          <Info label="Vendable au détail" value={data?.vendableAuDetail === false ? "Non" : "Oui"} />
        </CardContent>
      </Card>

      <Card>
        <CardHeader className="p-4 pb-2">
          <CardTitle className="text-base">Fiches recette ({recettes.data?.length ?? 0})</CardTitle>
        </CardHeader>
        <CardContent className="overflow-x-auto p-4 pt-2">
          {recettes.loading ? (
            <LoadingView label="Chargement des recettes…" />
          ) : recettes.data?.length ? (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-3 py-2">Code</th>
                  <th className="px-3 py-2">Libellé</th>
                  <th className="px-3 py-2">Version</th>
                  <th className="px-3 py-2">Ingrédients</th>
                  <th className="px-3 py-2">Actif</th>
                  <th className="px-3 py-2">Modifiable</th>
                  <th className="px-3 py-2 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {recettes.data.map((recette) => (
                  <tr key={recette.idRecette} className="border-b border-border last:border-0">
                    <td className="px-3 py-2 font-mono text-xs">{recette.code}</td>
                    <td className="px-3 py-2">{recette.libelle ?? "—"}</td>
                    <td className="px-3 py-2">v{recette.version ?? 1}</td>
                    <td className="px-3 py-2">{recette.ingredients?.length ?? 0}</td>
                    <td className="px-3 py-2">
                      <Badge variant={recette.active ? "success" : "outline"}>
                        {recette.active ? "Oui" : "Non"}
                      </Badge>
                    </td>
                    <td className="px-3 py-2">
                      <Badge variant={recette.modifiable === false ? "warning" : "outline"}>
                        {recette.modifiable === false ? "Figée" : "Oui"}
                      </Badge>
                    </td>
                    <td className="px-3 py-2 text-right">
                      <Link href={`/boulangerie/recettes/${recette.idRecette}`}>
                        <Button variant="ghost" size="sm">
                          Détail
                        </Button>
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <EmptyState label="Aucune recette pour ce produit" />
          )}
        </CardContent>
      </Card>
    </div>
  );
}
