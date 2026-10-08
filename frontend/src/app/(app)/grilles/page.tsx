"use client";

import { useState } from "react";
import Link from "next/link";
import { toast } from "sonner";
import { Copy, FileText, Lock, Play, Plus } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import {
  PageHeader,
  LoadingView,
  ErrorState,
  EmptyState,
} from "@/components/common/async-state";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { grilleApi, tenantApi } from "@/services/api";
import type { Grille, GrilleType } from "@/types";

const typeLabels: Record<GrilleType, string> = {
  notation: "Notation",
  stock: "Stock",
  conformite: "Conformité",
};

export default function GrillesPage() {
  const { tenantId } = useTenant();
  const { isAdmin } = useAuth();
  const tenant = useApi(() => tenantApi.get(tenantId ?? ""), [tenantId]);

  const notation = useApi(() => grilleApi.list("notation"), [tenantId]);
  const stock = useApi(() => grilleApi.list("stock"), [tenantId]);
  const conformite = useApi(() => grilleApi.list("conformite"), [tenantId]);

  const [creating, setCreating] = useState<GrilleType | null>(null);
  const [busy, setBusy] = useState(false);
  const [newGrille, setNewGrille] = useState({ code: "", libelle: "" });

  const allLoading = notation.loading || stock.loading || conformite.loading;
  const allError = notation.error || stock.error || conformite.error;

  if (allLoading) return <LoadingView label="Chargement des grilles…" />;
  if (allError)
    return <ErrorState error={allError} onRetry={() => { notation.reload(); stock.reload(); conformite.reload(); }} />;

  const collections: Record<GrilleType, Grille[]> = {
    notation: notation.data ?? [],
    stock: stock.data ?? [],
    conformite: conformite.data ?? [],
  };

  const reloadAll = () => {
    notation.reload();
    stock.reload();
    conformite.reload();
  };

  const run = async (action: () => Promise<unknown>, success: string) => {
    setBusy(true);
    try {
      await action();
      toast.success(success);
      reloadAll();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  return (
    <div>
      <PageHeader
        title="Grilles"
        description="Versionnement activer / figer. Toute correction crée une nouvelle version."
      />

      {tenant.data && (
        <div className="mb-6 flex items-center gap-2 text-sm text-slate-500">
          Profil actif :
          <Badge variant="secondary">{tenant.data.profil}</Badge>
        </div>
      )}

      {Object.entries(collections).map(([type, grilles]) => (
        <Card key={type} className="mb-6">
          <CardHeader className="flex flex-row items-center justify-between space-y-0">
            <CardTitle className="flex items-center gap-2 text-base">
              <FileText className="h-4 w-4 text-blue-600" />
              Grilles de {typeLabels[type as GrilleType]}
            </CardTitle>
            {isAdmin && (
              <Button
                variant="outline"
                size="sm"
                onClick={() => {
                  setCreating(type as GrilleType);
                  setNewGrille({ code: "", libelle: "" });
                }}
              >
                <Plus className="mr-2 h-4 w-4" />
                Nouvelle
              </Button>
            )}
          </CardHeader>
          <CardContent className="space-y-4">
            {creating === type && isAdmin && (
              <form
                className="grid gap-2 rounded-lg border border-border bg-slate-50 p-3 sm:grid-cols-[1fr_1fr_auto]"
                onSubmit={(e) => {
                  e.preventDefault();
                  run(
                    () =>
                      grilleApi.create(type as GrilleType, {
                        code: newGrille.code,
                        libelle: newGrille.libelle,
                        profil: tenant.data?.profil ?? "PRIMAIRE",
                        version: 1,
                      }),
                    "Grille créée"
                  ).then(() => setCreating(null));
                }}
              >
                <Input
                  placeholder="Code"
                  value={newGrille.code}
                  onChange={(e) => setNewGrille({ ...newGrille, code: e.target.value })}
                />
                <Input
                  placeholder="Libellé"
                  value={newGrille.libelle}
                  onChange={(e) => setNewGrille({ ...newGrille, libelle: e.target.value })}
                />
                <Button type="submit" disabled={busy || !newGrille.code || !newGrille.libelle}>
                  Créer
                </Button>
              </form>
            )}

            {grilles.length ? (
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                      <th className="px-3 py-2">Code</th>
                      <th className="px-3 py-2">Libellé</th>
                      <th className="px-3 py-2">Version</th>
                      <th className="px-3 py-2">Profil</th>
                      <th className="px-3 py-2">Statut</th>
                      <th className="px-3 py-2 text-right">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {grilles.map((grille) => (
                      <tr key={grille.id} className="border-b border-border">
                        <td className="px-3 py-2 font-mono">{grille.code}</td>
                        <td className="px-3 py-2">
                          <Link
                            href={`/grilles/${type}?id=${grille.id}`}
                            className="font-medium text-slate-900 hover:text-blue-600"
                          >
                            {grille.libelle}
                          </Link>
                        </td>
                        <td className="px-3 py-2">v{grille.version}</td>
                        <td className="px-3 py-2">
                          <Badge variant="secondary">{grille.profil}</Badge>
                        </td>
                        <td className="px-3 py-2">
                          {grille.active ? (
                            <Badge variant="default">Active</Badge>
                          ) : grille.modifiable ? (
                            <Badge variant="outline">En préparation</Badge>
                          ) : (
                            <Badge variant="outline">Figée</Badge>
                          )}
                        </td>
                        <td className="px-3 py-2">
                          <div className="flex justify-end gap-1">
                            {isAdmin && !grille.active && grille.modifiable && (
                              <Button
                                variant="outline"
                                size="sm"
                                title="Activer"
                                disabled={busy}
                                onClick={() =>
                                  run(() => grilleApi.activer(type as GrilleType, grille.id!), "Grille activée")
                                }
                              >
                                <Play className="h-3.5 w-3.5" />
                              </Button>
                            )}
                            {isAdmin && grille.modifiable && (
                              <Button
                                variant="outline"
                                size="sm"
                                title="Figer"
                                disabled={busy}
                                onClick={() =>
                                  run(() => grilleApi.figer(type as GrilleType, grille.id!), "Grille figée")
                                }
                              >
                                <Lock className="h-3.5 w-3.5" />
                              </Button>
                            )}
                            {isAdmin && (
                              <Button
                                variant="outline"
                                size="sm"
                                title="Dupliquer"
                                disabled={busy}
                                onClick={() =>
                                  run(
                                    () => grilleApi.dupliquer(type as GrilleType, grille.id!),
                                    "Grille dupliquée"
                                  )
                                }
                              >
                                <Copy className="h-3.5 w-3.5" />
                              </Button>
                            )}
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <EmptyState label={`Aucune grille de ${typeLabels[type as GrilleType]}`} />
            )}
          </CardContent>
        </Card>
      ))}
    </div>
  );
}