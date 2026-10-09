"use client";

import { Badge } from "@/components/ui/badge";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, TYPE_MATIERE } from "@/components/pedagogie/constants";
import { useAuth } from "@/components/providers/auth-provider";
import { structureApi } from "@/services/api";
import type { Matiere } from "@/types";

const fields: FieldDef[] = [
  { name: "code", label: "Code", required: true },
  { name: "libelle", label: "Libellé", required: true },
  { name: "discipline", label: "Discipline" },
  { name: "type", label: "Type", type: "select", options: enumOptions(TYPE_MATIERE) },
  { name: "couleur", label: "Couleur (hex)" },
  { name: "active", label: "Active", type: "checkbox", defaultValue: true },
];

const columns: ColumnDef<Matiere>[] = [
  { key: "code", label: "Code", render: (row) => <span className="font-mono">{row.code}</span> },
  { key: "libelle", label: "Libellé", render: (row) => <span className="font-medium text-slate-900">{row.libelle}</span> },
  { key: "discipline", label: "Discipline" },
  { key: "type", label: "Type" },
  {
    key: "active",
    label: "Statut",
    render: (row) =>
      row.active ? <Badge variant="success">Active</Badge> : <Badge variant="outline">Inactive</Badge>,
  },
];

export default function MatieresPage() {
  const { isAdmin } = useAuth();
  return (
    <ResourceCrud<Matiere>
      title="Matières"
      description="Matières enseignées et leur discipline."
      idKey="idMatiere"
      list={structureApi.matieres.list}
      create={structureApi.matieres.create}
      update={structureApi.matieres.update}
      remove={structureApi.matieres.remove}
      fields={fields}
      columns={columns}
      searchKeys={["code", "libelle", "discipline"]}
      canWrite={isAdmin}
    />
  );
}
