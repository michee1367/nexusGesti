"use client";

import { toast } from "sonner";
import { Ban } from "lucide-react";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/components/providers/auth-provider";
import { examApi } from "@/services/api";
import type { Rattrapage } from "@/types";

const fields: FieldDef[] = [
  { name: "sessionOrigineId", label: "Session d'origine (UUID)", colSpan: 2 },
  { name: "sessionRattrapageId", label: "Session de rattrapage (UUID)", colSpan: 2 },
  { name: "eleveId", label: "Élève (UUID)", colSpan: 2 },
  { name: "epreuvesConcernees", label: "Épreuves concernées (UUID, séparés par virgule)", type: "tags", colSpan: 2 },
];

const columns: ColumnDef<Rattrapage>[] = [
  {
    key: "eleveId",
    label: "Élève",
    render: (row) => (row.eleveId ? <span className="font-mono text-xs">{row.eleveId.slice(0, 8)}…</span> : "—"),
  },
  {
    key: "epreuvesConcernees",
    label: "Épreuves",
    render: (row) => (row.epreuvesConcernees?.length ? row.epreuvesConcernees.length : 0),
  },
  {
    key: "nePasPresenter",
    label: "Présence",
    render: (row) => (row.nePasPresenter ? <Badge variant="warning">Dispensé</Badge> : <Badge variant="outline">Convoqué</Badge>),
  },
  { key: "motif", label: "Motif", render: (row) => row.motif ?? "—" },
];

export default function RattrapagesPage() {
  const { isAdmin } = useAuth();

  const actions = (row: Rattrapage, reload: () => void) => (
    <>
      {isAdmin && !row.nePasPresenter && row.id && (
        <Button
          variant="ghost"
          size="sm"
          title="Déclarer exempt (ne pas présenter)"
          onClick={async () => {
            const motif = prompt("Motif de dispense :");
            if (motif == null) return;
            try {
              await examApi.rattrapages.exempt(row.id as string, motif);
              toast.success("Élève déclaré exempt");
              reload();
            } catch (error) {
              toast.error(error instanceof Error ? error.message : "Erreur");
            }
          }}
        >
          <Ban className="h-4 w-4" />
        </Button>
      )}
    </>
  );

  return (
    <ResourceCrud<Rattrapage>
      title="Rattrapages"
      description="Rattrapages et dispenses par élève (lot 4f)."
      idKey="id"
      list={examApi.rattrapages.list}
      create={examApi.rattrapages.create}
      fields={fields}
      columns={columns}
      searchKeys={["eleveId", "motif"]}
      canWrite={isAdmin}
      rowActions={actions}
    />
  );
}