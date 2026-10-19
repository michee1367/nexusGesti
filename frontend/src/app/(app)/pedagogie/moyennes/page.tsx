"use client";

import { useState } from "react";
import { BarChart3, GraduationCap, Trophy } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { PageHeader, LoadingView, ErrorState, EmptyState } from "@/components/common/async-state";
import { EntitySelect } from "@/components/common/entity-select";
import { classeLabel, eleveLabel, evaluationLabel, lookupLabel, periodeLabel } from "@/components/pedagogie/labels";
import { useApi } from "@/hooks/use-api";
import { useElevesMap } from "@/hooks/use-lookup";
import { useTenant } from "@/components/providers/tenant-provider";
import { anneeApi, assessmentApi, peopleApi, structureApi } from "@/services/api";
import type { Classement, Periode, ResultatMoyenne } from "@/types";

export default function MoyennesPage() {
  const { tenantId } = useTenant();
  const eleves = useElevesMap([tenantId]);

  const [evaluationId, setEvaluationId] = useState("");
  const [evalResultat, setEvalResultat] = useState<ResultatMoyenne | null>(null);
  const [evalLoading, setEvalLoading] = useState(false);

  const [eleveId, setEleveId] = useState("");
  const [periodeId, setPeriodeId] = useState("");
  const eleveMoyenne = useApi<ResultatMoyenne>(
    () => assessmentApi.moyennes.eleve(eleveId, periodeId || undefined),
    [eleveId, periodeId, tenantId],
  );

  const [classeId, setClasseId] = useState("");
  const classement = useApi<Classement[]>(
    () => assessmentApi.moyennes.classe(classeId),
    [classeId, tenantId],
  );

  const chargerEvaluation = async () => {
    if (!evaluationId) return;
    setEvalLoading(true);
    try {
      setEvalResultat(await assessmentApi.moyennes.evaluation(evaluationId));
    } catch {
      setEvalResultat(null);
    } finally {
      setEvalLoading(false);
    }
  };

  return (
    <div>
      <PageHeader title="Moyennes & classement" description="Calculs de moyennes par évaluation, par élève et classement de classe (lot 4e)." />

      <div className="grid gap-4 lg:grid-cols-3">
        <Card>
          <CardHeader className="p-4 pb-2">
            <CardTitle className="flex items-center gap-2 text-base">
              <BarChart3 className="h-4 w-4" />
              Moyenne d&apos;une évaluation
            </CardTitle>
          </CardHeader>
          <CardContent className="p-4 pt-2">
            <form
              className="mb-3 flex items-end gap-2"
              onSubmit={(event) => {
                event.preventDefault();
                chargerEvaluation();
              }}
            >
              <div className="flex-1">
                <Label className="mb-1 block">Évaluation</Label>
                <EntitySelect
                  fetcher={() => assessmentApi.evaluations.list()}
                  keyOf={(evaluation) => evaluation.idEvaluation}
                  labelOf={evaluationLabel}
                  value={evaluationId}
                  onChange={setEvaluationId}
                  deps={[tenantId]}
                  placeholder="Sélectionner une évaluation…"
                />
              </div>
              <Button type="submit" disabled={!evaluationId || evalLoading}>
                Calculer
              </Button>
            </form>
            {evalLoading ? (
              <LoadingView label="Calcul…" />
            ) : evalResultat ? (
              <div>
                <p className="text-3xl font-bold text-blue-600">{evalResultat.moyenne ?? "—"}</p>
                <div className="mt-2 flex flex-wrap gap-2">
                  <Badge variant="outline">Notes {evalResultat.nbNotes}</Badge>
                  <Badge variant="outline">Effectif {evalResultat.effectif}</Badge>
                </div>
              </div>
            ) : (
              <EmptyState label="Saisissez une évaluation" />
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="p-4 pb-2">
            <CardTitle className="flex items-center gap-2 text-base">
              <GraduationCap className="h-4 w-4" />
              Moyenne d&apos;un élève
            </CardTitle>
          </CardHeader>
          <CardContent className="p-4 pt-2">
            <form
              className="mb-3 flex flex-wrap items-end gap-2"
              onSubmit={(event) => {
                event.preventDefault();
                if (eleveId) eleveMoyenne.reload();
              }}
            >
              <div className="w-40 flex-1">
                <Label className="mb-1 block">Élève</Label>
                <EntitySelect
                  fetcher={() => peopleApi.eleves.list()}
                  keyOf={(eleve) => eleve.idEleve}
                  labelOf={eleveLabel}
                  value={eleveId}
                  onChange={setEleveId}
                  deps={[tenantId]}
                  placeholder="Sélectionner un élève…"
                />
              </div>
              <div className="w-40">
                <Label className="mb-1 block">Période</Label>
                <EntitySelect<Periode>
                  fetcher={async () => (await anneeApi.list()).flatMap((annee) => annee.periodes ?? [])}
                  keyOf={(periode) => periode.idPeriode}
                  labelOf={periodeLabel}
                  value={periodeId}
                  onChange={setPeriodeId}
                  deps={[tenantId]}
                  placeholder="Sélectionner…"
                />
              </div>
              <Button type="submit" disabled={!eleveId}>
                Calculer
              </Button>
            </form>
            {eleveMoyenne.loading ? (
              <LoadingView label="Calcul…" />
            ) : eleveMoyenne.error ? (
              <ErrorState error={eleveMoyenne.error} onRetry={eleveMoyenne.reload} />
            ) : eleveMoyenne.data ? (
              <div>
                <p className="text-3xl font-bold text-blue-600">{eleveMoyenne.data.moyenne ?? "—"}</p>
                <div className="mt-2 flex flex-wrap gap-2">
                  <Badge variant="outline">Notes {eleveMoyenne.data.nbNotes}</Badge>
                  <Badge variant="outline">Effectif {eleveMoyenne.data.effectif}</Badge>
                </div>
              </div>
            ) : (
              <EmptyState label="Saisissez un élève" />
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="p-4 pb-2">
            <CardTitle className="flex items-center gap-2 text-base">
              <Trophy className="h-4 w-4" />
              Classement d&apos;une classe
            </CardTitle>
          </CardHeader>
          <CardContent className="p-4 pt-2">
            <form
              className="mb-3 flex items-end gap-2"
              onSubmit={(event) => {
                event.preventDefault();
                if (classeId) classement.reload();
              }}
            >
              <div className="flex-1">
                <Label className="mb-1 block">Classe</Label>
                <EntitySelect
                  fetcher={() => structureApi.classes.list()}
                  keyOf={(classe) => classe.idClasse}
                  labelOf={classeLabel}
                  value={classeId}
                  onChange={setClasseId}
                  deps={[tenantId]}
                  placeholder="Sélectionner une classe…"
                />
              </div>
              <Button type="submit" disabled={!classeId}>
                Calculer
              </Button>
            </form>
            {classement.loading ? (
              <LoadingView label="Classement…" />
            ) : classement.error ? (
              <ErrorState error={classement.error} onRetry={classement.reload} />
            ) : classement.data?.length ? (
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                    <th className="px-2 py-1">Rang</th>
                    <th className="px-2 py-1">Élève</th>
                    <th className="px-2 py-1">Moyenne</th>
                  </tr>
                </thead>
                <tbody>
                  {classement.data.map((item) => (
                    <tr key={item.eleveId} className="border-b border-border last:border-0">
                      <td className="px-2 py-1 font-semibold">#{item.rang}</td>
                      <td className="px-2 py-1 text-xs">{lookupLabel(eleves, item.eleveId)}</td>
                      <td className="px-2 py-1">{item.moyenne}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <EmptyState label="Saisissez une classe" />
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}