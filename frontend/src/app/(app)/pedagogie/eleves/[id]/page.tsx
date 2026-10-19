"use client";

import { useState } from "react";
import Link from "next/link";
import { useParams } from "next/navigation";
import { toast } from "sonner";
import { ArrowLeft, Check, FileWarning, Plus, X } from "lucide-react";
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
import {
  CATEGORIE_PIECE,
  enumOptions,
  TYPE_CONGE,
  TYPE_LIEN,
  TYPE_SANCTION,
} from "@/components/pedagogie/constants";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { anneeApi, peopleApi, structureApi } from "@/services/api";

function InfoRow({ label, value }: { label: string; value: React.ReactNode }) {
  return (
    <div className="flex flex-col gap-0.5">
      <span className="text-xs uppercase tracking-wide text-slate-400">{label}</span>
      <span className="text-sm text-slate-900">{value ?? "—"}</span>
    </div>
  );
}

export default function EleveDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();

  const eleve = useApi(() => peopleApi.eleves.get(id), [id, tenantId]);
  const dossier = useApi(() => peopleApi.eleves.dossier(id), [id, tenantId]);
  const inscriptions = useApi(() => peopleApi.eleves.inscriptions(id), [id, tenantId]);
  const conges = useApi(() => peopleApi.eleves.conges(id), [id, tenantId]);
  const sanctions = useApi(() => peopleApi.eleves.sanctions(id), [id, tenantId]);
  const liens = useApi(() => peopleApi.eleves.liens(id), [id, tenantId]);
  const classes = useApi(structureApi.classes.list, [tenantId]);
  const annees = useApi(anneeApi.list, [tenantId]);
  const parents = useApi(peopleApi.parents.list, [tenantId]);

  const [busy, setBusy] = useState(false);
  const [piece, setPiece] = useState({ categorie: "PHOTO_IDENTITE", nom: "", uri: "", documentOfficiel: false });
  const [inscription, setInscription] = useState({ anneeId: "", classeId: "", redoublant: false, nouveau: true, fraisRegles: false });
  const [conge, setConge] = useState({ type: "MEDICAL", dateDebut: "", dateFin: "", motif: "" });
  const [sanction, setSanction] = useState({ dateSanction: "", type: "AVERTISSEMENT", motif: "", dureeJours: "" });
  const [lien, setLien] = useState({ parentId: "", type: "PERE", principal: true, payeur: false, receveurNotifications: true });

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

  if (eleve.loading) return <LoadingView label="Chargement de l'élève…" />;
  if (eleve.error) return <ErrorState error={eleve.error} onRetry={eleve.reload} />;
  const data = eleve.data!;

  return (
    <div>
      <PageHeader title={`${data.nom} ${data.postNom ?? ""} ${data.prenom}`} description={`Matricule ${data.matricule}`}>
        <Link href="/pedagogie/eleves">
          <Button variant="outline" size="sm">
            <ArrowLeft className="h-4 w-4" />
            Retour
          </Button>
        </Link>
        {isAdmin && (
          <>
            <Button
              variant="outline"
              size="sm"
              disabled={busy}
              onClick={() => {
                const destination = window.prompt("Établissement de destination ?");
                if (!destination) return;
                run(() => peopleApi.eleves.transferer(id, destination), "Élève transféré", eleve.reload);
              }}
            >
              Transférer
            </Button>
            <Button
              variant="destructive"
              size="sm"
              disabled={busy}
              onClick={() => {
                const motif = window.prompt("Motif de la radiation ?");
                if (!motif) return;
                run(() => peopleApi.eleves.radier(id, motif), "Élève radié", eleve.reload);
              }}
            >
              Radier
            </Button>
          </>
        )}
      </PageHeader>

      <Tabs defaultValue="identite">
        <TabsList>
          <TabsTrigger value="identite">Identité</TabsTrigger>
          <TabsTrigger value="dossier">Dossier</TabsTrigger>
          <TabsTrigger value="inscriptions">Inscriptions</TabsTrigger>
          <TabsTrigger value="conges">Congés</TabsTrigger>
          <TabsTrigger value="sanctions">Sanctions</TabsTrigger>
          <TabsTrigger value="liens">Parents</TabsTrigger>
        </TabsList>

        <TabsContent value="identite">
          <Card>
            <CardContent className="grid gap-4 p-6 sm:grid-cols-3">
              <InfoRow label="Statut" value={<Badge variant="secondary">{data.statut.replace(/_/g, " ")}</Badge>} />
              <InfoRow label="Sexe" value={data.sexe} />
              <InfoRow label="Date de naissance" value={formatDate(data.dateNaissance)} />
              <InfoRow label="Lieu de naissance" value={data.lieuNaissance} />
              <InfoRow label="Nationalité" value={data.nationalite} />
              <InfoRow label="Date d'inscription" value={formatDate(data.dateInscription)} />
              <InfoRow label="Établissement d'origine" value={data.etablissementOrigine} />
              <InfoRow label="Statut du dossier" value={data.statutDossier ? data.statutDossier.replace(/_/g, " ") : "—"} />
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="dossier">
          <Card className="mb-4">
            <CardHeader className="p-4 pb-2">
              <CardTitle className="flex items-center justify-between text-base">
                <span>
                  Dossier {dossier.data?.reference ?? ""}
                  {dossier.data?.statut ? ` · ${dossier.data.statut.replace(/_/g, " ")}` : ""}
                </span>
                {isAdmin && dossier.data && dossier.data.statut !== "VERIFIE" && (
                  <Button
                    size="sm"
                    variant="outline"
                    disabled={busy}
                    onClick={() =>
                      run(() => peopleApi.eleves.archiverDossier(id), "Dossier archivé", dossier.reload)
                    }
                  >
                    Archiver
                  </Button>
                )}
              </CardTitle>
            </CardHeader>
            <CardContent className="p-4 pt-2">
              {dossier.data?.pieces?.length ? (
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                      <th className="px-3 py-2">Catégorie</th>
                      <th className="px-3 py-2">Nom</th>
                      <th className="px-3 py-2">URI</th>
                      <th className="px-3 py-2">Officiel</th>
                    </tr>
                  </thead>
                  <tbody>
                    {dossier.data.pieces.map((item, index) => (
                      <tr key={item.id ?? index} className="border-b border-border last:border-0">
                        <td className="px-3 py-2">{item.categorie.replace(/_/g, " ")}</td>
                        <td className="px-3 py-2">{item.nom}</td>
                        <td className="px-3 py-2 font-mono text-xs">{item.uri}</td>
                        <td className="px-3 py-2">{item.documentOfficiel ? "Oui" : "Non"}</td>
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
                <CardTitle className="text-base">Ajouter une pièce</CardTitle>
              </CardHeader>
              <CardContent className="p-4 pt-2">
                <form
                  className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4"
                  onSubmit={(event) => {
                    event.preventDefault();
                    run(
                      () =>
                        peopleApi.eleves.ajouterPiece(id, {
                          categorie: piece.categorie as never,
                          nom: piece.nom,
                          uri: piece.uri || null,
                          documentOfficiel: piece.documentOfficiel,
                        }),
                      "Pièce ajoutée",
                      dossier.reload,
                    );
                  }}
                >
                  <div>
                    <Label className="mb-1 block">Catégorie</Label>
                    <Select value={piece.categorie} onChange={(e) => setPiece({ ...piece, categorie: e.target.value })}>
                      {enumOptions(CATEGORIE_PIECE).map((option) => (
                        <option key={option.value} value={option.value}>
                          {option.label}
                        </option>
                      ))}
                    </Select>
                  </div>
                  <div>
                    <Label className="mb-1 block">Nom</Label>
                    <Input value={piece.nom} onChange={(e) => setPiece({ ...piece, nom: e.target.value })} required />
                  </div>
                  <div>
                    <Label className="mb-1 block">URI</Label>
                    <Input value={piece.uri} onChange={(e) => setPiece({ ...piece, uri: e.target.value })} />
                  </div>
                  <div className="flex items-end gap-2">
                    <div className="flex h-10 items-center gap-2">
                      <Checkbox
                        checked={piece.documentOfficiel}
                        onChange={(checked) => setPiece({ ...piece, documentOfficiel: checked })}
                      />
                      <Label>Officiel</Label>
                    </div>
                    <Button type="submit" disabled={busy || !piece.nom}>
                      <Plus className="h-4 w-4" />
                    </Button>
                  </div>
                </form>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="inscriptions">
          <Card className="mb-4">
            <CardContent className="overflow-x-auto p-0">
              {inscriptions.data?.length ? (
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                      <th className="px-4 py-3">Année</th>
                      <th className="px-4 py-3">Classe</th>
                      <th className="px-4 py-3">Date</th>
                      <th className="px-4 py-3">Statut</th>
                      <th className="px-4 py-3">Redoublant</th>
                      <th className="px-4 py-3 text-right">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {inscriptions.data.map((item) => (
                      <tr key={item.idInscription} className="border-b border-border last:border-0">
                        <td className="px-4 py-3">{annees.data?.find((a) => a.idAnnee === item.anneeId)?.libelle ?? "—"}</td>
                        <td className="px-4 py-3">{classes.data?.find((c) => c.idClasse === item.classeId)?.libelle ?? "—"}</td>
                        <td className="px-4 py-3">{formatDate(item.dateInscription)}</td>
                        <td className="px-4 py-3">
                          <Badge variant={item.statut === "VALIDE" ? "success" : item.statut === "ANNULEE" ? "danger" : "secondary"}>
                            {item.statut}
                          </Badge>
                        </td>
                        <td className="px-4 py-3">{item.redoublant ? "Oui" : "Non"}</td>
                        <td className="px-4 py-3">
                          <div className="flex justify-end gap-1">
                            {isAdmin && item.statut === "EN_ATTENTE" && (
                              <>
                                <Button
                                  variant="ghost"
                                  size="sm"
                                  disabled={busy}
                                  onClick={() =>
                                    run(
                                      () => peopleApi.eleves.validerInscription(id, item.idInscription),
                                      "Inscription validée",
                                      inscriptions.reload,
                                    )
                                  }
                                >
                                  <Check className="h-4 w-4 text-success" />
                                </Button>
                                <Button
                                  variant="ghost"
                                  size="sm"
                                  disabled={busy}
                                  onClick={() => {
                                    const motif = window.prompt("Motif d'annulation ?");
                                    if (!motif) return;
                                    run(
                                      () => peopleApi.eleves.annulerInscription(id, item.idInscription, motif),
                                      "Inscription annulée",
                                      inscriptions.reload,
                                    );
                                  }}
                                >
                                  <X className="h-4 w-4 text-danger" />
                                </Button>
                              </>
                            )}
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <EmptyState label="Aucune inscription" />
              )}
            </CardContent>
          </Card>

          {isAdmin && (
            <Card>
              <CardHeader className="p-4 pb-2">
                <CardTitle className="text-base">Nouvelle inscription</CardTitle>
              </CardHeader>
              <CardContent className="p-4 pt-2">
                <form
                  className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3"
                  onSubmit={(event) => {
                    event.preventDefault();
                    run(
                      () =>
                        peopleApi.eleves.inscrire(id, {
                          anneeId: inscription.anneeId,
                          classeId: inscription.classeId,
                          redoublant: inscription.redoublant,
                          nouveau: inscription.nouveau,
                          fraisRegles: inscription.fraisRegles,
                        }),
                      "Inscription créée",
                      inscriptions.reload,
                    );
                  }}
                >
                  <div>
                    <Label className="mb-1 block">Année</Label>
                    <Select value={inscription.anneeId} onChange={(e) => setInscription({ ...inscription, anneeId: e.target.value })}>
                      <option value="">—</option>
                      {(annees.data ?? []).map((item) => (
                        <option key={item.idAnnee} value={item.idAnnee}>
                          {item.libelle}
                        </option>
                      ))}
                    </Select>
                  </div>
                  <div>
                    <Label className="mb-1 block">Classe</Label>
                    <Select value={inscription.classeId} onChange={(e) => setInscription({ ...inscription, classeId: e.target.value })}>
                      <option value="">—</option>
                      {(classes.data ?? []).map((item) => (
                        <option key={item.idClasse} value={item.idClasse}>
                          {item.libelle}
                        </option>
                      ))}
                    </Select>
                  </div>
                  <div className="flex items-end gap-4">
                    <div className="flex h-10 items-center gap-2">
                      <Checkbox checked={inscription.redoublant} onChange={(v) => setInscription({ ...inscription, redoublant: v })} />
                      <Label>Redoublant</Label>
                    </div>
                    <div className="flex h-10 items-center gap-2">
                      <Checkbox checked={inscription.fraisRegles} onChange={(v) => setInscription({ ...inscription, fraisRegles: v })} />
                      <Label>Frais réglés</Label>
                    </div>
                  </div>
                  <div className="lg:col-span-3">
                    <Button type="submit" disabled={busy || !inscription.anneeId || !inscription.classeId}>
                      <Plus className="h-4 w-4" />
                      Inscrire
                    </Button>
                  </div>
                </form>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="conges">
          <Card className="mb-4">
            <CardContent className="overflow-x-auto p-0">
              {conges.data?.length ? (
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                      <th className="px-4 py-3">Type</th>
                      <th className="px-4 py-3">Début</th>
                      <th className="px-4 py-3">Fin</th>
                      <th className="px-4 py-3">Motif</th>
                    </tr>
                  </thead>
                  <tbody>
                    {conges.data.map((item) => (
                      <tr key={item.id} className="border-b border-border last:border-0">
                        <td className="px-4 py-3">{item.type.replace(/_/g, " ")}</td>
                        <td className="px-4 py-3">{formatDate(item.dateDebut)}</td>
                        <td className="px-4 py-3">{formatDate(item.dateFin)}</td>
                        <td className="px-4 py-3">{item.motif ?? "—"}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <EmptyState label="Aucun congé" />
              )}
            </CardContent>
          </Card>

          {isAdmin && (
            <Card>
              <CardHeader className="p-4 pb-2">
                <CardTitle className="text-base">Ouvrir un congé</CardTitle>
              </CardHeader>
              <CardContent className="p-4 pt-2">
                <form
                  className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4"
                  onSubmit={(event) => {
                    event.preventDefault();
                    run(
                      () =>
                        peopleApi.eleves.ouvrirConge(id, {
                          type: conge.type as never,
                          dateDebut: conge.dateDebut,
                          dateFin: conge.dateFin || null,
                          motif: conge.motif || null,
                        }),
                      "Congé ouvert",
                      conges.reload,
                    );
                  }}
                >
                  <div>
                    <Label className="mb-1 block">Type</Label>
                    <Select value={conge.type} onChange={(e) => setConge({ ...conge, type: e.target.value })}>
                      {enumOptions(TYPE_CONGE).map((option) => (
                        <option key={option.value} value={option.value}>
                          {option.label}
                        </option>
                      ))}
                    </Select>
                  </div>
                  <div>
                    <Label className="mb-1 block">Début</Label>
                    <Input type="date" value={conge.dateDebut} onChange={(e) => setConge({ ...conge, dateDebut: e.target.value })} required />
                  </div>
                  <div>
                    <Label className="mb-1 block">Fin</Label>
                    <Input type="date" value={conge.dateFin} onChange={(e) => setConge({ ...conge, dateFin: e.target.value })} />
                  </div>
                  <div>
                    <Label className="mb-1 block">Motif</Label>
                    <Input value={conge.motif} onChange={(e) => setConge({ ...conge, motif: e.target.value })} />
                  </div>
                  <div className="lg:col-span-4">
                    <Button type="submit" disabled={busy || !conge.dateDebut}>
                      <Plus className="h-4 w-4" />
                      Ouvrir
                    </Button>
                  </div>
                </form>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="sanctions">
          <Card className="mb-4">
            <CardContent className="overflow-x-auto p-0">
              {sanctions.data?.length ? (
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                      <th className="px-4 py-3">Date</th>
                      <th className="px-4 py-3">Type</th>
                      <th className="px-4 py-3">Motif</th>
                      <th className="px-4 py-3">Durée</th>
                      <th className="px-4 py-3">Instance</th>
                    </tr>
                  </thead>
                  <tbody>
                    {sanctions.data.map((item) => (
                      <tr key={item.id} className="border-b border-border last:border-0">
                        <td className="px-4 py-3">{formatDate(item.dateSanction)}</td>
                        <td className="px-4 py-3">{item.type.replace(/_/g, " ")}</td>
                        <td className="px-4 py-3">{item.motif ?? "—"}</td>
                        <td className="px-4 py-3">{item.dureeJours ?? "—"}</td>
                        <td className="px-4 py-3">{item.instance ?? "—"}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <EmptyState label="Aucune sanction" />
              )}
            </CardContent>
          </Card>

          {isAdmin && (
            <Card>
              <CardHeader className="p-4 pb-2">
                <CardTitle className="flex items-center gap-2 text-base">
                  <FileWarning className="h-4 w-4" />
                  Prononcer une sanction
                </CardTitle>
              </CardHeader>
              <CardContent className="p-4 pt-2">
                <form
                  className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4"
                  onSubmit={(event) => {
                    event.preventDefault();
                    run(
                      () =>
                        peopleApi.eleves.prononcerSanction(id, {
                          dateSanction: sanction.dateSanction,
                          type: sanction.type as never,
                          motif: sanction.motif || null,
                          dureeJours: sanction.dureeJours ? Number(sanction.dureeJours) : null,
                        }),
                      "Sanction prononcée",
                      sanctions.reload,
                    );
                  }}
                >
                  <div>
                    <Label className="mb-1 block">Date</Label>
                    <Input type="date" value={sanction.dateSanction} onChange={(e) => setSanction({ ...sanction, dateSanction: e.target.value })} required />
                  </div>
                  <div>
                    <Label className="mb-1 block">Type</Label>
                    <Select value={sanction.type} onChange={(e) => setSanction({ ...sanction, type: e.target.value })}>
                      {enumOptions(TYPE_SANCTION).map((option) => (
                        <option key={option.value} value={option.value}>
                          {option.label}
                        </option>
                      ))}
                    </Select>
                  </div>
                  <div>
                    <Label className="mb-1 block">Motif</Label>
                    <Input value={sanction.motif} onChange={(e) => setSanction({ ...sanction, motif: e.target.value })} />
                  </div>
                  <div>
                    <Label className="mb-1 block">Durée (jours)</Label>
                    <Input type="number" value={sanction.dureeJours} onChange={(e) => setSanction({ ...sanction, dureeJours: e.target.value })} />
                  </div>
                  <div className="lg:col-span-4">
                    <Button type="submit" disabled={busy || !sanction.dateSanction}>
                      <Plus className="h-4 w-4" />
                      Prononcer
                    </Button>
                  </div>
                </form>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="liens">
          <Card className="mb-4">
            <CardContent className="overflow-x-auto p-0">
              {liens.data?.length ? (
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                      <th className="px-4 py-3">Parent</th>
                      <th className="px-4 py-3">Lien</th>
                      <th className="px-4 py-3">Principal</th>
                      <th className="px-4 py-3">Payeur</th>
                      <th className="px-4 py-3">Notifications</th>
                    </tr>
                  </thead>
                  <tbody>
                    {liens.data.map((item, index) => {
                      const parent = (parents.data ?? []).find((p) => p.id === item.parentId);
                      return (
                        <tr key={item.id ?? index} className="border-b border-border last:border-0">
                          <td className="px-4 py-3">
                            {parent ? `${parent.prenom} ${parent.nom}` : <span className="font-mono text-xs">{item.parentId}</span>}
                          </td>
                          <td className="px-4 py-3">{item.type.replace(/_/g, " ")}</td>
                          <td className="px-4 py-3">{item.principal ? "Oui" : "Non"}</td>
                          <td className="px-4 py-3">{item.payeur ? "Oui" : "Non"}</td>
                          <td className="px-4 py-3">{item.receveurNotifications ? "Oui" : "Non"}</td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              ) : (
                <EmptyState label="Aucun parent lié" />
              )}
            </CardContent>
          </Card>

          {isAdmin && (
            <Card>
              <CardHeader className="p-4 pb-2">
                <CardTitle className="text-base">Lier un parent</CardTitle>
              </CardHeader>
              <CardContent className="p-4 pt-2">
                <form
                  className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4"
                  onSubmit={(event) => {
                    event.preventDefault();
                    run(
                      () =>
                        peopleApi.eleves.lierParent(id, {
                          parentId: lien.parentId,
                          type: lien.type as never,
                          principal: lien.principal,
                          payeur: lien.payeur,
                          receveurNotifications: lien.receveurNotifications,
                        }),
                      "Parent lié",
                      liens.reload,
                    );
                  }}
                >
                  <div>
                    <Label className="mb-1 block">Parent</Label>
                    <Select value={lien.parentId} onChange={(e) => setLien({ ...lien, parentId: e.target.value })}>
                      <option value="">—</option>
                      {(parents.data ?? []).map((item) => (
                        <option key={item.id} value={item.id}>
                          {item.prenom} {item.nom}
                        </option>
                      ))}
                    </Select>
                  </div>
                  <div>
                    <Label className="mb-1 block">Type de lien</Label>
                    <Select value={lien.type} onChange={(e) => setLien({ ...lien, type: e.target.value })}>
                      {enumOptions(TYPE_LIEN).map((option) => (
                        <option key={option.value} value={option.value}>
                          {option.label}
                        </option>
                      ))}
                    </Select>
                  </div>
                  <div className="flex flex-wrap items-end gap-3">
                    <div className="flex h-10 items-center gap-2">
                      <Checkbox checked={lien.principal} onChange={(v) => setLien({ ...lien, principal: v })} />
                      <Label>Principal</Label>
                    </div>
                    <div className="flex h-10 items-center gap-2">
                      <Checkbox checked={lien.payeur} onChange={(v) => setLien({ ...lien, payeur: v })} />
                      <Label>Payeur</Label>
                    </div>
                    <div className="flex h-10 items-center gap-2">
                      <Checkbox checked={lien.receveurNotifications} onChange={(v) => setLien({ ...lien, receveurNotifications: v })} />
                      <Label>Notifs</Label>
                    </div>
                  </div>
                  <div className="lg:col-span-4">
                    <Button type="submit" disabled={busy || !lien.parentId}>
                      <Plus className="h-4 w-4" />
                      Lier
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
