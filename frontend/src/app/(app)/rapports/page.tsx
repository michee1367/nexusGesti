"use client";

import { useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select } from "@/components/ui/select";
import {
  PageHeader,
  LoadingView,
  ErrorState,
  EmptyState,
} from "@/components/common/async-state";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { reportApi } from "@/services/api";
import type { ReportFormat, ReportType } from "@/types";

const statusStyles: Record<string, string> = {
  READY: "bg-success/10 text-success border-success/30",
  PENDING: "bg-slate-100 text-slate-600 border-slate-300",
  GENERATING: "bg-warning/10 text-warning border-warning/30",
  FAILED: "bg-danger/10 text-danger border-danger/30",
  EXPIRED: "bg-slate-100 text-slate-500 border-slate-300",
};

export default function RapportsPage() {
  const { tenantId } = useTenant();
  const { isAdmin } = useAuth();
  const reports = useApi(reportApi.list, [tenantId]);
  const [form, setForm] = useState<{
    reportType: ReportType;
    format: ReportFormat;
    parameters: string;
  }>({ reportType: "ACCOUNT_STATEMENT", format: "PDF", parameters: "{}" });
  const [busy, setBusy] = useState(false);

  if (!isAdmin) {
    return (
      <div>
        <PageHeader title="Rapports" />
        <p className="text-sm text-slate-500">Page réservée aux ADMIN.</p>
      </div>
    );
  }

  const request = async () => {
    setBusy(true);
    try {
      const parameters = JSON.parse(form.parameters || "{}") as Record<string, string>;
      await reportApi.request({ reportType: form.reportType, format: form.format, parameters });
      toast.success("Rapport demandé");
      reports.reload();
    } catch (error) {
      if (error instanceof SyntaxError) {
        toast.error("Les paramètres doivent être du JSON valide (ex. { \"cle\": \"valeur\" })");
      } else {
        toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
      }
    } finally {
      setBusy(false);
    }
  };

  const download = async (id: string) => {
    try {
      const { downloadUrl } = await reportApi.download(id);
      window.open(downloadUrl, "_blank");
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Téléchargement impossible");
    }
  };

  return (
    <div>
      <PageHeader title="Rapports" description="Génération asynchrone et téléchargement (ADMIN)." />

      <Card className="mb-6">
        <CardHeader>
          <CardTitle className="text-base">Nouveau rapport</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-2 sm:grid-cols-3">
            <div className="space-y-1">
              <Label htmlFor="reportType">Type</Label>
              <Select
                id="reportType"
                value={form.reportType}
                onChange={(e) => setForm({ ...form, reportType: e.target.value as ReportType })}
              >
                <option value="ACCOUNT_STATEMENT">Relevé de compte</option>
                <option value="TRANSACTION_HISTORY">Historique de transactions</option>
                <option value="KYC_SUMMARY">Synthèse KYC (legacy)</option>
              </Select>
            </div>
            <div className="space-y-1">
              <Label htmlFor="format">Format</Label>
              <Select
                id="format"
                value={form.format}
                onChange={(e) => setForm({ ...form, format: e.target.value as ReportFormat })}
              >
                <option value="PDF">PDF</option>
                <option value="CSV">CSV</option>
                <option value="JSON">JSON</option>
              </Select>
            </div>
            <div className="space-y-1">
              <Label htmlFor="parameters">Paramètres (JSON)</Label>
              <Input
                id="parameters"
                value={form.parameters}
                onChange={(e) => setForm({ ...form, parameters: e.target.value })}
              />
            </div>
          </div>
          <Button onClick={request} disabled={busy}>
            Demander le rapport
          </Button>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Historique</CardTitle>
        </CardHeader>
        <CardContent>
          {reports.loading ? (
            <LoadingView label="Chargement…" />
          ) : reports.error ? (
            <ErrorState error={reports.error} onRetry={reports.reload} />
          ) : reports.data?.length ? (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                    <th className="px-3 py-2">Type</th>
                    <th className="px-3 py-2">Format</th>
                    <th className="px-3 py-2">Statut</th>
                    <th className="px-3 py-2">Demandé le</th>
                    <th className="px-3 py-2 text-right">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {reports.data.map((report) => (
                    <tr key={report.id} className="border-b border-border">
                      <td className="px-3 py-2 font-medium text-slate-900">{report.reportType}</td>
                      <td className="px-3 py-2">
                        <Badge variant="secondary">{report.format}</Badge>
                      </td>
                      <td className="px-3 py-2">
                        <Badge className={statusStyles[report.status] ?? ""}>{report.status}</Badge>
                      </td>
                      <td className="px-3 py-2 text-slate-500">
                        {new Date(report.createdAt).toLocaleString("fr-FR")}
                      </td>
                      <td className="px-3 py-2 text-right">
                        {report.status === "READY" && (
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => download(report.id)}
                          >
                            Télécharger
                          </Button>
                        )}
                        {report.status === "FAILED" && (
                          <span className="text-xs text-slate-500">{report.failedReason || "—"}</span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <EmptyState label="Aucun rapport" />
          )}
        </CardContent>
      </Card>
    </div>
  );
}