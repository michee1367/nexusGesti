"use client";

import { useState } from "react";
import { toast } from "sonner";
import { ChevronDown, ChevronRight, Search } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
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
import { referentielApi } from "@/services/api";

export default function ReferentielsPage() {
  const { tenantId } = useTenant();
  const { isAdmin } = useAuth();
  const referentiels = useApi(referentielApi.list, [tenantId]);
  const [expanded, setExpanded] = useState<string | null>(null);
  const [code, setCode] = useState("");
  const [busy, setBusy] = useState(false);
  const [newItem, setNewItem] = useState({ code: "", libelle: "", valeur: "" });

  const byCode = useApi(
    () => (code ? referentielApi.getByCode(code) : Promise.reject(new Error("no lookup"))),
    [code, referentiels.data]
  );

  if (referentiels.loading) return <LoadingView label="Chargement des référentiels…" />;
  if (referentiels.error)
    return <ErrorState error={referentiels.error} onRetry={referentiels.reload} />;

  const displayed = code && byCode.data ? [byCode.data] : referentiels.data ?? [];

  const addItem = async (referentielId: string) => {
    setBusy(true);
    try {
      await referentielApi.addItem(referentielId, {
        code: newItem.code,
        libelle: newItem.libelle,
        valeur: newItem.valeur || null,
      });
      toast.success("Élément ajouté");
      referentiels.reload();
      setNewItem({ code: "", libelle: "", valeur: "" });
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  const desactiver = async (referentielId: string, itemId: string) => {
    setBusy(true);
    try {
      await referentielApi.desactiverItem(referentielId, itemId);
      toast.success("Élément désactivé");
      referentiels.reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  return (
    <div>
      <PageHeader
        title="Référentiels"
        description="Conteneurs scopés tenant, créés à la création du tenant."
      />

      <div className="relative mb-4 max-w-sm">
        <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
        <Input
          placeholder="Recherche par code (ex. BAREME), vide = tout afficher"
          className="pl-9"
          value={code}
          onChange={(e) => setCode(e.target.value.toUpperCase())}
        />
      </div>

      {displayed.length ? (
        <div className="space-y-3">
          {displayed.map((referentiel) => {
            const open = expanded === referentiel.idReferentiel;
            return (
              <Card key={referentiel.idReferentiel}>
                <CardContent
                  className="flex cursor-pointer items-center justify-between gap-3 p-4"
                  onClick={() => setExpanded(open ? null : referentiel.idReferentiel)}
                >
                  <div className="flex items-center gap-3">
                    {open ? (
                      <ChevronDown className="h-4 w-4 text-slate-400" />
                    ) : (
                      <ChevronRight className="h-4 w-4 text-slate-400" />
                    )}
                    <div>
                      <p className="font-semibold text-slate-900">{referentiel.libelle}</p>
                      <p className="font-mono text-xs text-slate-500">
                        {referentiel.code} · {referentiel.nature}
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    {!referentiel.actif && <Badge variant="outline">Inactif</Badge>}
                    <Badge variant="secondary">{referentiel.items.length} élément(s)</Badge>
                  </div>
                </CardContent>

                {open && (
                  <div className="border-t border-border p-4">
                    {referentiel.items.length ? (
                      <div className="overflow-x-auto">
                        <table className="w-full text-sm">
                          <thead>
                            <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                              <th className="px-3 py-2">Code</th>
                              <th className="px-3 py-2">Libellé</th>
                              <th className="px-3 py-2">Valeur</th>
                              <th className="px-3 py-2">Ordre</th>
                              <th className="px-3 py-2">Statut</th>
                              {isAdmin && <th className="px-3 py-2 text-right">Action</th>}
                            </tr>
                          </thead>
                          <tbody>
                            {referentiel.items.map((item) => (
                              <tr key={item.idItem} className="border-b border-border">
                                <td className="px-3 py-2 font-mono">{item.code}</td>
                                <td className="px-3 py-2 font-medium text-slate-900">
                                  {item.libelle}
                                </td>
                                <td className="px-3 py-2">{item.valeur || "—"}</td>
                                <td className="px-3 py-2">{item.ordre}</td>
                                <td className="px-3 py-2">
                                  {item.actif ? (
                                    <Badge variant="secondary">Actif</Badge>
                                  ) : (
                                    <Badge variant="outline">Désactivé</Badge>
                                  )}
                                </td>
                                {isAdmin && (
                                  <td className="px-3 py-2 text-right">
                                    {item.actif && (
                                      <Button
                                        variant="ghost"
                                        size="sm"
                                        disabled={busy}
                                        onClick={() =>
                                          desactiver(referentiel.idReferentiel, item.idItem)
                                        }
                                      >
                                        Désactiver
                                      </Button>
                                    )}
                                  </td>
                                )}
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    ) : (
                      <EmptyState label="Aucun élément" />
                    )}

                    {isAdmin && (
                      <form
                        className="mt-4 grid gap-2 border-t border-border pt-4 sm:grid-cols-[1fr_1fr_1fr_auto]"
                        onSubmit={(e) => {
                          e.preventDefault();
                          addItem(referentiel.idReferentiel);
                        }}
                      >
                        <Input
                          placeholder="Code"
                          value={newItem.code}
                          onChange={(e) => setNewItem({ ...newItem, code: e.target.value })}
                        />
                        <Input
                          placeholder="Libellé"
                          value={newItem.libelle}
                          onChange={(e) => setNewItem({ ...newItem, libelle: e.target.value })}
                        />
                        <Input
                          placeholder="Valeur (optionnel)"
                          value={newItem.valeur}
                          onChange={(e) => setNewItem({ ...newItem, valeur: e.target.value })}
                        />
                        <Button
                          type="submit"
                          disabled={busy || !newItem.code || !newItem.libelle}
                        >
                          Ajouter
                        </Button>
                      </form>
                    )}
                  </div>
                )}
              </Card>
            );
          })}
        </div>
      ) : (
        <EmptyState label="Aucun référentiel" />
      )}
    </div>
  );
}