"use client";

import { createContext, useContext, useEffect, useState, useCallback, useMemo, type ReactNode } from "react";
import { decodeJwtPayload, isTokenExpired } from "@/lib/jwt";
import { authApi, clearSession, getAccessToken, getRefreshToken, tryRefresh } from "@/services/api";
import type { SessionUser } from "@/types";

const USER_KEY = "auth_user";

function buildUserFromToken(token: string): SessionUser {
  const payload = decodeJwtPayload(token) ?? {};
  const realmAccess = (payload.realm_access as { roles?: string[] } | undefined) ?? {};
  const roles = realmAccess.roles ?? [];
  const email =
    (payload.email as string | undefined) ??
    (payload.preferred_username as string | undefined) ??
    (typeof payload.sub === "string" ? payload.sub : "");
  const given = (payload.given_name as string | undefined) ?? "";
  const family = (payload.family_name as string | undefined) ?? "";
  const name = (payload.name as string | undefined) ?? [given, family].filter(Boolean).join(" ") ?? "";
  return {
    sub: typeof payload.sub === "string" ? payload.sub : null,
    email,
    name,
    roles,
    isAdmin: roles.includes("ADMIN"),
  };
}

interface AuthContextType {
  user: SessionUser | null;
  token: string | null;
  isLoading: boolean;
  isAdmin: boolean;
  login: (email: string, password: string) => Promise<SessionUser>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<SessionUser | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const applySession = useCallback((accessToken: string) => {
    const sessionUser = buildUserFromToken(accessToken);
    setUser(sessionUser);
    setToken(accessToken);
    if (typeof window !== "undefined") {
      window.localStorage.setItem(USER_KEY, JSON.stringify(sessionUser));
    }
    return sessionUser;
  }, []);

  useEffect(() => {
    const accessToken = getAccessToken();
    if (!accessToken) {
      setIsLoading(false);
      return;
    }

    if (isTokenExpired(accessToken)) {
      const refreshToken = getRefreshToken();
      if (!refreshToken) {
        clearSession();
        setIsLoading(false);
        return;
      }
      tryRefresh().then((ok) => {
        if (!ok) {
          setIsLoading(false);
          return;
        }
        const fresh = getAccessToken();
        if (fresh) applySession(fresh);
        setToken(fresh);
        setIsLoading(false);
      });
      return;
    }

    applySession(accessToken);
    setIsLoading(false);
  }, [applySession]);

  const login = useCallback(
    async (email: string, password: string): Promise<SessionUser> => {
      const response = await authApi.login(email, password);
      return applySession(response.accessToken);
    },
    [applySession]
  );

  const logout = useCallback(async (): Promise<void> => {
    const access = getAccessToken() ?? undefined;
    const refresh = getRefreshToken() ?? undefined;
    await authApi.logout(access, refresh);
    setUser(null);
    setToken(null);
    if (typeof window !== "undefined") {
      window.localStorage.removeItem(USER_KEY);
    }
  }, []);

  const value = useMemo<AuthContextType>(
    () => ({ user, token, isLoading, isAdmin: user?.isAdmin ?? false, login, logout }),
    [user, token, isLoading, login, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}