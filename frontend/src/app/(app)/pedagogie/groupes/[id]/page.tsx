"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import { ArrowLeft, UserMinus, UserPlus } from "lucide-react";
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
import { enrollmentApi } from "@/services/api";

export default function GroupeDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();

  const groupe = useApi(() => enrollmentApi.groupes.get(id), [id, tenantId]);
  const [eleveId, setEleveId] = useState("");
  const [busy, setBusy] = useState(false);

  const ajouter = async () => {
    if (!eleveId) return;
    setBusy(true);
    try {
      await enrollmentApi.groupes.ajouterMembre(id, { eleveId, rang: 1 });
      toast.success("Membre ajouté");
      setEleveId("");
      groupe.reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  const exclure = async (membreId: string) => {
    setBusy(true);
    try {
      await enrollmentApi.groupes.exclureMembre(id, membreId);
      toast.success("Membre exclu");
      groupe.reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  const ouvrir = async () => {
    setBusy(true);
    try {
      await enrollmentApi.groupes.ouvrir(id);
      toast.success("Groupe rouvert");
      groupe.reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  const clore = async () => {
    setBusy(true);
    try {
      await enrollmentApi.groupes.clore(id);
      toast.success("Groupe clos");
      groupe.reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  if (groupe.loading) return <LoadingView label="Chargement du groupe…" />;
  if (groupe.error) return <ErrorState error={groupe.error} onRetry={groupe.reload} />;
  const data = groupe.data!;

  return (
    <div>
      <PageHeader title={data.libelle} description={`Groupe ${humanize(data.type)} · ${data.code}`}>
        <Link href="/pedagogie/groupes">
          <Button variant="outline" size="sm">
            <ArrowLeft className="h-4 w-4" />
            Retour
          </Button>
        </Link>
        {isAdmin && data.statut === "OUVERT" && (
          <Button size="sm" disabled={busy} onClick={clore}>
            Clore
          </Button>
        )}
        {isAdmin && ["COMPLET", "CLOS"].includes(data.statut ?? "") && (
          <Button size="sm" variant="outline" disabled={busy} onClick={ouvrir}>
            Rouvrir
          </Button>
        )}
      </PageHeader>

      <div className="mb-4 flex flex-wrap items-center gap-2">
        <Badge variant="outline">Capacité {data.capacite}</Badge>
        <Badge variant="outline">Effectif {data.effectif}</Badge>
        {data.placesRestantes > 0 ? (
          <Badge variant="success">{data.placesRestantes} place(s) restante(s)</Badge>
        ) : (
          <Badge variant="danger">Plein</Badge>
        )}
        <Badge variant={data.statut === "OUVERT" ? "success" : "outline"}>{humanize(data.statut ?? "—")}</Badge>
      </div>

      <Card className="mb-4">
        <CardHeader className="p-4 pb-2">
          <CardTitle className="text-base">Membres ({data.membres?.length ?? 0})</CardTitle>
        </CardHeader>
        <CardContent className="overflow-x-auto p-4 pt-2">
          {data.membres?.length ? (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-3 py-2">Élève</th>
                  <th className="px-3 py-2">Rang</th>
                  <th className="px-3 py-2">Affecté le</th>
                  <th className="px-3 py-2">Par</th>
                  {isAdmin && <th className="px-3 py-2 text-right">Actions</th>}
                </tr>
              </thead>
              <tbody>
                {data.membres.map((membre) => (
                  <tr key={membre.idMembre} className="border-b border-border last:border-0">
                    <td className="px-3 py-2 font-mono text-xs">{membre.eleveId?.slice(0, 8)}…</td>
                    <td className="px-3 py-2">{membre.rang ?? "—"}</td>
                    <td className="px-3 py-2">{formatDate(membre.dateAffectation)}</td>
                    <td className="px-3 py-2 font-mono text-xs">{membre.affectePar?.slice(0, 8) ?? "—"}</td>
                    {isAdmin && (
                      <td className="px-3 py-2">
                        <div className="flex justify-end">
                          <Button variant="ghost" size="sm" disabled={busy} onClick={() => exclure(membre.idMembre)}>
                            <UserMinus className="h-4 w-4" />
                          </Button>
                        </div>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <EmptyState label="Aucun membre" />
          )}
        </CardContent>
      </Card>

      {isAdmin && data.statut === "OUVERT" && (
        <Card>
          <CardHeader className="p-4 pb-2">
            <CardTitle className="text-base">Ajouter un membre</CardTitle>
          </CardHeader>
          <CardContent className="p-4 pt-2">
            <form
              className="flex items-end gap-2"
              onSubmit={(event) => {
                event.preventDefault();
                ajouter();
              }}
            >
              <div className="flex-1">
                <Label className="mb-1 block">Élève (UUID)</Label>
                <Input value={eleveId} onChange={(event) => setEleveId(event.target.value)} placeholder="121f5678-…" />
              </div>
              <Button type="submit" disabled={busy || !eleveId}>
                <UserPlus className="h-4 w-4" />
                Ajouter
              </Button>
            </form>
          </CardContent>
        </Card>
      )}
    </div>
  );
}