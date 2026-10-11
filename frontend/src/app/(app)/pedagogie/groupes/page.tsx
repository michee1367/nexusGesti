"use client";

import Link from "next/link";
import { toast } from "sonner";
import { CheckCircle2, DoorOpen, Unlock } from "lucide-react";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, TYPE_GROUPE } from "@/components/pedagogie/constants";
import { humanize } from "@/components/pedagogie/format";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/components/providers/auth-provider";
import { enrollmentApi } from "@/services/api";
import type { Groupe } from "@/types";

const fields: FieldDef[] = [
  { name: "code", label: "Code", required: true },
  { name: "libelle", label: "Libellé", required: true },
  { name: "type", label: "Type", type: "select", options: enumOptions(TYPE_GROUPE) },
  { name: "capacite", label: "Capacité", type: "number", defaultValue: 30 },
  { name: "coursId", label: "Cours (UUID)" },
  { name: "ueId", label: "UE (UUID)" },
  { name: "classeId", label: "Classe (UUID)" },
  { name: "enseignantId", label: "Enseignant (UUID)" },
  { name: "salleId", label: "Salle (UUID)" },
  { name: "creneauId", label: "Créneau (UUID)" },
  { name: "jour", label: "Jour", type: "select", options: enumOptions(["LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI", "DIMANCHE"]) },
];

const columns: ColumnDef<Groupe>[] = [
  {
    key: "code",
    label: "Code",
    render: (row) => (
      <Link href={`/pedagogie/groupes/${row.idGroupe}`} className="font-mono font-medium text-blue-600 hover:underline">
        {row.code}
      </Link>
    ),
  },
  { key: "libelle", label: "Libellé" },
  {
    key: "type",
    label: "Type",
    render: (row) => <Badge variant="outline">{humanize(row.type)}</Badge>,
  },
  { key: "capacite", label: "Capacité" },
  { key: "effectif", label: "Effectif" },
  {
    key: "placesRestantes",
    label: "Places restantes",
    render: (row) => (row.placesRestantes > 0 ? <span>{row.placesRestantes}</span> : <Badge variant="danger">Plein</Badge>),
  },
  {
    key: "statut",
    label: "Statut",
    render: (row) => (
      <Badge variant={row.statut === "OUVERT" ? "success" : row.statut === "COMPLET" ? "warning" : "outline"}>
        {humanize(row.statut ?? "—")}
      </Badge>
    ),
  },
];

export default function GroupesPage() {
  const { isAdmin } = useAuth();

  const actions = (row: Groupe, reload: () => void) => (
    <>
      {isAdmin && row.statut === "OUVERT" && (
        <Button
          variant="ghost"
          size="sm"
          title="Clore le groupe"
          onClick={async () => {
            try {
              await enrollmentApi.groupes.clore(row.idGroupe);
              toast.success("Groupe clos");
              reload();
            } catch (error) {
              toast.error(error instanceof Error ? error.message : "Erreur");
            }
          }}
        >
          <DoorOpen className="h-4 w-4" />
        </Button>
      )}
      {isAdmin && row.statut === "COMPLET" && (
        <Button
          variant="ghost"
          size="sm"
          title="Rouvrir le groupe"
          onClick={async () => {
            try {
              await enrollmentApi.groupes.ouvrir(row.idGroupe);
              toast.success("Groupe rouvert");
              reload();
            } catch (error) {
              toast.error(error instanceof Error ? error.message : "Erreur");
            }
          }}
        >
          <Unlock className="h-4 w-4" />
        </Button>
      )}
      <Link href={`/pedagogie/groupes/${row.idGroupe}`}>
        <Button variant="ghost" size="sm" title="Détail">
          <CheckCircle2 className="h-4 w-4" />
        </Button>
      </Link>
    </>
  );

  return (
    <ResourceCrud<Groupe>
      title="Groupes TD / TP"
      description="Groupes pédagogiques d'inscription (TD, TP…) et leurs membres."
      idKey="idGroupe"
      list={enrollmentApi.groupes.list}
      create={enrollmentApi.groupes.create}
      update={enrollmentApi.groupes.update}
      fields={fields}
      columns={columns}
      searchKeys={["code", "libelle", "type", "statut"]}
      canWrite={isAdmin}
      rowActions={actions}
    />
  );
}