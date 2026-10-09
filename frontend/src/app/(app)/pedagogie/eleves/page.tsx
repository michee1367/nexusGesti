"use client";

import Link from "next/link";
import { Eye } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, SEXE, STATUT_ELEVE } from "@/components/pedagogie/constants";
import { formatDate } from "@/components/pedagogie/format";
import { useAuth } from "@/components/providers/auth-provider";
import { peopleApi } from "@/services/api";
import type { Eleve, StatutEleve } from "@/types";

const fields: FieldDef[] = [
  { name: "nom", label: "Nom", required: true },
  { name: "postNom", label: "Post-nom" },
  { name: "prenom", label: "Prénom", required: true },
  { name: "sexe", label: "Sexe", type: "select", options: enumOptions(SEXE) },
  { name: "dateNaissance", label: "Date de naissance", type: "date" },
  { name: "lieuNaissance", label: "Lieu de naissance" },
  { name: "nationalite", label: "Nationalité" },
  { name: "statut", label: "Statut", type: "select", options: enumOptions(STATUT_ELEVE), defaultValue: "PREINSCRIT" },
  { name: "dateInscription", label: "Date d'inscription", type: "date" },
  { name: "etablissementOrigine", label: "Établissement d'origine", colSpan: 2 },
];

const statutVariant = (statut: StatutEleve) => {
  if (statut === "INSCRIT" || statut === "REINSCRIT") return "success" as const;
  if (statut === "RADIE" || statut === "ABANDON") return "danger" as const;
  if (statut === "TRANSFERE" || statut === "OBTEN_DIPLOME") return "secondary" as const;
  return "outline" as const;
};

const columns: ColumnDef<Eleve>[] = [
  { key: "matricule", label: "Matricule", render: (row) => <span className="font-mono text-xs">{row.matricule}</span> },
  {
    key: "nom",
    label: "Nom complet",
    render: (row) => (
      <span className="font-medium text-slate-900">
        {row.nom} {row.postNom ?? ""} {row.prenom}
      </span>
    ),
  },
  { key: "sexe", label: "Sexe" },
  { key: "dateNaissance", label: "Naissance", render: (row) => formatDate(row.dateNaissance) },
  {
    key: "statut",
    label: "Statut",
    render: (row) => <Badge variant={statutVariant(row.statut)}>{row.statut.replace(/_/g, " ")}</Badge>,
  },
];

export default function ElevesPage() {
  const { isAdmin } = useAuth();
  return (
    <ResourceCrud<Eleve>
      title="Élèves"
      description="Dossiers, inscriptions et suivi disciplinaire."
      idKey="idEleve"
      list={peopleApi.eleves.list}
      create={peopleApi.eleves.create}
      update={peopleApi.eleves.update}
      fields={fields}
      columns={columns}
      searchKeys={["matricule", "nom", "postNom", "prenom"]}
      canWrite={isAdmin}
      rowActions={(row) => (
        <Link href={`/pedagogie/eleves/${row.idEleve}`}>
          <Button variant="ghost" size="sm">
            <Eye className="h-4 w-4" />
          </Button>
        </Link>
      )}
    />
  );
}
