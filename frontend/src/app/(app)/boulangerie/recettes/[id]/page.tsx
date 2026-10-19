"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import { ArrowLeft, CheckCircle2, Lock, Plus } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Checkbox } from "@/components/ui/checkbox";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { PageHeader, LoadingView, ErrorState, EmptyState } from "@/components/common/async-state";
import { humanize } from "@/components/pedagogie/format";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { boulangerieApi } from "@/services/api";
import type { BesoinMatiere, CoutRecette } from "@/types";

const emptyIngredient = {
  articleCode: "",
  libelle: "",
  quantite: "0",
  unite: "",
  tauxPerte: "0",
  ordre: "0",
  comptabiliseCout: true,
};

export default function RecetteDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const [busy, setBusy] = useState(false);

  const recette = useApi(() => boulangerieApi.recettes.get(id), [id, tenantId]);
  const data = recette.data;
  const reload = () => recette.reload();

  const [ingredientForm, setIngredientForm] = useState(emptyIngredient);
  const [besoinQte, setBesoinQte] = useState("1");
  const [besoins, setBesoins] = useState<BesoinMatiere[] | null>(null);
  const [prix, setPrix] = useState<Record<string, string>>({});
  const [cout, setCout] = useState<CoutRecette | null>(null);

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

  if (recette.loading) return <LoadingView label="Chargement de la recette…" />;
  if (recette.error) return <ErrorState error={recette.error} onRetry={reload} />;

  const modifiable = data?.modifiable !== false;
  const ingredients = data?.ingredients ?? [];

  return (
    <div>
      <PageHeader
        title={data?.libelle ?? data?.code ?? "Fiche recette"}
        description={`${data?.code ?? ""} · v${data?.version ?? 1}`}
      >
        <Link href="/boulangerie/recettes">
          <Button variant="outline" size="sm">
            <ArrowLeft className="h-4 w-4" />
            Retour
          </Button>
        </Link>
        {isAdmin && !data?.active && (
          <Button size="sm" disabled={busy} onClick={() => run(() => boulangerieApi.recettes.activer(id), "Recette activée")}>
            <CheckCircle2 className="h-4 w-4" />
            Activer
          </Button>
        )}
        {isAdmin && modifiable && (
          <Button variant="outline" size="sm" disabled={busy} onClick={() => run(() => boulangerieApi.recettes.figer(id), "Recette figée")}>
            <Lock className="h-4 w-4" />
            Figer
          </Button>
        )}
      </PageHeader>

      <div className="mb-4 flex flex-wrap items-center gap-2">
        <Badge variant={data?.active ? "success" : "outline"}>{data?.active ? "Active" : "Inactive"}</Badge>
        <Badge variant={modifiable ? "outline" : "warning"}>{modifiable ? "Modifiable" : "Figée — lecture seule"}</Badge>
        {data?.produitCode && <Badge variant="outline">Produit {data.produitCode}</Badge>}
        {data?.rendementQuantite != null && (
          <Badge variant="outline">
            Rendement {data.rendementQuantite} {data.rendementUnite ?? ""}
          </Badge>
        )}
      </div>

      <Card className="mb-4">
        <CardHeader className="p-4 pb-2">
          <CardTitle className="text-base">Procédé</CardTitle>
        </CardHeader>
        <CardContent className="grid gap-4 p-4 pt-2 sm:grid-cols-3 lg:grid-cols-4">
          <div>
            <p className="text-xs uppercase tracking-wide text-slate-400">Préparation</p>
            <p className="text-sm font-medium text-slate-900">{data?.tempsPreparationMin ?? "—"} min</p>
          </div>
          <div>
            <p className="text-xs uppercase tracking-wide text-slate-400">Cuisson</p>
            <p className="text-sm font-medium text-slate-900">{data?.tempsCuissonMin ?? "—"} min</p>
          </div>
          <div>
            <p className="text-xs uppercase tracking-wide text-slate-400">Température</p>
            <p className="text-sm font-medium text-slate-900">{data?.temperatureCuissonC ?? "—"} °C</p>
          </div>
          <div className="sm:col-span-3 lg:col-span-1">
            <p className="text-xs uppercase tracking-wide text-slate-400">Instructions</p>
            <p className="text-sm text-slate-900">{data?.instructions ?? "—"}</p>
          </div>
        </CardContent>
      </Card>

      <Card className="mb-4">
        <CardHeader className="p-4 pb-2">
          <CardTitle className="text-base">Ingrédients ({ingredients.length})</CardTitle>
        </CardHeader>
        <CardContent className="overflow-x-auto p-4 pt-2">
          {ingredients.length ? (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-3 py-2">Article</th>
                  <th className="px-3 py-2">Libellé</th>
                  <th className="px-3 py-2">Quantité</th>
                  <th className="px-3 py-2">Unité</th>
                  <th className="px-3 py-2">Perte %</th>
                  <th className="px-3 py-2">Nette</th>
                  <th className="px-3 py-2">Brute</th>
                  <th className="px-3 py-2">Coût</th>
                </tr>
              </thead>
              <tbody>
                {ingredients.map((ingredient, index) => (
                  <tr key={ingredient.id ?? index} className="border-b border-border last:border-0">
                    <td className="px-3 py-2 font-mono text-xs">{ingredient.articleCode}</td>
                    <td className="px-3 py-2">{ingredient.libelle ?? "—"}</td>
                    <td className="px-3 py-2">{ingredient.quantite ?? "—"}</td>
                    <td className="px-3 py-2">{ingredient.unite ?? "—"}</td>
                    <td className="px-3 py-2">{ingredient.tauxPerte ?? 0}</td>
                    <td className="px-3 py-2">{ingredient.quantiteNette ?? "—"}</td>
                    <td className="px-3 py-2">{ingredient.quantiteBrute ?? "—"}</td>
                    <td className="px-3 py-2">{ingredient.comptabiliseCout === false ? "Non" : "Oui"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <EmptyState label="Aucun ingrédient" />
          )}

          {isAdmin && modifiable && (
            <form
              className="mt-3 grid gap-3 border-t pt-3 sm:grid-cols-4 lg:grid-cols-8"
              onSubmit={(event) => {
                event.preventDefault();
                if (!ingredientForm.articleCode) return;
                run(
                  () =>
                    boulangerieApi.recettes.ajouterIngredient(id, {
                      articleCode: ingredientForm.articleCode,
                      libelle: ingredientForm.libelle,
                      quantite: Number(ingredientForm.quantite),
                      unite: ingredientForm.unite,
                      tauxPerte: Number(ingredientForm.tauxPerte),
                      ordre: Number(ingredientForm.ordre),
                      comptabiliseCout: ingredientForm.comptabiliseCout,
                    }),
                  "Ingrédient ajouté",
                ).then(() => setIngredientForm(emptyIngredient));
              }}
            >
              <div>
                <Label className="mb-1 block">Article</Label>
                <Input
                  placeholder="ART-..."
                  value={ingredientForm.articleCode}
                  onChange={(event) => setIngredientForm({ ...ingredientForm, articleCode: event.target.value })}
                />
              </div>
              <div>
                <Label className="mb-1 block">Libellé</Label>
                <Input
                  value={ingredientForm.libelle}
                  onChange={(event) => setIngredientForm({ ...ingredientForm, libelle: event.target.value })}
                />
              </div>
              <div>
                <Label className="mb-1 block">Quantité</Label>
                <Input
                  type="number"
                  step="0.0001"
                  value={ingredientForm.quantite}
                  onChange={(event) => setIngredientForm({ ...ingredientForm, quantite: event.target.value })}
                />
              </div>
              <div>
                <Label className="mb-1 block">Unité</Label>
                <Input
                  placeholder="KG, L…"
                  value={ingredientForm.unite}
                  onChange={(event) => setIngredientForm({ ...ingredientForm, unite: event.target.value })}
                />
              </div>
              <div>
                <Label className="mb-1 block">Taux de perte</Label>
                <Input
                  type="number"
                  step="0.0001"
                  value={ingredientForm.tauxPerte}
                  onChange={(event) => setIngredientForm({ ...ingredientForm, tauxPerte: event.target.value })}
                />
              </div>
              <div>
                <Label className="mb-1 block">Ordre</Label>
                <Input
                  type="number"
                  value={ingredientForm.ordre}
                  onChange={(event) => setIngredientForm({ ...ingredientForm, ordre: event.target.value })}
                />
              </div>
              <div className="flex h-10 items-center gap-2 self-end">
                <Checkbox
                  checked={ingredientForm.comptabiliseCout}
                  onChange={(checked) => setIngredientForm({ ...ingredientForm, comptabiliseCout: checked })}
                />
                <Label>Coût</Label>
              </div>
              <div className="flex items-end">
                <Button type="submit" disabled={busy || !ingredientForm.articleCode}>
                  <Plus className="h-4 w-4" />
                  Ajouter
                </Button>
              </div>
            </form>
          )}
        </CardContent>
      </Card>

      {!!data?.lignesCout?.length && (
        <Card className="mb-4">
          <CardHeader className="p-4 pb-2">
            <CardTitle className="text-base">Lignes de coût ({data.lignesCout.length})</CardTitle>
          </CardHeader>
          <CardContent className="overflow-x-auto p-4 pt-2">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-3 py-2">Nature</th>
                  <th className="px-3 py-2">Libellé</th>
                  <th className="px-3 py-2">Montant</th>
                </tr>
              </thead>
              <tbody>
                {data.lignesCout.map((ligne, index) => (
                  <tr key={ligne.id ?? index} className="border-b border-border last:border-0">
                    <td className="px-3 py-2">{ligne.nature ? humanize(ligne.nature) : "—"}</td>
                    <td className="px-3 py-2">{ligne.libelle ?? "—"}</td>
                    <td className="px-3 py-2 font-semibold">{ligne.montant ?? "—"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
      )}

      <div className="grid gap-4 lg:grid-cols-2">
        <Card>
          <CardHeader className="p-4 pb-2">
            <CardTitle className="text-base">Besoins matières</CardTitle>
          </CardHeader>
          <CardContent className="p-4 pt-2">
            <form
              className="mb-3 flex items-end gap-3"
              onSubmit={async (event) => {
                event.preventDefault();
                try {
                  const result = await boulangerieApi.recettes.besoins(id, Number(besoinQte));
                  setBesoins(result);
                } catch (error) {
                  toast.error(error instanceof Error ? error.message : "Erreur");
                }
              }}
            >
              <div>
                <Label className="mb-1 block">Quantité produite</Label>
                <Input type="number" step="0.0001" value={besoinQte} onChange={(event) => setBesoinQte(event.target.value)} />
              </div>
              <Button type="submit" variant="outline">
                Calculer
              </Button>
            </form>
            {besoins?.length ? (
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                    <th className="px-3 py-2">Article</th>
                    <th className="px-3 py-2">Nette</th>
                    <th className="px-3 py-2">Brute</th>
                    <th className="px-3 py-2">Unité</th>
                  </tr>
                </thead>
                <tbody>
                  {besoins.map((besoin) => (
                    <tr key={besoin.articleCode} className="border-b border-border last:border-0">
                      <td className="px-3 py-2 font-mono text-xs">{besoin.articleCode}</td>
                      <td className="px-3 py-2">{besoin.quantiteNette}</td>
                      <td className="px-3 py-2 font-semibold">{besoin.quantiteBrute}</td>
                      <td className="px-3 py-2">{besoin.unite ?? "—"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <EmptyState label="Aucun besoin calculé" />
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="p-4 pb-2">
            <CardTitle className="text-base">Coût de revient</CardTitle>
          </CardHeader>
          <CardContent className="p-4 pt-2">
            <form
              className="mb-3 grid gap-3"
              onSubmit={async (event) => {
                event.preventDefault();
                const prixMoyens: Record<string, number> = {};
                for (const [code, value] of Object.entries(prix)) {
                  if (value !== "" && !Number.isNaN(Number(value))) prixMoyens[code] = Number(value);
                }
                try {
                  const result = await boulangerieApi.recettes.cout(id, prixMoyens);
                  setCout(result);
                } catch (error) {
                  toast.error(error instanceof Error ? error.message : "Erreur");
                }
              }}
            >
              {ingredients.filter((ingredient) => ingredient.comptabiliseCout !== false).map((ingredient) => (
                <div key={ingredient.articleCode} className="flex items-end gap-3">
                  <div className="flex-1">
                    <Label className="mb-1 block">{ingredient.libelle ?? ingredient.articleCode}</Label>
                    <Input
                      type="number"
                      step="0.0001"
                      placeholder="Prix moyen"
                      value={prix[ingredient.articleCode] ?? ""}
                      onChange={(event) => setPrix({ ...prix, [ingredient.articleCode]: event.target.value })}
                    />
                  </div>
                </div>
              ))}
              <Button type="submit" variant="outline" disabled={!ingredients.length}>
                Calculer le coût
              </Button>
            </form>
            {cout && (
              <div className="grid gap-3 sm:grid-cols-3">
                <div>
                  <p className="text-xs uppercase tracking-wide text-slate-400">Coût matière</p>
                  <p className="text-lg font-semibold text-slate-900">{cout.coutMatiere}</p>
                </div>
                <div>
                  <p className="text-xs uppercase tracking-wide text-slate-400">Coût de revient</p>
                  <p className="text-lg font-semibold text-slate-900">{cout.coutRevient}</p>
                </div>
                <div>
                  <p className="text-xs uppercase tracking-wide text-slate-400">Prix disponibles</p>
                  <Badge variant={cout.coutMatiereDisponible ? "success" : "warning"}>
                    {cout.coutMatiereDisponible ? "Complet" : "Incomplet"}
                  </Badge>
                </div>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
