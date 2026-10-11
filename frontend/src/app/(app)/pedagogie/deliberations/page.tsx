"use client";

import Link from "next/link";
import { toast } from "sonner";
import { CheckCircle2, Play } from "lucide-react";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { humanize, formatDate } from "@/components/pedagogie/format";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/components/providers/auth-provider";
import { examApi } from "@/services/api";
import type { Deliberation } from "@/types";

const fields: FieldDef[] = [
  { name: "reference", label: "Référence", required: true },
  { name: "sessionId", label: "Session (UUID)", colSpan: 2 },
  { name: "date", label: "Date", type: "date" },
  { name: "president", label: "Président" },
  { name: "grilleVersion", label: "Version de grille", type: "number" },
];

const columns: ColumnDef<Deliberation>[] = [
  {
    key: "reference",
    label: "Référence",
    render: (row) => (
      <Link href={`/pedagogie/deliberations/${row.id}`} className="font-mono font-medium text-blue-600 hover:underline">
        {row.reference}
      </Link>
    ),
  },
  { key: "date", label: "Date", render: (row) => formatDate(row.date) },
  { key: "president", label: "Président", render: (row) => row.president ?? "—" },
  {
    key: "decisions",
    label: "Décisions",
    render: (row) => row.decisions?.length ?? 0,
  },
  {
    key: "quorumAtteint",
    label: "Quorum",
    render: (row) => (row.quorumAtteint ? <Badge variant="success">Atteint</Badge> : <Badge variant="warning">Non</Badge>),
  },
  {
    key: "tauxReussite",
    label: "Taux réussite",
    render: (row) => (row.tauxReussite != null ? `${row.tauxReussite} %` : "—"),
  },
  {
    key: "statut",
    label: "Statut",
    render: (row) => (
      <Badge variant={row.statut === "CLOTUREE" ? "success" : row.statut === "EN_COURS" ? "warning" : "outline"}>
        {humanize(row.statut)}
      </Badge>
    ),
  },
];

export default function DeliberationsPage() {
  const { isAdmin } = useAuth();

  const actions = (row: Deliberation, reload: () => void) => (
    <>
      {isAdmin && ["PLANIFIEE", "REPRISE"].includes(row.statut) && (
        <Button
          variant="ghost"
          size="sm"
          title="Ouvrir"
          onClick={async () => {
            try {
              await examApi.deliberations.ouvrir(row.id as string);
              toast.success("Délibération ouverte");
              reload();
            } catch (error) {
              toast.error(error instanceof Error ? error.message : "Erreur");
            }
          }}
        >
          <Play className="h-4 w-4" />
        </Button>
      )}
      {isAdmin && row.statut === "EN_COURS" && (
        <Button
          variant="ghost"
          size="sm"
          title="Clôturer"
          onClick={async () => {
            try {
              await examApi.deliberations.cloturer(row.id as string);
              toast.success("Délibération clôturée");
              reload();
            } catch (error) {
              toast.error(error instanceof Error ? error.message : "Erreur");
            }
          }}
        >
          <CheckCircle2 className="h-4 w-4" />
        </Button>
      )}
      <Link href={`/pedagogie/deliberations/${row.id}`}>
        <Button variant="ghost" size="sm">
          Détail
        </Button>
      </Link>
    </>
  );

  return (
    <ResourceCrud<Deliberation>
      title="Délibérations"
      description="Délibérations, décisions par classe et par UE, résultats officiels (lot 4f)."
      idKey="id"
      list={examApi.deliberations.list}
      create={examApi.deliberations.create}
      fields={fields}
      columns={columns}
      searchKeys={["reference", "president", "statut"]}
      canWrite={isAdmin}
      rowActions={actions}
    />
  );
}