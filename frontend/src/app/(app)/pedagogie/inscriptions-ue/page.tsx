"use client";

import { toast } from "sonner";
import { CheckCircle2, ListX, Undo2 } from "lucide-react";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { humanize } from "@/components/pedagogie/format";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/components/providers/auth-provider";
import { enrollmentApi } from "@/services/api";
import type { InscriptionUE } from "@/types";

const fields: FieldDef[] = [
  { name: "eleveId", label: "Élève (UUID)", colSpan: 2 },
  { name: "ueId", label: "UE (UUID)", colSpan: 2 },
  { name: "anneeId", label: "Année (UUID)", colSpan: 2 },
];

const columns: ColumnDef<InscriptionUE>[] = [
  {
    key: "eleveId",
    label: "Élève",
    render: (row) => <span className="font-mono text-xs">{row.eleveId?.slice(0, 8) ?? "—"}…</span>,
  },
  {
    key: "ueId",
    label: "UE",
    render: (row) => <span className="font-mono text-xs">{row.ueId?.slice(0, 8) ?? "—"}…</span>,
  },
  {
    key: "statut",
    label: "Statut",
    render: (row) => <Badge variant="outline">{humanize(row.statut)}</Badge>,
  },
  { key: "creditsReconnus", label: "Crédits reconnus" },
  {
    key: "rangListeAttente",
    label: "Rang liste attente",
    render: (row) => (row.rangListeAttente != null ? `#${row.rangListeAttente}` : "—"),
  },
  {
    key: "motifRefus",
    label: "Motif",
    render: (row) => (row.motifRefus ? <span className="text-slate-500">{row.motifRefus}</span> : "—"),
  },
];

export default function InscriptionsUEPage() {
  const { isAdmin } = useAuth();

  const actions = (row: InscriptionUE, reload: () => void) => (
    <>
      {isAdmin && row.statut === "EN_ATTENTE" && (
        <>
          <Button
            variant="ghost"
            size="sm"
            title="Valider"
            onClick={async () => {
              try {
                await enrollmentApi.inscriptions.valider(row.idInscription);
                toast.success("Inscription validée");
                reload();
              } catch (error) {
                toast.error(error instanceof Error ? error.message : "Erreur");
              }
            }}
          >
            <CheckCircle2 className="h-4 w-4" />
          </Button>
          <Button
            variant="ghost"
            size="sm"
            title="Refuser"
            onClick={async () => {
              const motif = prompt("Motif du refus :");
              if (motif == null) return;
              try {
                await enrollmentApi.inscriptions.refuser(row.idInscription, motif);
                toast.success("Inscription refusée");
                reload();
              } catch (error) {
                toast.error(error instanceof Error ? error.message : "Erreur");
              }
            }}
          >
            <ListX className="h-4 w-4" />
          </Button>
          <Button
            variant="ghost"
            size="sm"
            title="Mettre en liste d'attente"
            onClick={async () => {
              const réponse = prompt("Rang (défaut 0) :");
              const rang = réponse ? Number(réponse) : 0;
              try {
                await enrollmentApi.inscriptions.listeAttente(row.idInscription, rang);
                toast.success("Mis en liste d'attente");
                reload();
              } catch (error) {
                toast.error(error instanceof Error ? error.message : "Erreur");
              }
            }}
          >
            <Undo2 className="h-4 w-4" />
          </Button>
        </>
      )}
    </>
  );

  return (
    <ResourceCrud<InscriptionUE>
      title="Inscriptions aux UE"
      description="Inscriptions universitaires aux unités d'enseignement (lot 4c)."
      idKey="idInscription"
      list={enrollmentApi.inscriptions.list}
      create={enrollmentApi.inscriptions.create}
      update={enrollmentApi.inscriptions.update}
      fields={fields}
      columns={columns}
      searchKeys={["eleveId", "ueId", "statut", "motifRefus"]}
      canWrite={isAdmin}
      rowActions={actions}
    />
  );
}