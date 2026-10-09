"use client";

import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { useAuth } from "@/components/providers/auth-provider";
import { structureApi } from "@/services/api";
import type { RegimeHoraire } from "@/types";

const fields: FieldDef[] = [
  { name: "code", label: "Code", required: true },
  { name: "libelle", label: "Libellé", required: true },
  {
    name: "jours",
    label: "Jours (LUNDI, MARDI, … — séparés par des virgules)",
    type: "tags",
    colSpan: 2,
  },
  { name: "creneauxParJour", label: "Créneaux / jour", type: "number", defaultValue: 6 },
  { name: "dureeCreneau", label: "Durée d'un créneau (min)", type: "number", defaultValue: 60 },
  { name: "pauseMatinale", label: "Pause matinale (min)", type: "number" },
  { name: "pauseMeridienne", label: "Pause méridienne (min)", type: "number" },
];

const columns: ColumnDef<RegimeHoraire>[] = [
  { key: "code", label: "Code", render: (row) => <span className="font-mono">{row.code}</span> },
  { key: "libelle", label: "Libellé", render: (row) => <span className="font-medium text-slate-900">{row.libelle}</span> },
  { key: "jours", label: "Jours", render: (row) => (row.jours?.length ? row.jours.join(", ") : "—") },
  { key: "creneauxParJour", label: "Créneaux/j" },
  { key: "dureeCreneau", label: "Durée (min)" },
  { key: "volumeHebdomadaire", label: "Volume hebdo (min)" },
];

export default function RegimesHorairesPage() {
  const { isAdmin } = useAuth();
  return (
    <ResourceCrud<RegimeHoraire>
      title="Régimes horaires"
      description="Organisation hebdomadaire des créneaux par classe."
      idKey="code"
      list={structureApi.regimesHoraires.list}
      create={structureApi.regimesHoraires.create}
      update={structureApi.regimesHoraires.update}
      fields={fields}
      columns={columns}
      searchKeys={["code", "libelle"]}
      canWrite={isAdmin}
    />
  );
}
