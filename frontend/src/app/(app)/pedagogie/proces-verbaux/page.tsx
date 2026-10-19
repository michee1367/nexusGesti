"use client";

import { toast } from "sonner";
import { BadgeCheck, FileSignature, Send, Stamp } from "lucide-react";
import { ResourceCrud, type ColumnDef, type FieldDef } from "@/components/pedagogie/resource-crud";
import { humanize, formatDate } from "@/components/pedagogie/format";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { useEnseignantsMap, useSessionsMap } from "@/hooks/use-lookup";
import { examApi } from "@/services/api";
import type { ProcesVerbal } from "@/types";

const fields: FieldDef[] = [
  { name: "reference", label: "Référence", required: true },
  { name: "sessionId", label: "Session", type: "select", entityKey: "sessions", colSpan: 2 },
  { name: "dateRedaction", label: "Date de rédaction", type: "date" },
  { name: "redigePar", label: "Rédigé par", type: "select", entityKey: "enseignants" },
  { name: "presenceDirecteur", label: "Présence du directeur", type: "checkbox" },
  { name: "observations", label: "Observations", type: "textarea", colSpan: 2 },
];

const columns: ColumnDef<ProcesVerbal>[] = [
  { key: "reference", label: "Référence", render: (row) => <span className="font-mono font-medium text-slate-900">{row.reference}</span> },
  { key: "dateRedaction", label: "Rédaction", render: (row) => formatDate(row.dateRedaction) },
  { key: "redigePar", label: "Rédigé par", lookupKey: "enseignants" },
  { key: "observations", label: "Observations", render: (row) => row.observations ?? "—" },
  {
    key: "signatures",
    label: "Signatures",
    render: (row) => row.signatures?.length ?? 0,
  },
  {
    key: "statut",
    label: "Statut",
    render: (row) => (
      <Badge
        variant={
          row.statut === "HOMOLOGUE"
            ? "success"
            : row.statut === "SIGNE"
              ? "warning"
              : row.statut === "CONTESTE"
                ? "danger"
                : "outline"
        }
      >
        {humanize(row.statut)}
      </Badge>
    ),
  },
];

export default function ProcesVerbauxPage() {
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const sessions = useSessionsMap([tenantId]);
  const enseignants = useEnseignantsMap([tenantId]);

  const actions = (row: ProcesVerbal, reload: () => void) => {
    const id = row.id as string;
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
          <Button variant="ghost" size="sm" title="Soumettre" onClick={() => run(() => examApi.procesVerbaux.soumettre(id), "PV soumis")}>
            <Send className="h-4 w-4" />
          </Button>
        )}
        {isAdmin && row.statut === "SOUMIS" && (
          <Button
            variant="ghost"
            size="sm"
            title="Signer"
            onClick={() => run(() => examApi.procesVerbaux.signer(id, "PRESIDENT"), "PV signé")}
          >
            <FileSignature className="h-4 w-4" />
          </Button>
        )}
        {isAdmin && row.statut === "SIGNE" && (
          <Button variant="ghost" size="sm" title="Homologuer" onClick={() => run(() => examApi.procesVerbaux.homologuer(id), "PV homologué")}>
            <Stamp className="h-4 w-4" />
          </Button>
        )}
        {isAdmin && !["CONTESTE", "HOMOLOGUE"].includes(row.statut) && (
          <Button
            variant="ghost"
            size="sm"
            title="Contester"
            onClick={() => {
              const motif = prompt("Motif de contestation :");
              if (motif == null) return;
              run(() => examApi.procesVerbaux.contester(id, motif), "PV contesté");
            }}
          >
            <BadgeCheck className="h-4 w-4" />
          </Button>
        )}
      </>
    );
  };

  return (
    <ResourceCrud<ProcesVerbal>
      title="Procès-verbaux"
      description="Procès-verbaux de délibération, signatures et homologation (lot 4f)."
      idKey="id"
      list={examApi.procesVerbaux.list}
      create={examApi.procesVerbaux.create}
      fields={fields}
      columns={columns}
      entities={{ sessions, enseignants }}
      searchKeys={["reference", "observations", "statut"]}
      canWrite={isAdmin}
      rowActions={actions}
    />
  );
}