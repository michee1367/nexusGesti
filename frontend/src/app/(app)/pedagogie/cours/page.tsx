"use client";

import { useMemo } from "react";
import { Badge } from "@/components/ui/badge";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, toOptions, TYPE_EVALUATION } from "@/components/pedagogie/constants";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { useApi } from "@/hooks/use-api";
import { structureApi } from "@/services/api";
import type { CoursECUE } from "@/types";

export default function CoursPage() {
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const ues = useApi(structureApi.ues.list, [tenantId]);
  const classes = useApi(structureApi.classes.list, [tenantId]);
  const matieres = useApi(structureApi.matieres.list, [tenantId]);

  const fields = useMemo<FieldDef[]>(
    () => [
      { name: "code", label: "Code", required: true },
      { name: "libelle", label: "Libellé", required: true },
      { name: "matiereId", label: "Matière", type: "select", options: toOptions(matieres.data, "idMatiere", "libelle") },
      { name: "ueId", label: "UE", type: "select", options: toOptions(ues.data, "idUE", "libelle") },
      { name: "classeId", label: "Classe", type: "select", options: toOptions(classes.data, "idClasse", "libelle") },
      { name: "type", label: "Type d'évaluation", type: "select", options: enumOptions(TYPE_EVALUATION) },
      { name: "volumeHoraire", label: "Volume horaire", type: "number" },
      { name: "credits", label: "Crédits", type: "number" },
      { name: "coefficient", label: "Coefficient", type: "number" },
      { name: "regroupement", label: "Regroupement" },
      { name: "hebdomadaire", label: "Hebdomadaire", type: "checkbox" },
    ],
    [ues.data, classes.data, matieres.data],
  );

  const columns: ColumnDef<CoursECUE>[] = [
    { key: "code", label: "Code", render: (row) => <span className="font-mono">{row.code}</span> },
    { key: "libelle", label: "Libellé", render: (row) => <span className="font-medium text-slate-900">{row.libelle}</span> },
    { key: "type", label: "Type" },
    { key: "volumeHoraire", label: "Volume" },
    { key: "credits", label: "Crédits" },
    {
      key: "hebdomadaire",
      label: "Cadence",
      render: (row) =>
        row.hebdomadaire ? <Badge variant="secondary">Hebdo</Badge> : <Badge variant="outline">Ponctuel</Badge>,
    },
  ];

  return (
    <ResourceCrud<CoursECUE>
      title="Cours / ECUE"
      description="Cours et éléments constitutifs d'UE (matière + classe + évaluation)."
      idKey="idCours"
      list={structureApi.cours.list}
      create={structureApi.cours.create}
      update={structureApi.cours.update}
      fields={fields}
      columns={columns}
      searchKeys={["code", "libelle"]}
      canWrite={isAdmin}
    />
  );
}
