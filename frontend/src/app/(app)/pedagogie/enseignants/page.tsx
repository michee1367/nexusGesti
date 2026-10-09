"use client";

import { useMemo } from "react";
import Link from "next/link";
import { Eye } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, SEXE, STATUT_ENSEIGNANT, toOptions } from "@/components/pedagogie/constants";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { useApi } from "@/hooks/use-api";
import { peopleApi } from "@/services/api";
import type { Enseignant, StatutEnseignant } from "@/types";

const statutVariant = (statut: StatutEnseignant) => {
  if (statut === "ACTIF") return "success" as const;
  if (statut === "INACTIF" || statut === "RETRAITE") return "outline" as const;
  return "secondary" as const;
};

export default function EnseignantsPage() {
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const services = useApi(peopleApi.personnel.services.list, [tenantId]);

  const fields = useMemo<FieldDef[]>(
    () => [
      { name: "nom", label: "Nom", required: true },
      { name: "postNom", label: "Post-nom" },
      { name: "prenom", label: "Prénom", required: true },
      { name: "sexe", label: "Sexe", type: "select", options: enumOptions(SEXE) },
      { name: "dateNaissance", label: "Date de naissance", type: "date" },
      { name: "telephone", label: "Téléphone" },
      { name: "email", label: "Email" },
      { name: "diplome", label: "Diplôme" },
      { name: "diplomePlus", label: "Diplôme le plus élevé" },
      { name: "specialite", label: "Spécialités (séparées par des virgules)", type: "tags", colSpan: 2 },
      { name: "dateRecrutement", label: "Date de recrutement", type: "date" },
      { name: "statut", label: "Statut", type: "select", options: enumOptions(STATUT_ENSEIGNANT), defaultValue: "ACTIF" },
      { name: "volumeHoraireHebdo", label: "Charge horaire hebdo", type: "number" },
      {
        name: "serviceRattachementId",
        label: "Service",
        type: "select",
        options: toOptions(services.data, "id", "libelle"),
      },
    ],
    [services.data],
  );

  const columns: ColumnDef<Enseignant>[] = [
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
    { key: "specialite", label: "Spécialités", render: (row) => (row.specialite?.length ? row.specialite.join(", ") : "—") },
    { key: "volumeHoraireHebdo", label: "Charge hebdo" },
    {
      key: "statut",
      label: "Statut",
      render: (row) => <Badge variant={statutVariant(row.statut)}>{row.statut}</Badge>,
    },
  ];

  return (
    <ResourceCrud<Enseignant>
      title="Enseignants"
      description="Corps enseignant, affectations et titulariat."
      idKey="id"
      list={peopleApi.enseignants.list}
      create={peopleApi.enseignants.create}
      update={peopleApi.enseignants.update}
      fields={fields}
      columns={columns}
      searchKeys={["matricule", "nom", "postNom", "prenom", "email"]}
      canWrite={isAdmin}
      rowActions={(row) => (
        <Link href={`/pedagogie/enseignants/${row.id}`}>
          <Button variant="ghost" size="sm">
            <Eye className="h-4 w-4" />
          </Button>
        </Link>
      )}
    />
  );
}
