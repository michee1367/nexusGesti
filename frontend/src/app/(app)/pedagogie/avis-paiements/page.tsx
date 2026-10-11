"use client";

import Link from "next/link";
import { toast } from "sonner";
import { Ban, CheckCircle2, Send } from "lucide-react";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, TYPE_FRAIS } from "@/components/pedagogie/constants";
import { humanize, formatDate } from "@/components/pedagogie/format";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/components/providers/auth-provider";
import { feesApi } from "@/services/api";
import type { AvisPaiement } from "@/types";

const fields: FieldDef[] = [
  { name: "eleve", label: "Élève (UUID)", colSpan: 2, required: true },
  { name: "annee", label: "Année" },
  { name: "classe", label: "Classe" },
  { name: "grilleId", label: "Grille tarifaire (UUID)", colSpan: 2 },
  { name: "typeFrais", label: "Frais initial", type: "select", options: enumOptions(TYPE_FRAIS) },
  { name: "libelle", label: "Libellé du frais" },
  { name: "montant", label: "Montant", type: "number" },
  { name: "nbEcheances", label: "Échéances", type: "number", defaultValue: 1 },
];

const columns: ColumnDef<AvisPaiement>[] = [
  {
    key: "reference",
    label: "Référence",
    render: (row) => (
      <Link href={`/pedagogie/avis-paiements/${row.idAvis}`} className="font-mono font-medium text-blue-600 hover:underline">
        {row.reference}
      </Link>
    ),
  },
  { key: "eleve", label: "Élève", render: (row) => (row.eleve ? <span className="font-mono text-xs">{row.eleve.slice(0, 8)}…</span> : "—") },
  { key: "annee", label: "Année", render: (row) => row.annee ?? "—" },
  { key: "montantNet", label: "Net", render: (row) => row.montantNet ?? "—" },
  { key: "montantRegle", label: "Réglé", render: (row) => row.montantRegle ?? "—" },
  { key: "soldeDu", label: "Solde", render: (row) => row.soldeDu ?? "—" },
  {
    key: "statut",
    label: "Statut",
    render: (row) => (
      <Badge
        variant={
          row.statut === "REGLE"
            ? "success"
            : row.statut === "ANNULE"
              ? "danger"
              : row.statut === "EMIS" || row.statut === "PARTIELLEMENT_REGLE"
                ? "warning"
                : "outline"
        }
      >
        {humanize(row.statut)}
      </Badge>
    ),
  },
  { key: "dateEmission", label: "Émis le", render: (row) => formatDate(row.dateEmission) },
];

export default function AvisPaiementsPage() {
  const { isAdmin } = useAuth();

  const create = async (body: Partial<AvisPaiement>) => {
    const b = body as Record<string, unknown>;
    const lignes =
      b.typeFrais || b.montant != null
        ? [
            {
              typeFrais: b.typeFrais,
              libelle: b.libelle,
              montant: b.montant != null ? Number(b.montant) : 0,
              nbEcheances: b.nbEcheances != null ? Number(b.nbEcheances) : 1,
            },
          ]
        : [];
    return feesApi.avis.create({
      eleve: (b.eleve as string) ?? null,
      annee: (b.annee as string) ?? null,
      classe: (b.classe as string) ?? null,
      grilleId: (b.grilleId as string) ?? null,
      lignes: lignes as never,
    });
  };

  const actions = (row: AvisPaiement, reload: () => void) => {
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
        {isAdmin && row.statut === "BROUILLON" && (
          <Button variant="ghost" size="sm" title="Valider" onClick={() => run(() => feesApi.avis.valider(row.idAvis), "Avis validé")}>
            <CheckCircle2 className="h-4 w-4" />
          </Button>
        )}
        {isAdmin && !["ANNULE", "REGLE"].includes(row.statut) && (
          <Button variant="ghost" size="sm" title="Émettre" onClick={() => run(() => feesApi.avis.emettre(row.idAvis), "Avis émis")}>
            <Send className="h-4 w-4" />
          </Button>
        )}
        {isAdmin && !["ANNULE", "REGLE"].includes(row.statut) && (
          <Button
            variant="ghost"
            size="sm"
            title="Annuler"
            onClick={() => {
              const motif = prompt("Motif d'annulation :");
              if (motif == null) return;
              run(() => feesApi.avis.annuler(row.idAvis, motif), "Avis annulé");
            }}
          >
            <Ban className="h-4 w-4" />
          </Button>
        )}
        <Link href={`/pedagogie/avis-paiements/${row.idAvis}`}>
          <Button variant="ghost" size="sm">
            Détail
          </Button>
        </Link>
      </>
    );
  };

  return (
    <ResourceCrud<AvisPaiement>
      title="Avis de paiement"
      description="Avis de frais, échéances et réductions (lot 4g)."
      idKey="idAvis"
      list={feesApi.avis.list}
      create={create}
      fields={fields}
      columns={columns}
      searchKeys={["reference", "eleve", "annee", "statut"]}
      canWrite={isAdmin}
      rowActions={actions}
    />
  );
}