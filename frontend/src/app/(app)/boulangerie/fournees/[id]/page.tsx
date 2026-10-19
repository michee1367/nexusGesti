"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import {
  ArrowLeft,
  CheckCircle2,
  CircleDot,
  Flame,
  Plus,
  Trash2,
} from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select } from "@/components/ui/select";
import { PageHeader, LoadingView, ErrorState } from "@/components/common/async-state";
import { formatDate, humanize } from "@/components/pedagogie/format";
import { TYPE_PERTE, enumOptions } from "@/components/pedagogie/constants";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { boulangerieApi } from "@/services/api";
import type { LigneConsommation, TypePerte } from "@/types";

const STATUT_BADGE: Record<string, "outline" | "success" | "warning" | "danger" | "default"> = {
  PLANIFIEE: "outline",
  EN_COURS: "warning",
  CUITE: "success",
  VALIDEE: "default",
  ANNULEE: "danger",
};

const emptyConsommation = {
  articleCode: "",
  lotNumero: "",
  quantiteTheorique: "0",
  quantiteReelle: "0",
  prixMoyen: "",
};

const emptyPerte = {
  articleCode: "",
  quantite: "0",
  typePerte: "PATE_PERDUE",
  motif: "",
  prixMoyen: "",
};

export default function FourneeDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const [busy, setBusy] = useState(false);

  const fournee = useApi(() => boulangerieApi.fournees.get(id), [id, tenantId]);
  const data = fournee.data;
  const reload = () => fournee.reload();

  const [rendu, setRendu] = useState("");
  const [motifAnnulation, setMotifAnnulation] = useState("");
  const [consommation, setConsommation] = useState(emptyConsommation);
  const [perte, setPerte] = useState(emptyPerte);

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

  const enregistrerRendu = () => {
    const quantite = Number(rendu);
    if (!rendu || quantite <= 0) {
      toast.error("Rendement attendu > 0");
      return;
    }
    return run(
      () => boulangerieApi.fournees.enregistrerRendu(id, quantite),
      "Rendement enregistré",
    );
  };

  const annuler = () => {
    if (!motifAnnulation.trim()) {
      toast.error("Le motif d'annulation est obligatoire");
      return;
    }
    return run(
      () => boulangerieApi.fournees.annuler(id, motifAnnulation.trim()),
      "Fournée annulée",
    );
  };

  const declarerConsommation = async () => {
    if (!consommation.articleCode.trim()) {
      toast.error("L'article est obligatoire");
      return;
    }
    const ligne: Partial<LigneConsommation> = {
      articleCode: consommation.articleCode.trim(),
      lotNumero: consommation.lotNumero.trim() || null,
      quantiteTheorique: Number(consommation.quantiteTheorique) || 0,
      quantiteReelle: Number(consommation.quantiteReelle) || 0,
      prixMoyen: consommation.prixMoyen ? Number(consommation.prixMoyen) : null,
    };
    await run(
      () => boulangerieApi.fournees.consommations(id, [ligne]),
      "Consommation déclarée",
    );
    setConsommation(emptyConsommation);
  };

  const declarerPerte = async () => {
    if (!perte.articleCode.trim() || !Number(perte.quantite)) {
      toast.error("Article et quantité (> 0) sont obligatoires");
      return;
    }
    await run(
      () =>
        boulangerieApi.fournees.pertes.create(id, {
          articleCode: perte.articleCode.trim(),
          quantite: Number(perte.quantite),
          typePerte: perte.typePerte as TypePerte,
          motif: perte.motif.trim() || undefined,
          prixMoyen: perte.prixMoyen ? Number(perte.prixMoyen) : undefined,
        }),
      "Perte déclarée",
    );
    setPerte(emptyPerte);
  };

  if (fournee.loading) return <LoadingView label="Chargement de la fournée…" />;
  if (fournee.error) return <ErrorState error={fournee.error} onRetry={reload} />;

  const statut = data?.statut ?? "PLANIFIEE";
  const consommations = data?.consommations ?? [];
  const pertes = data?.pertes ?? [];

  return (
    <div>
      <PageHeader
        title={`Fournée ${data?.numero ?? ""}`}
        description={`${data?.recetteCode ?? ""}${data?.versionRecette ? ` v${data.versionRecette}` : ""}`}
      >
        <Link href="/boulangerie/fournees">
          <Button variant="outline" size="sm">
            <ArrowLeft className="h-4 w-4" />
            Retour
          </Button>
        </Link>
        {isAdmin && statut === "PLANIFIEE" && (
          <Button size="sm" disabled={busy} onClick={() => run(() => boulangerieApi.fournees.lancer(id), "Fournée lancée")}>
            <CircleDot className="h-4 w-4" />
            Lancer
          </Button>
        )}
        {isAdmin && statut === "EN_COURS" && (
          <Button size="sm" disabled={busy} onClick={() => run(() => boulangerieApi.fournees.cuire(id), "Fournée cuite")}>
            <Flame className="h-4 w-4" />
            Cuire
          </Button>
        )}
        {isAdmin && statut === "CUITE" && (
          <Button size="sm" disabled={busy} onClick={() => run(() => boulangerieApi.fournees.valider(id), "Fournée validée")}>
            <CheckCircle2 className="h-4 w-4" />
            Valider
          </Button>
        )}
      </PageHeader>

      <div className="mb-4 flex flex-wrap items-center gap-2">
        <Badge variant={STATUT_BADGE[statut] ?? "outline"}>{humanize(statut)}</Badge>
        <Badge variant="outline">Prévue : {data?.quantitePrevue ?? 0}</Badge>
        <Badge variant="outline">Réalisée : {data?.quantiteRealisee ?? 0}</Badge>
        {data?.rendement != null && (
          <Badge variant={statut === "VALIDEE" && data.rendement < 1 ? "warning" : "success"}>
            Rendement {(Number(data.rendement) * 100).toFixed(1)} %
          </Badge>
        )}
        {data?.ecartMatiere != null && Number(data.ecartMatiere) !== 0 && (
          <Badge variant="warning">Écart matière {data.ecartMatiere}</Badge>
        )}
        {data?.motifAnnulation && (
          <Badge variant="danger">Annulée — {data.motifAnnulation}</Badge>
        )}
      </div>

      <Card className="mb-4">
        <CardHeader className="p-4 pb-2">
          <CardTitle className="text-base">Fiche</CardTitle>
        </CardHeader>
        <CardContent className="grid gap-4 p-4 pt-2 sm:grid-cols-3 lg:grid-cols-4">
          <div>
            <p className="text-xs uppercase tracking-wide text-slate-400">Produit</p>
            <p className="text-sm font-medium text-slate-900">{data?.produitCode ?? "—"}</p>
          </div>
          <div>
            <p className="text-xs uppercase tracking-wide text-slate-400">Four</p>
            <p className="text-sm font-medium text-slate-900">{data?.fourCode ?? "—"}</p>
          </div>
          <div>
            <p className="text-xs uppercase tracking-wide text-slate-400">Début</p>
            <p className="text-sm font-medium text-slate-900">{formatDate(data?.dateDebut)}</p>
          </div>
          <div>
            <p className="text-xs uppercase tracking-wide text-slate-400">Fin</p>
            <p className="text-sm font-medium text-slate-900">{formatDate(data?.dateFin)}</p>
          </div>
        </CardContent>
      </Card>

      {isAdmin && statut !== "VALIDEE" && statut !== "ANNULEE" && (
        <Card className="mb-4">
          <CardContent className="grid gap-4 p-4 sm:grid-cols-2">
            <div>
              <p className="mb-2 text-xs font-medium uppercase text-slate-500">
                Rendement (quantité réelle)
              </p>
              <div className="flex items-end gap-2">
                <Input
                  type="number"
                  min={0}
                  value={rendu}
                  placeholder="Quantité produite"
                  onChange={(e) => setRendu(e.target.value)}
                />
                <Button
                  size="sm"
                  variant="outline"
                  disabled={busy || statut !== "EN_COURS"}
                  onClick={enregistrerRendu}
                >
                  Enregistrer
                </Button>
              </div>
            </div>
            <div>
              <p className="mb-2 text-xs font-medium uppercase text-slate-500">Annuler la fournée</p>
              <div className="flex items-end gap-2">
                <Input
                  value={motifAnnulation}
                  placeholder="Motif (obligatoire)"
                  onChange={(e) => setMotifAnnulation(e.target.value)}
                />
                <Button size="sm" variant="destructive" disabled={busy} onClick={annuler}>
                  Annuler
                </Button>
              </div>
            </div>
          </CardContent>
        </Card>
      )}

      <div className="grid gap-4 xl:grid-cols-2">
        <Card>
          <CardHeader className="p-4 pb-2">
            <CardTitle className="text-base">Consommations matières (par lot)</CardTitle>
          </CardHeader>
          <CardContent className="p-4 pt-2">
            <table className="mb-3 w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="py-2">Article</th>
                  <th className="py-2">Lot</th>
                  <th className="py-2 text-right">Théorique</th>
                  <th className="py-2 text-right">Réelle</th>
                  <th className="py-2 text-right">Écart</th>
                </tr>
              </thead>
              <tbody>
                {consommations.map((ligne) => (
                  <tr key={ligne.id} className="border-b border-border last:border-0">
                    <td className="py-2 font-mono text-xs">{ligne.articleCode}</td>
                    <td className="py-2 font-mono text-xs">{ligne.lotNumero ?? "—"}</td>
                    <td className="py-2 text-right">{ligne.quantiteTheorique ?? "—"}</td>
                    <td className="py-2 text-right">{ligne.quantiteReelle ?? "—"}</td>
                    <td className="py-2 text-right">{ligne.ecart ?? "—"}</td>
                  </tr>
                ))}
                {!consommations.length && (
                  <tr>
                    <td colSpan={5} className="py-3 text-center text-xs text-slate-400">
                      Aucune consommation déclarée
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
            {isAdmin && (
              <div className="grid items-end gap-2 sm:grid-cols-[2fr_1fr_1fr_1fr_1fr_auto]">
                <div>
                  <Label className="mb-1 block text-xs">Article *</Label>
                  <Input
                    value={consommation.articleCode}
                    placeholder="FARINE-T55"
                    onChange={(e) => setConsommation((c) => ({ ...c, articleCode: e.target.value }))}
                  />
                </div>
                <div>
                  <Label className="mb-1 block text-xs">Lot</Label>
                  <Input
                    value={consommation.lotNumero}
                    onChange={(e) => setConsommation((c) => ({ ...c, lotNumero: e.target.value }))}
                  />
                </div>
                <div>
                  <Label className="mb-1 block text-xs">Théorique</Label>
                  <Input
                    type="number"
                    min={0}
                    value={consommation.quantiteTheorique}
                    onChange={(e) => setConsommation((c) => ({ ...c, quantiteTheorique: e.target.value }))}
                  />
                </div>
                <div>
                  <Label className="mb-1 block text-xs">Réelle *</Label>
                  <Input
                    type="number"
                    min={0}
                    value={consommation.quantiteReelle}
                    onChange={(e) => setConsommation((c) => ({ ...c, quantiteReelle: e.target.value }))}
                  />
                </div>
                <div>
                  <Label className="mb-1 block text-xs">Prix moyen</Label>
                  <Input
                    type="number"
                    min={0}
                    step="0.01"
                    value={consommation.prixMoyen}
                    onChange={(e) => setConsommation((c) => ({ ...c, prixMoyen: e.target.value }))}
                  />
                </div>
                <Button
                  size="sm"
                  variant="outline"
                  disabled={busy || statut === "ANNULEE" || statut === "VALIDEE"}
                  onClick={declarerConsommation}
                >
                  <Plus className="h-4 w-4" />
                </Button>
              </div>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="p-4 pb-2">
            <CardTitle className="text-base">Pertes valorisées</CardTitle>
          </CardHeader>
          <CardContent className="p-4 pt-2">
            <table className="mb-3 w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="py-2">Article</th>
                  <th className="py-2">Type</th>
                  <th className="py-2 text-right">Quantité</th>
                  <th className="py-2 text-right">Valorisation</th>
                  <th className="py-2">Motif</th>
                </tr>
              </thead>
              <tbody>
                {pertes.map((perteRow) => (
                  <tr key={perteRow.idPerte} className="border-b border-border last:border-0">
                    <td className="py-2 font-mono text-xs">{perteRow.articleCode ?? "—"}</td>
                    <td className="py-2">{humanize(perteRow.typePerte)}</td>
                    <td className="py-2 text-right">{perteRow.quantite ?? "—"}</td>
                    <td className="py-2 text-right">{perteRow.valorisation ?? "—"}</td>
                    <td className="py-2 text-xs">{perteRow.motif ?? "—"}</td>
                  </tr>
                ))}
                {!pertes.length && (
                  <tr>
                    <td colSpan={5} className="py-3 text-center text-xs text-slate-400">
                      Aucune perte déclarée
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
            {isAdmin && (
              <div className="grid items-end gap-2 sm:grid-cols-[2fr_1fr_1.2fr_1.2fr_1fr_auto]">
                <div>
                  <Label className="mb-1 block text-xs">Article *</Label>
                  <Input
                    value={perte.articleCode}
                    placeholder="PATE-FERMENTEE"
                    onChange={(e) => setPerte((p) => ({ ...p, articleCode: e.target.value }))}
                  />
                </div>
                <div>
                  <Label className="mb-1 block text-xs">Quantité *</Label>
                  <Input
                    type="number"
                    min={0}
                    value={perte.quantite}
                    onChange={(e) => setPerte((p) => ({ ...p, quantite: e.target.value }))}
                  />
                </div>
                <div>
                  <Label className="mb-1 block text-xs">Type *</Label>
                  <Select value={perte.typePerte} onChange={(e) => setPerte((p) => ({ ...p, typePerte: e.target.value }))}>
                    {enumOptions(TYPE_PERTE).map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </Select>
                </div>
                <div>
                  <Label className="mb-1 block text-xs">Motif</Label>
                  <Input
                    value={perte.motif}
                    onChange={(e) => setPerte((p) => ({ ...p, motif: e.target.value }))}
                  />
                </div>
                <div>
                  <Label className="mb-1 block text-xs">Prix moyen</Label>
                  <Input
                    type="number"
                    min={0}
                    step="0.01"
                    value={perte.prixMoyen}
                    onChange={(e) => setPerte((p) => ({ ...p, prixMoyen: e.target.value }))}
                  />
                </div>
                <Button
                  size="sm"
                  variant="outline"
                  disabled={busy || statut === "ANNULEE" || statut === "VALIDEE"}
                  onClick={declarerPerte}
                >
                  <Trash2 className="h-4 w-4 text-danger" />
                </Button>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
