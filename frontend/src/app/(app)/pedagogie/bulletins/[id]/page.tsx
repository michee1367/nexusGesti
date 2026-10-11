"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import {
  ArrowLeft,
  Calculator,
  CheckCheck,
  FileSignature,
  Minus,
  Printer,
  Receipt,
  Save,
  Scale,
  Send,
  UnfoldVertical,
} from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { PageHeader, LoadingView, ErrorState, EmptyState } from "@/components/common/async-state";
import { formatDate, humanize } from "@/components/pedagogie/format";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { assessmentApi } from "@/services/api";
import type { CouponNotes } from "@/types";

export default function BulletinDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();

  const bulletin = useApi(() => assessmentApi.bulletins.get(id), [id, tenantId]);
  const coupons = useApi(() => assessmentApi.bulletins.coupons(id), [id, tenantId]);
  const [busy, setBusy] = useState(false);

  const [ligne, setLigne] = useState({ libelle: "", code: "", coefficient: "1", moyennePeriode: "0" });
  const [observation, setObservation] = useState("");

  const data = bulletin.data;

  const reload = () => {
    bulletin.reload();
    coupons.reload();
  };

  const transition = async (action: "calculer" | "publier" | "generer" | "signer") => {
    setBusy(true);
    try {
      await assessmentApi.bulletins[action](id);
      toast.success("Transition effectuée");
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  const rectifier = async () => {
    const motif = prompt("Motif de la rectification :");
    if (motif == null) return;
    setBusy(true);
    try {
      await assessmentApi.bulletins.rectifier(id, motif);
      toast.success("Bulletin rectifié — version incrémentée");
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  const ajouterLigne = async () => {
    setBusy(true);
    try {
      await assessmentApi.bulletins.ajouterLigne(id, {
        libelle: ligne.libelle,
        code: ligne.code,
        coefficient: Number(ligne.coefficient),
        moyennePeriode: Number(ligne.moyennePeriode),
      });
      toast.success("Ligne ajoutée");
      setLigne({ libelle: "", code: "", coefficient: "1", moyennePeriode: "0" });
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  const coupier = async () => {
    if (!observation) {
      toast.error("Renseignez une observation");
      return;
    }
    setBusy(true);
    try {
      await assessmentApi.bulletins.creerCoupon(id, observation);
      toast.success("Coupon créé");
      setObservation("");
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  const transitionCoupon = async (coupon: CouponNotes, action: "tirer" | "detacher" | "signer" | "rendre") => {
    if (!coupon.id) return;
    setBusy(true);
    try {
      await assessmentApi.bulletins[`${action}Coupon` as const](id, coupon.id);
      toast.success("Coupon mis à jour");
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  if (bulletin.loading) return <LoadingView label="Chargement du bulletin…" />;
  if (bulletin.error) return <ErrorState error={bulletin.error} onRetry={reload} />;

  const statut = data?.statut ?? "";

  return (
    <div>
      <PageHeader title="Bulletin" description={`${data?.reference ?? "—"} · ${humanize(data?.type ?? "")}`}>
        <Link href="/pedagogie/bulletins">
          <Button variant="outline" size="sm">
            <ArrowLeft className="h-4 w-4" />
            Retour
          </Button>
        </Link>
        {isAdmin && ["BROUILLON", "RECTIFIE"].includes(statut) && (
          <Button size="sm" disabled={busy} onClick={() => transition("calculer")}>
            <Calculator className="h-4 w-4" />
            Calculer
          </Button>
        )}
        {isAdmin && statut === "CALCULE" && (
          <>
            <Button size="sm" variant="outline" disabled={busy} onClick={() => transition("publier")}>
              <Send className="h-4 w-4" />
              Publier
            </Button>
            <Button size="sm" variant="outline" disabled={busy} onClick={() => transition("generer")}>
              <Printer className="h-4 w-4" />
              Générer
            </Button>
          </>
        )}
        {isAdmin && ["PUBLIE", "GENERE"].includes(statut) && (
          <Button size="sm" disabled={busy} onClick={() => transition("signer")}>
            <FileSignature className="h-4 w-4" />
            Signer
          </Button>
        )}
        {isAdmin && ["CALCULE", "PUBLIE", "GENERE", "SIGNE"].includes(statut) && (
          <Button size="sm" variant="outline" disabled={busy} onClick={rectifier}>
            <Minus className="h-4 w-4" />
            Rectifier
          </Button>
        )}
      </PageHeader>

      <div className="mb-4 flex flex-wrap items-center gap-2">
        <Badge variant="outline">{humanize(statut || "—")}</Badge>
        <Badge variant="outline">Version v{data?.version}</Badge>
        {data?.moyenneGenerale != null && <Badge variant="success">Moyenne {data.moyenneGenerale}</Badge>}
        {data?.rang != null && <Badge variant="outline">Rang {data.rang}/{data.effectif ?? "—"}</Badge>}
        {data?.mentionGenerale && <Badge variant="outline">{data.mentionGenerale}</Badge>}
      </div>

      <Card className="mb-4">
        <CardHeader className="p-4 pb-2">
          <CardTitle className="text-base">Lignes ({data?.lignes?.length ?? 0})</CardTitle>
        </CardHeader>
        <CardContent className="overflow-x-auto p-4 pt-2">
          {data?.lignes?.length ? (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-3 py-2">Code</th>
                  <th className="px-3 py-2">Libellé</th>
                  <th className="px-3 py-2">Coef</th>
                  <th className="px-3 py-2">Poids</th>
                  <th className="px-3 py-2">Moyenne période</th>
                  <th className="px-3 py-2">Moyenne annuelle</th>
                  <th className="px-3 py-2">Rang</th>
                  <th className="px-3 py-2">Mention</th>
                </tr>
              </thead>
              <tbody>
                {data.lignes.map((item) => (
                  <tr key={item.id ?? item.code} className="border-b border-border last:border-0">
                    <td className="px-3 py-2 font-mono text-xs">{item.code ?? "—"}</td>
                    <td className="px-3 py-2">{item.libelle ?? "—"}</td>
                    <td className="px-3 py-2">{item.coefficient}</td>
                    <td className="px-3 py-2">{item.poidsNormalise ?? "—"}</td>
                    <td className="px-3 py-2">{item.moyennePeriode ?? "—"}</td>
                    <td className="px-3 py-2">{item.moyenneAnnuelle ?? "—"}</td>
                    <td className="px-3 py-2">{item.rang ?? "—"}</td>
                    <td className="px-3 py-2">{item.mention ?? "—"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <EmptyState label="Aucune ligne" />
          )}
        </CardContent>
      </Card>

      {isAdmin && ["BROUILLON", "RECTIFIE"].includes(statut) && (
        <Card className="mb-4">
          <CardHeader className="p-4 pb-2">
            <CardTitle className="text-base">Ajouter une ligne</CardTitle>
          </CardHeader>
          <CardContent className="p-4 pt-2">
            <form
              className="grid gap-3 sm:grid-cols-4"
              onSubmit={(event) => {
                event.preventDefault();
                ajouterLigne();
              }}
            >
              <div>
                <Label className="mb-1 block">Libellé</Label>
                <Input value={ligne.libelle} onChange={(event) => setLigne({ ...ligne, libelle: event.target.value })} />
              </div>
              <div>
                <Label className="mb-1 block">Code</Label>
                <Input value={ligne.code} onChange={(event) => setLigne({ ...ligne, code: event.target.value })} />
              </div>
              <div>
                <Label className="mb-1 block">Coefficient</Label>
                <Input type="number" value={ligne.coefficient} onChange={(event) => setLigne({ ...ligne, coefficient: event.target.value })} />
              </div>
              <div>
                <Label className="mb-1 block">Moyenne période</Label>
                <Input type="number" step="0.01" value={ligne.moyennePeriode} onChange={(event) => setLigne({ ...ligne, moyennePeriode: event.target.value })} />
              </div>
              <div className="sm:col-span-4">
                <Button type="submit" disabled={busy || !ligne.libelle}>
                  <Save className="h-4 w-4" />
                  Ajouter
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      )}

      <Card>
        <CardHeader className="p-4 pb-2">
          <CardTitle className="flex items-center gap-2 text-base">
            <Receipt className="h-4 w-4" />
            Coupons de notes ({coupons.data?.length ?? 0})
          </CardTitle>
        </CardHeader>
        <CardContent className="p-4 pt-2">
          {isAdmin && (
            <div className="mb-3 flex items-end gap-2">
              <div className="flex-1">
                <Label className="mb-1 block">Observation du coupon</Label>
                <Input value={observation} onChange={(event) => setObservation(event.target.value)} />
              </div>
              <Button variant="outline" disabled={busy || !observation} onClick={coupier}>
                <Receipt className="h-4 w-4" />
                Créer
              </Button>
            </div>
          )}
          {coupons.data?.length ? (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-3 py-2">Numéro</th>
                  <th className="px-3 py-2">Moyenne</th>
                  <th className="px-3 py-2">Mention</th>
                  <th className="px-3 py-2">Édité le</th>
                  <th className="px-3 py-2">Statut</th>
                  <th className="px-3 py-2">Empreinte</th>
                  {isAdmin && <th className="px-3 py-2 text-right">Actions</th>}
                </tr>
              </thead>
              <tbody>
                {coupons.data.map((coupon) => (
                  <tr key={coupon.id ?? coupon.numero} className="border-b border-border last:border-0">
                    <td className="px-3 py-2 font-mono text-xs">{coupon.numero ?? "—"}</td>
                    <td className="px-3 py-2">{coupon.noteMoyenne ?? "—"}</td>
                    <td className="px-3 py-2">{coupon.mention ?? "—"}</td>
                    <td className="px-3 py-2">{formatDate(coupon.dateEdition)}</td>
                    <td className="px-3 py-2">{humanize(coupon.statut)}</td>
                    <td className="px-3 py-2 font-mono text-[10px] text-slate-400">{coupon.empreinte?.slice(0, 16)}…</td>
                    {isAdmin && (
                      <td className="px-3 py-2">
                        <div className="flex justify-end gap-1">
                          {["A_TIRER"].includes(coupon.statut) && (
                            <Button variant="ghost" size="sm" title="Tirer" onClick={() => transitionCoupon(coupon, "tirer")}>
                              <UnfoldVertical className="h-4 w-4" />
                            </Button>
                          )}
                          {["TIRE"].includes(coupon.statut) && (
                            <>
                              <Button variant="ghost" size="sm" title="Détacher" onClick={() => transitionCoupon(coupon, "detacher")}>
                                <Scale className="h-4 w-4" />
                              </Button>
                              <Button variant="ghost" size="sm" title="Rendre" onClick={() => transitionCoupon(coupon, "rendre")}>
                                <CheckCheck className="h-4 w-4" />
                              </Button>
                            </>
                          )}
                          {["DETACHE"].includes(coupon.statut) && (
                            <Button variant="ghost" size="sm" title="Signer" onClick={() => transitionCoupon(coupon, "signer")}>
                              <FileSignature className="h-4 w-4" />
                            </Button>
                          )}
                        </div>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <EmptyState label="Aucun coupon" />
          )}
        </CardContent>
      </Card>
    </div>
  );
}