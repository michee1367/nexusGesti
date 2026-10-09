"use client";

import { useMemo, useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, STATUT_CLASSE, toOptions } from "@/components/pedagogie/constants";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { useApi } from "@/hooks/use-api";
import { anneeApi, etablissementApi, structureApi } from "@/services/api";
import type { Classe } from "@/types";

function ClasseActions({ classe, reload }: { classe: Classe; reload: () => void }) {
  const [busy, setBusy] = useState(false);
  const act = async (action: () => Promise<unknown>, message: string) => {
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
  return (
    <>
      {classe.statut === "OUVERTE" && (
        <Button
          size="sm"
          variant="outline"
          disabled={busy}
          onClick={() => act(() => structureApi.classes.ouvrir(classe.idClasse), "Classe ouverte")}
        >
          Ouvrir
        </Button>
      )}
      {classe.statut === "EN_COURS" && (
        <Button
          size="sm"
          variant="outline"
          disabled={busy}
          onClick={() => act(() => structureApi.classes.cloturer(classe.idClasse), "Classe clôturée")}
        >
          Clôturer
        </Button>
      )}
    </>
  );
}

export default function ClassesPage() {
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const niveaux = useApi(structureApi.niveaux.list, [tenantId]);
  const annees = useApi(anneeApi.list, [tenantId]);
  const sites = useApi(etablissementApi.sites.list, [tenantId]);
  const regimes = useApi(structureApi.regimesHoraires.list, [tenantId]);

  const fields = useMemo<FieldDef[]>(
    () => [
      { name: "code", label: "Code", required: true },
      { name: "libelle", label: "Libellé", required: true },
      { name: "niveauId", label: "Niveau", type: "select", options: toOptions(niveaux.data, "idNiveau", "libelle") },
      { name: "anneeId", label: "Année", type: "select", options: toOptions(annees.data, "idAnnee", "libelle") },
      { name: "siteId", label: "Site", type: "select", options: toOptions(sites.data, "idSite", "libelle") },
      { name: "regimeHoraireCode", label: "Régime horaire", type: "select", options: toOptions(regimes.data, "code", "libelle") },
      { name: "capacite", label: "Capacité", type: "number" },
      { name: "statut", label: "Statut", type: "select", options: enumOptions(STATUT_CLASSE), defaultValue: "OUVERTE" },
    ],
    [niveaux.data, annees.data, sites.data, regimes.data],
  );

  const columns: ColumnDef<Classe>[] = [
    { key: "code", label: "Code", render: (row) => <span className="font-mono">{row.code}</span> },
    { key: "libelle", label: "Libellé", render: (row) => <span className="font-medium text-slate-900">{row.libelle}</span> },
    { key: "effectifReel", label: "Effectif" },
    { key: "capacite", label: "Capacité" },
    { key: "regimeHoraireCode", label: "Régime" },
    {
      key: "statut",
      label: "Statut",
      render: (row) => (
        <Badge variant={row.statut === "EN_COURS" ? "success" : row.statut === "CLOTUREE" ? "outline" : "secondary"}>
          {row.statut.replace(/_/g, " ")}
        </Badge>
      ),
    },
  ];

  return (
    <ResourceCrud<Classe>
      title="Classes"
      description="Groupes d'élèves rattachés à un niveau et une année scolaire."
      idKey="idClasse"
      list={structureApi.classes.list}
      create={structureApi.classes.create}
      update={structureApi.classes.update}
      fields={fields}
      columns={columns}
      searchKeys={["code", "libelle"]}
      canWrite={isAdmin}
      rowActions={(row, reload) => <ClasseActions classe={row} reload={reload} />}
    />
  );
}
