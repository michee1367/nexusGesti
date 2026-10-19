"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import { ArrowLeft, Check, CheckCheck, Clock, FileCheck2, UserMinus, UserPlus } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select } from "@/components/ui/select";
import { PageHeader, LoadingView, ErrorState, EmptyState } from "@/components/common/async-state";
import { EntitySelect } from "@/components/common/entity-select";
import { enumOptions, STATUT_PRESENCE, TYPE_ABSENCE } from "@/components/pedagogie/constants";
import { formatDate, humanize } from "@/components/pedagogie/format";
import { eleveLabel, lookupLabel } from "@/components/pedagogie/labels";
import { useApi } from "@/hooks/use-api";
import { useElevesMap } from "@/hooks/use-lookup";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { attendanceApi, peopleApi } from "@/services/api";
import type { FeuillePresence, PresenceEleve } from "@/types";

function presenceBadge(statut: string | null | undefined) {
  const map: Record<string, "success" | "danger" | "warning" | "outline"> = {
    PRESENT: "success",
    RETARD: "warning",
    ABSENT: "danger",
    ABSENT_NON_EXCUSE: "danger",
    ABSENT_EXCUSE: "outline",
    CONGE: "outline",
    MALADE: "warning",
    SORTIE_AUTORISEE: "outline",
  };
  return map[statut ?? ""] ?? "outline";
}

export default function AppelDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const eleves = useElevesMap([tenantId]);

  const feuille = useApi(() => attendanceApi.appels.get(id), [id, tenantId]);
  const [busy, setBusy] = useState(false);

  const [pointerEleveId, setPointerEleveId] = useState("");
  const [pointerStatut, setPointerStatut] = useState("PRESENT");
  const [absenceEleveId, setAbsenceEleveId] = useState("");
  const [absenceType, setAbsenceType] = useState("INJUSTIFIEE");
  const [retardEleveId, setRetardEleveId] = useState("");
  const [retardMinutes, setRetardMinutes] = useState(10);

  const data: FeuillePresence | null = feuille.data ?? null;

  const reload = () => feuille.reload();

  const pointer = async () => {
    if (!pointerEleveId) return;
    setBusy(true);
    try {
      await attendanceApi.appels.pointer(id, { eleveId: pointerEleveId, statut: pointerStatut as PresenceEleve["statut"] });
      toast.success("Présence pointée");
      setPointerEleveId("");
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  const marquerAbsence = async () => {
    if (!absenceEleveId) return;
    setBusy(true);
    try {
      await attendanceApi.appels.absence(id, { eleveId: absenceEleveId, typeAbsence: absenceType as PresenceEleve["typeAbsence"] });
      toast.success("Absence enregistrée");
      setAbsenceEleveId("");
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  const marquerRetard = async () => {
    if (!retardEleveId) return;
    setBusy(true);
    try {
      await attendanceApi.appels.retard(id, { eleveId: retardEleveId, minutes: retardMinutes });
      toast.success("Retard enregistré");
      setRetardEleveId("");
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  const transition = async (action: "soumettre" | "valider" | "cloturer") => {
    setBusy(true);
    try {
      await attendanceApi.appels[action](id);
      toast.success(action === "soumettre" ? "Feuille soumise" : action === "valider" ? "Feuille validée" : "Feuille clôturée");
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  if (feuille.loading) return <LoadingView label="Chargement de la feuille…" />;
  if (feuille.error) return <ErrorState error={feuille.error} onRetry={reload} />;

  const editable = isAdmin && !!data && ["BROUILLON", "SOUMISE"].includes(data.statut);

  return (
    <div>
      <PageHeader title="Feuille d'appel" description={`Statut ${humanize(data?.statut ?? "—")}`}>
        <Link href="/pedagogie/appels">
          <Button variant="outline" size="sm">
            <ArrowLeft className="h-4 w-4" />
            Retour
          </Button>
        </Link>
        {isAdmin && data && data.statut === "BROUILLON" && (
          <Button size="sm" variant="outline" disabled={busy} onClick={() => transition("soumettre")}>
            <FileCheck2 className="h-4 w-4" />
            Soumettre
          </Button>
        )}
        {isAdmin && data && data.statut === "SOUMISE" && (
          <Button size="sm" variant="outline" disabled={busy} onClick={() => transition("valider")}>
            <CheckCheck className="h-4 w-4" />
            Valider
          </Button>
        )}
        {isAdmin && data && ["SOUMISE", "VALIDEE"].includes(data.statut) && (
          <Button size="sm" disabled={busy} onClick={() => transition("cloturer")}>
            <Check className="h-4 w-4" />
            Clôturer
          </Button>
        )}
      </PageHeader>

      {data && (
        <div className="mb-4 flex flex-wrap items-center gap-2">
          <Badge variant="outline">Date {formatDate(data.date)}</Badge>
          <Badge variant="outline">Effectif {data.effectifPresent ?? 0}/{data.effectifTheorique ?? "—"}</Badge>
          <Badge variant="outline">Taux {data.tauxPresence != null ? `${data.tauxPresence} %` : "—"}</Badge>
          <Badge variant={data.nbAbsents > 0 ? "danger" : "success"}>{data.nbAbsents} absent(s)</Badge>
        </div>
      )}

      <Card className="mb-4">
        <CardHeader className="p-4 pb-2">
          <CardTitle className="text-base">Présences ({data?.presences?.length ?? 0})</CardTitle>
        </CardHeader>
        <CardContent className="overflow-x-auto p-4 pt-2">
          {data?.presences?.length ? (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-3 py-2">Élève</th>
                  <th className="px-3 py-2">Statut</th>
                  <th className="px-3 py-2">Retard</th>
                  <th className="px-3 py-2">Justifié</th>
                  <th className="px-3 py-2">Type</th>
                  <th className="px-3 py-2">Saisi par</th>
                </tr>
              </thead>
              <tbody>
                {data.presences.map((presence) => (
                  <tr key={presence.idPresence} className="border-b border-border last:border-0">
                    <td className="px-3 py-2 text-xs">{lookupLabel(eleves, presence.eleveId)}</td>
                    <td className="px-3 py-2">
                      <Badge variant={presenceBadge(presence.statut)}>{humanize(presence.statut ?? "—")}</Badge>
                    </td>
                    <td className="px-3 py-2">{presence.minutesRetard > 0 ? `${presence.minutesRetard} min` : "—"}</td>
                    <td className="px-3 py-2">{presence.justifiee ? <Badge variant="success">Oui</Badge> : <span className="text-slate-400">Non</span>}</td>
                    <td className="px-3 py-2">{presence.typeAbsence ? humanize(presence.typeAbsence) : "—"}</td>
                    <td className="px-3 py-2 font-mono text-xs">{presence.saisiePar?.slice(0, 8) ?? "—"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <EmptyState label="Aucune présence pointée" />
          )}
        </CardContent>
      </Card>

      {editable && (
        <div className="grid gap-4 lg:grid-cols-3">
          <Card>
            <CardHeader className="p-4 pb-2">
              <CardTitle className="flex items-center gap-2 text-base">
                <UserPlus className="h-4 w-4" />
                Pointer
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-3 p-4 pt-2">
              <div>
                <Label className="mb-1 block">Élève</Label>
                <EntitySelect
                  fetcher={() => peopleApi.eleves.list()}
                  keyOf={(eleve) => eleve.idEleve}
                  labelOf={eleveLabel}
                  value={pointerEleveId}
                  onChange={setPointerEleveId}
                  deps={[tenantId]}
                  placeholder="Sélectionner un élève…"
                />
              </div>
              <div>
                <Label className="mb-1 block">Statut</Label>
                <Select value={pointerStatut} onChange={(event) => setPointerStatut(event.target.value)}>
                  {enumOptions(STATUT_PRESENCE).map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </Select>
              </div>
              <Button disabled={busy || !pointerEleveId} onClick={pointer}>
                Pointer
              </Button>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="p-4 pb-2">
              <CardTitle className="flex items-center gap-2 text-base">
                <UserMinus className="h-4 w-4" />
                Absence
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-3 p-4 pt-2">
              <div>
                <Label className="mb-1 block">Élève</Label>
                <EntitySelect
                  fetcher={() => peopleApi.eleves.list()}
                  keyOf={(eleve) => eleve.idEleve}
                  labelOf={eleveLabel}
                  value={absenceEleveId}
                  onChange={setAbsenceEleveId}
                  deps={[tenantId]}
                  placeholder="Sélectionner un élève…"
                />
              </div>
              <div>
                <Label className="mb-1 block">Type d&apos;absence</Label>
                <Select value={absenceType} onChange={(event) => setAbsenceType(event.target.value)}>
                  {enumOptions(TYPE_ABSENCE).map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </Select>
              </div>
              <Button disabled={busy || !absenceEleveId} onClick={marquerAbsence}>
                Enregistrer
              </Button>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="p-4 pb-2">
              <CardTitle className="flex items-center gap-2 text-base">
                <Clock className="h-4 w-4" />
                Retard
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-3 p-4 pt-2">
              <div>
                <Label className="mb-1 block">Élève</Label>
                <EntitySelect
                  fetcher={() => peopleApi.eleves.list()}
                  keyOf={(eleve) => eleve.idEleve}
                  labelOf={eleveLabel}
                  value={retardEleveId}
                  onChange={setRetardEleveId}
                  deps={[tenantId]}
                  placeholder="Sélectionner un élève…"
                />
              </div>
              <div>
                <Label className="mb-1 block">Minutes de retard</Label>
                <Input type="number" value={retardMinutes} onChange={(event) => setRetardMinutes(Number(event.target.value))} />
              </div>
              <Button disabled={busy || !retardEleveId} onClick={marquerRetard}>
                Enregistrer
              </Button>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}