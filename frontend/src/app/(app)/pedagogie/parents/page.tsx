"use client";

import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, SEXE } from "@/components/pedagogie/constants";
import { useAuth } from "@/components/providers/auth-provider";
import { peopleApi } from "@/services/api";
import type { Parent } from "@/types";

const fields: FieldDef[] = [
  { name: "nom", label: "Nom", required: true },
  { name: "postNom", label: "Post-nom" },
  { name: "prenom", label: "Prénom", required: true },
  { name: "sexe", label: "Sexe", type: "select", options: enumOptions(SEXE) },
  { name: "telephone", label: "Téléphone" },
  { name: "telephoneSecondaire", label: "Téléphone secondaire" },
  { name: "email", label: "Email" },
  { name: "profession", label: "Profession" },
  { name: "statut", label: "Statut" },
];

const columns: ColumnDef<Parent>[] = [
  { key: "matricule", label: "Matricule", render: (row) => <span className="font-mono text-xs">{row.matricule}</span> },
  {
    key: "nom",
    label: "Nom complet",
    render: (row) => (
      <span className="font-medium text-slate-900">
        {row.nom} {row.postNom ?? ""} {row.prenom}
      </span>
    ),
  },
  { key: "telephone", label: "Téléphone" },
  { key: "email", label: "Email" },
  { key: "profession", label: "Profession" },
];

export default function ParentsPage() {
  const { isAdmin } = useAuth();
  return (
    <ResourceCrud<Parent>
      title="Parents / tuteurs"
      description="Parents et responsables légaux, liés aux élèves."
      idKey="id"
      list={peopleApi.parents.list}
      create={peopleApi.parents.create}
      update={peopleApi.parents.update}
      fields={fields}
      columns={columns}
      searchKeys={["matricule", "nom", "postNom", "prenom", "telephone", "email"]}
      canWrite={isAdmin}
    />
  );
}
