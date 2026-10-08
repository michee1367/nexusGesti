"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  PageHeader,
  LoadingView,
  ErrorState,
  EmptyState,
} from "@/components/common/async-state";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { auditApi } from "@/services/api";
import type { AuditSearchCriteria } from "@/types";

export default function AuditPage() {
  const { tenantId } = useTenant();
  const { isAdmin } = useAuth();
  const [criteria, setCriteria] = useState<AuditSearchCriteria>({});
  const [applied, setApplied] = useState<AuditSearchCriteria>({});
  const audit = useApi(() => auditApi.search(applied), [applied, tenantId]);

  if (!isAdmin) {
    return (
      <div>
        <PageHeader title="Audit" description="Recherche des traces d'audit." />
        <p className="text-sm text-slate-500">Page réservée aux ADMIN.</p>
      </div>
    );
  }

  const set = (key: keyof AuditSearchCriteria, value: string) =>
    setCriteria((current) => ({ ...current, [key]: value || undefined }));

  return (
    <div>
      <PageHeader title="Audit" description="Recherche dans les journaux d'audit des services (ADMIN)." />

      <Card className="mb-6">
        <CardHeader>
          <CardTitle className="text-base">Critères de recherche</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-2 sm:grid-cols-3">
            <div className="space-y-1">
              <Label htmlFor="entityType">Type d&apos;entité</Label>
              <Input
                id="entityType"
                placeholder="ETABLISSEMENT, GRILLE…"
                value={criteria.entityType ?? ""}
                onChange={(e) => set("entityType", e.target.value)}
              />
            </div>
            <div className="space-y-1">
              <Label htmlFor="entityId">Id d&apos;entité</Label>
              <Input
                id="entityId"
                value={criteria.entityId ?? ""}
                onChange={(e) => set("entityId", e.target.value)}
              />
            </div>
            <div className="space-y-1">
              <Label htmlFor="eventType">Type d&apos;événement</Label>
              <Input
                id="eventType"
                placeholder="GRILLE_FIGEE, TENANT_CREE…"
                value={criteria.eventType ?? ""}
                onChange={(e) => set("eventType", e.target.value)}
              />
            </div>
            <div className="space-y-1">
              <Label htmlFor="userId">Utilisateur (UUID)</Label>
              <Input
                id="userId"
                value={criteria.userId ?? ""}
                onChange={(e) => set("userId", e.target.value)}
              />
            </div>
            <div className="space-y-1">
              <Label htmlFor="from">Depuis</Label>
              <Input
                id="from"
                type="datetime-local"
                value={criteria.from ?? ""}
                onChange={(e) => set("from", e.target.value)}
              />
            </div>
            <div className="space-y-1">
              <Label htmlFor="to">Jusqu&apos;à</Label>
              <Input
                id="to"
                type="datetime-local"
                value={criteria.to ?? ""}
                onChange={(e) => set("to", e.target.value)}
              />
            </div>
          </div>
          <Button
            onClick={() => {
              setApplied(criteria);
              audit.reload();
            }}
          >
            Rechercher
          </Button>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Résultats</CardTitle>
        </CardHeader>
        <CardContent>
          {audit.loading ? (
            <LoadingView label="Recherche…" />
          ) : audit.error ? (
            <ErrorState error={audit.error} onRetry={audit.reload} />
          ) : audit.data?.length ? (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                    <th className="px-3 py-2">Date</th>
                    <th className="px-3 py-2">Service</th>
                    <th className="px-3 py-2">Événement</th>
                    <th className="px-3 py-2">Action</th>
                    <th className="px-3 py-2">Utilisateur</th>
                    <th className="px-3 py-2">IP</th>
                  </tr>
                </thead>
                <tbody>
                  {audit.data.map((entry) => (
                    <tr key={entry.id} className="border-b border-border">
                      <td className="px-3 py-2 whitespace-nowrap text-slate-500">
                        {new Date(entry.timestamp).toLocaleString("fr-FR")}
                      </td>
                      <td className="px-3 py-2 font-mono text-xs">{entry.serviceName}</td>
                      <td className="px-3 py-2">
                        <span className="font-medium text-slate-900">{entry.eventType}</span>
                        {entry.entityType && (
                          <p className="text-xs text-slate-500">
                            {entry.entityType} {entry.entityId ? `/${entry.entityId.slice(0, 8)}` : ""}
                          </p>
                        )}
                      </td>
                      <td className="px-3 py-2">{entry.action || "—"}</td>
                      <td className="px-3 py-2 font-mono text-xs">{entry.userId?.slice(0, 8) || "—"}</td>
                      <td className="px-3 py-2 font-mono text-xs">{entry.ipAddress || "—"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <EmptyState label="Aucune trace" />
          )}
        </CardContent>
      </Card>
    </div>
  );
}