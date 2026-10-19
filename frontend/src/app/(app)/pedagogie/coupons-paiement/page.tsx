"use client";

import { toast } from "sonner";
import { Ban, HandCoins, ScanLine } from "lucide-react";
import { ResourceCrud, type ColumnDef } from "@/components/pedagogie/resource-crud";
import { humanize, formatDate } from "@/components/pedagogie/format";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { useElevesMap } from "@/hooks/use-lookup";
import { feesApi } from "@/services/api";
import type { CouponPaiement } from "@/types";

const columns: ColumnDef<CouponPaiement>[] = [
  { key: "numero", label: "Numéro", render: (row) => <span className="font-mono font-medium text-slate-900">{row.numero}</span> },
  { key: "libelle", label: "Libellé", render: (row) => row.libelle ?? "—" },
  { key: "eleve", label: "Élève", lookupKey: "eleves" },
  { key: "montant", label: "Montant", render: (row) => row.montant ?? "—" },
  { key: "moyen", label: "Moyen", render: (row) => (row.moyen ? humanize(row.moyen) : "—") },
  { key: "dateEdition", label: "Édition", render: (row) => formatDate(row.dateEdition) },
  {
    key: "statut",
    label: "Statut",
    render: (row) => (
      <Badge
        variant={
          row.statut === "REMIS"
            ? "success"
            : row.statut === "ANNULE"
              ? "danger"
              : row.statut === "TIRE"
                ? "warning"
                : "outline"
        }
      >
        {humanize(row.statut)}
      </Badge>
    ),
  },
  {
    key: "remisA",
    label: "Remis à",
    lookupKey: "eleves",
  },
];

export default function CouponsPaiementPage() {
  const { isAdmin } = useAuth();
  const { tenantId } = useTenant();
  const eleves = useElevesMap([tenantId]);

  const actions = (row: CouponPaiement, reload: () => void) => {
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
        {row.statut === "A_TIRER" && (
          <Button variant="ghost" size="sm" title="Tirer" onClick={() => run(() => feesApi.coupons.tirer(id), "Coupon tiré")}>
            <HandCoins className="h-4 w-4" />
          </Button>
        )}
        {isAdmin && row.statut === "TIRE" && (
          <Button
            variant="ghost"
            size="sm"
            title="Remettre"
            onClick={() => {
              const destinataire = prompt("Destinataire (UUID) :");
              if (destinataire == null) return;
              run(() => feesApi.coupons.remettre(id, destinataire), "Coupon remis");
            }}
          >
            <HandCoins className="h-4 w-4" />
          </Button>
        )}
        <Button
          variant="ghost"
          size="sm"
          title="Vérifier l'empreinte"
          onClick={async () => {
            try {
              const valide = await feesApi.coupons.verifier(id);
              toast[valide ? "success" : "error"](valide ? "Empreinte valide" : "Empreinte invalide");
            } catch (error) {
              toast.error(error instanceof Error ? error.message : "Erreur");
            }
          }}
        >
          <ScanLine className="h-4 w-4" />
        </Button>
        {isAdmin && !["ANNULE", "ARCHIVE"].includes(row.statut) && (
          <Button
            variant="ghost"
            size="sm"
            title="Annuler"
            onClick={() => {
              const motif = prompt("Motif d'annulation :");
              if (motif == null) return;
              run(() => feesApi.coupons.annuler(id, motif), "Coupon annulé");
            }}
          >
            <Ban className="h-4 w-4" />
          </Button>
        )}
      </>
    );
  };

  return (
    <ResourceCrud<CouponPaiement>
      title="Coupons de paiement"
      description="Coupons numérotés par année, empreinte SHA-256 (lot 4g). Les coupons sont générés lors de la validation des paiements."
      idKey="id"
      list={feesApi.coupons.list}
      fields={[]}
      columns={columns}
      entities={{ eleves }}
      searchKeys={["numero", "libelle", "statut"]}
      canWrite={isAdmin}
      rowActions={actions}
    />
  );
}