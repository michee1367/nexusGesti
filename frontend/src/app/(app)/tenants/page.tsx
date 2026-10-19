"use client";

import { useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select } from "@/components/ui/select";
import { PageHeader } from "@/components/common/async-state";
import { tenantApi } from "@/services/api";
import { useTenant } from "@/components/providers/tenant-provider";
import type { Etablissement } from "@/types";

export default function TenantsPage() {
  const { setTenantId } = useTenant();
  const [denomination, setDenomination] = useState("");
  const [code, setCode] = useState("");
  const [profil, setProfil] = useState<"PRIMAIRE" | "SECONDAIRE" | "UNIVERSITAIRE" | "BOULANGERIE">("PRIMAIRE");
  const [created, setCreated] = useState<Etablissement | null>(null);
  const [busy, setBusy] = useState(false);

  const [consultId, setConsultId] = useState("");
  const [consulted, setConsulted] = useState<Etablissement | null>(null);
  const [newStatut, setNewStatut] = useState("");

  const create = async () => {
    setBusy(true);
    try {
      const id = crypto.randomUUID();
      setTenantId(id);
      const etablissement = await tenantApi.create({
        code,
        denomination,
        profil,
        statut: "PREPARATION",
      });
      setCreated(etablissement);
      toast.success("Tenant créé — le tenant courant a été basculé sur lui (D8)");
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  const consult = async () => {
    setBusy(true);
    try {
      const etablissement = await tenantApi.get(consultId);
      setConsulted(etablissement);
      setNewStatut(etablissement.statut);
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  const changeStatut = async () => {
    if (!consulted || !newStatut) return;
    setBusy(true);
    try {
      const updated = await tenantApi.changeStatut(consulted.id, newStatut as never);
      setConsulted(updated);
      toast.success("Statut mis à jour");
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  return (
    <div>
      <PageHeader
        title="Tenants"
        description="Création d'établissement et contrôle d'identité (ADMIN)."
      />

      <div className="grid gap-6 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="text-base">Nouveau tenant</CardTitle>
            <CardDescription>
              Le POST porte X-Tenant-Id = UUID du nouveau tenant (D8), jamais le corps. Après
              création, la console bascule sur ce tenant.
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="denomination">Dénomination</Label>
              <Input
                id="denomination"
                placeholder="École Secondaire Kimbanguiste"
                value={denomination}
                onChange={(e) => setDenomination(e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="code">Code</Label>
              <Input
                id="code"
                placeholder="ESC-KIM-01"
                maxLength={30}
                value={code}
                onChange={(e) => setCode(e.target.value.toUpperCase())}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="profil">Profil</Label>
              <Select id="profil" value={profil} onChange={(e) => setProfil(e.target.value as never)}>
                <option value="PRIMAIRE">Primaire</option>
                <option value="SECONDAIRE">Secondaire</option>
                <option value="UNIVERSITAIRE">Universitaire</option>
                <option value="BOULANGERIE">Boulangerie</option>
              </Select>
            </div>
            <Button
              onClick={create}
              disabled={busy || !denomination || !code}
            >
              Créer le tenant
            </Button>

            {created && (
              <div className="rounded-lg border border-success/30 bg-success/5 p-3 text-sm">
                <p className="font-semibold text-slate-900">
                  {created.denomination} ({created.code})
                </p>
                <p className="mt-1 break-all font-mono text-xs text-slate-500">{created.id}</p>
                <p className="text-xs text-slate-500">Profil : {created.profil}</p>
              </div>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-base">Consulter un tenant</CardTitle>
            <CardDescription>Lecture et changement de statut par id.</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex gap-2">
              <Input
                placeholder="UUID du tenant"
                value={consultId}
                onChange={(e) => setConsultId(e.target.value)}
              />
              <Button onClick={consult} disabled={busy || !consultId}>
                Consulter
              </Button>
            </div>

            {consulted && (
              <div className="space-y-4">
                <dl className="grid grid-cols-2 gap-x-4 gap-y-2 rounded-lg border border-border p-3 text-sm">
                  <div>
                    <dt className="text-xs text-slate-500">Dénomination</dt>
                    <dd className="font-medium text-slate-900">{consulted.denomination}</dd>
                  </div>
                  <div>
                    <dt className="text-xs text-slate-500">Code</dt>
                    <dd className="font-mono">{consulted.code}</dd>
                  </div>
                  <div>
                    <dt className="text-xs text-slate-500">Profil</dt>
                    <dd>{consulted.profil}</dd>
                  </div>
                  <div>
                    <dt className="text-xs text-slate-500">Statut</dt>
                    <dd>
                      <Badge variant="secondary">{consulted.statut}</Badge>
                    </dd>
                  </div>
                </dl>
                <div className="flex gap-2">
                  <Select value={newStatut} onChange={(e) => setNewStatut(e.target.value)}>
                    <option value="PREPARATION">Préparation</option>
                    <option value="ACTIF">Actif</option>
                    <option value="SUSPENDU">Suspendu</option>
                    <option value="ARCHIVE">Archivé</option>
                  </Select>
                  <Button
                    variant="outline"
                    onClick={changeStatut}
                    disabled={busy || !newStatut || newStatut === consulted.statut}
                  >
                    Appliquer
                  </Button>
                </div>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}