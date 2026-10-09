"use client";

import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { useAuth } from "@/components/providers/auth-provider";
import { structureApi } from "@/services/api";
import type { Semestre } from "@/types";

const fields: FieldDef[] = [
  { name: "code", label: "Code", required: true },
  { name: "libelle", label: "Libellé", required: true },
  { name: "rang", label: "Rang", type: "number", defaultValue: 1 },
  { name: "totalCreditsAttendu", label: "Crédits attendus", type: "number" },
];

const columns: ColumnDef<Semestre>[] = [
  { key: "code", label: "Code", render: (row) => <span className="font-mono">{row.code}</span> },
  { key: "libelle", label: "Libellé", render: (row) => <span className="font-medium text-slate-900">{row.libelle}</span> },
  { key: "rang", label: "Rang" },
  { key: "totalCreditsAttendu", label: "Crédits attendus" },
];

export default function SemestresPage() {
  const { isAdmin } = useAuth();
  return (
    <ResourceCrud<Semestre>
      title="Semestres"
      description="Découpage semestriel du cursus universitaire."
      idKey="idSemestre"
      list={structureApi.semestres.list}
      create={structureApi.semestres.create}
      update={structureApi.semestres.update}
      fields={fields}
      columns={columns}
      searchKeys={["code", "libelle"]}
      canWrite={isAdmin}
    />
  );
}
