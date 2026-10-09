"use client";

import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, JOURS } from "@/components/pedagogie/constants";
import { formatTime } from "@/components/pedagogie/format";
import { useAuth } from "@/components/providers/auth-provider";
import { structureApi } from "@/services/api";
import type { Creneau } from "@/types";

const fields: FieldDef[] = [
  { name: "jour", label: "Jour", type: "select", options: enumOptions(JOURS), required: true },
  { name: "heureDebut", label: "Heure de début", type: "time", required: true },
  { name: "heureFin", label: "Heure de fin", type: "time", required: true },
  { name: "ordre", label: "Ordre", type: "number", defaultValue: 1 },
];

const columns: ColumnDef<Creneau>[] = [
  { key: "jour", label: "Jour" },
  { key: "heureDebut", label: "Début", render: (row) => formatTime(row.heureDebut) },
  { key: "heureFin", label: "Fin", render: (row) => formatTime(row.heureFin) },
  { key: "ordre", label: "Ordre" },
];

export default function CreneauxPage() {
  const { isAdmin } = useAuth();
  return (
    <ResourceCrud<Creneau>
      title="Créneaux"
      description="Plages horaires élémentaires utilisées par les emplois du temps."
      idKey="idCreneau"
      list={structureApi.creneaux.list}
      create={structureApi.creneaux.create}
      update={structureApi.creneaux.update}
      fields={fields}
      columns={columns}
      searchKeys={["jour"]}
      canWrite={isAdmin}
    />
  );
}
