"use client";

import { Badge } from "@/components/ui/badge";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, TYPE_SALLE } from "@/components/pedagogie/constants";
import { useAuth } from "@/components/providers/auth-provider";
import { structureApi } from "@/services/api";
import type { Salle } from "@/types";

const fields: FieldDef[] = [
  { name: "code", label: "Code", required: true },
  { name: "libelle", label: "Libellé", required: true },
  { name: "type", label: "Type", type: "select", options: enumOptions(TYPE_SALLE) },
  { name: "capacite", label: "Capacité", type: "number", defaultValue: 0 },
  { name: "equipements", label: "Équipements (séparés par des virgules)", type: "tags", colSpan: 2 },
  { name: "accessible", label: "Accessible PMR", type: "checkbox" },
  { name: "disponible", label: "Disponible", type: "checkbox", defaultValue: true },
];

const columns: ColumnDef<Salle>[] = [
  { key: "code", label: "Code", render: (row) => <span className="font-mono">{row.code}</span> },
  { key: "libelle", label: "Libellé", render: (row) => <span className="font-medium text-slate-900">{row.libelle}</span> },
  { key: "type", label: "Type" },
  { key: "capacite", label: "Capacité" },
  {
    key: "equipements",
    label: "Équipements",
    render: (row) => (row.equipements?.length ? row.equipements.join(", ") : "—"),
  },
  {
    key: "disponible",
    label: "Statut",
    render: (row) =>
      row.disponible ? (
        <Badge variant="success">Disponible</Badge>
      ) : (
        <Badge variant="outline">Indisponible</Badge>
      ),
  },
];

export default function SallesPage() {
  const { isAdmin } = useAuth();
  return (
    <ResourceCrud<Salle>
      title="Salles"
      description="Salles et espaces physiques disponibles pour les cours."
      idKey="idSalle"
      list={structureApi.salles.list}
      create={structureApi.salles.create}
      update={structureApi.salles.update}
      fields={fields}
      columns={columns}
      searchKeys={["code", "libelle", "type"]}
      canWrite={isAdmin}
    />
  );
}
