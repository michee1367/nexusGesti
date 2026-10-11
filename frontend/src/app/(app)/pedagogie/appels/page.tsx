"use client";

import Link from "next/link";
import { Suspense } from "react";
import { useSearchParams } from "next/navigation";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, METHODE_SAISIE } from "@/components/pedagogie/constants";
import { formatDate, humanize } from "@/components/pedagogie/format";
import { LoadingView } from "@/components/common/async-state";
import { Badge } from "@/components/ui/badge";
import { useAuth } from "@/components/providers/auth-provider";
import { attendanceApi } from "@/services/api";
import type { FeuillePresence } from "@/types";

const fields: FieldDef[] = [
  { name: "seanceId", label: "Séance (UUID)", colSpan: 2 },
  { name: "classeId", label: "Classe (UUID)", colSpan: 2 },
  { name: "coursId", label: "Cours (UUID)" },
  { name: "enseignantId", label: "Enseignant (UUID)" },
  { name: "date", label: "Date", type: "date" },
  { name: "methode", label: "Méthode", type: "select", options: enumOptions(METHODE_SAISIE) },
];

const columns: ColumnDef<FeuillePresence>[] = [
  {
    key: "idFeuille",
    label: "Feuille",
    render: (row) => (
      <Link href={`/pedagogie/appels/${row.idFeuille}`} className="font-mono text-xs font-medium text-blue-600 hover:underline">
        {row.idFeuille.slice(0, 8)}…
      </Link>
    ),
  },
  { key: "date", label: "Date", render: (row) => formatDate(row.date) },
  {
    key: "classeId",
    label: "Classe",
    render: (row) => (row.classeId ? <span className="font-mono text-xs">{row.classeId.slice(0, 8)}…</span> : "—"),
  },
  {
    key: "effectifTheorique",
    label: "Effectif",
    render: (row) => `${row.effectifPresent ?? 0}/${row.effectifTheorique ?? "—"}`,
  },
  {
    key: "tauxPresence",
    label: "Taux présence",
    render: (row) => (row.tauxPresence != null ? `${row.tauxPresence} %` : "—"),
  },
  {
    key: "statut",
    label: "Statut",
    render: (row) => (
      <Badge
        variant={row.statut === "VALIDEE" ? "success" : row.statut === "CLOTUREE" ? "secondary" : "outline"}
      >
        {humanize(row.statut)}
      </Badge>
    ),
  },
];

function AppelsContent() {
  const { isAdmin } = useAuth();
  const seance = useSearchParams().get("seance") ?? "";
  return (
    <ResourceCrud<FeuillePresence>
      title="Feuilles d'appel"
      description="Feuilles de présence émises pour chaque séance (lot 4d)."
      idKey="idFeuille"
      list={attendanceApi.appels.list}
      create={attendanceApi.appels.create}
      fields={fields}
      columns={columns}
      searchKeys={["seanceId", "classeId", "statut"]}
      initialSearch={seance}
      canWrite={isAdmin}
      createLabel="Ouvrir une feuille"
    />
  );
}

export default function AppelsPage() {
  return (
    <Suspense fallback={<LoadingView label="Chargement — Feuilles d'appel…" />}>
      <AppelsContent />
    </Suspense>
  );
}