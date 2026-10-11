"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import { Archive, ArrowLeft, CheckCircle2, Lock, Plus } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select } from "@/components/ui/select";
import { PageHeader, LoadingView, ErrorState, EmptyState } from "@/components/common/async-state";
import { enumOptions, CATEGORIE_FRAIS, PERIODICITE, TYPE_FRAIS, TYPE_REDUCTION } from "@/components/pedagogie/constants";
import { humanize, formatDate } from "@/components/pedagogie/format";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { feesApi } from "@/services/api";

export default function GrilleTarifaireDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();

  const grille = useApi(() => feesApi.grilles.get(id), [id, tenantId]);
  const [busy, setBusy] = useState(false);

  const [ligneForm, setLigneForm] = useState({ typeFrais: "INSCRIPTION", libelle: "", montant: "0", categorie: "OBLIGATOIRE", periodicite: "PONCTUELLE", nbEcheances: "1" });
  const [reductionForm, setReductionForm] = useState({ code: "", libelle: "", type: "POURCENTAGE", valeur: "0" });

  const data = grille.data;
  const reload = () => grille.reload();

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

  if (grille.loading) return <LoadingView label="Chargement de la grille…" />;
  if (grille.error) return <ErrorState error={grille.error} onRetry={reload} />;

  const modifiable = data?.modifiable ?? false;

  return (
    <div>
      <PageHeader title={data?.libelle ?? data?.code ?? "Grille tarifaire"} description={`${data?.code ?? ""} · v${data?.version ?? "—"}`}>
        <Link href="/pedagogie/grilles-tarifaires">
          <Button variant="outline" size="sm">
            <ArrowLeft className="h-4 w-4" />
            Retour
          </Button>
        </Link>
        {isAdmin && data?.statut === "BROUILLON" && (
          <Button size="sm" disabled={busy} onClick={() => run(() => feesApi.grilles.activer(id), "Grille activée")}>
            <CheckCircle2 className="h-4 w-4" />
            Activer
          </Button>
        )}
        {isAdmin && data?.statut !== "ARCHIVEE" && (
          <Button variant="outline" size="sm" disabled={busy} onClick={() => run(() => feesApi.grilles.figer(id), "Grille figée")}>
            <Lock className="h-4 w-4" />
            Figer
          </Button>
        )}
        {isAdmin && data?.statut !== "ARCHIVEE" && (
          <Button variant="outline" size="sm" disabled={busy} onClick={() => run(() => feesApi.grilles.archiver(id), "Grille archivée")}>
            <Archive className="h-4 w-4" />
            Archiver
          </Button>
        )}
      </PageHeader>

      <div className="mb-4 flex flex-wrap items-center gap-2">
        <Badge variant={data?.statut === "ACTIVE" ? "success" : "outline"}>{humanize(data?.statut ?? "—")}</Badge>
        {data?.annee && <Badge variant="outline">Année {data.annee}</Badge>}
        {data?.profil && <Badge variant="outline">Profil {data.profil}</Badge>}
        <Badge variant="outline">Devise {data?.devise ?? "USD"}</Badge>
        {data?.dateActivation && <Badge variant="outline">Activée {formatDate(data.dateActivation)}</Badge>}
        {!modifiable && <Badge variant="danger">Figée — lecture seule</Badge>}
      </div>

      <Card className="mb-4">
        <CardHeader className="p-4 pb-2">
          <CardTitle className="text-base">Lignes de frais ({data?.lignes?.length ?? 0})</CardTitle>
        </CardHeader>
        <CardContent className="overflow-x-auto p-4 pt-2">
          {data?.lignes?.length ? (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-3 py-2">Type</th>
                  <th className="px-3 py-2">Libellé</th>
                  <th className="px-3 py-2">Montant</th>
                  <th className="px-3 py-2">Catégorie</th>
                  <th className="px-3 py-2">Périodicité</th>
                  <th className="px-3 py-2">Échéances</th>
                  <th className="px-3 py-2">Obligatoire</th>
                </tr>
              </thead>
              <tbody>
                {data.lignes.map((ligne, index) => (
                  <tr key={ligne.id ?? index} className="border-b border-border last:border-0">
                    <td className="px-3 py-2">{humanize(ligne.typeFrais)}</td>
                    <td className="px-3 py-2">{ligne.libelle ?? "—"}</td>
                    <td className="px-3 py-2 font-semibold">{ligne.montantEffectif ?? ligne.montant}</td>
                    <td className="px-3 py-2">{ligne.categorie ? humanize(ligne.categorie) : "—"}</td>
                    <td className="px-3 py-2">{ligne.periodicite ? humanize(ligne.periodicite) : "—"}</td>
                    <td className="px-3 py-2">{ligne.nbEcheances ?? "—"}</td>
                    <td className="px-3 py-2">{ligne.obligatoire ? "Oui" : "Non"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <EmptyState label="Aucune ligne" />
          )}

          {isAdmin && modifiable && (
            <form
              className="mt-3 grid gap-3 border-t pt-3 sm:grid-cols-6"
              onSubmit={(event) => {
                event.preventDefault();
                run(
                  () =>
                    feesApi.grilles.ajouterLigne(id, {
                      typeFrais: ligneForm.typeFrais as never,
                      libelle: ligneForm.libelle,
                      montant: Number(ligneForm.montant),
                      categorie: ligneForm.categorie as never,
                      periodicite: ligneForm.periodicite as never,
                      nbEcheances: Number(ligneForm.nbEcheances),
                    }),
                  "Ligne ajoutée",
                );
                setLigneForm({ typeFrais: "INSCRIPTION", libelle: "", montant: "0", categorie: "OBLIGATOIRE", periodicite: "PONCTUELLE", nbEcheances: "1" });
              }}
            >
              <div>
                <Label className="mb-1 block">Type</Label>
                <Select value={ligneForm.typeFrais} onChange={(event) => setLigneForm({ ...ligneForm, typeFrais: event.target.value })}>
                  {enumOptions(TYPE_FRAIS).map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </Select>
              </div>
              <div>
                <Label className="mb-1 block">Libellé</Label>
                <Input value={ligneForm.libelle} onChange={(event) => setLigneForm({ ...ligneForm, libelle: event.target.value })} />
              </div>
              <div>
                <Label className="mb-1 block">Montant</Label>
                <Input type="number" step="0.01" value={ligneForm.montant} onChange={(event) => setLigneForm({ ...ligneForm, montant: event.target.value })} />
              </div>
              <div>
                <Label className="mb-1 block">Catégorie</Label>
                <Select value={ligneForm.categorie} onChange={(event) => setLigneForm({ ...ligneForm, categorie: event.target.value })}>
                  {enumOptions(CATEGORIE_FRAIS).map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </Select>
              </div>
              <div>
                <Label className="mb-1 block">Périodicité</Label>
                <Select value={ligneForm.periodicite} onChange={(event) => setLigneForm({ ...ligneForm, periodicite: event.target.value })}>
                  {enumOptions(PERIODICITE).map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </Select>
              </div>
              <div className="flex items-end">
                <Button type="submit" disabled={busy}>
                  <Plus className="h-4 w-4" />
                  Ajouter
                </Button>
              </div>
            </form>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardHeader className="p-4 pb-2">
          <CardTitle className="text-base">Réductions ({data?.reductions?.length ?? 0})</CardTitle>
        </CardHeader>
        <CardContent className="overflow-x-auto p-4 pt-2">
          {data?.reductions?.length ? (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-3 py-2">Code</th>
                  <th className="px-3 py-2">Libellé</th>
                  <th className="px-3 py-2">Type</th>
                  <th className="px-3 py-2">Valeur</th>
                  <th className="px-3 py-2">Cumulable</th>
                </tr>
              </thead>
              <tbody>
                {data.reductions.map((reduction, index) => (
                  <tr key={reduction.id ?? index} className="border-b border-border last:border-0">
                    <td className="px-3 py-2 font-mono text-xs">{reduction.code}</td>
                    <td className="px-3 py-2">{reduction.libelle}</td>
                    <td className="px-3 py-2">{humanize(reduction.type)}</td>
                    <td className="px-3 py-2 font-semibold">{reduction.valeur}</td>
                    <td className="px-3 py-2">{reduction.cumulable ? "Oui" : "Non"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <EmptyState label="Aucune réduction" />
          )}

          {isAdmin && modifiable && (
            <form
              className="mt-3 grid gap-3 border-t pt-3 sm:grid-cols-5"
              onSubmit={(event) => {
                event.preventDefault();
                if (!reductionForm.code) return;
                run(
                  () =>
                    feesApi.grilles.ajouterReduction(id, {
                      code: reductionForm.code,
                      libelle: reductionForm.libelle,
                      type: reductionForm.type as never,
                      valeur: Number(reductionForm.valeur),
                    }),
                  "Réduction ajoutée",
                );
                setReductionForm({ code: "", libelle: "", type: "POURCENTAGE", valeur: "0" });
              }}
            >
              <div>
                <Label className="mb-1 block">Code</Label>
                <Input value={reductionForm.code} onChange={(event) => setReductionForm({ ...reductionForm, code: event.target.value })} />
              </div>
              <div>
                <Label className="mb-1 block">Libellé</Label>
                <Input value={reductionForm.libelle} onChange={(event) => setReductionForm({ ...reductionForm, libelle: event.target.value })} />
              </div>
              <div>
                <Label className="mb-1 block">Type</Label>
                <Select value={reductionForm.type} onChange={(event) => setReductionForm({ ...reductionForm, type: event.target.value })}>
                  {enumOptions(TYPE_REDUCTION).map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </Select>
              </div>
              <div>
                <Label className="mb-1 block">Valeur</Label>
                <Input type="number" step="0.01" value={reductionForm.valeur} onChange={(event) => setReductionForm({ ...reductionForm, valeur: event.target.value })} />
              </div>
              <div className="flex items-end">
                <Button type="submit" disabled={busy || !reductionForm.code}>
                  <Plus className="h-4 w-4" />
                  Ajouter
                </Button>
              </div>
            </form>
          )}
        </CardContent>
      </Card>
    </div>
  );
}