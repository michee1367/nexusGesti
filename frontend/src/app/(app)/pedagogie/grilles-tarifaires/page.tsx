"use client";

import Link from "next/link";
import { toast } from "sonner";
import { Archive, Lock, CheckCircle2 } from "lucide-react";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { humanize, formatDate } from "@/components/pedagogie/format";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/components/providers/auth-provider";
import { feesApi } from "@/services/api";
import type { GrilleTarifaire } from "@/types";

const fields: FieldDef[] = [
  { name: "code", label: "Code", required: true },
  { name: "libelle", label: "Libellé" },
  { name: "annee", label: "Année" },
  { name: "profil", label: "Profil" },
  { name: "devise", label: "Devise", defaultValue: "USD" },
];

const columns: ColumnDef<GrilleTarifaire>[] = [
  {
    key: "code",
    label: "Code",
    render: (row) => (
      <Link href={`/pedagogie/grilles-tarifaires/${row.id}`} className="font-mono font-medium text-blue-600 hover:underline">
        {row.code}
      </Link>
    ),
  },
  { key: "libelle", label: "Libellé", render: (row) => <span className="font-medium text-slate-900">{row.libelle ?? "—"}</span> },
  { key: "annee", label: "Année", render: (row) => row.annee ?? "—" },
  { key: "profil", label: "Profil", render: (row) => row.profil ?? "—" },
  { key: "version", label: "Version", render: (row) => `v${row.version}` },
  { key: "lignes", label: "Lignes", render: (row) => row.lignes?.length ?? 0 },
  { key: "reductions", label: "Réductions", render: (row) => row.reductions?.length ?? 0 },
  { key: "dateActivation", label: "Activée le", render: (row) => formatDate(row.dateActivation) },
  {
    key: "statut",
    label: "Statut",
    render: (row) => (
      <Badge variant={row.statut === "ACTIVE" ? "success" : row.statut === "ARCHIVEE" ? "danger" : "outline"}>
        {humanize(row.statut)}
      </Badge>
    ),
  },
];

export default function GrillesTarifairesPage() {
  const { isAdmin } = useAuth();

  const actions = (row: GrilleTarifaire, reload: () => void) => {
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
          <Button variant="ghost" size="sm" title="Activer" onClick={() => run(() => feesApi.grilles.activer(row.id), "Grille activée")}>
            <CheckCircle2 className="h-4 w-4" />
          </Button>
        )}
        {isAdmin && row.statut !== "ARCHIVEE" && (
          <Button variant="ghost" size="sm" title="Figer" onClick={() => run(() => feesApi.grilles.figer(row.id), "Grille figée")}>
            <Lock className="h-4 w-4" />
          </Button>
        )}
        {isAdmin && row.statut !== "ARCHIVEE" && (
          <Button variant="ghost" size="sm" title="Archiver" onClick={() => run(() => feesApi.grilles.archiver(row.id), "Grille archivée")}>
            <Archive className="h-4 w-4" />
          </Button>
        )}
        <Link href={`/pedagogie/grilles-tarifaires/${row.id}`}>
          <Button variant="ghost" size="sm">
            Détail
          </Button>
        </Link>
      </>
    );
  };

  return (
    <ResourceCrud<GrilleTarifaire>
      title="Grilles tarifaires"
      description="Grilles tarifaires versionnées, lignes de frais et réductions (lot 4g)."
      idKey="id"
      list={feesApi.grilles.list}
      create={feesApi.grilles.create}
      fields={fields}
      columns={columns}
      searchKeys={["code", "libelle", "annee", "profil", "statut"]}
      canWrite={isAdmin}
      rowActions={actions}
    />
  );
}