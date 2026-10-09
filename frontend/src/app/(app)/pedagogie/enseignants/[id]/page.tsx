"use client";

import { useState } from "react";
import Link from "next/link";
import { useParams } from "next/navigation";
import { toast } from "sonner";
import { ArrowLeft, Plus } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Checkbox } from "@/components/ui/checkbox";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select } from "@/components/ui/select";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { PageHeader, LoadingView, ErrorState, EmptyState } from "@/components/common/async-state";
import { formatDate } from "@/components/pedagogie/format";
import { enumOptions, STATUT_ENSEIGNANT, TYPE_AFFECTION } from "@/components/pedagogie/constants";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { peopleApi, structureApi } from "@/services/api";
import type { StatutEnseignant } from "@/types";

function InfoRow({ label, value }: { label: string; value: React.ReactNode }) {
  return (
    <div className="flex flex-col gap-0.5">
      <span className="text-xs uppercase tracking-wide text-slate-400">{label}</span>
      <span className="text-sm text-slate-900">{value ?? "—"}</span>
    </div>
  );
}

export default function EnseignantDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();

  const enseignant = useApi(() => peopleApi.enseignants.get(id), [id, tenantId]);
  const affectations = useApi(() => peopleApi.enseignants.affectations(id), [id, tenantId]);
  const classes = useApi(structureApi.classes.list, [tenantId]);
  const matieres = useApi(structureApi.matieres.list, [tenantId]);

  const [busy, setBusy] = useState(false);
  const [statut, setStatut] = useState<StatutEnseignant>("ACTIF");
  const [piece, setPiece] = useState({ type: "", numero: "", dateDelivrance: "", dateExpiration: "" });
  const [affectation, setAffectation] = useState({
    classeId: "",
    matiereId: "",
    type: "TITULAIRE",
    titulaire: false,
    principale: false,
    dateDebut: "",
  });

  const run = async (action: () => Promise<unknown>, message: string, reload: () => void) => {
    setBusy(true);
    try {
      await action();
      toast.success(message);
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  if (enseignant.loading) return <LoadingView label="Chargement de l'enseignant…" />;
  if (enseignant.error) return <ErrorState error={enseignant.error} onRetry={enseignant.reload} />;
  const data = enseignant.data!;

  return (
    <div>
      <PageHeader title={`${data.prenom} ${data.nom} ${data.postNom ?? ""}`} description={`Matricule ${data.matricule}`}>
        <Link href="/pedagogie/enseignants">
          <Button variant="outline" size="sm">
            <ArrowLeft className="h-4 w-4" />
            Retour
          </Button>
        </Link>
      </PageHeader>

      <Tabs defaultValue="identite">
        <TabsList>
          <TabsTrigger value="identite">Identité</TabsTrigger>
          <TabsTrigger value="pieces">Pièces</TabsTrigger>
          <TabsTrigger value="affectations">Affectations</TabsTrigger>
        </TabsList>

        <TabsContent value="identite">
          <Card>
            <CardContent className="grid gap-4 p-6 sm:grid-cols-3">
              <InfoRow label="Statut" value={<Badge variant={data.statut === "ACTIF" ? "success" : "secondary"}>{data.statut}</Badge>} />
              <InfoRow label="Sexe" value={data.sexe} />
              <InfoRow label="Date de naissance" value={formatDate(data.dateNaissance)} />
              <InfoRow label="Téléphone" value={data.telephone} />
              <InfoRow label="Email" value={data.email} />
              <InfoRow label="Diplôme" value={data.diplome} />
              <InfoRow label="Diplôme le plus élevé" value={data.diplomePlus} />
              <InfoRow label="Charge horaire hebdo" value={data.volumeHoraireHebdo} />
              <InfoRow label="Date de recrutement" value={formatDate(data.dateRecrutement)} />
              <InfoRow label="Spécialités" value={data.specialite?.length ? data.specialite.join(", ") : "—"} />
            </CardContent>
            {isAdmin && (
              <CardContent className="flex items-end gap-2 border-t border-border p-4">
                <div>
                  <Label className="mb-1 block">Changer le statut</Label>
                  <Select value={statut} onChange={(e) => setStatut(e.target.value as StatutEnseignant)}>
                    {enumOptions(STATUT_ENSEIGNANT).map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </Select>
                </div>
                <Button
                  disabled={busy}
                  onClick={() =>
                    run(() => peopleApi.enseignants.changerStatut(id, statut), "Statut modifié", enseignant.reload)
                  }
                >
                  Appliquer
                </Button>
              </CardContent>
            )}
          </Card>
        </TabsContent>

        <TabsContent value="pieces">
          <Card className="mb-4">
            <CardContent className="overflow-x-auto p-0">
              {data.pieces?.length ? (
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                      <th className="px-4 py-3">Type</th>
                      <th className="px-4 py-3">Numéro</th>
                      <th className="px-4 py-3">Délivrance</th>
                      <th className="px-4 py-3">Expiration</th>
                      <th className="px-4 py-3">Pays</th>
                    </tr>
                  </thead>
                  <tbody>
                    {data.pieces.map((item, index) => (
                      <tr key={item.id ?? index} className="border-b border-border last:border-0">
                        <td className="px-4 py-3">{item.type}</td>
                        <td className="px-4 py-3 font-mono text-xs">{item.numero ?? "—"}</td>
                        <td className="px-4 py-3">{formatDate(item.dateDelivrance)}</td>
                        <td className="px-4 py-3">{formatDate(item.dateExpiration)}</td>
                        <td className="px-4 py-3">{item.pays ?? "—"}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <EmptyState label="Aucune pièce" />
              )}
            </CardContent>
          </Card>

          {isAdmin && (
            <Card>
              <CardHeader className="p-4 pb-2">
                <CardTitle className="text-base">Ajouter une pièce d&apos;identité</CardTitle>
              </CardHeader>
              <CardContent className="p-4 pt-2">
                <form
                  className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4"
                  onSubmit={(event) => {
                    event.preventDefault();
                    run(
                      () =>
                        peopleApi.enseignants.ajouterPiece(id, {
                          type: piece.type,
                          numero: piece.numero || null,
                          dateDelivrance: piece.dateDelivrance || null,
                          dateExpiration: piece.dateExpiration || null,
                        }),
                      "Pièce ajoutée",
                      enseignant.reload,
                    );
                  }}
                >
                  <div>
                    <Label className="mb-1 block">Type</Label>
                    <Input value={piece.type} onChange={(e) => setPiece({ ...piece, type: e.target.value })} required />
                  </div>
                  <div>
                    <Label className="mb-1 block">Numéro</Label>
                    <Input value={piece.numero} onChange={(e) => setPiece({ ...piece, numero: e.target.value })} />
                  </div>
                  <div>
                    <Label className="mb-1 block">Délivrance</Label>
                    <Input type="date" value={piece.dateDelivrance} onChange={(e) => setPiece({ ...piece, dateDelivrance: e.target.value })} />
                  </div>
                  <div>
                    <Label className="mb-1 block">Expiration</Label>
                    <Input type="date" value={piece.dateExpiration} onChange={(e) => setPiece({ ...piece, dateExpiration: e.target.value })} />
                  </div>
                  <div className="lg:col-span-4">
                    <Button type="submit" disabled={busy || !piece.type}>
                      <Plus className="h-4 w-4" />
                      Ajouter
                    </Button>
                  </div>
                </form>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="affectations">
          <Card className="mb-4">
            <CardContent className="overflow-x-auto p-0">
              {affectations.data?.length ? (
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                      <th className="px-4 py-3">Classe</th>
                      <th className="px-4 py-3">Matière</th>
                      <th className="px-4 py-3">Type</th>
                      <th className="px-4 py-3">Titulaire</th>
                      <th className="px-4 py-3">Début</th>
                    </tr>
                  </thead>
                  <tbody>
                    {affectations.data.map((item) => {
                      const classe = (classes.data ?? []).find((c) => c.idClasse === item.classeId);
                      const matiere = (matieres.data ?? []).find((m) => m.idMatiere === item.matiereId);
                      return (
                        <tr key={item.id} className="border-b border-border last:border-0">
                          <td className="px-4 py-3">
                            {classe ? classe.libelle : <span className="font-mono text-xs">{item.classeId.slice(0, 8)}…</span>}
                          </td>
                          <td className="px-4 py-3">{matiere ? matiere.libelle : "—"}</td>
                          <td className="px-4 py-3">{item.type.replace(/_/g, " ")}</td>
                          <td className="px-4 py-3">
                            {item.titulaire ? <Badge variant="success">Titulaire</Badge> : "—"}
                          </td>
                          <td className="px-4 py-3">{formatDate(item.dateDebut)}</td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              ) : (
                <EmptyState label="Aucune affectation" />
              )}
            </CardContent>
          </Card>

          {isAdmin && (
            <Card>
              <CardHeader className="p-4 pb-2">
                <CardTitle className="text-base">Nouvelle affectation</CardTitle>
              </CardHeader>
              <CardContent className="p-4 pt-2">
                <form
                  className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4"
                  onSubmit={(event) => {
                    event.preventDefault();
                    run(
                      () =>
                        peopleApi.enseignants.affecter(id, {
                          classeId: affectation.classeId,
                          matiereId: affectation.matiereId || null,
                          type: affectation.type as never,
                          titulaire: affectation.titulaire,
                          principale: affectation.principale,
                          dateDebut: affectation.dateDebut || null,
                        }),
                      "Affectation créée",
                      affectations.reload,
                    );
                  }}
                >
                  <div>
                    <Label className="mb-1 block">Classe</Label>
                    <Select value={affectation.classeId} onChange={(e) => setAffectation({ ...affectation, classeId: e.target.value })}>
                      <option value="">—</option>
                      {(classes.data ?? []).map((item) => (
                        <option key={item.idClasse} value={item.idClasse}>
                          {item.libelle}
                        </option>
                      ))}
                    </Select>
                  </div>
                  <div>
                    <Label className="mb-1 block">Matière</Label>
                    <Select value={affectation.matiereId} onChange={(e) => setAffectation({ ...affectation, matiereId: e.target.value })}>
                      <option value="">—</option>
                      {(matieres.data ?? []).map((item) => (
                        <option key={item.idMatiere} value={item.idMatiere}>
                          {item.libelle}
                        </option>
                      ))}
                    </Select>
                  </div>
                  <div>
                    <Label className="mb-1 block">Type</Label>
                    <Select value={affectation.type} onChange={(e) => setAffectation({ ...affectation, type: e.target.value })}>
                      {enumOptions(TYPE_AFFECTION).map((option) => (
                        <option key={option.value} value={option.value}>
                          {option.label}
                        </option>
                      ))}
                    </Select>
                  </div>
                  <div>
                    <Label className="mb-1 block">Début</Label>
                    <Input type="date" value={affectation.dateDebut} onChange={(e) => setAffectation({ ...affectation, dateDebut: e.target.value })} />
                  </div>
                  <div className="flex items-center gap-4 lg:col-span-4">
                    <div className="flex h-10 items-center gap-2">
                      <Checkbox checked={affectation.titulaire} onChange={(v) => setAffectation({ ...affectation, titulaire: v })} />
                      <Label>Titulaire</Label>
                    </div>
                    <div className="flex h-10 items-center gap-2">
                      <Checkbox checked={affectation.principale} onChange={(v) => setAffectation({ ...affectation, principale: v })} />
                      <Label>Principale</Label>
                    </div>
                    <Button type="submit" disabled={busy || !affectation.classeId}>
                      <Plus className="h-4 w-4" />
                      Affecter
                    </Button>
                  </div>
                </form>
              </CardContent>
            </Card>
          )}
        </TabsContent>
      </Tabs>
    </div>
  );
}
