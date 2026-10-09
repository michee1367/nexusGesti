"use client";

import { useMemo, useState } from "react";
import Link from "next/link";
import { useParams } from "next/navigation";
import { toast } from "sonner";
import { ArrowLeft, AlertTriangle, Send } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Select } from "@/components/ui/select";
import {
  PageHeader,
  LoadingView,
  ErrorState,
  EmptyState,
} from "@/components/common/async-state";
import { formatInstant } from "@/components/pedagogie/format";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { peopleApi, structureApi } from "@/services/api";

export default function EmploiDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();

  const emploi = useApi(() => structureApi.emploisDuTemps.get(id), [id, tenantId]);
  const cours = useApi(structureApi.cours.list, [tenantId]);
  const creneaux = useApi(structureApi.creneaux.list, [tenantId]);
  const salles = useApi(structureApi.salles.list, [tenantId]);
  const enseignants = useApi(peopleApi.enseignants.list, [tenantId]);

  const [plage, setPlage] = useState({ coursId: "", creneauId: "", salleId: "", enseignantId: "" });
  const [busy, setBusy] = useState(false);

  const labels = useMemo(() => {
    const coursMap = new Map((cours.data ?? []).map((item) => [item.idCours, item.libelle]));
    const creneauMap = new Map((creneaux.data ?? []).map((item) => [item.idCreneau, `${item.jour} (${item.ordre})`]));
    const salleMap = new Map((salles.data ?? []).map((item) => [item.idSalle, item.libelle]));
    const enseignantMap = new Map((enseignants.data ?? []).map((item) => [item.id, `${item.prenom} ${item.nom}`]));
    return { coursMap, creneauMap, salleMap, enseignantMap };
  }, [cours.data, creneaux.data, salles.data, enseignants.data]);

  const reloadAll = () => emploi.reload();

  const ajouter = async () => {
    setBusy(true);
    try {
      await structureApi.emploisDuTemps.ajouterPlage(id, {
        coursId: plage.coursId,
        creneauId: plage.creneauId,
        salleId: plage.salleId || null,
        enseignantId: plage.enseignantId || null,
      });
      toast.success("Plage ajoutée");
      setPlage({ coursId: "", creneauId: "", salleId: "", enseignantId: "" });
      reloadAll();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  const publier = async () => {
    setBusy(true);
    try {
      await structureApi.emploisDuTemps.publier(id);
      toast.success("Emploi du temps publié");
      reloadAll();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  if (emploi.loading) return <LoadingView label="Chargement de l'emploi du temps…" />;
  if (emploi.error) return <ErrorState error={emploi.error} onRetry={emploi.reload} />;
  const data = emploi.data!;

  return (
    <div>
      <PageHeader title="Emploi du temps" description={`Classe ${data.classeId}`}>
        <Link href="/pedagogie/emplois-du-temps">
          <Button variant="outline" size="sm">
            <ArrowLeft className="h-4 w-4" />
            Retour
          </Button>
        </Link>
        {isAdmin && !data.publie && (
          <Button size="sm" disabled={busy} onClick={publier}>
            <Send className="h-4 w-4" />
            Publier
          </Button>
        )}
      </PageHeader>

      <div className="mb-4 flex flex-wrap items-center gap-2">
        <Badge variant="outline">Version v{data.version}</Badge>
        {data.publie ? <Badge variant="success">Publié</Badge> : <Badge variant="warning">Brouillon</Badge>}
        {data.active && <Badge variant="secondary">Actif</Badge>}
        {data.dateActivation && <Badge variant="outline">Activé {formatInstant(data.dateActivation)}</Badge>}
      </div>

      {data.conflits?.length > 0 && (
        <Card className="mb-4 border-danger/40">
          <CardHeader className="p-4 pb-2">
            <CardTitle className="flex items-center gap-2 text-base text-danger">
              <AlertTriangle className="h-4 w-4" />
              {data.conflits.length} conflit(s) détecté(s)
            </CardTitle>
          </CardHeader>
          <CardContent className="flex flex-wrap gap-2 p-4 pt-2">
            {data.conflits.map((conflit, index) => (
              <Badge key={conflit.idConflit ?? index} variant="danger">
                {conflit.type.replace(/_/g, " ")}
              </Badge>
            ))}
          </CardContent>
        </Card>
      )}

      <Card className="mb-4">
        <CardHeader className="p-4 pb-2">
          <CardTitle className="text-base">Plages ({data.plages?.length ?? 0})</CardTitle>
        </CardHeader>
        <CardContent className="overflow-x-auto p-4 pt-2">
          {data.plages?.length ? (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-3 py-2">Cours</th>
                  <th className="px-3 py-2">Créneau</th>
                  <th className="px-3 py-2">Salle</th>
                  <th className="px-3 py-2">Enseignant</th>
                </tr>
              </thead>
              <tbody>
                {data.plages.map((item, index) => (
                  <tr key={item.idPlage ?? index} className="border-b border-border last:border-0">
                    <td className="px-3 py-2">{labels.coursMap.get(item.coursId) ?? item.coursId}</td>
                    <td className="px-3 py-2">{labels.creneauMap.get(item.creneauId) ?? item.creneauId}</td>
                    <td className="px-3 py-2">{item.salleId ? labels.salleMap.get(item.salleId) ?? item.salleId : "—"}</td>
                    <td className="px-3 py-2">
                      {item.enseignantId ? labels.enseignantMap.get(item.enseignantId) ?? item.enseignantId : "—"}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <EmptyState label="Aucune plage" />
          )}
        </CardContent>
      </Card>

      {isAdmin && (
        <Card>
          <CardHeader className="p-4 pb-2">
            <CardTitle className="text-base">Ajouter une plage</CardTitle>
          </CardHeader>
          <CardContent className="p-4 pt-2">
            <form
              className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4"
              onSubmit={(event) => {
                event.preventDefault();
                ajouter();
              }}
            >
              <div>
                <Label className="mb-1 block">Cours</Label>
                <Select value={plage.coursId} onChange={(e) => setPlage({ ...plage, coursId: e.target.value })}>
                  <option value="">—</option>
                  {(cours.data ?? []).map((item) => (
                    <option key={item.idCours} value={item.idCours}>
                      {item.libelle}
                    </option>
                  ))}
                </Select>
              </div>
              <div>
                <Label className="mb-1 block">Créneau</Label>
                <Select value={plage.creneauId} onChange={(e) => setPlage({ ...plage, creneauId: e.target.value })}>
                  <option value="">—</option>
                  {(creneaux.data ?? []).map((item) => (
                    <option key={item.idCreneau} value={item.idCreneau}>
                      {item.jour} — ordre {item.ordre}
                    </option>
                  ))}
                </Select>
              </div>
              <div>
                <Label className="mb-1 block">Salle</Label>
                <Select value={plage.salleId} onChange={(e) => setPlage({ ...plage, salleId: e.target.value })}>
                  <option value="">—</option>
                  {(salles.data ?? []).map((item) => (
                    <option key={item.idSalle} value={item.idSalle}>
                      {item.libelle}
                    </option>
                  ))}
                </Select>
              </div>
              <div>
                <Label className="mb-1 block">Enseignant</Label>
                <Select
                  value={plage.enseignantId}
                  onChange={(e) => setPlage({ ...plage, enseignantId: e.target.value })}
                >
                  <option value="">—</option>
                  {(enseignants.data ?? []).map((item) => (
                    <option key={item.id} value={item.id}>
                      {item.prenom} {item.nom}
                    </option>
                  ))}
                </Select>
              </div>
              <div className="lg:col-span-4">
                <Button type="submit" disabled={busy || !plage.coursId || !plage.creneauId}>
                  Ajouter la plage
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
