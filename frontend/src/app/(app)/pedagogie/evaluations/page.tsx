"use client";

import Link from "next/link";
import { toast } from "sonner";
import { Ban, CheckCircle2, FolderOpen, Play } from "lucide-react";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, TYPE_EVALUATION_ELARGI } from "@/components/pedagogie/constants";
import { formatDate, formatTime, humanize } from "@/components/pedagogie/format";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/components/providers/auth-provider";
import { assessmentApi } from "@/services/api";
import type { Evaluation } from "@/types";

const fields: FieldDef[] = [
  { name: "libelle", label: "Libellé", required: true },
  { name: "type", label: "Type", type: "select", options: enumOptions(TYPE_EVALUATION_ELARGI) },
  { name: "classeId", label: "Classe (UUID)", colSpan: 2 },
  { name: "coursId", label: "Cours (UUID)" },
  { name: "ueId", label: "UE (UUID)" },
  { name: "matiereId", label: "Matière (UUID)" },
  { name: "enseignantId", label: "Enseignant (UUID)" },
  { name: "anneeId", label: "Année (UUID)" },
  { name: "periodeId", label: "Période (UUID)" },
  { name: "date", label: "Date", type: "date" },
  { name: "heureDebut", label: "Heure de début", type: "time" },
  { name: "duree", label: "Durée (min)", type: "number" },
  { name: "noteMax", label: "Note max", type: "number", defaultValue: 20 },
  { name: "coefficientDeclare", label: "Coefficient", type: "number", defaultValue: 1 },
  { name: "salleId", label: "Salle (UUID)" },
  { name: "consignes", label: "Consignes", type: "textarea", colSpan: 2 },
];

const columns: ColumnDef<Evaluation>[] = [
  {
    key: "reference",
    label: "Référence",
    render: (row) => (
      <Link href={`/pedagogie/evaluations/${row.idEvaluation}`} className="font-mono font-medium text-blue-600 hover:underline">
        {row.reference ?? row.idEvaluation.slice(0, 8)}
      </Link>
    ),
  },
  { key: "libelle", label: "Libellé", render: (row) => <span className="font-medium text-slate-900">{row.libelle ?? "—"}</span> },
  { key: "type", label: "Type", render: (row) => (row.type ? <Badge variant="outline">{humanize(row.type)}</Badge> : "—") },
  { key: "date", label: "Date", render: (row) => formatDate(row.date) },
  { key: "heureDebut", label: "Début", render: (row) => formatTime(row.heureDebut) },
  {
    key: "noteMax",
    label: "Note max",
    render: (row) => (row.maxNoteAtteignable != null ? row.maxNoteAtteignable : row.noteMax ?? "—"),
  },
  {
    key: "compteeDansMoyenne",
    label: "Moyenne",
    render: (row) => (row.compteeDansMoyenne ? <Badge variant="success">Comptée</Badge> : <Badge variant="outline">Non</Badge>),
  },
  {
    key: "statut",
    label: "Statut",
    render: (row) => (
      <Badge variant={row.statut === "PUBLIEE" ? "success" : row.statut === "OUVERTE" ? "warning" : "outline"}>
        {humanize(row.statut ?? "—")}
      </Badge>
    ),
  },
];

export default function EvaluationsPage() {
  const { isAdmin } = useAuth();

  const actions = (row: Evaluation, reload: () => void) => (
    <>
      {isAdmin && ["BROUILLON", "PLANIFIEE"].includes(row.statut ?? "") && (
        <>
          <Button
            variant="ghost"
            size="sm"
            title="Ouvrir"
            onClick={async () => {
              try {
                await assessmentApi.evaluations.ouvrir(row.idEvaluation);
                toast.success("Évaluation ouverte");
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
            title="Annuler"
            onClick={async () => {
              const motif = prompt("Motif d'annulation :");
              if (motif == null) return;
              try {
                await assessmentApi.evaluations.annuler(row.idEvaluation, motif);
                toast.success("Évaluation annulée");
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
      {isAdmin && row.statut === "OUVERTE" && (
        <Button
          variant="ghost"
          size="sm"
          title="Clôturer"
          onClick={async () => {
            try {
              await assessmentApi.evaluations.cloturer(row.idEvaluation);
              toast.success("Évaluation clôturée");
              reload();
            } catch (error) {
              toast.error(error instanceof Error ? error.message : "Erreur");
            }
          }}
        >
          <CheckCircle2 className="h-4 w-4" />
        </Button>
      )}
      {isAdmin && ["CLOTUREE", "PUBLIEE"].includes(row.statut ?? "") && row.statut !== "PUBLIEE" && (
        <Button
          variant="ghost"
          size="sm"
          title="Publier"
          onClick={async () => {
            try {
              await assessmentApi.evaluations.publier(row.idEvaluation);
              toast.success("Évaluation publiée");
              reload();
            } catch (error) {
              toast.error(error instanceof Error ? error.message : "Erreur");
            }
          }}
        >
          <FolderOpen className="h-4 w-4" />
        </Button>
      )}
      <Link href={`/pedagogie/evaluations/${row.idEvaluation}`}>
        <Button variant="ghost" size="sm" title="Notes">
          Détail
        </Button>
      </Link>
    </>
  );

  return (
    <ResourceCrud<Evaluation>
      title="Évaluations"
      description="Évaluations continues, feuilles de notes et moyennes (lot 4e)."
      idKey="idEvaluation"
      list={assessmentApi.evaluations.list}
      create={assessmentApi.evaluations.create}
      update={assessmentApi.evaluations.update}
      fields={fields}
      columns={columns}
      searchKeys={["reference", "libelle", "statut", "type"]}
      canWrite={isAdmin}
      rowActions={actions}
    />
  );
}