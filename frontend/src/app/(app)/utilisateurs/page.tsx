"use client";

import { useState } from "react";
import { toast } from "sonner";
import { Badge, type BadgeProps } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Checkbox } from "@/components/ui/checkbox";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select } from "@/components/ui/select";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  PageHeader,
  LoadingView,
  ErrorState,
  EmptyState,
} from "@/components/common/async-state";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { identiteApi, utilisateurApi } from "@/services/api";
import type { StatutUtilisateur, TypeCompte, Utilisateur } from "@/types";

const statutVariant: Record<StatutUtilisateur, BadgeProps["variant"]> = {
  ACTIF: "success",
  INVITE: "warning",
  SUSPENDU: "warning",
  DESACTIF: "secondary",
  SUPPRIME: "danger",
};

const emptyCreate = {
  email: "",
  nom: "",
  prenom: "",
  telephone: "",
  sexe: "",
  dateNaissance: "",
  nationalite: "",
  typeCompte: "KEYCLOAK" as TypeCompte,
  motDePasse: "",
};

export default function UtilisateursPage() {
  const { tenantId } = useTenant();
  const { isAdmin } = useAuth();

  const [query, setQuery] = useState("");
  const [applied, setApplied] = useState("");
  const liste = useApi(() => utilisateurApi.list(applied), [applied, tenantId]);
  const roles = useApi(() => identiteApi.roles.list(), [tenantId]);

  const [selected, setSelected] = useState<Utilisateur | null>(null);
  const [busy, setBusy] = useState(false);

  const [createForm, setCreateForm] = useState(emptyCreate);
  const [inviteForm, setInviteForm] = useState({ email: "", nom: "", prenom: "" });
  const [createRoles, setCreateRoles] = useState<string[]>([]);

  const [editForm, setEditForm] = useState(emptyEdit());
  const [motif, setMotif] = useState("");
  const [resetPassword, setResetPassword] = useState("");
  const [roleToAdd, setRoleToAdd] = useState("");

  function emptyEdit() {
    return {
      nom: "",
      prenom: "",
      postNom: "",
      telephone: "",
      sexe: "",
      dateNaissance: "",
      nationalite: "",
      languePreferee: "",
      photoUrl: "",
    };
  }

  function select(user: Utilisateur) {
    setSelected(user);
    setEditForm({
      nom: user.nom ?? "",
      prenom: user.prenom ?? "",
      postNom: user.postNom ?? "",
      telephone: user.telephone ?? "",
      sexe: user.sexe ?? "",
      dateNaissance: user.dateNaissance ?? "",
      nationalite: user.nationalite ?? "",
      languePreferee: user.languePreferee ?? "",
      photoUrl: user.photoUrl ?? "",
    });
    setMotif("");
    setResetPassword("");
    setRoleToAdd("");
  }

  const apply = (updated: Utilisateur) => {
    setSelected(updated);
    liste.reload();
  };

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

  const toggleCreateRole = (code: string, checked: boolean) =>
    setCreateRoles((current) => (checked ? [...current, code] : current.filter((c) => c !== code)));

  const create = () =>
    run(async () => {
      const created = await utilisateurApi.create({
        email: createForm.email,
        nom: createForm.nom || undefined,
        prenom: createForm.prenom || undefined,
        telephone: createForm.telephone || undefined,
        sexe: createForm.sexe || undefined,
        dateNaissance: createForm.dateNaissance || undefined,
        nationalite: createForm.nationalite || undefined,
        typeCompte: createForm.typeCompte,
        motDePasse: createForm.motDePasse || undefined,
        roles: createRoles.length ? createRoles : undefined,
      });
      setCreateForm(emptyCreate);
      setCreateRoles([]);
      apply(created);
    }, "Utilisateur créé");

  const inviter = () =>
    run(async () => {
      const created = await utilisateurApi.inviter({
        email: inviteForm.email,
        nom: inviteForm.nom || undefined,
        prenom: inviteForm.prenom || undefined,
        roles: createRoles.length ? createRoles : undefined,
      });
      setInviteForm({ email: "", nom: "", prenom: "" });
      setCreateRoles([]);
      apply(created);
    }, "Invitation envoyée");

  const enregistrer = () => {
    if (!selected) return;
    run(async () => {
      const updated = await utilisateurApi.modifier(selected.idUtilisateur, {
        nom: editForm.nom || undefined,
        prenom: editForm.prenom || undefined,
        postNom: editForm.postNom || undefined,
        telephone: editForm.telephone || undefined,
        sexe: editForm.sexe || undefined,
        dateNaissance: editForm.dateNaissance || undefined,
        nationalite: editForm.nationalite || undefined,
        languePreferee: editForm.languePreferee || undefined,
        photoUrl: editForm.photoUrl || undefined,
      });
      apply(updated);
    }, "Utilisateur mis à jour");
  };

  const ajouterRole = () => {
    if (!selected || !roleToAdd) return;
    run(async () => {
      const updated = await utilisateurApi.attribuerRole(selected.idUtilisateur, roleToAdd);
      setRoleToAdd("");
      apply(updated);
    }, "Rôle attribué");
  };

  const disponibleRoles = (roles.data ?? []).filter(
    (r) => !(selected?.roles ?? []).some((ur) => ur.code === r.code),
  );

  const canWrite = isAdmin;

  return (
    <div>
      <PageHeader
        title="Utilisateurs"
        description="Comptes, cycle de vie et rôles (ADMIN). Miroir Keycloak côté identity-service."
      />

      <Card className="mb-6">
        <CardHeader>
          <CardTitle className="text-base">Recherche</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex max-w-xl gap-2">
            <Input
              placeholder="Email, nom, prénom…"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") setApplied(query);
              }}
            />
            <Button variant="outline" onClick={() => setApplied(query)}>
              Rechercher
            </Button>
          </div>
        </CardContent>
      </Card>

      <Card className="mb-6">
        <CardHeader>
          <CardTitle className="text-base">Liste</CardTitle>
          <CardDescription>{liste.data?.length ?? 0} utilisateur(s)</CardDescription>
        </CardHeader>
        <CardContent>
          {liste.loading ? (
            <LoadingView />
          ) : liste.error ? (
            <ErrorState error={liste.error} onRetry={liste.reload} />
          ) : liste.data?.length ? (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                    <th className="px-3 py-2">Email</th>
                    <th className="px-3 py-2">Nom</th>
                    <th className="px-3 py-2">Statut</th>
                    <th className="px-3 py-2">Rôles</th>
                  </tr>
                </thead>
                <tbody>
                  {liste.data.map((user) => (
                    <tr
                      key={user.idUtilisateur}
                      onClick={() => select(user)}
                      className={`cursor-pointer border-b border-border hover:bg-slate-50 ${
                        selected?.idUtilisateur === user.idUtilisateur ? "bg-blue-50/60" : ""
                      }`}
                    >
                      <td className="px-3 py-2 font-medium text-slate-900">{user.email}</td>
                      <td className="px-3 py-2">
                        {[user.prenom, user.nom].filter(Boolean).join(" ") || "—"}
                      </td>
                      <td className="px-3 py-2">
                        <Badge variant={statutVariant[user.statut] ?? "secondary"}>{user.statut}</Badge>
                      </td>
                      <td className="px-3 py-2">
                        <span className="flex flex-wrap gap-1">
                          {(user.roles ?? []).length
                            ? (user.roles ?? []).map((role) => (
                                <Badge key={role.code} variant="outline">
                                  {role.code}
                                </Badge>
                              ))
                            : "—"}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <EmptyState label="Aucun utilisateur" />
          )}
        </CardContent>
      </Card>

      <div className="grid gap-6 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="text-base">Nouvel utilisateur</CardTitle>
            <CardDescription>Création directe ou invitation (miroir Keycloak).</CardDescription>
          </CardHeader>
          <CardContent>
            <Tabs defaultValue="creer">
              <TabsList>
                <TabsTrigger value="creer">Créer</TabsTrigger>
                <TabsTrigger value="inviter">Inviter</TabsTrigger>
              </TabsList>

              <TabsContent value="creer">
                <div className="space-y-3">
                  <Field label="Email">
                    <Input
                      type="email"
                      value={createForm.email}
                      onChange={(e) => setCreateForm({ ...createForm, email: e.target.value })}
                    />
                  </Field>
                  <div className="grid grid-cols-2 gap-3">
                    <Field label="Prénom">
                      <Input
                        value={createForm.prenom}
                        onChange={(e) => setCreateForm({ ...createForm, prenom: e.target.value })}
                      />
                    </Field>
                    <Field label="Nom">
                      <Input
                        value={createForm.nom}
                        onChange={(e) => setCreateForm({ ...createForm, nom: e.target.value })}
                      />
                    </Field>
                    <Field label="Téléphone">
                      <Input
                        value={createForm.telephone}
                        onChange={(e) => setCreateForm({ ...createForm, telephone: e.target.value })}
                      />
                    </Field>
                    <Field label="Sexe">
                      <Select
                        value={createForm.sexe}
                        onChange={(e) => setCreateForm({ ...createForm, sexe: e.target.value })}
                      >
                        <option value="">—</option>
                        <option value="M">M</option>
                        <option value="F">F</option>
                      </Select>
                    </Field>
                    <Field label="Date de naissance">
                      <Input
                        type="date"
                        value={createForm.dateNaissance}
                        onChange={(e) => setCreateForm({ ...createForm, dateNaissance: e.target.value })}
                      />
                    </Field>
                    <Field label="Nationalité">
                      <Input
                        value={createForm.nationalite}
                        onChange={(e) => setCreateForm({ ...createForm, nationalite: e.target.value })}
                      />
                    </Field>
                    <Field label="Type de compte">
                      <Select
                        value={createForm.typeCompte}
                        onChange={(e) =>
                          setCreateForm({ ...createForm, typeCompte: e.target.value as TypeCompte })
                        }
                      >
                        <option value="KEYCLOAK">Keycloak</option>
                        <option value="LOCAL">Local</option>
                      </Select>
                    </Field>
                    <Field label="Mot de passe">
                      <Input
                        type="password"
                        value={createForm.motDePasse}
                        onChange={(e) => setCreateForm({ ...createForm, motDePasse: e.target.value })}
                      />
                    </Field>
                  </div>
                  <RolesPicker
                    roles={roles.data ?? []}
                    selected={createRoles}
                    onToggle={toggleCreateRole}
                  />
                  <Button
                    onClick={create}
                    disabled={!canWrite || busy || !createForm.email}
                  >
                    Créer
                  </Button>
                </div>
              </TabsContent>

              <TabsContent value="inviter">
                <div className="space-y-3">
                  <Field label="Email">
                    <Input
                      type="email"
                      value={inviteForm.email}
                      onChange={(e) => setInviteForm({ ...inviteForm, email: e.target.value })}
                    />
                  </Field>
                  <div className="grid grid-cols-2 gap-3">
                    <Field label="Prénom">
                      <Input
                        value={inviteForm.prenom}
                        onChange={(e) => setInviteForm({ ...inviteForm, prenom: e.target.value })}
                      />
                    </Field>
                    <Field label="Nom">
                      <Input
                        value={inviteForm.nom}
                        onChange={(e) => setInviteForm({ ...inviteForm, nom: e.target.value })}
                      />
                    </Field>
                  </div>
                  <RolesPicker
                    roles={roles.data ?? []}
                    selected={createRoles}
                    onToggle={toggleCreateRole}
                  />
                  <Button onClick={inviter} disabled={!canWrite || busy || !inviteForm.email}>
                    Inviter
                  </Button>
                </div>
              </TabsContent>
            </Tabs>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-base">Détail</CardTitle>
            <CardDescription>
              {selected ? selected.email : "Sélectionne un utilisateur dans la liste."}
            </CardDescription>
          </CardHeader>
          <CardContent>
            {!selected ? (
              <EmptyState label="Aucune sélection" />
            ) : (
              <div className="space-y-5">
                <dl className="grid grid-cols-2 gap-x-4 gap-y-2 rounded-lg border border-border p-3 text-sm">
                  <Info label="Statut">
                    <Badge variant={statutVariant[selected.statut] ?? "secondary"}>
                      {selected.statut}
                    </Badge>
                  </Info>
                  <Info label="Type de compte">{selected.typeCompte ?? "—"}</Info>
                  <Info label="Keycloak">
                    <span className="font-mono text-xs">{selected.keycloakUserId ?? "—"}</span>
                  </Info>
                  <Info label="Dernière connexion">
                    {selected.derniereConnexion
                      ? new Date(selected.derniereConnexion).toLocaleString("fr-FR")
                      : "—"}
                  </Info>
                  <Info label="Mot de passe à changer">
                    {selected.doitChangerMotDePasse ? "oui" : "non"}
                  </Info>
                  <Info label="Rôles actifs">{(selected.roles ?? []).length}</Info>
                </dl>

                <div className="space-y-3">
                  <p className="text-xs font-semibold uppercase tracking-wide text-slate-400">
                    Modification
                  </p>
                  <div className="grid grid-cols-2 gap-3">
                    <Field label="Prénom">
                      <Input
                        value={editForm.prenom}
                        onChange={(e) => setEditForm({ ...editForm, prenom: e.target.value })}
                      />
                    </Field>
                    <Field label="Nom">
                      <Input
                        value={editForm.nom}
                        onChange={(e) => setEditForm({ ...editForm, nom: e.target.value })}
                      />
                    </Field>
                    <Field label="Post-nom">
                      <Input
                        value={editForm.postNom}
                        onChange={(e) => setEditForm({ ...editForm, postNom: e.target.value })}
                      />
                    </Field>
                    <Field label="Téléphone">
                      <Input
                        value={editForm.telephone}
                        onChange={(e) => setEditForm({ ...editForm, telephone: e.target.value })}
                      />
                    </Field>
                    <Field label="Langue préférée">
                      <Input
                        value={editForm.languePreferee}
                        onChange={(e) => setEditForm({ ...editForm, languePreferee: e.target.value })}
                      />
                    </Field>
                    <Field label="Photo (URL)">
                      <Input
                        value={editForm.photoUrl}
                        onChange={(e) => setEditForm({ ...editForm, photoUrl: e.target.value })}
                      />
                    </Field>
                  </div>
                  <Button
                    variant="outline"
                    onClick={enregistrer}
                    disabled={!canWrite || busy}
                  >
                    Enregistrer
                  </Button>
                </div>

                <div className="space-y-3">
                  <p className="text-xs font-semibold uppercase tracking-wide text-slate-400">
                    Cycle de vie
                  </p>
                  <div className="flex flex-wrap gap-2">
                    <Button
                      size="sm"
                      variant="outline"
                      disabled={!canWrite || busy}
                      onClick={() =>
                        run(async () => apply(await utilisateurApi.activer(selected.idUtilisateur)), "Utilisateur activé")
                      }
                    >
                      Activer
                    </Button>
                    <Button
                      size="sm"
                      variant="outline"
                      disabled={!canWrite || busy || !motif}
                      onClick={() =>
                        run(
                          async () => apply(await utilisateurApi.desactiver(selected.idUtilisateur, motif)),
                          "Utilisateur désactivé",
                        )
                      }
                    >
                      Désactiver
                    </Button>
                    <Button
                      size="sm"
                      variant="outline"
                      disabled={!canWrite || busy}
                      onClick={() =>
                        run(async () => apply(await utilisateurApi.suspendre(selected.idUtilisateur)), "Utilisateur suspendu")
                      }
                    >
                      Suspendre
                    </Button>
                  </div>
                  <Input
                    placeholder="Motif de désactivation (obligatoire)"
                    value={motif}
                    onChange={(e) => setMotif(e.target.value)}
                  />
                  <div className="flex gap-2">
                    <Input
                      placeholder="Nouveau mot de passe"
                      value={resetPassword}
                      onChange={(e) => setResetPassword(e.target.value)}
                    />
                    <Button
                      variant="outline"
                      disabled={!canWrite || busy || !resetPassword}
                      onClick={() =>
                        run(async () => {
                          await utilisateurApi.reinitialiserMotDePasse(selected.idUtilisateur, resetPassword);
                          setResetPassword("");
                        }, "Mot de passe réinitialisé")
                      }
                    >
                      Réinitialiser
                    </Button>
                  </div>
                </div>

                <div className="space-y-3">
                  <p className="text-xs font-semibold uppercase tracking-wide text-slate-400">Rôles</p>
                  <div className="flex flex-wrap gap-1">
                    {(selected.roles ?? []).length ? (
                      (selected.roles ?? []).map((role) => (
                        <span key={role.code} className="inline-flex items-center gap-1">
                          <Badge variant="outline">{role.code}</Badge>
                          <button
                            type="button"
                            className="text-xs text-danger disabled:opacity-40"
                            disabled={!canWrite || busy}
                            onClick={() =>
                              run(
                                async () => apply(await utilisateurApi.retirerRole(selected.idUtilisateur, role.code)),
                                "Rôle retiré",
                              )
                            }
                          >
                            ×
                          </button>
                        </span>
                      ))
                    ) : (
                      <span className="text-sm text-slate-400">Aucun rôle</span>
                    )}
                  </div>
                  <div className="flex gap-2">
                    <Select value={roleToAdd} onChange={(e) => setRoleToAdd(e.target.value)}>
                      <option value="">Ajouter un rôle…</option>
                      {disponibleRoles.map((role) => (
                        <option key={role.code} value={role.code}>
                          {role.libelle ?? role.code}
                        </option>
                      ))}
                    </Select>
                    <Button
                      variant="outline"
                      disabled={!canWrite || busy || !roleToAdd}
                      onClick={ajouterRole}
                    >
                      Ajouter
                    </Button>
                  </div>
                </div>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="space-y-1.5">
      <Label>{label}</Label>
      {children}
    </div>
  );
}

function Info({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div>
      <dt className="text-xs text-slate-500">{label}</dt>
      <dd className="font-medium text-slate-900">{children}</dd>
    </div>
  );
}

function RolesPicker({
  roles,
  selected,
  onToggle,
}: {
  roles: { code: string; libelle?: string | null }[];
  selected: string[];
  onToggle: (code: string, checked: boolean) => void;
}) {
  return (
    <div className="space-y-1.5">
      <Label>Rôles</Label>
      {roles.length ? (
        <div className="flex flex-wrap gap-3">
          {roles.map((role) => (
            <label key={role.code} className="flex items-center gap-2 text-sm">
              <Checkbox
                checked={selected.includes(role.code)}
                onChange={(checked) => onToggle(role.code, checked)}
              />
              {role.code}
            </label>
          ))}
        </div>
      ) : (
        <p className="text-xs text-slate-400">Aucun rôle disponible</p>
      )}
    </div>
  );
}
