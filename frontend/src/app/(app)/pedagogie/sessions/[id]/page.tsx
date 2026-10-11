"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import { ArrowLeft, Bell, CalendarPlus, CheckCircle2, FileCheck2 } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select } from "@/components/ui/select";
import { PageHeader, LoadingView, ErrorState, EmptyState } from "@/components/common/async-state";
import { enumOptions, TYPE_CRENEAU_EXAM, TYPE_EPREUVE } from "@/components/pedagogie/constants";
import { formatDate, formatTime, humanize } from "@/components/pedagogie/format";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { examApi } from "@/services/api";

export default function SessionDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();

  const session = useApi(() => examApi.sessions.get(id), [id, tenantId]);
  const [busy, setBusy] = useState(false);

  const [epreuveForm, setEpreuveForm] = useState({ reference: "", libelle: "", type: "ECRIT", matiereId: "", coefficient: "1", dureeMinutes: "60" });
  const [creneauForm, setCreneauForm] = useState({ date: "", type: "MATIN", heureDebut: "", heureFin: "", ordre: "1" });
  const [convocationEleve, setConvocationEleve] = useState("");

  const [notesEpreuve, setNotesEpreuve] = useState("");
  const notes = useApi(() => examApi.notes.list({ epreuveId: notesEpreuve }), [notesEpreuve, tenantId]);
  const [noteForm, setNoteForm] = useState({ eleveId: "", valeur: "0", correcteur: "" });

  const data = session.data;
  const reload = () => session.reload();

  const simuler = async (fn: () => Promise<unknown>, message: string) => {
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

  if (session.loading) return <LoadingView label="Chargement de la session…" />;
  if (session.error) return <ErrorState error={session.error} onRetry={reload} />;

  return (
    <div>
      <PageHeader title={data?.libelle ?? "Session d'examen"} description={data?.reference ?? "—"}>
        <Link href="/pedagogie/sessions">
          <Button variant="outline" size="sm">
            <ArrowLeft className="h-4 w-4" />
            Retour
          </Button>
        </Link>
      </PageHeader>

      <div className="mb-4 flex flex-wrap items-center gap-2">
        <Badge variant="outline">{humanize(data?.statut ?? "—")}</Badge>
        {data?.type && <Badge variant="outline">{humanize(data.type)}</Badge>}
        <Badge variant="outline">Ouverture {formatDate(data?.dateOuverture)}</Badge>
        <Badge variant="outline">Clôture {formatDate(data?.dateCloture)}</Badge>
        <Badge variant="outline">Candidats {data?.nbCandidatsConvoques ?? 0}/{data?.nbCandidatsAttendus ?? "—"}</Badge>
      </div>

      <Card className="mb-4">
        <CardHeader className="p-4 pb-2">
          <CardTitle className="text-base">Épreuves ({data?.epreuves?.length ?? 0})</CardTitle>
        </CardHeader>
        <CardContent className="overflow-x-auto p-4 pt-2">
          {data?.epreuves?.length ? (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-3 py-2">Référence</th>
                  <th className="px-3 py-2">Libellé</th>
                  <th className="px-3 py-2">Type</th>
                  <th className="px-3 py-2">Date</th>
                  <th className="px-3 py-2">Durée</th>
                  <th className="px-3 py-2">Statut</th>
                  <th className="px-3 py-2">Moyenne</th>
                  {isAdmin && <th className="px-3 py-2 text-right">Actions</th>}
                </tr>
              </thead>
              <tbody>
                {data.epreuves.map((epreuve) => (
                  <tr key={epreuve.idEpreuve} className="border-b border-border last:border-0">
                    <td className="px-3 py-2 font-mono text-xs">{epreuve.reference}</td>
                    <td className="px-3 py-2">{epreuve.libelle ?? "—"}</td>
                    <td className="px-3 py-2">{epreuve.type ? humanize(epreuve.type) : "—"}</td>
                    <td className="px-3 py-2">{formatDate(epreuve.date)}</td>
                    <td className="px-3 py-2">{epreuve.dureeMinutes != null ? `${epreuve.dureeMinutes} min` : "—"}</td>
                    <td className="px-3 py-2">
                      <Badge variant={epreuve.statut === "VALIDEE" ? "success" : "outline"}>{humanize(epreuve.statut)}</Badge>
                    </td>
                    <td className="px-3 py-2">{epreuve.comptabiliseDansMoyenne ? "Oui" : "Non"}</td>
                    {isAdmin && (
                      <td className="px-3 py-2">
                        <div className="flex justify-end gap-1">
                          {epreuve.statut !== "VALIDEE" && (
                            <Button
                              variant="ghost"
                              size="sm"
                              title="Valider"
                              disabled={busy}
                              onClick={() => simuler(() => examApi.epreuves.valider(epreuve.idEpreuve), "Épreuve validée")}
                            >
                              <CheckCircle2 className="h-4 w-4" />
                            </Button>
                          )}
                          <Button
                            variant="ghost"
                            size="sm"
                            title="Hors moyenne"
                            disabled={busy}
                            onClick={() => simuler(() => examApi.epreuves.horsMoyenne(epreuve.idEpreuve), "Épreuve marquée hors moyenne")}
                          >
                            <FileCheck2 className="h-4 w-4" />
                          </Button>
                          <Button
                            variant="ghost"
                            size="sm"
                            title="Ouvrir une correction"
                            disabled={busy}
                            onClick={() => simuler(() => examApi.corrections.ouvrir(epreuve.idEpreuve), "Correction ouverte")}
                          >
                            Cor.
                          </Button>
                        </div>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <EmptyState label="Aucune épreuve" />
          )}

          {isAdmin && (
            <form
              className="mt-3 grid gap-3 border-t pt-3 sm:grid-cols-6"
              onSubmit={(event) => {
                event.preventDefault();
                if (!epreuveForm.reference) return;
                simuler(
                  () =>
                    examApi.sessions.ajouterEpreuve(id, {
                      reference: epreuveForm.reference,
                      libelle: epreuveForm.libelle,
                      type: epreuveForm.type as never,
                      matiereId: epreuveForm.matiereId || null,
                      coefficient: Number(epreuveForm.coefficient),
                      dureeMinutes: Number(epreuveForm.dureeMinutes),
                    }),
                  "Épreuve ajoutée",
                );
                setEpreuveForm({ reference: "", libelle: "", type: "ECRIT", matiereId: "", coefficient: "1", dureeMinutes: "60" });
              }}
            >
              <div>
                <Label className="mb-1 block">Référence</Label>
                <Input value={epreuveForm.reference} onChange={(event) => setEpreuveForm({ ...epreuveForm, reference: event.target.value })} />
              </div>
              <div>
                <Label className="mb-1 block">Libellé</Label>
                <Input value={epreuveForm.libelle} onChange={(event) => setEpreuveForm({ ...epreuveForm, libelle: event.target.value })} />
              </div>
              <div>
                <Label className="mb-1 block">Type</Label>
                <Select value={epreuveForm.type} onChange={(event) => setEpreuveForm({ ...epreuveForm, type: event.target.value })}>
                  {enumOptions(TYPE_EPREUVE).map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </Select>
              </div>
              <div>
                <Label className="mb-1 block">Coef</Label>
                <Input type="number" value={epreuveForm.coefficient} onChange={(event) => setEpreuveForm({ ...epreuveForm, coefficient: event.target.value })} />
              </div>
              <div>
                <Label className="mb-1 block">Durée</Label>
                <Input type="number" value={epreuveForm.dureeMinutes} onChange={(event) => setEpreuveForm({ ...epreuveForm, dureeMinutes: event.target.value })} />
              </div>
              <div className="flex items-end">
                <Button type="submit" disabled={busy || !epreuveForm.reference}>
                  Ajouter
                </Button>
              </div>
            </form>
          )}
        </CardContent>
      </Card>

      <div className="mb-4 grid gap-4 lg:grid-cols-2">
        <Card>
          <CardHeader className="p-4 pb-2">
            <CardTitle className="flex items-center gap-2 text-base">
              <CalendarPlus className="h-4 w-4" />
              Créneaux ({data?.creneaux?.length ?? 0})
            </CardTitle>
          </CardHeader>
          <CardContent className="p-4 pt-2">
            {data?.creneaux?.length ? (
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                    <th className="px-3 py-2">Date</th>
                    <th className="px-3 py-2">Type</th>
                    <th className="px-3 py-2">Début</th>
                    <th className="px-3 py-2">Fin</th>
                    <th className="px-3 py-2">Ordre</th>
                  </tr>
                </thead>
                <tbody>
                  {data.creneaux.map((creneau, index) => (
                    <tr key={creneau.id ?? index} className="border-b border-border last:border-0">
                      <td className="px-3 py-2">{formatDate(creneau.date)}</td>
                      <td className="px-3 py-2">{creneau.type ? humanize(creneau.type) : "—"}</td>
                      <td className="px-3 py-2">{formatTime(creneau.heureDebut)}</td>
                      <td className="px-3 py-2">{formatTime(creneau.heureFin)}</td>
                      <td className="px-3 py-2">{creneau.ordre ?? "—"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <EmptyState label="Aucun créneau" />
            )}
            {isAdmin && (
              <form
                className="mt-3 grid gap-3 border-t pt-3 sm:grid-cols-3"
                onSubmit={(event) => {
                  event.preventDefault();
                  if (!creneauForm.date) return;
                  simuler(
                    () =>
                      examApi.sessions.ajouterCreneau(id, {
                        date: creneauForm.date as unknown as number[],
                        type: creneauForm.type as never,
                        heureDebut: (creneauForm.heureDebut || null) as unknown as number[] | null,
                        heureFin: (creneauForm.heureFin || null) as unknown as number[] | null,
                        ordre: Number(creneauForm.ordre),
                      }),
                    "Créneau ajouté",
                  );
                  setCreneauForm({ date: "", type: "MATIN", heureDebut: "", heureFin: "", ordre: "1" });
                }}
              >
                <div>
                  <Label className="mb-1 block">Date</Label>
                  <Input type="date" value={creneauForm.date} onChange={(event) => setCreneauForm({ ...creneauForm, date: event.target.value })} />
                </div>
                <div>
                  <Label className="mb-1 block">Type</Label>
                  <Select value={creneauForm.type} onChange={(event) => setCreneauForm({ ...creneauForm, type: event.target.value })}>
                    {enumOptions(TYPE_CRENEAU_EXAM).map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </Select>
                </div>
                <div className="flex items-end">
                  <Button type="submit" disabled={busy || !creneauForm.date}>
                    Ajouter
                  </Button>
                </div>
              </form>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="p-4 pb-2">
            <CardTitle className="flex items-center gap-2 text-base">
              <Bell className="h-4 w-4" />
              Convocations ({data?.convocations?.length ?? 0})
            </CardTitle>
          </CardHeader>
          <CardContent className="p-4 pt-2">
            {data?.convocations?.length ? (
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                    <th className="px-3 py-2">Numéro</th>
                    <th className="px-3 py-2">Élève</th>
                    <th className="px-3 py-2">Statut</th>
                    {isAdmin && <th className="px-3 py-2 text-right">Actions</th>}
                  </tr>
                </thead>
                <tbody>
                  {data.convocations.map((convocation, index) => (
                    <tr key={convocation.id ?? index} className="border-b border-border last:border-0">
                      <td className="px-3 py-2 font-mono text-xs">{convocation.numero ?? "—"}</td>
                      <td className="px-3 py-2 font-mono text-xs">{convocation.eleveId?.slice(0, 8)}…</td>
                      <td className="px-3 py-2">
                        <Badge variant={convocation.statut === "NOTIFIEE" ? "success" : "outline"}>{humanize(convocation.statut)}</Badge>
                      </td>
                      {isAdmin && (
                        <td className="px-3 py-2">
                          <div className="flex justify-end gap-1">
                            {convocation.statut === "EMISE" && convocation.id && (
                              <Button
                                variant="ghost"
                                size="sm"
                                title="Notifier"
                                disabled={busy}
                                onClick={() => simuler(() => examApi.convocations.notifier(convocation.id as string, "EMAIL"), "Convocation notifiée")}
                              >
                                <Bell className="h-4 w-4" />
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
              <EmptyState label="Aucune convocation" />
            )}
            {isAdmin && (
              <form
                className="mt-3 flex items-end gap-2 border-t pt-3"
                onSubmit={(event) => {
                  event.preventDefault();
                  if (!convocationEleve) return;
                  simuler(() => examApi.sessions.creerConvocation(id, { eleveId: convocationEleve }), "Convocation créée");
                  setConvocationEleve("");
                }}
              >
                <div className="flex-1">
                  <Label className="mb-1 block">Élève (UUID)</Label>
                  <Input value={convocationEleve} onChange={(event) => setConvocationEleve(event.target.value)} placeholder="121f5678-…" />
                </div>
                <Button type="submit" disabled={busy || !convocationEleve}>
                  Convoquer
                </Button>
              </form>
            )}
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader className="p-4 pb-2">
          <CardTitle className="text-base">Notes d&apos;examen</CardTitle>
        </CardHeader>
        <CardContent className="p-4 pt-2">
          <form
            className="mb-3 flex items-end gap-2"
            onSubmit={(event) => {
              event.preventDefault();
              if (notesEpreuve) notes.reload();
            }}
          >
            <div className="flex-1">
              <Label className="mb-1 block">Épreuve (UUID)</Label>
              <Input value={notesEpreuve} onChange={(event) => setNotesEpreuve(event.target.value)} />
            </div>
            <Button type="submit" disabled={!notesEpreuve}>
              Charger
            </Button>
          </form>
          {notes.loading ? (
            <LoadingView label="Chargement des notes…" />
          ) : notes.error ? (
            <ErrorState error={notes.error} onRetry={notes.reload} />
          ) : notes.data?.length ? (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-3 py-2">Élève</th>
                  <th className="px-3 py-2">Valeur</th>
                  <th className="px-3 py-2">Statut</th>
                  <th className="px-3 py-2">Absent</th>
                  {isAdmin && <th className="px-3 py-2 text-right">Actions</th>}
                </tr>
              </thead>
              <tbody>
                {notes.data.map((note) => (
                  <tr key={note.id ?? note.eleveId} className="border-b border-border last:border-0">
                    <td className="px-3 py-2 font-mono text-xs">{note.eleveId?.slice(0, 8)}…</td>
                    <td className="px-3 py-2 font-semibold">{note.valeurNormalisee ?? note.valeur ?? "—"}</td>
                    <td className="px-3 py-2">{humanize(note.statut)}</td>
                    <td className="px-3 py-2">{note.absent ? "Oui" : "Non"}</td>
                    {isAdmin && note.id && (
                      <td className="px-3 py-2">
                        <div className="flex justify-end gap-1">
                          {note.statut !== "DEFINITIVE" && (
                            <Button variant="ghost" size="sm" title="Valider" disabled={busy} onClick={() => simuler(() => examApi.notes.valider(note.id as string), "Note validée")}>
                              <CheckCircle2 className="h-4 w-4" />
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
            <EmptyState label="Saisissez une épreuve pour voir ses notes" />
          )}

          {isAdmin && notesEpreuve && (
            <form
              className="mt-3 grid gap-3 border-t pt-3 sm:grid-cols-4"
              onSubmit={(event) => {
                event.preventDefault();
                if (!noteForm.eleveId) return;
                simuler(
                  () =>
                    examApi.notes.saisir(notesEpreuve, {
                      eleveId: noteForm.eleveId,
                      valeur: Number(noteForm.valeur),
                      correcteur: noteForm.correcteur || null,
                    }),
                  "Note saisie",
                );
                setNoteForm({ eleveId: "", valeur: "0", correcteur: "" });
              }}
            >
              <div>
                <Label className="mb-1 block">Élève (UUID)</Label>
                <Input value={noteForm.eleveId} onChange={(event) => setNoteForm({ ...noteForm, eleveId: event.target.value })} />
              </div>
              <div>
                <Label className="mb-1 block">Valeur</Label>
                <Input type="number" step="0.25" value={noteForm.valeur} onChange={(event) => setNoteForm({ ...noteForm, valeur: event.target.value })} />
              </div>
              <div>
                <Label className="mb-1 block">Correcteur (UUID)</Label>
                <Input value={noteForm.correcteur} onChange={(event) => setNoteForm({ ...noteForm, correcteur: event.target.value })} />
              </div>
              <div className="flex items-end">
                <Button type="submit" disabled={busy || !noteForm.eleveId}>
                  Saisir
                </Button>
              </div>
            </form>
          )}
        </CardContent>
      </Card>
    </div>
  );
}