"use client";

import { useState } from "react";
import { toast } from "sonner";
import { GraduationCap, Search, ClipboardCheck } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { PageHeader, LoadingView, ErrorState, EmptyState } from "@/components/common/async-state";
import { EntitySelect } from "@/components/common/entity-select";
import { formatDate } from "@/components/pedagogie/format";
import { eleveLabel, lookupLabel, programmeLabel } from "@/components/pedagogie/labels";
import { useApi } from "@/hooks/use-api";
import { useUesMap } from "@/hooks/use-lookup";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { enrollmentApi, etablissementApi, peopleApi } from "@/services/api";
import type { Candidat, ParcoursAcademique } from "@/types";

export default function AdmissionsPage() {
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const ues = useUesMap([tenantId]);

  const [candidat, setCandidat] = useState<Candidat>({
    eleveId: "",
    programmeId: "",
    creditsValides: 0,
    creditsRequis: 0,
    moyenneGenerale: 0,
    age: 0,
    niveauxValides: [],
  });
  const [decision, setDecision] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const [parcoursEleve, setParcoursEleve] = useState("");
  const [creditsRequis, setCreditsRequis] = useState(60);
  const parcours = useApi<ParcoursAcademique>(
    () => enrollmentApi.parcours(parcoursEleve, { creditsRequis }),
    [parcoursEleve, creditsRequis, tenantId],
  );

  const evaluer = async () => {
    if (!candidat.eleveId) return;
    setBusy(true);
    try {
      const result = await enrollmentApi.evaluer(candidat);
      setDecision(result.decision);
      toast.success(`Décision : ${result.decision}`);
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  return (
    <div>
      <PageHeader title="Admissions" description="Évaluation d'éligibilité et parcours académique (lot 4c)." />

      {isAdmin && (
        <Card className="mb-4">
          <CardHeader className="p-4 pb-2">
            <CardTitle className="flex items-center gap-2 text-base">
              <ClipboardCheck className="h-4 w-4" />
              Évaluer un candidat
            </CardTitle>
          </CardHeader>
          <CardContent className="p-4 pt-2">
            <form
              className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4"
              onSubmit={(event) => {
                event.preventDefault();
                evaluer();
              }}
            >
              <div>
                <Label className="mb-1 block">Élève *</Label>
                <EntitySelect
                  fetcher={() => peopleApi.eleves.list()}
                  keyOf={(eleve) => eleve.idEleve}
                  labelOf={eleveLabel}
                  value={candidat.eleveId}
                  onChange={(value) => setCandidat({ ...candidat, eleveId: value })}
                  deps={[tenantId]}
                  placeholder="Sélectionner un élève…"
                />
              </div>
              <div>
                <Label className="mb-1 block">Programme</Label>
                <EntitySelect
                  fetcher={() => etablissementApi.programmes.list()}
                  keyOf={(programme) => programme.idProgramme}
                  labelOf={programmeLabel}
                  value={candidat.programmeId ?? ""}
                  onChange={(value) => setCandidat({ ...candidat, programmeId: value })}
                  deps={[tenantId]}
                  placeholder="Sélectionner un programme…"
                />
              </div>
              <div>
                <Label className="mb-1 block">Crédits validés</Label>
                <Input
                  type="number"
                  value={candidat.creditsValides ?? 0}
                  onChange={(event) => setCandidat({ ...candidat, creditsValides: Number(event.target.value) })}
                />
              </div>
              <div>
                <Label className="mb-1 block">Crédits requis</Label>
                <Input
                  type="number"
                  value={candidat.creditsRequis ?? 0}
                  onChange={(event) => setCandidat({ ...candidat, creditsRequis: Number(event.target.value) })}
                />
              </div>
              <div>
                <Label className="mb-1 block">Moyenne générale</Label>
                <Input
                  type="number"
                  step="0.01"
                  value={candidat.moyenneGenerale ?? 0}
                  onChange={(event) => setCandidat({ ...candidat, moyenneGenerale: Number(event.target.value) })}
                />
              </div>
              <div>
                <Label className="mb-1 block">Âge</Label>
                <Input
                  type="number"
                  value={candidat.age ?? 0}
                  onChange={(event) => setCandidat({ ...candidat, age: Number(event.target.value) })}
                />
              </div>
              <div className="flex items-end">
                <Button type="submit" disabled={busy || !candidat.eleveId}>
                  Évaluer
                </Button>
              </div>
              {decision && (
                <div className="flex items-end">
                  <Badge variant="success">{decision}</Badge>
                </div>
              )}
            </form>
          </CardContent>
        </Card>
      )}

      <Card>
        <CardHeader className="p-4 pb-2">
          <CardTitle className="flex items-center gap-2 text-base">
            <GraduationCap className="h-4 w-4" />
            Parcours académique d&apos;un élève
          </CardTitle>
        </CardHeader>
        <CardContent className="p-4 pt-2">
          <form
            className="mb-4 flex flex-wrap items-end gap-2"
            onSubmit={(event) => {
              event.preventDefault();
              if (parcoursEleve) parcours.reload();
            }}
          >
            <div className="min-w-64 flex-1">
              <Label className="mb-1 block">Élève</Label>
              <EntitySelect
                fetcher={() => peopleApi.eleves.list()}
                keyOf={(eleve) => eleve.idEleve}
                labelOf={eleveLabel}
                value={parcoursEleve}
                onChange={setParcoursEleve}
                deps={[tenantId]}
                placeholder="Sélectionner un élève…"
              />
            </div>
            <div className="w-40">
              <Label className="mb-1 block">Crédits requis</Label>
              <Input type="number" value={creditsRequis} onChange={(event) => setCreditsRequis(Number(event.target.value))} />
            </div>
            <Button disabled={!parcoursEleve}>
              <Search className="h-4 w-4" />
              Consulter
            </Button>
          </form>

          {parcours.loading ? (
            <LoadingView label="Calcul du parcours…" />
          ) : parcours.error ? (
            <ErrorState error={parcours.error} onRetry={parcours.reload} />
          ) : parcours.data ? (
            <div>
              <div className="mb-3 flex flex-wrap gap-2">
                <Badge variant="outline">Crédits acquis {parcours.data.creditsAcquis}</Badge>
                <Badge variant="outline">Taux {Math.round(parcours.data.tauxProgression * 100)}%</Badge>
                <Badge variant="outline">UE validées {parcours.data.nombreValidees}</Badge>
                {parcours.data.moyennePonderee != null && (
                  <Badge variant="success">Moyenne {parcours.data.moyennePonderee}</Badge>
                )}
              </div>
              {parcours.data.inscriptions?.length ? (
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                      <th className="px-3 py-2">UE</th>
                      <th className="px-3 py-2">Crédits</th>
                      <th className="px-3 py-2">Statut</th>
                      <th className="px-3 py-2">Date</th>
                    </tr>
                  </thead>
                  <tbody>
                    {parcours.data.inscriptions.map((item) => (
                      <tr key={item.idInscription} className="border-b border-border last:border-0">
                        <td className="px-3 py-2 text-xs">{lookupLabel(ues, item.ueId)}</td>
                        <td className="px-3 py-2">{item.creditsAcquis}</td>
                        <td className="px-3 py-2">{item.statut ?? "—"}</td>
                        <td className="px-3 py-2">{formatDate(item.dateInscription)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <EmptyState label="Aucune inscription" />
              )}
            </div>
          ) : (
            <EmptyState label="Saisissez un élève pour calculer son parcours" />
          )}
        </CardContent>
      </Card>
    </div>
  );
}