"use client";

import { useMemo } from "react";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { PageHeader } from "@/components/common/async-state";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { enumOptions, SEXE, toOptions, TYPE_SERVICE } from "@/components/pedagogie/constants";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { useApi } from "@/hooks/use-api";
import { peopleApi } from "@/services/api";
import type { Agent, ServiceEtablissement } from "@/types";

const serviceFields: FieldDef[] = [
  { name: "code", label: "Code", required: true },
  { name: "libelle", label: "Libellé", required: true },
  { name: "type", label: "Type", type: "select", options: enumOptions(TYPE_SERVICE) },
  { name: "email", label: "Email" },
];

const serviceColumns: ColumnDef<ServiceEtablissement>[] = [
  { key: "code", label: "Code", render: (row) => <span className="font-mono">{row.code}</span> },
  { key: "libelle", label: "Libellé", render: (row) => <span className="font-medium text-slate-900">{row.libelle}</span> },
  { key: "type", label: "Type", render: (row) => row.type.replace(/_/g, " ") },
  { key: "email", label: "Email" },
];

const agentColumns: ColumnDef<Agent>[] = [
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
  { key: "fonctionOfficielle", label: "Fonction" },
  { key: "telephone", label: "Téléphone" },
  { key: "statut", label: "Statut" },
];

export default function PersonnelPage() {
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const services = useApi(peopleApi.personnel.services.list, [tenantId]);

  const agentFields = useMemo<FieldDef[]>(
    () => [
      { name: "nom", label: "Nom", required: true },
      { name: "postNom", label: "Post-nom" },
      { name: "prenom", label: "Prénom", required: true },
      { name: "sexe", label: "Sexe", type: "select", options: enumOptions(SEXE) },
      { name: "dateNaissance", label: "Date de naissance", type: "date" },
      { name: "telephone", label: "Téléphone" },
      { name: "email", label: "Email" },
      { name: "fonctionOfficielle", label: "Fonction officielle" },
      { name: "dateRecrutement", label: "Date de recrutement", type: "date" },
      { name: "statut", label: "Statut" },
      {
        name: "serviceRattachementId",
        label: "Service",
        type: "select",
        options: toOptions(services.data, "id", "libelle"),
      },
    ],
    [services.data],
  );

  return (
    <div>
      <PageHeader title="Personnel" description="Services de l'établissement et agents administratifs." />
      <Tabs defaultValue="agents">
        <TabsList>
          <TabsTrigger value="agents">Agents</TabsTrigger>
          <TabsTrigger value="services">Services</TabsTrigger>
        </TabsList>

        <TabsContent value="agents">
          <ResourceCrud<Agent>
            embedded
            title="Agents"
            idKey="id"
            list={peopleApi.personnel.agents.list}
            create={peopleApi.personnel.agents.create}
            update={peopleApi.personnel.agents.update}
            fields={agentFields}
            columns={agentColumns}
            searchKeys={["matricule", "nom", "postNom", "prenom", "fonctionOfficielle"]}
            canWrite={isAdmin}
          />
        </TabsContent>

        <TabsContent value="services">
          <ResourceCrud<ServiceEtablissement>
            embedded
            title="Services"
            idKey="id"
            list={peopleApi.personnel.services.list}
            create={peopleApi.personnel.services.create}
            update={peopleApi.personnel.services.update}
            fields={serviceFields}
            columns={serviceColumns}
            searchKeys={["code", "libelle", "type"]}
            canWrite={isAdmin}
          />
        </TabsContent>
      </Tabs>
    </div>
  );
}
