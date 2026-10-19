"use client";

import { Select } from "@/components/ui/select";
import { useApi } from "@/hooks/use-api";

interface EntitySelectProps<T> {
  fetcher: () => Promise<T[]>;
  keyOf: (row: T) => string;
  labelOf: (row: T) => string;
  value: string | null | undefined;
  onChange: (value: string) => void;
  placeholder?: string;
  className?: string;
  disabled?: boolean;
  deps?: unknown[];
}

export function EntitySelect<T>({
  fetcher,
  keyOf,
  labelOf,
  value,
  onChange,
  placeholder = "Sélectionner…",
  className,
  disabled,
  deps = [],
}: EntitySelectProps<T>) {
  const { data, loading } = useApi(fetcher, deps);
  const rows = data ?? [];
  const selected = value ?? "";
  const hasSelected = rows.some((row) => keyOf(row) === selected);

  return (
    <Select
      value={selected}
      onChange={(event) => onChange(event.target.value)}
      className={className}
      disabled={disabled || loading}
    >
      <option value="">{loading ? "Chargement…" : placeholder}</option>
      {rows.map((row) => (
        <option key={keyOf(row)} value={keyOf(row)}>
          {labelOf(row)}
        </option>
      ))}
      {selected && !hasSelected ? <option value={selected}>{selected}</option> : null}
    </Select>
  );
}
