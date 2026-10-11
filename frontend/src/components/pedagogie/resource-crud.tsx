"use client";

import { useMemo, useState, type ReactNode } from "react";
import { toast } from "sonner";
import { Pencil, Plus, Search, Trash2, X } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Checkbox } from "@/components/ui/checkbox";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select } from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import {
  PageHeader,
  LoadingView,
  ErrorState,
  EmptyState,
} from "@/components/common/async-state";
import { useApi } from "@/hooks/use-api";
import { useTenant } from "@/components/providers/tenant-provider";
import { toDateInput, toTimeInput } from "@/components/pedagogie/format";

export type FieldType =
  | "text"
  | "number"
  | "textarea"
  | "select"
  | "checkbox"
  | "time"
  | "date"
  | "tags";

export interface FieldDef {
  name: string;
  label: string;
  type?: FieldType;
  options?: { value: string; label: string }[];
  required?: boolean;
  placeholder?: string;
  defaultValue?: string | number | boolean;
  colSpan?: 1 | 2;
  hideInForm?: boolean;
}

export interface ColumnDef<T> {
  key: string;
  label: string;
  render?: (row: T) => ReactNode;
  className?: string;
}

interface ResourceCrudProps<T> {
  title: string;
  description?: string;
  idKey: string;
  list: () => Promise<T[]>;
  create?: (body: Partial<T>) => Promise<T>;
  update?: (id: string, body: Partial<T>) => Promise<T>;
  remove?: (id: string) => Promise<void>;
  fields: FieldDef[];
  columns: ColumnDef<T>[];
  searchKeys?: string[];
  initialSearch?: string;
  canWrite?: boolean;
  createLabel?: string;
  rowActions?: (row: T, reload: () => void) => ReactNode;
  headerExtra?: ReactNode;
  embedded?: boolean;
}

type FormValue = string | boolean;

function defaultValues(fields: FieldDef[]): Record<string, FormValue> {
  const values: Record<string, FormValue> = {};
  for (const field of fields) {
    if (field.type === "checkbox") values[field.name] = Boolean(field.defaultValue);
    else values[field.name] = field.defaultValue == null ? "" : String(field.defaultValue);
  }
  return values;
}

function rowToValues<T>(fields: FieldDef[], row: T): Record<string, FormValue> {
  const source = row as Record<string, unknown>;
  const values: Record<string, FormValue> = {};
  for (const field of fields) {
    const raw = source[field.name];
    if (field.type === "checkbox") values[field.name] = Boolean(raw);
    else if (field.type === "tags") values[field.name] = Array.isArray(raw) ? raw.join(", ") : "";
    else if (field.type === "time") values[field.name] = toTimeInput(raw);
    else if (field.type === "date") values[field.name] = toDateInput(raw);
    else values[field.name] = raw == null ? "" : String(raw);
  }
  return values;
}

export function ResourceCrud<T extends object>({
  title,
  description,
  idKey,
  list,
  create,
  update,
  remove,
  fields,
  columns,
  searchKeys = [],
  initialSearch = "",
  canWrite = false,
  createLabel = "Nouveau",
  rowActions,
  headerExtra,
  embedded = false,
}: ResourceCrudProps<T>) {
  const { tenantId } = useTenant();
  const { data, loading, error, reload } = useApi(list, [tenantId]);
  const [search, setSearch] = useState(initialSearch);
  const [showForm, setShowForm] = useState(false);
  const [editing, setEditing] = useState<T | null>(null);
  const [values, setValues] = useState<Record<string, FormValue>>(() => defaultValues(fields));
  const [busy, setBusy] = useState(false);

  const formFields = useMemo(() => fields.filter((f) => !f.hideInForm), [fields]);

  const rows = useMemo(() => {
    const all = data ?? [];
    if (!search.trim() || !searchKeys.length) return all;
    const needle = search.trim().toLowerCase();
    return all.filter((row) => {
      const record = row as Record<string, unknown>;
      return searchKeys.some((key) =>
        String(record[key] ?? "")
          .toLowerCase()
          .includes(needle),
      );
    });
  }, [data, search, searchKeys]);

  const openCreate = () => {
    setEditing(null);
    setValues(defaultValues(formFields));
    setShowForm(true);
  };

  const openEdit = (row: T) => {
    setEditing(row);
    setValues(rowToValues(formFields, row));
    setShowForm(true);
  };

  const closeForm = () => {
    setShowForm(false);
    setEditing(null);
    setValues(defaultValues(formFields));
  };

  const buildBody = (): Partial<T> => {
    const base: Record<string, unknown> = editing
      ? { ...(editing as Record<string, unknown>) }
      : {};
    for (const field of formFields) {
      const value = values[field.name];
      if (field.type === "checkbox") base[field.name] = Boolean(value);
      else if (field.type === "number") base[field.name] = value === "" ? null : Number(value);
      else if (field.type === "tags")
        base[field.name] = String(value)
          .split(",")
          .map((item) => item.trim())
          .filter(Boolean);
      else base[field.name] = value === "" ? null : value;
    }
    return base as Partial<T>;
  };

  const submit = async () => {
    setBusy(true);
    try {
      if (editing && update) {
        await update(String((editing as Record<string, unknown>)[idKey]), buildBody());
        toast.success("Modification enregistrée");
      } else if (create) {
        await create(buildBody());
        toast.success("Élément créé");
      }
      closeForm();
      reload();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  const destroy = async (row: T) => {
    if (!remove) return;
    setBusy(true);
    try {
      await remove(String((row as Record<string, unknown>)[idKey]));
      toast.success("Élément supprimé");
      reload();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  if (loading) return <LoadingView label={`Chargement — ${title}…`} />;
  if (error) return <ErrorState error={error} onRetry={reload} />;

  const editable = canWrite && (update != null || create != null);

  return (
    <div>
      {embedded ? (
        canWrite && create && !showForm ? (
          <div className="mb-4 flex justify-end">
            <Button onClick={openCreate}>
              <Plus className="h-4 w-4" />
              {createLabel}
            </Button>
          </div>
        ) : null
      ) : (
        <PageHeader title={title} description={description}>
          {headerExtra}
          {canWrite && create && !showForm && (
            <Button onClick={openCreate}>
              <Plus className="h-4 w-4" />
              {createLabel}
            </Button>
          )}
        </PageHeader>
      )}

      {showForm && (
        <Card className="mb-4">
          <CardContent className="p-4">
            <div className="mb-3 flex items-center justify-between">
              <p className="font-semibold text-slate-900">
                {editing ? "Modifier" : "Créer"} — {title.toLowerCase()}
              </p>
              <Button variant="ghost" size="sm" onClick={closeForm}>
                <X className="h-4 w-4" />
              </Button>
            </div>
            <form
              className="grid gap-3 sm:grid-cols-2"
              onSubmit={(event) => {
                event.preventDefault();
                submit();
              }}
            >
              {formFields.map((field) => (
                <div
                  key={field.name}
                  className={field.colSpan === 2 ? "sm:col-span-2" : undefined}
                >
                  {field.type === "checkbox" ? (
                    <div className="flex h-10 items-center gap-2">
                      <Checkbox
                        checked={Boolean(values[field.name])}
                        onChange={(checked) =>
                          setValues((prev) => ({ ...prev, [field.name]: checked }))
                        }
                      />
                      <Label>{field.label}</Label>
                    </div>
                  ) : (
                    <>
                      <Label className="mb-1 block">{field.label}</Label>
                      {field.type === "textarea" ? (
                        <Textarea
                          value={String(values[field.name] ?? "")}
                          placeholder={field.placeholder}
                          onChange={(event) =>
                            setValues((prev) => ({ ...prev, [field.name]: event.target.value }))
                          }
                        />
                      ) : field.type === "select" ? (
                        <Select
                          value={String(values[field.name] ?? "")}
                          onChange={(event) =>
                            setValues((prev) => ({ ...prev, [field.name]: event.target.value }))
                          }
                        >
                          <option value="">—</option>
                          {field.options?.map((option) => (
                            <option key={option.value} value={option.value}>
                              {option.label}
                            </option>
                          ))}
                        </Select>
                      ) : (
                        <Input
                          type={
                            field.type === "number"
                              ? "number"
                              : field.type === "time"
                                ? "time"
                                : field.type === "date"
                                  ? "date"
                                  : "text"
                          }
                          value={String(values[field.name] ?? "")}
                          placeholder={field.placeholder}
                          onChange={(event) =>
                            setValues((prev) => ({ ...prev, [field.name]: event.target.value }))
                          }
                        />
                      )}
                    </>
                  )}
                </div>
              ))}
              <div className="flex items-center gap-2 sm:col-span-2">
                <Button type="submit" disabled={busy}>
                  {editing ? "Enregistrer" : "Créer"}
                </Button>
                <Button type="button" variant="outline" onClick={closeForm} disabled={busy}>
                  Annuler
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      )}

      {searchKeys.length > 0 && (
        <div className="relative mb-4 max-w-sm">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
          <Input
            placeholder="Rechercher…"
            className="pl-9"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
          />
        </div>
      )}

      {rows.length ? (
        <Card>
          <CardContent className="overflow-x-auto p-0">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                  {columns.map((column) => (
                    <th key={column.key} className={`px-4 py-3 ${column.className ?? ""}`}>
                      {column.label}
                    </th>
                  ))}
                  {(editable || rowActions) && <th className="px-4 py-3 text-right">Actions</th>}
                </tr>
              </thead>
              <tbody>
                {rows.map((row, index) => (
                  <tr
                    key={String((row as Record<string, unknown>)[idKey] ?? index)}
                    className="border-b border-border last:border-0"
                  >
                    {columns.map((column) => {
                      const value = (row as Record<string, unknown>)[column.key];
                      return (
                        <td key={column.key} className={`px-4 py-3 ${column.className ?? ""}`}>
                          {column.render
                            ? column.render(row)
                            : value == null
                              ? <span className="text-slate-400">—</span>
                              : String(value)}
                        </td>
                      );
                    })}
                    {(editable || rowActions) && (
                      <td className="px-4 py-3">
                        <div className="flex items-center justify-end gap-1">
                          {rowActions?.(row, reload)}
                          {editable && update && (
                            <Button variant="ghost" size="sm" onClick={() => openEdit(row)}>
                              <Pencil className="h-4 w-4" />
                            </Button>
                          )}
                          {canWrite && remove && (
                            <Button
                              variant="ghost"
                              size="sm"
                              disabled={busy}
                              onClick={() => destroy(row)}
                            >
                              <Trash2 className="h-4 w-4 text-danger" />
                            </Button>
                          )}
                        </div>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
      ) : (
        <EmptyState label={search ? "Aucun résultat" : "Aucune donnée"} />
      )}

      <p className="mt-3 text-xs text-slate-400">
        {rows.length} élément(s)
        {rows.length > 0 && <Badge variant="outline" className="ml-2">tenant {tenantId?.slice(0, 8)}…</Badge>}
      </p>
    </div>
  );
}
