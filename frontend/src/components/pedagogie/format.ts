export function formatDate(value: unknown): string {
  if (value == null || value === "") return "—";
  if (Array.isArray(value)) {
    const [y, m, d] = value as number[];
    if (y == null) return "—";
    return `${String(d ?? 1).padStart(2, "0")}/${String(m ?? 1).padStart(2, "0")}/${y}`;
  }
  if (typeof value === "string") {
    const iso = value.slice(0, 10);
    const [y, m, d] = iso.split("-");
    if (y && m && d) return `${d}/${m}/${y}`;
    return value;
  }
  return String(value);
}

export function formatTime(value: unknown): string {
  if (value == null || value === "") return "—";
  if (Array.isArray(value)) {
    const [h, m] = value as number[];
    return `${String(h ?? 0).padStart(2, "0")}:${String(m ?? 0).padStart(2, "0")}`;
  }
  if (typeof value === "string") {
    const parts = value.split(":");
    if (parts.length >= 2) return `${parts[0].padStart(2, "0")}:${parts[1].padStart(2, "0")}`;
    return value;
  }
  return String(value);
}

export function formatInstant(value: unknown): string {
  if (value == null || value === "") return "—";
  const date =
    typeof value === "number"
      ? new Date(value < 1e12 ? value * 1000 : value)
      : new Date(String(value));
  if (Number.isNaN(date.getTime())) return String(value);
  return date.toLocaleString("fr-FR");
}

export function toDateInput(value: unknown): string {
  if (value == null || value === "") return "";
  if (Array.isArray(value)) {
    const [y, m, d] = value as number[];
    if (y == null) return "";
    return `${y}-${String(m ?? 1).padStart(2, "0")}-${String(d ?? 1).padStart(2, "0")}`;
  }
  return String(value).slice(0, 10);
}

export function toTimeInput(value: unknown): string {
  if (value == null || value === "") return "";
  if (Array.isArray(value)) {
    const [h, m] = value as number[];
    return `${String(h ?? 0).padStart(2, "0")}:${String(m ?? 0).padStart(2, "0")}`;
  }
  return formatTime(value);
}

export function humanize(value: unknown): string {
  if (value == null || value === "") return "—";
  return String(value).replace(/_/g, " ");
}
