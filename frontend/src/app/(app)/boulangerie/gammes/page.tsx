"use client";

import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { Badge } from "@/components/ui/badge";
import { useAuth } from "@/components/providers/auth-provider";
import { boulangerieApi } from "@/services/api";
import type { Gamme } from "@/types";

const fields: FieldDef[] = [
  { name: "code", label: "Code", required: true },
  { name: "libelle", label: "Libellé" },
  { name: "ordre", label: "Ordre", type: "number" },
  { name: "actif", label: "Actif", type: "checkbox", defaultValue: true },
];

const columns: ColumnDef<Gamme>[] = [
  { key: "code", label: "Code", render: (row) => <span className="font-mono font-medium">{row.code}</span> },
  { key: "libelle", label: "Libellé", render: (row) => row.libelle ?? "—" },
  { key: "ordre", label: "Ordre", render: (row) => row.ordre ?? "—" },
  {
    key: "actif",
    label: "Statut",
    render: (row) => (
      <Badge variant={row.actif === false ? "outline" : "success"}>
        {row.actif === false ? "Inactif" : "Actif"}
      </Badge>
    ),
  },
];

export default function GammesPage() {
  const { isAdmin } = useAuth();

  return (
    <ResourceCrud<Gamme>
      title="Gammes"
      description="Gammes de produits boulangerie (lot 1)."
      idKey="id"
      list={boulangerieApi.gammes.list}
      create={boulangerieApi.gammes.create}
      fields={fields}
      columns={columns}
      searchKeys={["code", "libelle"]}
      canWrite={isAdmin}
      createLabel="Nouvelle gamme"
    />
  );
}
