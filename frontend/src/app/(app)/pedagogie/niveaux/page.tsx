"use client";

import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, TYPE_CYCLE } from "@/components/pedagogie/constants";
import { useAuth } from "@/components/providers/auth-provider";
import { structureApi } from "@/services/api";
import type { Niveau } from "@/types";

const fields: FieldDef[] = [
  { name: "code", label: "Code", required: true },
  { name: "libelle", label: "Libellé", required: true },
  { name: "cycle", label: "Cycle", type: "select", options: enumOptions(TYPE_CYCLE) },
  { name: "rang", label: "Rang", type: "number", defaultValue: 1 },
  { name: "ordre", label: "Ordre", type: "number", defaultValue: 1 },
];

const columns: ColumnDef<Niveau>[] = [
  { key: "code", label: "Code", render: (row) => <span className="font-mono">{row.code}</span> },
  { key: "libelle", label: "Libellé", render: (row) => <span className="font-medium text-slate-900">{row.libelle}</span> },
  { key: "cycle", label: "Cycle" },
  { key: "rang", label: "Rang" },
  { key: "ordre", label: "Ordre" },
];

export default function NiveauxPage() {
  const { isAdmin } = useAuth();
  return (
    <ResourceCrud<Niveau>
      title="Niveaux"
      description="Hiérarchie des niveaux d'études (primaire, secondaire, supérieur)."
      idKey="idNiveau"
      list={structureApi.niveaux.list}
      create={structureApi.niveaux.create}
      update={structureApi.niveaux.update}
      remove={structureApi.niveaux.remove}
      fields={fields}
      columns={columns}
      searchKeys={["code", "libelle"]}
      canWrite={isAdmin}
    />
  );
}
