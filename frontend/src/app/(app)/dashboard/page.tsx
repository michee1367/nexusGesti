"use client";

import Link from "next/link";
import { Building2, CalendarDays, FileText, FolderOpen, ShieldCheck } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import {
  PageHeader,
  LoadingView,
  ErrorState,
} from "@/components/common/async-state";
import { useApi } from "@/hooks/use-api";
import { useTenant } from "@/components/providers/tenant-provider";
import {
  anneeApi,
  grilleApi,
  referentielApi,
  tenantApi,
} from "@/services/api";
import { cn } from "@/lib/utils";

const profiles: Record<string, string> = {
  PRIMAIRE: "Primaire",
  SECONDAIRE: "Secondaire",
  UNIVERSITAIRE: "Universitaire",
};

function CardLink({ href, icon: Icon, title, value, hint }: { href: string; icon: typeof Building2; title: string; value: string; hint?: string }) {
  return (
    <Link href={href} className="block">
      <Card className="transition-shadow hover:shadow-md">
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium text-slate-500">{title}</CardTitle>
          <span className="flex h-9 w-9 items-center justify-center rounded-lg bg-blue-50">
            <Icon className="h-4 w-4 text-blue-600" />
          </span>
        </CardHeader>
        <CardContent>
          <p className="text-2xl font-bold text-slate-900">{value}</p>
          {hint && <p className="mt-1 text-xs text-slate-500">{hint}</p>}
        </CardContent>
      </Card>
    </Link>
  );
}

const statutStyles: Record<string, string> = {
  ACTIF: "bg-success/10 text-success border-success/30",
  PREPARATION: "bg-warning/10 text-warning border-warning/30",
  SUSPENDU: "bg-danger/10 text-danger border-danger/30",
  ARCHIVE: "bg-slate-100 text-slate-500 border-slate-300",
};

export default function DashboardPage() {
  const { tenantId } = useTenant();
  const tenant = useApi(
    () => tenantApi.get(tenantId ?? ""),
    [tenantId]
  );
  const annees = useApi(anneeApi.list, [tenantId]);
  const grilles = useApi(() => grilleApi.list("notation"), [tenantId]);
  const referentiels = useApi(referentielApi.list, [tenantId]);

  if (tenant.loading) return <LoadingView label="Chargement de l'établissement…" />;
  if (tenant.error) return <ErrorState error={tenant.error} onRetry={tenant.reload} />;

  const etablissement = tenant.data;

  return (
    <div>
      <PageHeader title="Tableau de bord" description="Vue d'ensemble du tenant courant." />

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <CardLink
          href="/tenant"
          icon={Building2}
          title="Établissement"
          value={etablissement?.denomination ?? "—"}
          hint={etablissement?.code}
        />
        <CardLink
          href="/annees"
          icon={CalendarDays}
          title="Années scolaires"
          value={String(annees.data?.length ?? 0)}
          hint="Cloturables / ouvrables"
        />
        <CardLink
          href="/grilles"
          icon={FileText}
          title="Grilles"
          value={String(grilles.data?.length ?? 0)}
          hint="Notation · stock · conformité"
        />
        <CardLink
          href="/referentiels"
          icon={FolderOpen}
          title="Référentiels"
          value={String(referentiels.data?.length ?? 0)}
          hint="17 conteneurs à la création"
        />
      </div>

      <div className="mt-6 grid gap-4 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-base">
              <ShieldCheck className="h-4 w-4 text-blue-600" />
              Établissement
            </CardTitle>
            <CardDescription>Identité et cycle de vie du tenant</CardDescription>
          </CardHeader>
          <CardContent className="space-y-3 text-sm">
            <div className="flex flex-wrap items-center gap-2">
              <Badge className={cn("border", statutStyles[etablissement?.statut ?? "PREPARATION"])}>
                {etablissement?.statut ?? "—"}
              </Badge>
              <Badge variant="secondary">{profiles[etablissement?.profil ?? ""] ?? etablissement?.profil ?? "—"}</Badge>
            </div>
            <dl className="grid grid-cols-2 gap-x-4 gap-y-2">
              <div>
                <dt className="text-xs text-slate-500">Code</dt>
                <dd className="font-mono text-slate-900">{etablissement?.code ?? "—"}</dd>
              </div>
              <div>
                <dt className="text-xs text-slate-500">Id tenant</dt>
                <dd className="truncate font-mono text-xs text-slate-900">{tenantId ?? "—"}</dd>
              </div>
            </dl>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-base">
              <FolderOpen className="h-4 w-4 text-blue-600" />
              Accès rapides
            </CardTitle>
            <CardDescription>Pages courantes du socle</CardDescription>
          </CardHeader>
          <CardContent className="grid gap-2 sm:grid-cols-2">
            {[
              { href: "/grilles", label: "Grilles de notation", hint: "activer / figer" },
              { href: "/annees", label: "Année en cours", hint: "ouvrir / clôturer" },
              { href: "/referentiels", label: "Référentiels", hint: "17 conteneurs" },
              { href: "/tenant", label: "Sites & paramètres", hint: "structure" },
            ].map((item) => (
              <Link href={item.href} key={item.href}>
                <Card className="bg-slate-50 transition-colors hover:bg-slate-100">
                  <CardContent className="p-4">
                    <p className="text-sm font-medium text-slate-900">{item.label}</p>
                    <p className="text-xs text-slate-500">{item.hint}</p>
                  </CardContent>
                </Card>
              </Link>
            ))}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}