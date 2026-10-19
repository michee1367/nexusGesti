"use client";

import Link from "next/link";
import { toast } from "sonner";
import { Ban, CalendarX2, Flag, Play, Square } from "lucide-react";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, TYPE_SEANCE } from "@/components/pedagogie/constants";
import { formatDate, formatTime, humanize } from "@/components/pedagogie/format";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { useAnneesMap, useClassesMap, useCoursMap, useEnseignantsMap, usePeriodesMap, useSallesMap } from "@/hooks/use-lookup";
import { attendanceApi } from "@/services/api";
import type { Seance } from "@/types";

const fields: FieldDef[] = [
  { name: "classeId", label: "Classe", type: "select", entityKey: "classes", colSpan: 2 },
  { name: "coursId", label: "Cours", type: "select", entityKey: "cours", colSpan: 2 },
  { name: "enseignantId", label: "Enseignant", type: "select", entityKey: "enseignants" },
  { name: "salleId", label: "Salle", type: "select", entityKey: "salles" },
  { name: "anneeId", label: "Année", type: "select", entityKey: "annees" },
  { name: "periodeId", label: "Période", type: "select", entityKey: "periodes" },
  { name: "type", label: "Type", type: "select", options: enumOptions(TYPE_SEANCE) },
  { name: "date", label: "Date", type: "date" },
  { name: "heureDebut", label: "Heure de début", type: "time" },
  { name: "heureFin", label: "Heure de fin", type: "time" },
];

const columns: ColumnDef<Seance>[] = [
  {
    key: "date",
    label: "Date",
    render: (row) => (
      <Link href={`/pedagogie/appels?seance=${row.idSeance}`} className="font-medium text-blue-600 hover:underline">
        {formatDate(row.date)}
      </Link>
    ),
  },
  { key: "type", label: "Type", render: (row) => <Badge variant="outline">{humanize(row.type ?? "—")}</Badge> },
  {
    key: "classeId",
    label: "Classe",
    lookupKey: "classes",
  },
  { key: "heureDebut", label: "Début", render: (row) => formatTime(row.heureDebut) },
  { key: "heureFin", label: "Fin", render: (row) => formatTime(row.heureFin) },
  {
    key: "dureeReelle",
    label: "Durée (min)",
    render: (row) => (row.dureeReelle != null ? `${row.dureeReelle} min` : "—"),
  },
  {
    key: "statut",
    label: "Statut",
    render: (row) => (
      <Badge
        variant={
          row.statut === "EN_COURS" ? "warning" : row.statut === "TERMINEE" ? "success" : row.statut === "ANNULEE" ? "danger" : "outline"
        }
      >
        {humanize(row.statut ?? "—")}
      </Badge>
    ),
  },
];

export default function SeancesPage() {
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const classes = useClassesMap([tenantId]);
  const cours = useCoursMap([tenantId]);
  const enseignants = useEnseignantsMap([tenantId]);
  const salles = useSallesMap([tenantId]);
  const annees = useAnneesMap([tenantId]);
  const periodes = usePeriodesMap([tenantId]);

  const actions = (row: Seance, reload: () => void) => (
    <>
      {isAdmin && ["PLANIFIEE", "REPORTEE"].includes(row.statut ?? "") && (
        <>
          <Button
            variant="ghost"
            size="sm"
            title="Démarrer"
            onClick={async () => {
              try {
                await attendanceApi.seances.demarrer(row.idSeance);
                toast.success("Séance démarrée");
                reload();
              } catch (error) {
                toast.error(error instanceof Error ? error.message : "Erreur");
              }
            }}
          >
            <Play className="h-4 w-4" />
          </Button>
          <Button
            variant="ghost"
            size="sm"
            title="Reporter"
            onClick={async () => {
              try {
                await attendanceApi.seances.reporter(row.idSeance);
                toast.success("Séance reportée");
                reload();
              } catch (error) {
                toast.error(error instanceof Error ? error.message : "Erreur");
              }
            }}
          >
            <CalendarX2 className="h-4 w-4" />
          </Button>
          <Button
            variant="ghost"
            size="sm"
            title="Annuler"
            onClick={async () => {
              const motif = prompt("Motif d'annulation :");
              if (motif == null) return;
              try {
                await attendanceApi.seances.annuler(row.idSeance, motif);
                toast.success("Séance annulée");
                reload();
              } catch (error) {
                toast.error(error instanceof Error ? error.message : "Erreur");
              }
            }}
          >
            <Ban className="h-4 w-4" />
          </Button>
        </>
      )}
      {isAdmin && row.statut === "EN_COURS" && (
        <Button
          variant="ghost"
          size="sm"
          title="Terminer"
          onClick={async () => {
            try {
              await attendanceApi.seances.terminer(row.idSeance);
              toast.success("Séance terminée");
              reload();
            } catch (error) {
              toast.error(error instanceof Error ? error.message : "Erreur");
            }
          }}
        >
          <Square className="h-4 w-4" />
        </Button>
      )}
      <Link href={`/pedagogie/appels?seance=${row.idSeance}`}>
        <Button variant="ghost" size="sm" title="Feuille d'appel">
          <Flag className="h-4 w-4" />
        </Button>
      </Link>
    </>
  );

  return (
    <ResourceCrud<Seance>
      title="Séances"
      description="Séances de cours, TD, TP et examens — feuilles d'appel et suivi (lot 4d)."
      idKey="idSeance"
      list={attendanceApi.seances.list}
      create={attendanceApi.seances.create}
      update={attendanceApi.seances.update}
      fields={fields}
      columns={columns}
      entities={{ classes, cours, enseignants, salles, annees, periodes }}
      searchKeys={["type", "statut"]}
      canWrite={isAdmin}
      rowActions={actions}
    />
  );
}