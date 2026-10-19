"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import { ArrowLeft, Ban, ClipboardPen, FileX2, FolderOpen } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { PageHeader, LoadingView, ErrorState, EmptyState } from "@/components/common/async-state";
import { EntitySelect } from "@/components/common/entity-select";
import { formatDate, formatInstant, formatTime, humanize } from "@/components/pedagogie/format";
import { eleveLabel, lookupLabel } from "@/components/pedagogie/labels";
import { useApi } from "@/hooks/use-api";
import { useElevesMap } from "@/hooks/use-lookup";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { assessmentApi, peopleApi } from "@/services/api";

export default function EvaluationDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const eleves = useElevesMap([tenantId]);

  const evaluation = useApi(() => assessmentApi.evaluations.get(id), [id, tenantId]);
  const notes = useApi(() => assessmentApi.evaluations.notes(id), [id, tenantId]);
  const [busy, setBusy] = useState(false);

  const [noteForm, setNoteForm] = useState({ eleveId: "", valeur: "0", eleveAbsent: "" });
  const [exclusion, setExclusion] = useState({ eleveId: "", motif: "" });
  const [contestation, setContestation] = useState({ eleveId: "", motif: "" });

  const data = evaluation.data;

  const reload = () => {
    evaluation.reload();
    notes.reload();
  };

  const saisir = async () => {
    if (!noteForm.eleveId) return;
    setBusy(true);
    try {
      await assessmentApi.evaluations.saisirNote(id, { eleveId: noteForm.eleveId, valeur: Number(noteForm.valeur) });
      toast.success("Note saisie");
      setNoteForm({ eleveId: "", valeur: "0", eleveAbsent: "" });
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  const marquerAbsent = async () => {
    if (!noteForm.eleveAbsent) return;
    setBusy(true);
    try {
      await assessmentApi.evaluations.marquerAbsent(id, { eleveId: noteForm.eleveAbsent });
      toast.success("Absence enregistrée");
      setNoteForm({ eleveId: "", valeur: "0", eleveAbsent: "" });
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  const exclure = async () => {
    if (!exclusion.eleveId || !exclusion.motif) return;
    setBusy(true);
    try {
      await assessmentApi.evaluations.exclureNote(id, exclusion);
      toast.success("Note exclue");
      setExclusion({ eleveId: "", motif: "" });
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  const contester = async () => {
    if (!contestation.eleveId || !contestation.motif) return;
    setBusy(true);
    try {
      await assessmentApi.evaluations.contesterNote(id, contestation);
      toast.success("Contestation enregistrée");
      setContestation({ eleveId: "", motif: "" });
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  const transition = async (action: "ouvrir" | "cloturer" | "publier") => {
    setBusy(true);
    try {
      await assessmentApi.evaluations[action](id);
      toast.success("Transition effectuée");
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Erreur");
    } finally {
      setBusy(false);
    }
  };

  if (evaluation.loading) return <LoadingView label="Chargement de l'évaluation…" />;
  if (evaluation.error) return <ErrorState error={evaluation.error} onRetry={reload} />;

  const statut = data?.statut ?? "";
  const saisieOuverte = isAdmin && ["OUVERTE", "CLOTUREE"].includes(statut);

  return (
    <div>
      <PageHeader title={data?.libelle ?? "Évaluation"} description={`${data?.reference ?? "—"} · ${humanize(data?.type ?? "")}`}>
        <Link href="/pedagogie/evaluations">
          <Button variant="outline" size="sm">
            <ArrowLeft className="h-4 w-4" />
            Retour
          </Button>
        </Link>
        {isAdmin && ["BROUILLON", "PLANIFIEE"].includes(statut) && (
          <Button size="sm" disabled={busy} onClick={() => transition("ouvrir")}>
            Ouvrir
          </Button>
        )}
        {isAdmin && statut === "OUVERTE" && (
          <Button size="sm" variant="outline" disabled={busy} onClick={() => transition("cloturer")}>
            Clôturer
          </Button>
        )}
        {isAdmin && ["CLOTUREE", "PUBLIEE"].includes(statut) && statut !== "PUBLIEE" && (
          <Button size="sm" disabled={busy} onClick={() => transition("publier")}>
            <FolderOpen className="h-4 w-4" />
            Publier
          </Button>
        )}
      </PageHeader>

      <div className="mb-4 flex flex-wrap items-center gap-2">
        <Badge variant="outline">{humanize(statut || "—")}</Badge>
        <Badge variant="outline">Date {formatDate(data?.date)}</Badge>
        {data?.heureDebut && <Badge variant="outline">Début {formatTime(data.heureDebut)}</Badge>}
        {data?.duree != null && <Badge variant="outline">Durée {data.duree} min</Badge>}
        <Badge variant="outline">Coef {data?.coefficientDeclare ?? "—"}</Badge>
        {data?.grilleVersion != null && <Badge variant="outline">Grille v{data.grilleVersion}</Badge>}
      </div>

      <Card className="mb-4">
        <CardHeader className="p-4 pb-2">
          <CardTitle className="text-base">
            Notes ({notes.data?.length ?? 0}){" "}
            {data?.feuille && (
              <span className="ml-2 text-sm font-normal text-slate-500">
                — {data.feuille.nbNotesSaisies}/{data.feuille.nbNotesAttendues} saisies · progression {data.feuille.progression}%
              </span>
            )}
          </CardTitle>
        </CardHeader>
        <CardContent className="overflow-x-auto p-4 pt-2">
          {notes.data?.length ? (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-3 py-2">Élève</th>
                  <th className="px-3 py-2">Valeur</th>
                  <th className="px-3 py-2">Statut</th>
                  <th className="px-3 py-2">Absent</th>
                  <th className="px-3 py-2">Exclue</th>
                  <th className="px-3 py-2">Coefficient</th>
                  <th className="px-3 py-2">Saisie le</th>
                </tr>
              </thead>
              <tbody>
                {notes.data.map((note) => (
                  <tr key={note.id ?? note.eleveId} className="border-b border-border last:border-0">
                    <td className="px-3 py-2 text-xs">{lookupLabel(eleves, note.eleveId)}</td>
                    <td className="px-3 py-2 font-semibold">{note.valeurEffective ?? note.valeur ?? "—"}</td>
                    <td className="px-3 py-2">
                      <Badge variant={note.statut === "DEFINITIVE" ? "success" : note.statut === "CONTESTEE" ? "warning" : "outline"}>
                        {humanize(note.statut)}
                      </Badge>
                    </td>
                    <td className="px-3 py-2">{note.absent ? <Badge variant="danger">Oui</Badge> : <span className="text-slate-400">Non</span>}</td>
                    <td className="px-3 py-2">{note.exclue ? <Badge variant="warning">{note.motifExclusion}</Badge> : <span className="text-slate-400">Non</span>}</td>
                    <td className="px-3 py-2">{note.coefficientReel ?? "—"}</td>
                    <td className="px-3 py-2">{formatInstant(note.saisieLe)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <EmptyState label="Aucune note saisie" />
          )}
        </CardContent>
      </Card>

      {saisieOuverte && (
        <div className="grid gap-4 lg:grid-cols-2">
          <Card>
            <CardHeader className="p-4 pb-2">
              <CardTitle className="flex items-center gap-2 text-base">
                <ClipboardPen className="h-4 w-4" />
                Saisie / absence
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-3 p-4 pt-2">
              <div>
                <Label className="mb-1 block">Élève</Label>
                <EntitySelect
                  fetcher={() => peopleApi.eleves.list()}
                  keyOf={(eleve) => eleve.idEleve}
                  labelOf={eleveLabel}
                  value={noteForm.eleveId}
                  onChange={(value) => setNoteForm({ ...noteForm, eleveId: value })}
                  deps={[tenantId]}
                  placeholder="Sélectionner un élève…"
                />
              </div>
              <div>
                <Label className="mb-1 block">Valeur</Label>
                <Input type="number" step="0.25" value={noteForm.valeur} onChange={(event) => setNoteForm({ ...noteForm, valeur: event.target.value })} />
              </div>
              <Button disabled={busy || !noteForm.eleveId} onClick={saisir}>
                Saisir la note
              </Button>
              <div className="flex items-end gap-2 border-t pt-3">
                <div className="flex-1">
                  <Label className="mb-1 block">Élève absent</Label>
                  <EntitySelect
                    fetcher={() => peopleApi.eleves.list()}
                    keyOf={(eleve) => eleve.idEleve}
                    labelOf={eleveLabel}
                    value={noteForm.eleveAbsent}
                    onChange={(value) => setNoteForm({ ...noteForm, eleveAbsent: value })}
                    deps={[tenantId]}
                    placeholder="Sélectionner un élève…"
                  />
                </div>
                <Button variant="outline" disabled={busy || !noteForm.eleveAbsent} onClick={marquerAbsent}>
                  <FileX2 className="h-4 w-4" />
                </Button>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="p-4 pb-2">
              <CardTitle className="flex items-center gap-2 text-base">
                <Ban className="h-4 w-4" />
                Exclusion / contestation
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4 p-4 pt-2">
              <div>
                <Label className="mb-1 block">Exclure — élève</Label>
                <div className="flex gap-2">
                  <EntitySelect
                    fetcher={() => peopleApi.eleves.list()}
                    keyOf={(eleve) => eleve.idEleve}
                    labelOf={eleveLabel}
                    value={exclusion.eleveId}
                    onChange={(value) => setExclusion({ ...exclusion, eleveId: value })}
                    deps={[tenantId]}
                    placeholder="Sélectionner un élève…"
                    className="flex-1"
                  />
                </div>
                <Input
                  value={exclusion.motif}
                  onChange={(event) => setExclusion({ ...exclusion, motif: event.target.value })}
                  placeholder="Motif d'exclusion (fraude…)"
                  className="mt-2"
                />
                <Button className="mt-2" variant="outline" disabled={busy || !exclusion.eleveId || !exclusion.motif} onClick={exclure}>
                  Exclure
                </Button>
              </div>
              <div className="border-t pt-3">
                <Label className="mb-1 block">Contester — élève</Label>
                <EntitySelect
                  fetcher={() => peopleApi.eleves.list()}
                  keyOf={(eleve) => eleve.idEleve}
                  labelOf={eleveLabel}
                  value={contestation.eleveId}
                  onChange={(value) => setContestation({ ...contestation, eleveId: value })}
                  deps={[tenantId]}
                  placeholder="Sélectionner un élève…"
                />
                <Input
                  value={contestation.motif}
                  onChange={(event) => setContestation({ ...contestation, motif: event.target.value })}
                  placeholder="Motif de contestation"
                  className="mt-2"
                />
                <Button className="mt-2" variant="outline" disabled={busy || !contestation.eleveId || !contestation.motif} onClick={contester}>
                  Contester
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}