"use client";

import { useState } from "react";
import { Activity, AlarmClock, BookOpenCheck, Search, Users } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { PageHeader, LoadingView, ErrorState, EmptyState } from "@/components/common/async-state";
import { formatDate } from "@/components/pedagogie/format";
import { useApi } from "@/hooks/use-api";
import { useTenant } from "@/components/providers/tenant-provider";
import { attendanceApi } from "@/services/api";
import type { SuiviAssiduite } from "@/types";
import type { ApiError } from "@/types";

export default function AssiduitePage() {
  const { tenantId } = useTenant();

  const [eleveId, setEleveId] = useState("");
  const [classeId, setClasseId] = useState("");
  const [date, setDate] = useState("");

  const [suivi, setSuivi] = useState<SuiviAssiduite | null>(null);
  const [suiviLoading, setSuiviLoading] = useState(false);
  const [suiviError, setSuiviError] = useState<ApiError | null>(null);

  const classeTaux = useApi<number>(
    () => attendanceApi.assiduite.classeTaux(classeId),
    [classeId, tenantId],
  );
  const registre = useApi(
    () => attendanceApi.assiduite.registre({ classeId: classeId || undefined, date: date || undefined }),
    [classeId, date, tenantId],
  );

  const chargerSuivi = async () => {
    if (!eleveId) return;
    setSuiviLoading(true);
    setSuiviError(null);
    try {
      setSuivi(await attendanceApi.assiduite.eleve(eleveId));
    } catch (error) {
      setSuiviError(error as ApiError);
    } finally {
      setSuiviLoading(false);
    }
  };

  return (
    <div>
      <PageHeader title="Suivi d'assiduité" description="Taux de présence individuels, par classe et registre (lot 4d)." />

      <div className="grid gap-4 lg:grid-cols-2">
        <Card>
          <CardHeader className="p-4 pb-2">
            <CardTitle className="flex items-center gap-2 text-base">
              <Activity className="h-4 w-4" />
              Suivi d&apos;un élève
            </CardTitle>
          </CardHeader>
          <CardContent className="p-4 pt-2">
            <form
              className="mb-4 flex items-end gap-2"
              onSubmit={(event) => {
                event.preventDefault();
                chargerSuivi();
              }}
            >
              <div className="flex-1">
                <Label className="mb-1 block">Élève (UUID)</Label>
                <Input value={eleveId} onChange={(event) => setEleveId(event.target.value)} placeholder="121f5678-…" />
              </div>
              <Button type="submit" disabled={!eleveId || suiviLoading}>
                <Search className="h-4 w-4" />
                Consulter
              </Button>
            </form>

            {suiviLoading ? (
              <LoadingView label="Calcul du suivi…" />
            ) : suiviError ? (
              <ErrorState error={suiviError} onRetry={chargerSuivi} />
            ) : suivi ? (
              <div className="space-y-4">
                <div className="flex flex-wrap gap-2">
                  <Badge variant="success">{suivi.tauxPresence != null ? `${suivi.tauxPresence} % présence` : "—"}</Badge>
                  <Badge variant="outline">Séances tenues {suivi.nbSeancesTenues ?? "—"}</Badge>
                  <Badge variant="outline">Abs. justifiées {suivi.nbAbsencesJustifiees ?? 0}</Badge>
                  <Badge variant="danger">Abs. injustifiées {suivi.nbAbsencesInjustifiees ?? 0}</Badge>
                  <Badge variant="warning">Retards {suivi.nbRetards ?? 0}</Badge>
                </div>
                <dl className="grid gap-2 text-sm sm:grid-cols-2">
                  <div className="flex justify-between rounded-md border p-2">
                    <dt className="text-slate-500">Heures prévues</dt>
                    <dd className="font-medium">{suivi.nbHeuresPrevues}</dd>
                  </div>
                  <div className="flex justify-between rounded-md border p-2">
                    <dt className="text-slate-500">Heures absentes</dt>
                    <dd className="font-medium">{suivi.nbHeuresAbsentes}</dd>
                  </div>
                  <div className="flex justify-between rounded-md border p-2">
                    <dt className="text-slate-500">Minutes de retard</dt>
                    <dd className="font-medium">{suivi.minutesRetardTotal ?? 0}</dd>
                  </div>
                  <div className="flex justify-between rounded-md border p-2">
                    <dt className="text-slate-500">Règle applicable</dt>
                    <dd className="font-medium">{suivi.regleApplicable ?? "—"}</dd>
                  </div>
                </dl>
                {suivi.respecteRegle != null && (
                  <Badge variant={suivi.respecteRegle ? "success" : "danger"}>
                    Règle {suivi.respecteRegle ? "respectée" : "non respectée"}
                  </Badge>
                )}
              </div>
            ) : (
              <EmptyState label="Saisissez un élève pour voir son suivi" />
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="p-4 pb-2">
            <CardTitle className="flex items-center gap-2 text-base">
              <Users className="h-4 w-4" />
              Taux de présence — classe
            </CardTitle>
          </CardHeader>
          <CardContent className="p-4 pt-2">
            <form
              className="mb-4 flex items-end gap-2"
              onSubmit={(event) => {
                event.preventDefault();
                if (classeId) classeTaux.reload();
              }}
            >
              <div className="flex-1">
                <Label className="mb-1 block">Classe (UUID)</Label>
                <Input value={classeId} onChange={(event) => setClasseId(event.target.value)} placeholder="…" />
              </div>
              <Button type="submit" disabled={!classeId}>
                <BookOpenCheck className="h-4 w-4" />
                Calculer
              </Button>
            </form>
            {classeTaux.loading ? (
              <LoadingView label="Calcul du taux…" />
            ) : classeTaux.error ? (
              <ErrorState error={classeTaux.error} onRetry={classeTaux.reload} />
            ) : classeTaux.data != null ? (
              <p className="text-2xl font-semibold text-blue-600">{classeTaux.data} %</p>
            ) : (
              <EmptyState label="Calculez le taux d'une classe" />
            )}
          </CardContent>
        </Card>
      </div>

      <Card className="mt-4">
        <CardHeader className="p-4 pb-2">
          <CardTitle className="flex items-center gap-2 text-base">
            <AlarmClock className="h-4 w-4" />
            Registre des feuilles
          </CardTitle>
        </CardHeader>
        <CardContent className="p-4 pt-2">
          <div className="mb-4 flex flex-wrap items-end gap-2">
            <div className="w-64">
              <Label className="mb-1 block">Classe (UUID)</Label>
              <Input value={classeId} onChange={(event) => setClasseId(event.target.value)} />
            </div>
            <div className="w-40">
              <Label className="mb-1 block">Date</Label>
              <Input type="date" value={date} onChange={(event) => setDate(event.target.value)} />
            </div>
            <Button onClick={() => registre.reload()}>Filtrer</Button>
          </div>
          {registre.loading ? (
            <LoadingView label="Chargement du registre…" />
          ) : registre.error ? (
            <ErrorState error={registre.error} onRetry={registre.reload} />
          ) : registre.data?.length ? (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  <th className="px-3 py-2">Feuille</th>
                  <th className="px-3 py-2">Date</th>
                  <th className="px-3 py-2">Statut</th>
                  <th className="px-3 py-2">Taux</th>
                </tr>
              </thead>
              <tbody>
                {registre.data.map((feuille) => (
                  <tr key={feuille.idFeuille} className="border-b border-border last:border-0">
                    <td className="px-3 py-2 font-mono text-xs">{feuille.idFeuille.slice(0, 8)}…</td>
                    <td className="px-3 py-2">{formatDate(feuille.date)}</td>
                    <td className="px-3 py-2">{feuille.statut}</td>
                    <td className="px-3 py-2">{feuille.tauxPresence != null ? `${feuille.tauxPresence} %` : "—"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <EmptyState label="Aucune feuille pour ces critères" />
          )}
        </CardContent>
      </Card>
    </div>
  );
}