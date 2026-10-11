"use client";

import Link from "next/link";
import { toast } from "sonner";
import { Calculator, CheckCircle2, FileSignature } from "lucide-react";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, TYPE_BULLETIN } from "@/components/pedagogie/constants";
import { humanize } from "@/components/pedagogie/format";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/components/providers/auth-provider";
import { assessmentApi } from "@/services/api";
import type { Bulletin } from "@/types";

const fields: FieldDef[] = [
  { name: "eleveId", label: "Élève (UUID)", colSpan: 2 },
  { name: "anneeId", label: "Année (UUID)", colSpan: 2 },
  { name: "periodeId", label: "Période (UUID)" },
  { name: "classeId", label: "Classe (UUID)" },
  { name: "type", label: "Type", type: "select", options: enumOptions(TYPE_BULLETIN) },
];

const columns: ColumnDef<Bulletin>[] = [
  {
    key: "reference",
    label: "Référence",
    render: (row) => (
      <Link href={`/pedagogie/bulletins/${row.idBulletin}`} className="font-mono font-medium text-blue-600 hover:underline">
        {row.reference ?? row.idBulletin.slice(0, 8)}
      </Link>
    ),
  },
  {
    key: "eleveId",
    label: "Élève",
    render: (row) => (row.eleveId ? <span className="font-mono text-xs">{row.eleveId.slice(0, 8)}…</span> : "—"),
  },
  { key: "type", label: "Type", render: (row) => (row.type ? humanize(row.type) : "—") },
  {
    key: "moyenneGenerale",
    label: "Moyenne",
    render: (row) => (row.moyenneGenerale != null ? <span className="font-semibold">{row.moyenneGenerale}</span> : "—"),
  },
  {
    key: "rang",
    label: "Rang",
    render: (row) => (row.rang != null ? <span>#{row.rang}/{row.effectif ?? "—"}</span> : "—"),
  },
  { key: "version", label: "Version", render: (row) => <Badge variant="outline">v{row.version}</Badge> },
  {
    key: "statut",
    label: "Statut",
    render: (row) => (
      <Badge variant={row.statut === "SIGNE" ? "success" : row.statut === "PUBLIE" ? "warning" : "outline"}>
        {humanize(row.statut ?? "—")}
      </Badge>
    ),
  },
];

export default function BulletinsPage() {
  const { isAdmin } = useAuth();

  const actions = (row: Bulletin, reload: () => void) => (
    <>
      {isAdmin && (row.statut === "BROUILLON" || row.statut === "RECTIFIE") && (
        <Button
          variant="ghost"
          size="sm"
          title="Calculer"
          onClick={async () => {
            try {
              await assessmentApi.bulletins.calculer(row.idBulletin);
              toast.success("Bulletin calculé");
              reload();
            } catch (error) {
              toast.error(error instanceof Error ? error.message : "Erreur");
            }
          }}
        >
          <Calculator className="h-4 w-4" />
        </Button>
      )}
      {isAdmin && row.statut === "CALCULE" && (
        <>
          <Button
            variant="ghost"
            size="sm"
            title="Publier"
            onClick={async () => {
              try {
                await assessmentApi.bulletins.publier(row.idBulletin);
                toast.success("Bulletin publié");
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
            title="Générer les documents"
            onClick={async () => {
              try {
                await assessmentApi.bulletins.generer(row.idBulletin);
                toast.success("Documents générés");
                reload();
              } catch (error) {
                toast.error(error instanceof Error ? error.message : "Erreur");
              }
            }}
          >
            <FileSignature className="h-4 w-4" />
          </Button>
        </>
      )}
      <Link href={`/pedagogie/bulletins/${row.idBulletin}`}>
        <Button variant="ghost" size="sm">
          Détail
        </Button>
      </Link>
    </>
  );

  return (
    <ResourceCrud<Bulletin>
      title="Bulletins"
      description="Bulletins de notes, moyennes pondérées et coupons de notes (lot 4e)."
      idKey="idBulletin"
      list={assessmentApi.bulletins.list}
      create={assessmentApi.bulletins.create}
      fields={fields}
      columns={columns}
      searchKeys={["reference", "eleveId", "type", "statut"]}
      canWrite={isAdmin}
      rowActions={actions}
    />
  );
}