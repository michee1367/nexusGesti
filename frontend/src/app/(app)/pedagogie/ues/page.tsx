"use client";

import { useMemo } from "react";
import { Badge } from "@/components/ui/badge";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { toOptions } from "@/components/pedagogie/constants";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { useApi } from "@/hooks/use-api";
import { structureApi } from "@/services/api";
import type { UE } from "@/types";

export default function UesPage() {
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const semestres = useApi(structureApi.semestres.list, [tenantId]);

  const fields = useMemo<FieldDef[]>(
    () => [
      { name: "code", label: "Code", required: true },
      { name: "libelle", label: "Libellé", required: true },
      { name: "semestreId", label: "Semestre", type: "select", options: toOptions(semestres.data, "idSemestre", "libelle") },
      { name: "credits", label: "Crédits", type: "number" },
      { name: "coefficient", label: "Coefficient", type: "number" },
      { name: "volumeHoraire", label: "Volume horaire", type: "number" },
      { name: "niveauAcalcul", label: "Niveau de calcul" },
      { name: "obligatoire", label: "Obligatoire", type: "checkbox", defaultValue: true },
    ],
    [semestres.data],
  );

  const columns: ColumnDef<UE>[] = [
    { key: "code", label: "Code", render: (row) => <span className="font-mono">{row.code}</span> },
    { key: "libelle", label: "Libellé", render: (row) => <span className="font-medium text-slate-900">{row.libelle}</span> },
    { key: "credits", label: "Crédits" },
    { key: "coefficient", label: "Coef." },
    {
      key: "obligatoire",
      label: "Régime",
      render: (row) =>
        row.obligatoire ? <Badge variant="secondary">Obligatoire</Badge> : <Badge variant="outline">Option</Badge>,
    },
  ];

  return (
    <ResourceCrud<UE>
      title="Unités d'enseignement"
      description="UE rattachées aux semestres (cursus universitaire)."
      idKey="idUE"
      list={structureApi.ues.list}
      create={structureApi.ues.create}
      update={structureApi.ues.update}
      fields={fields}
      columns={columns}
      searchKeys={["code", "libelle"]}
      canWrite={isAdmin}
    />
  );
}
