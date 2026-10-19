"use client";

import Link from "next/link";
import { toast } from "sonner";
import { CheckCircle2, Lock } from "lucide-react";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { enumOptions, UNITE_VENTE } from "@/components/pedagogie/constants";
import { useAuth } from "@/components/providers/auth-provider";
import { useProduitsMap } from "@/hooks/use-lookup";
import { boulangerieApi } from "@/services/api";
import type { FicheRecette } from "@/types";

const fields: FieldDef[] = [
  { name: "code", label: "Code", required: true },
  { name: "produitCode", label: "Produit", entityKey: "produits", required: true },
  { name: "libelle", label: "Libellé" },
  { name: "rendementQuantite", label: "Rendement (quantité)", type: "number", defaultValue: 1 },
  { name: "rendementUnite", label: "Unité de rendement", type: "select", options: enumOptions(UNITE_VENTE) },
  { name: "tempsPreparationMin", label: "Préparation (min)", type: "number" },
  { name: "tempsCuissonMin", label: "Cuisson (min)", type: "number" },
  { name: "temperatureCuissonC", label: "Température cuisson (°C)", type: "number" },
  { name: "instructions", label: "Instructions", type: "textarea", colSpan: 2 },
];

const columns: ColumnDef<FicheRecette>[] = [
  {
    key: "code",
    label: "Code",
    render: (row) => (
      <Link
        href={`/boulangerie/recettes/${row.idRecette}`}
        className="font-mono font-medium text-blue-600 hover:underline"
      >
        {row.code}
      </Link>
    ),
  },
  { key: "produitCode", label: "Produit", lookupKey: "produits" },
  { key: "libelle", label: "Libellé", render: (row) => row.libelle ?? "—" },
  { key: "version", label: "Version", render: (row) => `v${row.version ?? 1}` },
  { key: "ingredients", label: "Ingrédients", render: (row) => row.ingredients?.length ?? 0 },
  {
    key: "active",
    label: "Actif",
    render: (row) => <Badge variant={row.active ? "success" : "outline"}>{row.active ? "Oui" : "Non"}</Badge>,
  },
  {
    key: "modifiable",
    label: "Statut",
    render: (row) => (
      <Badge variant={row.modifiable === false ? "warning" : "outline"}>
        {row.modifiable === false ? "Figée" : "Modifiable"}
      </Badge>
    ),
  },
];

export default function RecettesPage() {
  const { isAdmin } = useAuth();
  const produits = useProduitsMap();

  const actions = (row: FicheRecette, reload: () => void) => {
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
        {isAdmin && !row.active && (
          <Button
            variant="ghost"
            size="sm"
            title="Activer"
            onClick={() => run(() => boulangerieApi.recettes.activer(row.idRecette!), "Recette activée")}
          >
            <CheckCircle2 className="h-4 w-4" />
          </Button>
        )}
        {isAdmin && row.modifiable !== false && (
          <Button
            variant="ghost"
            size="sm"
            title="Figer"
            onClick={() => run(() => boulangerieApi.recettes.figer(row.idRecette!), "Recette figée")}
          >
            <Lock className="h-4 w-4" />
          </Button>
        )}
        <Link href={`/boulangerie/recettes/${row.idRecette}`}>
          <Button variant="ghost" size="sm">
            Détail
          </Button>
        </Link>
      </>
    );
  };

  return (
    <ResourceCrud<FicheRecette>
      title="Fiches recette"
      description="Recettes versionnées : ingrédients, rendement, temps et température de cuisson (lot 1)."
      idKey="idRecette"
      list={boulangerieApi.recettes.list}
      create={boulangerieApi.recettes.create}
      fields={fields}
      columns={columns}
      entities={{ produits }}
      searchKeys={["code", "libelle", "produitCode"]}
      canWrite={isAdmin}
      createLabel="Nouvelle recette"
      rowActions={actions}
    />
  );
}
