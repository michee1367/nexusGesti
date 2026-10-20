"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import { ArrowLeft, Ban, CheckCircle2, ShieldCheck } from "lucide-react";
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
  ATTENDUE: "outline",
  EN_CONTROLE: "warning",
  ACCEPTEE: "success",
  REFUSEE: "danger",
  PARTIELLE: "warning",
};

export default function ReceptionDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const reception = useApi(() => boulangerieApi.receptions.get(id), [id, tenantId]);
  const [busy, setBusy] = useState(false);
  const [motif, setMotif] = useState("");
  const [showRefus, setShowRefus] = useState(false);

  const reload = () => reception.reload();

  const transition = async (action: () => Promise<unknown>, message: string) => {
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

  const refuser = async () => {
    if (!motif.trim()) {
      toast.error("Un motif de refus est obligatoire");
      return;
    }
    await transition(async () => {
      await boulangerieApi.receptions.refuser(id, motif.trim());
      setMotif("");
      setShowRefus(false);
    }, "Réception refusée");
  };

  if (reception.loading) return <LoadingView label="Chargement de la réception…" />;
  if (reception.error) return <ErrorState error={reception.error} onRetry={reload} />;

  const r = reception.data;
  if (!r) return <ErrorState error={new ApiError(404, "Réception introuvable", "NOT_FOUND")} onRetry={reload} />;
  const statut = r.statut ?? "ATTENDUE";
  const nonConforme = (r.lignes ?? []).some((ligne) => ligne.conforme === false);

  return (
    <div>
      <PageHeader
        title={`Réception ${r.numero ?? ""}`}
        description={`Commande ${r.commandeNumero ?? "—"} — contrôle puis acceptation ou refus (lot 3).`}
      >
        <Link href="/boulangerie/receptions">
          <Button variant="ghost" size="sm">
            <ArrowLeft className="h-4 w-4" />
            Retour
          </Button>
        </Link>
        {isAdmin && (
          <>
            {statut === "ATTENDUE" && (
              <Button
                size="sm"
                disabled={busy}
                onClick={() => transition(() => boulangerieApi.receptions.controle(id), "Réception mise sous contrôle")}
              >
                <ShieldCheck className="h-4 w-4" />
                Contrôler
              </Button>
            )}
            {statut === "EN_CONTROLE" && (
              <Button
                size="sm"
                disabled={busy}
                onClick={() =>
                  transition(() => boulangerieApi.receptions.accepter(id), "Réception acceptée")
                }
              >
                <CheckCircle2 className="h-4 w-4" />
                Accepter
              </Button>
            )}
            {(statut === "ATTENDUE" || statut === "EN_CONTROLE") && (
              <Button variant="outline" size="sm" onClick={() => setShowRefus((v) => !v)}>
                <Ban className="h-4 w-4" />
                Refuser
              </Button>
            )}
          </>
        )}
      </PageHeader>

      {showRefus && (
        <Card className="mb-4">
          <CardContent className="flex flex-wrap items-end gap-3 p-4">
            <div className="min-w-64 flex-1">
              <Label className="mb-1 block">Motif du refus *</Label>
              <Input
                value={motif}
                placeholder="Marchandise abîmée, DLC non conforme…"
                onChange={(e) => setMotif(e.target.value)}
              />
            </div>
            <Button size="sm" variant="destructive" disabled={busy} onClick={refuser}>
              Confirmer le refus
            </Button>
            <Button size="sm" variant="ghost" onClick={() => setShowRefus(false)}>
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
            <p className="text-xs uppercase text-slate-500">Date</p>
            <p className="font-medium">{formatDate(r.date)}</p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 text-sm">
            <p className="text-xs uppercase text-slate-500">Bon de livraison</p>
            <p className="font-mono text-xs">{r.bonLivraison ?? "—"}</p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 text-sm">
            <p className="text-xs uppercase text-slate-500">Conformité</p>
            <Badge variant={nonConforme ? "warning" : "success"}>
              {nonConforme ? "Ligne(s) non conforme(s)" : "Tout est conforme"}
            </Badge>
          </CardContent>
        </Card>
      </div>

      {r.motif && (
        <Card className="mb-4">
          <CardContent className="p-4 text-sm">
            <p className="text-xs uppercase text-slate-500">Motif de refus</p>
            <p>{r.motif}</p>
          </CardContent>
        </Card>
      )}

      <Card>
        <CardHeader>
          <CardTitle>Lignes reçues</CardTitle>
        </CardHeader>
        <CardContent className="overflow-x-auto p-0">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                <th className="px-4 py-3">Article</th>
                <th className="px-4 py-3">Qté reçue</th>
                <th className="px-4 py-3">Prix achat</th>
                <th className="px-4 py-3">Lot</th>
                <th className="px-4 py-3">Péremption</th>
                <th className="px-4 py-3">Conformité</th>
              </tr>
            </thead>
            <tbody>
              {(r.lignes ?? []).map((ligne) => (
                <tr key={ligne.id} className="border-b border-border last:border-0">
                  <td className="px-4 py-3 font-mono font-medium">{ligne.articleCode}</td>
                  <td className="px-4 py-3">{ligne.quantiteRecue ?? 0}</td>
                  <td className="px-4 py-3">{ligne.prixAchat ?? 0}</td>
                  <td className="px-4 py-3 font-mono text-xs">{ligne.lotNumero ?? "—"}</td>
                  <td className="px-4 py-3">{formatDate(ligne.datePeremption)}</td>
                  <td className="px-4 py-3">
                    {ligne.conforme === false ? (
                      <Badge variant="danger" className="mr-2">
                        Non conforme
                      </Badge>
                    ) : (
                      <Badge variant="success" className="mr-2">
                        Conforme
                      </Badge>
                    )}
                    {ligne.motifNonConformite ? (
                      <span className="text-xs text-slate-500">{ligne.motifNonConformite}</span>
                    ) : null}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </CardContent>
      </Card>

      <p className="mt-3 text-xs text-slate-400">
        Une réception non conforme ne peut pas être acceptée (409) — corrigez la ligne ou refusez la réception.
      </p>
    </div>
  );
}
