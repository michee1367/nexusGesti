"use client";

import { toast } from "sonner";
import { Check, X } from "lucide-react";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, TYPE_ABSENCE } from "@/components/pedagogie/constants";
import { formatDate, humanize } from "@/components/pedagogie/format";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/components/providers/auth-provider";
import { attendanceApi } from "@/services/api";
import type { Justificatif } from "@/types";

const fields: FieldDef[] = [
  { name: "eleveId", label: "Élève (UUID)", colSpan: 2 },
  { name: "type", label: "Type", type: "select", options: enumOptions(TYPE_ABSENCE) },
  { name: "dateReference", label: "Date de référence", type: "date" },
  { name: "motif", label: "Motif", type: "textarea", colSpan: 2 },
  { name: "fichierUri", label: "Justificatif (URL)", colSpan: 2 },
];

const columns: ColumnDef<Justificatif>[] = [
  {
    key: "eleveId",
    label: "Élève",
    render: (row) => (row.eleveId ? <span className="font-mono text-xs">{row.eleveId.slice(0, 8)}…</span> : "—"),
  },
  { key: "type", label: "Type", render: (row) => (row.type ? humanize(row.type) : "—") },
  { key: "dateReference", label: "Référence", render: (row) => formatDate(row.dateReference) },
  { key: "dateDepot", label: "Dépôt", render: (row) => formatDate(row.dateDepot) },
  {
    key: "motif",
    label: "Motif",
    render: (row) => (row.motif ? <span className="max-w-56 truncate text-slate-500">{row.motif}</span> : "—"),
  },
  {
    key: "statut",
    label: "Statut",
    render: (row) => (
      <Badge variant={row.statut === "ACCEPTE" ? "success" : row.statut === "REJETE" ? "danger" : "warning"}>
        {humanize(row.statut)}
      </Badge>
    ),
  },
];

export default function JustificatifsPage() {
  const { isAdmin } = useAuth();

  const actions = (row: Justificatif, reload: () => void) => (
    <>
      {isAdmin && row.statut === "EN_ATTENTE" && (
        <>
          <Button
            variant="ghost"
            size="sm"
            title="Accepter"
            onClick={async () => {
              try {
                await attendanceApi.justificatifs.accepter(row.idJustificatif);
                toast.success("Justificatif accepté — absences du jour requalifiées");
                reload();
              } catch (error) {
                toast.error(error instanceof Error ? error.message : "Erreur");
              }
            }}
          >
            <Check className="h-4 w-4 text-success" />
          </Button>
          <Button
            variant="ghost"
            size="sm"
            title="Rejeter"
            onClick={async () => {
              const motif = prompt("Motif du rejet :");
              if (motif == null) return;
              try {
                await attendanceApi.justificatifs.rejeter(row.idJustificatif, motif);
                toast.success("Justificatif rejeté");
                reload();
              } catch (error) {
                toast.error(error instanceof Error ? error.message : "Erreur");
              }
            }}
          >
            <X className="h-4 w-4 text-danger" />
          </Button>
        </>
      )}
    </>
  );

  return (
    <ResourceCrud<Justificatif>
      title="Justificatifs d'absence"
      description="Certificats et justificatifs déposés par les élèves, acceptés ou rejetés (lot 4d)."
      idKey="idJustificatif"
      list={attendanceApi.justificatifs.list}
      create={attendanceApi.justificatifs.create}
      fields={fields}
      columns={columns}
      searchKeys={["eleveId", "type", "motif", "statut"]}
      canWrite={isAdmin}
      createLabel="Déposer un justificatif"
      rowActions={actions}
    />
  );
}