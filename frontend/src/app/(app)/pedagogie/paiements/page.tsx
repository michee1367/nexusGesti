"use client";

import { toast } from "sonner";
import { Ban, CheckCircle2, XCircle } from "lucide-react";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, MOYEN_PAIEMENT, TYPE_BENEFICIAIRE } from "@/components/pedagogie/constants";
import { humanize, formatDate } from "@/components/pedagogie/format";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/components/providers/auth-provider";
import { feesApi } from "@/services/api";
import type { Paiement } from "@/types";

const fields: FieldDef[] = [
  { name: "avis", label: "Avis (UUID)", colSpan: 2 },
  { name: "eleve", label: "Élève (UUID)", colSpan: 2 },
  { name: "montant", label: "Montant", type: "number", required: true },
  { name: "moyen", label: "Moyen", type: "select", options: enumOptions(MOYEN_PAIEMENT) },
  { name: "referenceTransaction", label: "Référence transaction" },
  { name: "beneficiaire", label: "Bénéficiaire", type: "select", options: enumOptions(TYPE_BENEFICIAIRE) },
];

const columns: ColumnDef<Paiement>[] = [
  { key: "reference", label: "Référence", render: (row) => <span className="font-mono font-medium text-slate-900">{row.reference}</span> },
  { key: "eleve", label: "Élève", render: (row) => (row.eleve ? <span className="font-mono text-xs">{row.eleve.slice(0, 8)}…</span> : "—") },
  { key: "montant", label: "Montant", render: (row) => <span className="font-semibold">{row.montant}</span> },
  { key: "moyen", label: "Moyen", render: (row) => (row.moyen ? humanize(row.moyen) : "—") },
  { key: "date", label: "Date", render: (row) => formatDate(row.date) },
  {
    key: "statut",
    label: "Statut",
    render: (row) => (
      <Badge
        variant={
          row.statut === "VALIDE"
            ? "success"
            : row.statut === "REJETE" || row.statut === "ANNULE"
              ? "danger"
              : row.statut === "EN_ATTENTE"
                ? "warning"
                : "outline"
        }
      >
        {humanize(row.statut)}
      </Badge>
    ),
  },
  { key: "resteARembourser", label: "À rembourser", render: (row) => row.resteARembourser },
];

export default function PaiementsPage() {
  const { isAdmin } = useAuth();

  const create = (body: Partial<Paiement>) => feesApi.paiements.create(body, crypto.randomUUID());

  const actions = (row: Paiement, reload: () => void) => {
    const run = async (fn: () => Promise<unknown>, message: string) => {
      try {
        await fn();
        toast.success(message);
        reload();
      } catch (error) {
        toast.error(error instanceof Error ? error.message : "Erreur");
      }
    };
    return (
      <>
        {isAdmin && ["EN_ATTENTE", "A_VERIFIER"].includes(row.statut) && (
          <Button variant="ghost" size="sm" title="Valider" onClick={() => run(() => feesApi.paiements.valider(row.idPaiement), "Paiement validé")}>
            <CheckCircle2 className="h-4 w-4" />
          </Button>
        )}
        {isAdmin && row.statut === "EN_ATTENTE" && (
          <Button
            variant="ghost"
            size="sm"
            title="Rejeter"
            onClick={() => {
              const motif = prompt("Motif de rejet :");
              if (motif == null) return;
              run(() => feesApi.paiements.rejeter(row.idPaiement, motif), "Paiement rejeté");
            }}
          >
            <XCircle className="h-4 w-4" />
          </Button>
        )}
        {isAdmin && !["ANNULE", "REJETE"].includes(row.statut) && (
          <Button
            variant="ghost"
            size="sm"
            title="Annuler"
            onClick={() => {
              const motif = prompt("Motif d'annulation :");
              if (motif == null) return;
              run(() => feesApi.paiements.annuler(row.idPaiement, motif), "Paiement annulé");
            }}
          >
            <Ban className="h-4 w-4" />
          </Button>
        )}
      </>
    );
  };

  return (
    <ResourceCrud<Paiement>
      title="Paiements"
      description="Encaissements idempotents (chaque création porte une clé d'idempotence) — lot 4g."
      idKey="idPaiement"
      list={feesApi.paiements.list}
      create={create}
      fields={fields}
      columns={columns}
      searchKeys={["reference", "eleve", "statut", "moyen"]}
      canWrite={isAdmin}
      rowActions={actions}
    />
  );
}