"use client";

import Link from "next/link";
import { toast } from "sonner";
import { CheckCircle2, Ban } from "lucide-react";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { enumOptions, UNITE_VENTE } from "@/components/pedagogie/constants";
import { useAuth } from "@/components/providers/auth-provider";
import { useCategoriesMap, useGammesMap } from "@/hooks/use-lookup";
import { boulangerieApi } from "@/services/api";
import type { Produit } from "@/types";

const fields: FieldDef[] = [
  { name: "code", label: "Code", required: true },
  { name: "designation", label: "Désignation", required: true },
  { name: "gammeCode", label: "Gamme", entityKey: "gammes" },
  { name: "categorieCode", label: "Catégorie", entityKey: "categories" },
  { name: "uniteVente", label: "Unité de vente", type: "select", options: enumOptions(UNITE_VENTE) },
  { name: "prixVenteHT", label: "Prix de vente HT", type: "number" },
  { name: "tauxTva", label: "Taux TVA (%)", type: "number" },
  { name: "poidsUnitaireGrammes", label: "Poids unitaire (g)", type: "number" },
  { name: "dlcHeures", label: "DLC (heures)", type: "number" },
  { name: "articleCode", label: "Article (stock)", placeholder: "ART-..." },
  { name: "codeBarre", label: "Code-barres" },
  {
    name: "allergenes",
    label: "Allergènes",
    type: "tags",
    colSpan: 2,
    placeholder: "GLUTEN, LACTOSE, OEUF…",
  },
  { name: "vendableAuDetail", label: "Vendable au détail", type: "checkbox", defaultValue: true },
];

const columns: ColumnDef<Produit>[] = [
  {
    key: "code",
    label: "Code",
    render: (row) => (
      <Link
        href={`/boulangerie/produits/${row.idProduit}`}
        className="font-mono font-medium text-blue-600 hover:underline"
      >
        {row.code}
      </Link>
    ),
  },
  { key: "designation", label: "Désignation", render: (row) => <span className="font-medium text-slate-900">{row.designation}</span> },
  { key: "gammeCode", label: "Gamme", lookupKey: "gammes" },
  { key: "categorieCode", label: "Catégorie", lookupKey: "categories" },
  { key: "prixVenteHT", label: "Prix HT", render: (row) => row.prixVenteHT ?? "—" },
  { key: "uniteVente", label: "Unité", render: (row) => row.uniteVente ?? "—" },
  {
    key: "fabricable",
    label: "Fabricable",
    render: (row) => (
      <Badge variant={row.fabricable ? "success" : "outline"}>{row.fabricable ? "Oui" : "Non"}</Badge>
    ),
  },
  {
    key: "actif",
    label: "Statut",
    render: (row) => (
      <Badge variant={row.actif === false ? "danger" : "success"}>
        {row.actif === false ? "Inactif" : "Actif"}
      </Badge>
    ),
  },
];

export default function ProduitsPage() {
  const { isAdmin } = useAuth();
  const gammes = useGammesMap();
  const categories = useCategoriesMap();

  const actions = (row: Produit, reload: () => void) => {
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
        {isAdmin && row.actif === false && (
          <Button
            variant="ghost"
            size="sm"
            title="Activer"
            onClick={() => run(() => boulangerieApi.produits.activer(row.idProduit!), "Produit activé")}
          >
            <CheckCircle2 className="h-4 w-4" />
          </Button>
        )}
        {isAdmin && row.actif !== false && (
          <Button
            variant="ghost"
            size="sm"
            title="Désactiver"
            onClick={() => run(() => boulangerieApi.produits.desactiver(row.idProduit!), "Produit désactivé")}
          >
            <Ban className="h-4 w-4" />
          </Button>
        )}
        <Link href={`/boulangerie/produits/${row.idProduit}`}>
          <Button variant="ghost" size="sm">
            Détail
          </Button>
        </Link>
      </>
    );
  };

  return (
    <ResourceCrud<Produit>
      title="Produits"
      description="Catalogue de produits boulangerie : gammes, catégories, prix et allergènes (lot 1)."
      idKey="idProduit"
      list={boulangerieApi.produits.list}
      create={boulangerieApi.produits.create}
      fields={fields}
      columns={columns}
      entities={{ gammes, categories }}
      searchKeys={["code", "designation", "codeBarre"]}
      canWrite={isAdmin}
      createLabel="Nouveau produit"
      rowActions={actions}
    />
  );
}
