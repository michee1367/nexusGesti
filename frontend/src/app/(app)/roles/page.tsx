"use client";

import { useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Checkbox } from "@/components/ui/checkbox";
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
import { identiteApi } from "@/services/api";
import type { CategorieRole, Role } from "@/types";

const CATEGORIES: CategorieRole[] = [
  "ADMINISTRATION",
  "PEDAGOGIQUE",
  "SCOLARITE",
  "SANTE",
  "FINANCE",
  "SECURITE",
  "SYSTEME",
  "CONSULTATION",
];

export default function RolesPage() {
  const { tenantId } = useTenant();
  const { isAdmin } = useAuth();

  const roles = useApi(() => identiteApi.roles.list(), [tenantId]);
  const permissions = useApi(() => identiteApi.permissions.list(), [tenantId]);

  const [busy, setBusy] = useState(false);
  const [selectedRole, setSelectedRole] = useState<Role | null>(null);
  const [selectedPermIds, setSelectedPermIds] = useState<string[]>([]);

  const [roleForm, setRoleForm] = useState({ code: "", libelle: "", categorie: "" as CategorieRole | "" });
  const [permissionForm, setPermissionForm] = useState({
    code: "",
    libelle: "",
    service: "",
    ressource: "",
    action: "",
  });

  const run = async (action: () => Promise<void>, success: string) => {
    setBusy(true);
    try {
      await action();
      toast.success(success);
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  const selectRole = (role: Role) => {
    setSelectedRole(role);
    setSelectedPermIds((role.permissions ?? []).map((p) => p.idPermission));
  };

  const togglePermission = (id: string, checked: boolean) =>
    setSelectedPermIds((current) => (checked ? [...current, id] : current.filter((p) => p !== id)));

  const creerRole = () =>
    run(async () => {
      const created = await identiteApi.roles.create({
        code: roleForm.code,
        libelle: roleForm.libelle || undefined,
        categorie: roleForm.categorie || undefined,
        systeme: false,
      });
      setRoleForm({ code: "", libelle: "", categorie: "" });
      roles.reload();
      selectRole(created);
    }, "Rôle créé");

  const enregistrerPermissions = () => {
    if (!selectedRole) return;
    run(async () => {
      const updated = await identiteApi.roles.definirPermissions(selectedRole.code, selectedPermIds);
      roles.reload();
      selectRole(updated);
    }, "Permissions enregistrées");
  };

  const creerPermission = () =>
    run(async () => {
      await identiteApi.permissions.create({
        code: permissionForm.code,
        libelle: permissionForm.libelle || undefined,
        service: permissionForm.service || undefined,
        ressource: permissionForm.ressource || undefined,
        action: permissionForm.action || undefined,
      });
      setPermissionForm({ code: "", libelle: "", service: "", ressource: "", action: "" });
      permissions.reload();
    }, "Permission créée");

  return (
    <div>
      <PageHeader
        title="Rôles & permissions"
        description="Rôles de realm Keycloak et permissions locales (ADMIN)."
      />

      {!isAdmin && (
        <p className="mb-4 rounded-lg border border-amber-200 bg-amber-50 p-3 text-sm text-amber-800">
          Lecture seule : les modifications sont réservées aux ADMIN.
        </p>
      )}

      <div className="grid gap-6 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="text-base">Nouveau rôle</CardTitle>
            <CardDescription>Crée un rôle de realm dans Keycloak.</CardDescription>
          </CardHeader>
          <CardContent className="space-y-3">
            <div className="space-y-1.5">
              <Label>Code</Label>
              <Input
                placeholder="ENCADRANT"
                value={roleForm.code}
                onChange={(e) => setRoleForm({ ...roleForm, code: e.target.value.toUpperCase() })}
              />
            </div>
            <div className="space-y-1.5">
              <Label>Libellé</Label>
              <Input
                value={roleForm.libelle}
                onChange={(e) => setRoleForm({ ...roleForm, libelle: e.target.value })}
              />
            </div>
            <div className="space-y-1.5">
              <Label>Catégorie</Label>
              <Select
                value={roleForm.categorie}
                onChange={(e) => setRoleForm({ ...roleForm, categorie: e.target.value as CategorieRole })}
              >
                <option value="">—</option>
                {CATEGORIES.map((categorie) => (
                  <option key={categorie} value={categorie}>
                    {categorie}
                  </option>
                ))}
              </Select>
            </div>
            <Button onClick={creerRole} disabled={!isAdmin || busy || !roleForm.code}>
              Créer le rôle
            </Button>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-base">Nouvelle permission</CardTitle>
            <CardDescription>Permission locale (service / ressource / action).</CardDescription>
          </CardHeader>
          <CardContent className="space-y-3">
            <div className="space-y-1.5">
              <Label>Code</Label>
              <Input
                placeholder="socle.utilisateurs.lire"
                value={permissionForm.code}
                onChange={(e) => setPermissionForm({ ...permissionForm, code: e.target.value })}
              />
            </div>
            <div className="space-y-1.5">
              <Label>Libellé</Label>
              <Input
                value={permissionForm.libelle}
                onChange={(e) => setPermissionForm({ ...permissionForm, libelle: e.target.value })}
              />
            </div>
            <div className="grid grid-cols-3 gap-3">
              <div className="space-y-1.5">
                <Label>Service</Label>
                <Input
                  value={permissionForm.service}
                  onChange={(e) => setPermissionForm({ ...permissionForm, service: e.target.value })}
                />
              </div>
              <div className="space-y-1.5">
                <Label>Ressource</Label>
                <Input
                  value={permissionForm.ressource}
                  onChange={(e) => setPermissionForm({ ...permissionForm, ressource: e.target.value })}
                />
              </div>
              <div className="space-y-1.5">
                <Label>Action</Label>
                <Input
                  placeholder="LIRE"
                  value={permissionForm.action}
                  onChange={(e) => setPermissionForm({ ...permissionForm, action: e.target.value })}
                />
              </div>
            </div>
            <Button onClick={creerPermission} disabled={!isAdmin || busy || !permissionForm.code}>
              Créer la permission
            </Button>
          </CardContent>
        </Card>
      </div>

      <Card className="mt-6">
        <CardHeader>
          <CardTitle className="text-base">Rôles</CardTitle>
          <CardDescription>{roles.data?.length ?? 0} rôle(s)</CardDescription>
        </CardHeader>
        <CardContent>
          {roles.loading ? (
            <LoadingView />
          ) : roles.error ? (
            <ErrorState error={roles.error} onRetry={roles.reload} />
          ) : roles.data?.length ? (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                    <th className="px-3 py-2">Code</th>
                    <th className="px-3 py-2">Libellé</th>
                    <th className="px-3 py-2">Catégorie</th>
                    <th className="px-3 py-2">Système</th>
                    <th className="px-3 py-2">Permissions</th>
                  </tr>
                </thead>
                <tbody>
                  {roles.data.map((role) => (
                    <tr
                      key={role.code}
                      onClick={() => selectRole(role)}
                      className={`cursor-pointer border-b border-border hover:bg-slate-50 ${
                        selectedRole?.code === role.code ? "bg-blue-50/60" : ""
                      }`}
                    >
                      <td className="px-3 py-2 font-mono text-xs font-medium text-slate-900">
                        {role.code}
                      </td>
                      <td className="px-3 py-2">{role.libelle || "—"}</td>
                      <td className="px-3 py-2">{role.categorie || "—"}</td>
                      <td className="px-3 py-2">
                        {role.systeme ? (
                          <Badge variant="secondary">système</Badge>
                        ) : (
                          <span className="text-slate-400">—</span>
                        )}
                      </td>
                      <td className="px-3 py-2">{(role.permissions ?? []).length}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <EmptyState label="Aucun rôle" />
          )}
        </CardContent>
      </Card>

      <Card className="mt-6">
        <CardHeader>
          <CardTitle className="text-base">Permissions du rôle</CardTitle>
          <CardDescription>
            {selectedRole ? `Rôle : ${selectedRole.code}` : "Sélectionne un rôle dans la liste."}
          </CardDescription>
        </CardHeader>
        <CardContent>
          {!selectedRole ? (
            <EmptyState label="Aucune sélection" />
          ) : permissions.loading ? (
            <LoadingView />
          ) : permissions.error ? (
            <ErrorState error={permissions.error} onRetry={permissions.reload} />
          ) : permissions.data?.length ? (
            <div className="space-y-4">
              <div className="grid gap-2 sm:grid-cols-2 lg:grid-cols-3">
                {permissions.data.map((permission) => (
                  <label
                    key={permission.idPermission}
                    className="flex items-start gap-2 rounded-lg border border-border p-2 text-sm"
                  >
                    <Checkbox
                      checked={selectedPermIds.includes(permission.idPermission)}
                      onChange={(checked) => togglePermission(permission.idPermission, checked)}
                      className="mt-0.5"
                    />
                    <span>
                      <span className="block font-mono text-xs text-slate-900">{permission.code}</span>
                      <span className="block text-xs text-slate-500">
                        {[permission.service, permission.ressource, permission.action]
                          .filter(Boolean)
                          .join(" · ") || permission.libelle}
                      </span>
                    </span>
                  </label>
                ))}
              </div>
              <Button onClick={enregistrerPermissions} disabled={!isAdmin || busy}>
                Enregistrer les permissions
              </Button>
            </div>
          ) : (
            <EmptyState label="Aucune permission définie" />
          )}
        </CardContent>
      </Card>
    </div>
  );
}
