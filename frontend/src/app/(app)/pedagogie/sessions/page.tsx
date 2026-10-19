"use client";

import Link from "next/link";
import { toast } from "sonner";
import { Ban, CalendarCheck, CalendarPlus, Lock } from "lucide-react";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, TYPE_SESSION } from "@/components/pedagogie/constants";
import { formatDate, humanize } from "@/components/pedagogie/format";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { useAnneesMap, useClassesMap, usePeriodesMap } from "@/hooks/use-lookup";
import { examApi } from "@/services/api";
import type { SessionExamen } from "@/types";

const fields: FieldDef[] = [
  { name: "libelle", label: "Libellé", colSpan: 2 },
  { name: "type", label: "Type", type: "select", options: enumOptions(TYPE_SESSION) },
  { name: "anneeId", label: "Année", type: "select", entityKey: "annees", colSpan: 2 },
  { name: "periodeId", label: "Période", type: "select", entityKey: "periodes" },
  { name: "classeId", label: "Classe", type: "select", entityKey: "classes" },
  { name: "dateOuverture", label: "Ouverture", type: "date" },
  { name: "dateCloture", label: "Clôture", type: "date" },
  { name: "grilleDeliberation", label: "Grille de délibération" },
  { name: "nbCandidatsAttendus", label: "Candidats attendus", type: "number" },
];

const columns: ColumnDef<SessionExamen>[] = [
  {
    key: "reference",
    label: "Référence",
    render: (row) => (
      <Link href={`/pedagogie/sessions/${row.idSession}`} className="font-mono font-medium text-blue-600 hover:underline">
        {row.reference}
      </Link>
    ),
  },
  { key: "libelle", label: "Libellé", render: (row) => <span className="font-medium text-slate-900">{row.libelle ?? "—"}</span> },
  { key: "type", label: "Type", render: (row) => (row.type ? <Badge variant="outline">{humanize(row.type)}</Badge> : "—") },
  { key: "dateOuverture", label: "Ouverture", render: (row) => formatDate(row.dateOuverture) },
  { key: "dateCloture", label: "Clôture", render: (row) => formatDate(row.dateCloture) },
  {
    key: "epreuves",
    label: "Épreuves",
    render: (row) => row.epreuves?.length ?? 0,
  },
  {
    key: "statut",
    label: "Statut",
    render: (row) => (
      <Badge
        variant={
          row.statut === "CLOTUREE"
            ? "success"
            : row.statut === "EN_COURS"
              ? "warning"
              : row.statut === "ANNULEE"
                ? "danger"
                : "outline"
        }
      >
        {humanize(row.statut)}
      </Badge>
    ),
  },
];

export default function SessionsPage() {
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const annees = useAnneesMap([tenantId]);
  const periodes = usePeriodesMap([tenantId]);
  const classes = useClassesMap([tenantId]);

  const actions = (row: SessionExamen, reload: () => void) => (
    <>
      {isAdmin && row.statut === "BROUILLON" && (
        <Button
          variant="ghost"
          size="sm"
          title="Programmer"
          onClick={async () => {
            try {
              await examApi.sessions.programmer(row.idSession);
              toast.success("Session programmée");
              reload();
            } catch (error) {
              toast.error(error instanceof Error ? error.message : "Erreur");
            }
          }}
        >
          <CalendarPlus className="h-4 w-4" />
        </Button>
      )}
      {isAdmin && ["PROGRAMMEE"].includes(row.statut) && (
        <Button
          variant="ghost"
          size="sm"
          title="Ouvrir"
          onClick={async () => {
            try {
              await examApi.sessions.ouvrir(row.idSession);
              toast.success("Session ouverte");
              reload();
            } catch (error) {
              toast.error(error instanceof Error ? error.message : "Erreur");
            }
          }}
        >
          <CalendarCheck className="h-4 w-4" />
        </Button>
      )}
      {isAdmin && ["PROGRAMMEE", "EN_COURS", "CORRECTION", "DELIBERATION"].includes(row.statut) && (
        <Button
          variant="ghost"
          size="sm"
          title="Clôturer"
          onClick={async () => {
            try {
              await examApi.sessions.cloturer(row.idSession);
              toast.success("Session clôturée");
              reload();
            } catch (error) {
              toast.error(error instanceof Error ? error.message : "Erreur");
            }
          }}
        >
          <Lock className="h-4 w-4" />
        </Button>
      )}
      {isAdmin && !["CLOTUREE", "ANNULEE"].includes(row.statut) && (
        <Button
          variant="ghost"
          size="sm"
          title="Annuler"
          onClick={async () => {
            const motif = prompt("Motif d'annulation :");
            if (motif == null) return;
            try {
              await examApi.sessions.annuler(row.idSession, motif);
              toast.success("Session annulée");
              reload();
            } catch (error) {
              toast.error(error instanceof Error ? error.message : "Erreur");
            }
          }}
        >
          <Ban className="h-4 w-4" />
        </Button>
      )}
      <Link href={`/pedagogie/sessions/${row.idSession}`}>
        <Button variant="ghost" size="sm">
          Détail
        </Button>
      </Link>
    </>
  );

  return (
    <ResourceCrud<SessionExamen>
      title="Sessions d'examen"
      description="Sessions, épreuves, créneaux, convocations et notes d'examen (lot 4f)."
      idKey="idSession"
      list={examApi.sessions.list}
      create={examApi.sessions.create}
      fields={fields}
      columns={columns}
      entities={{ annees, periodes, classes }}
      searchKeys={["reference", "libelle", "statut", "type"]}
      canWrite={isAdmin}
      rowActions={actions}
    />
  );
}