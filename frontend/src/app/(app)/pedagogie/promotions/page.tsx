"use client";

import { useMemo } from "react";
import { Badge } from "@/components/ui/badge";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, STATUT_CLASSE, toOptions } from "@/components/pedagogie/constants";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { useApi } from "@/hooks/use-api";
import { anneeApi, etablissementApi, structureApi } from "@/services/api";
import type { Promotion } from "@/types";

export default function PromotionsPage() {
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const programmes = useApi(etablissementApi.programmes.list, [tenantId]);
  const semestres = useApi(structureApi.semestres.list, [tenantId]);
  const annees = useApi(anneeApi.list, [tenantId]);

  const fields = useMemo<FieldDef[]>(
    () => [
      { name: "code", label: "Code", required: true },
      { name: "libelle", label: "Libellé", required: true },
      { name: "programmeId", label: "Programme", type: "select", options: toOptions(programmes.data, "idProgramme", "libelle") },
      { name: "semestreId", label: "Semestre", type: "select", options: toOptions(semestres.data, "idSemestre", "libelle") },
      { name: "anneeId", label: "Année", type: "select", options: toOptions(annees.data, "idAnnee", "libelle") },
      { name: "capacite", label: "Capacité", type: "number" },
      { name: "statut", label: "Statut", type: "select", options: enumOptions(STATUT_CLASSE), defaultValue: "OUVERTE" },
    ],
    [programmes.data, semestres.data, annees.data],
  );

  const columns: ColumnDef<Promotion>[] = [
    { key: "code", label: "Code", render: (row) => <span className="font-mono">{row.code}</span> },
    { key: "libelle", label: "Libellé", render: (row) => <span className="font-medium text-slate-900">{row.libelle}</span> },
    { key: "effectifReel", label: "Effectif" },
    {
      key: "statut",
      label: "Statut",
      render: (row) => <Badge variant="secondary">{row.statut.replace(/_/g, " ")}</Badge>,
    },
  ];

  return (
    <ResourceCrud<Promotion>
      title="Promotions"
      description="Cohortes d'étudiants rattachées à un programme et un semestre."
      idKey="idPromotion"
      list={structureApi.promotions.list}
      create={structureApi.promotions.create}
      update={structureApi.promotions.update}
      fields={fields}
      columns={columns}
      searchKeys={["code", "libelle"]}
      canWrite={isAdmin}
    />
  );
}
