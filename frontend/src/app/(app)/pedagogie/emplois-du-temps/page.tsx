"use client";

import { useMemo, useState } from "react";
import Link from "next/link";
import { toast } from "sonner";
import { Eye } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { toOptions } from "@/components/pedagogie/constants";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { useApi } from "@/hooks/use-api";
import { anneeApi, structureApi } from "@/services/api";
import type { EmploiDuTemps } from "@/types";

function EmploiActions({ emploi, canWrite, reload }: { emploi: EmploiDuTemps; canWrite: boolean; reload: () => void }) {
  const [busy, setBusy] = useState(false);
  const publier = async () => {
    setBusy(true);
    try {
      await structureApi.emploisDuTemps.publier(emploi.idEmploi);
      toast.success("Emploi du temps publié");
      reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };
  return (
    <>
      <Link href={`/pedagogie/emplois-du-temps/${emploi.idEmploi}`}>
        <Button variant="ghost" size="sm">
          <Eye className="h-4 w-4" />
        </Button>
      </Link>
      {canWrite && !emploi.publie && (
        <Button size="sm" variant="outline" disabled={busy} onClick={publier}>
          Publier
        </Button>
      )}
    </>
  );
}

export default function EmploisDuTempsPage() {
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const annees = useApi(anneeApi.list, [tenantId]);
  const classes = useApi(structureApi.classes.list, [tenantId]);

  const fields = useMemo<FieldDef[]>(
    () => [
      { name: "anneeId", label: "Année", type: "select", options: toOptions(annees.data, "idAnnee", "libelle") },
      { name: "classeId", label: "Classe", type: "select", options: toOptions(classes.data, "idClasse", "libelle") },
    ],
    [annees.data, classes.data],
  );

  const columns: ColumnDef<EmploiDuTemps>[] = [
    { key: "classeId", label: "Classe", render: (row) => <span className="font-mono text-xs">{row.classeId}</span> },
    { key: "version", label: "Version", render: (row) => `v${row.version}` },
    { key: "plages", label: "Plages", render: (row) => row.plages?.length ?? 0 },
    {
      key: "conflits",
      label: "Conflits",
      render: (row) =>
        row.conflits?.length ? (
          <Badge variant="danger">{row.conflits.length}</Badge>
        ) : (
          <Badge variant="secondary">0</Badge>
        ),
    },
    {
      key: "publie",
      label: "Statut",
      render: (row) =>
        row.publie ? <Badge variant="success">Publié</Badge> : <Badge variant="outline">Brouillon</Badge>,
    },
  ];

  return (
    <ResourceCrud<EmploiDuTemps>
      title="Emplois du temps"
      description="Planning hebdomadaire par classe : plages, conflits et publication."
      idKey="idEmploi"
      list={structureApi.emploisDuTemps.list}
      create={structureApi.emploisDuTemps.create}
      fields={fields}
      columns={columns}
      canWrite={isAdmin}
      createLabel="Nouvel emploi"
      rowActions={(row, reload) => <EmploiActions emploi={row} canWrite={isAdmin} reload={reload} />}
    />
  );
}
