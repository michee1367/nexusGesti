"use client";

import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { Badge } from "@/components/ui/badge";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { useProgrammesMap } from "@/hooks/use-lookup";
import { enrollmentApi } from "@/services/api";
import type { RegleInscription } from "@/types";

const fields: FieldDef[] = [
  { name: "libelle", label: "Libellé", required: true },
  { name: "programmeId", label: "Programme", type: "select", entityKey: "programmes", colSpan: 2 },
  { name: "condition", label: "Condition", type: "textarea", colSpan: 2 },
  { name: "creditsMin", label: "Crédits minimum", type: "number" },
  { name: "moyenneMin", label: "Moyenne minimum", type: "number" },
  { name: "creneauAge", label: "Créneau d'âge", type: "number" },
  { name: "priorite", label: "Priorité", type: "number", defaultValue: 0 },
];

const columns: ColumnDef<RegleInscription>[] = [
  { key: "libelle", label: "Libellé", render: (row) => <span className="font-medium text-slate-900">{row.libelle}</span> },
  {
    key: "programmeId",
    label: "Programme",
    lookupKey: "programmes",
  },
  { key: "creditsMin", label: "Crédits min" },
  { key: "moyenneMin", label: "Moyenne min" },
  { key: "creneauAge", label: "Âge" },
  {
    key: "priorite",
    label: "Priorité",
    render: (row) => <Badge variant="outline">#{row.priorite}</Badge>,
  },
];

export default function ReglesInscriptionPage() {
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const programmes = useProgrammesMap([tenantId]);
  return (
    <ResourceCrud<RegleInscription>
      title="Règles d'inscription"
      description="Règles d'éligibilité appliquées à l'inscription des élèves aux UE."
      idKey="idRegle"
      list={enrollmentApi.regles.list}
      create={enrollmentApi.regles.create}
      update={enrollmentApi.regles.update}
      remove={enrollmentApi.regles.remove}
      fields={fields}
      columns={columns}
      entities={{ programmes }}
      searchKeys={["libelle", "condition", "programmeId"]}
      canWrite={isAdmin}
    />
  );
}