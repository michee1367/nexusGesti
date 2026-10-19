"use client";

import { toast } from "sonner";
import { Lock } from "lucide-react";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { formatDate } from "@/components/pedagogie/format";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { useSitesMap } from "@/hooks/use-lookup";
import { feesApi } from "@/services/api";
import type { Journalier } from "@/types";

const fields: FieldDef[] = [
  { name: "date", label: "Date", type: "date" },
  { name: "site", label: "Site", type: "select", entityKey: "sites" },
  { name: "fond", label: "Fond de caisse", type: "number" },
];

const columns: ColumnDef<Journalier>[] = [
  { key: "date", label: "Date", render: (row) => formatDate(row.date) },
  { key: "site", label: "Site", lookupKey: "sites" },
  { key: "fond", label: "Fond", render: (row) => (row.fond != null ? row.fond : "—") },
  {
    key: "statut",
    label: "Statut",
    render: (row) => <Badge variant={row.statut === "CLOTURE" ? "success" : "warning"}>{row.statut}</Badge>,
  },
];

export default function JournaliersPage() {
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const sites = useSitesMap([tenantId]);

  const actions = (row: Journalier, reload: () => void) => (
    <>
      {isAdmin && row.statut !== "CLOTURE" && (
        <Button
          variant="ghost"
          size="sm"
          title="Clôturer"
          onClick={async () => {
            try {
              await feesApi.journaliers.cloturer(row.id);
              toast.success("Journalier clôturé");
              reload();
            } catch (error) {
              toast.error(error instanceof Error ? error.message : "Erreur");
            }
          }}
        >
          <Lock className="h-4 w-4" />
        </Button>
      )}
    </>
  );

  return (
    <ResourceCrud<Journalier>
      title="Journaliers de caisse"
      description="Journaliers d'encaissement par site (lot 4g)."
      idKey="id"
      list={feesApi.journaliers.list}
      create={feesApi.journaliers.ouvrir}
      fields={fields}
      columns={columns}
      entities={{ sites }}
      searchKeys={["statut"]}
      canWrite={isAdmin}
      createLabel="Ouvrir"
      rowActions={actions}
    />
  );
}