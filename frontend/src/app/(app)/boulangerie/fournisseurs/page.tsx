"use client";

import Link from "next/link";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/components/providers/auth-provider";
import { boulangerieApi } from "@/services/api";
import type { FournisseurBoulangerie } from "@/types";

const fields: FieldDef[] = [
  { name: "code", label: "Code", required: true },
  { name: "raisonSociale", label: "Raison sociale", required: true },
  { name: "tiersCode", label: "Tiers (code)", placeholder: "TIER-…" },
  { name: "delaiLivraisonJours", label: "Délai de livraison (jours)", type: "number", defaultValue: 0 },
  { name: "categorieProduits", label: "Catégorie de produits" },
  { name: "conditionsReglement", label: "Conditions de règlement", type: "textarea" },
  { name: "montantMiniCommande", label: "Montant mini commande", type: "number" },
  { name: "actif", label: "Actif", type: "checkbox", defaultValue: true },
];

const columns: ColumnDef<FournisseurBoulangerie>[] = [
  {
    key: "code",
    label: "Code",
    render: (row) => <span className="font-mono font-medium">{row.code}</span>,
  },
  { key: "raisonSociale", label: "Raison sociale", render: (row) => row.raisonSociale ?? "—" },
  { key: "categorieProduits", label: "Catégorie", render: (row) => row.categorieProduits ?? "—" },
  {
    key: "delaiLivraisonJours",
    label: "Délai",
    render: (row) => `${row.delaiLivraisonJours ?? 0} j`,
  },
  {
    key: "actif",
    label: "Statut",
    render: (row) => (
      <Badge variant={row.actif === false ? "outline" : "success"}>
        {row.actif === false ? "Inactif" : "Actif"}
      </Badge>
    ),
  },
];

export default function FournisseursPage() {
  const { isAdmin } = useAuth();

  return (
    <ResourceCrud<FournisseurBoulangerie>
      title="Fournisseurs"
      description="Fournisseurs de matières premières et consommables (lot 3 approvisionnement)."
      idKey="idFournisseur"
      list={boulangerieApi.fournisseurs.list}
      create={boulangerieApi.fournisseurs.create}
      update={boulangerieApi.fournisseurs.update}
      fields={fields}
      columns={columns}
      searchKeys={["code", "raisonSociale", "categorieProduits"]}
      canWrite={isAdmin}
      createLabel="Nouveau fournisseur"
      rowActions={(row) => (
        <Link href={`/boulangerie/fournisseurs/${row.idFournisseur}`}>
          <Button variant="ghost" size="sm">
            Détail
          </Button>
        </Link>
      )}
    />
  );
}
