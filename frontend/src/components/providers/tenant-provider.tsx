"use client";

import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { isValidUuid } from "@/services/api";

export const DEFAULT_TENANT_ID = "11111111-2222-3333-4444-555555555570";

const TENANT_KEY = "x_tenant_id";

interface TenantContextType {
  tenantId: string | null;
  setTenantId: (id: string) => void;
}

const TenantContext = createContext<TenantContextType | null>(null);

export function TenantProvider({ children }: { children: ReactNode }) {
  const [tenantId, setTenantIdState] = useState<string | null>(null);

  useEffect(() => {
    const stored = window.localStorage.getItem(TENANT_KEY);
    if (stored && isValidUuid(stored)) {
      setTenantIdState(stored);
      return;
    }
    window.localStorage.setItem(TENANT_KEY, DEFAULT_TENANT_ID);
    setTenantIdState(DEFAULT_TENANT_ID);
  }, []);

  const setTenantId = (id: string) => {
    if (!isValidUuid(id)) return;
    window.localStorage.setItem(TENANT_KEY, id);
    setTenantIdState(id);
  };

  const value = useMemo<TenantContextType>(() => ({ tenantId, setTenantId }), [tenantId]);
  return <TenantContext.Provider value={value}>{children}</TenantContext.Provider>;
}

export function useTenant() {
  const ctx = useContext(TenantContext);
  if (!ctx) throw new Error("useTenant must be used within TenantProvider");
  return ctx;
}