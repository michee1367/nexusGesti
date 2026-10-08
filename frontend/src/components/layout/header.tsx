"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { LogOut, ChevronDown, Menu, Building2 } from "lucide-react";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";

interface HeaderProps {
  onMenuToggle?: () => void;
}

function shortId(id: string | null): string {
  if (!id) return "—";
  return `${id.slice(0, 8)}…`;
}

export function Header({ onMenuToggle }: HeaderProps) {
  const { user, logout } = useAuth();
  const { tenantId } = useTenant();
  const router = useRouter();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = async () => {
    await logout();
    router.push("/login");
  };

  const displayName = user?.name || user?.email.split("@")[0] || "Utilisateur";
  const displayEmail = user?.email || "";
  const initials = (user?.name || user?.email || "U").slice(0, 1).toUpperCase();
  const userAvatar = `https://ui-avatars.com/api/?name=${encodeURIComponent(initials)}&background=2563EB&color=fff&size=128`;

  return (
    <header className="sticky top-0 z-30 flex h-16 items-center justify-between gap-2 border-b border-border bg-white/80 px-4 backdrop-blur-sm md:px-6">
      <div className="flex items-center gap-2">
        <button
          type="button"
          onClick={onMenuToggle}
          className="rounded-lg p-2 text-slate-600 hover:bg-slate-100 lg:hidden"
          aria-label="Ouvrir le menu"
        >
          <Menu className="h-5 w-5" />
        </button>

        <span className="hidden items-center gap-2 rounded-lg border border-border bg-background px-3 py-1.5 text-xs text-slate-600 sm:flex">
          <Building2 className="h-3.5 w-3.5 text-blue-600" />
          Tenant <span className="font-mono font-medium text-slate-900">{shortId(tenantId)}</span>
        </span>
      </div>

      <div className="flex items-center gap-3">
        {user?.roles.map((role) => (
          <Badge
            key={role}
            variant={role === "ADMIN" ? "default" : "secondary"}
            className="text-xs"
          >
            {role}
          </Badge>
        ))}

        <div className="relative">
          <button
            type="button"
            onClick={() => setMenuOpen(!menuOpen)}
            className="flex items-center gap-2 rounded-lg p-1.5 hover:bg-slate-100"
          >
            <Avatar className="h-8 w-8">
              <AvatarImage src={userAvatar} alt={displayName} />
              <AvatarFallback className="bg-blue-600 text-xs font-semibold text-white">
                {initials}
              </AvatarFallback>
            </Avatar>
            <div className="hidden text-left md:block">
              <p className="text-sm font-medium leading-tight text-slate-900">{displayName}</p>
              <p className="text-xs leading-tight text-slate-500">{displayEmail}</p>
            </div>
            <ChevronDown className="hidden h-4 w-4 text-slate-400 md:block" />
          </button>

          {menuOpen && (
            <>
              <div className="fixed inset-0 z-40" onClick={() => setMenuOpen(false)} />
              <div className="absolute right-0 top-full z-50 mt-1 w-52 rounded-lg border border-border bg-white py-1 shadow-lg">
                <div className="border-b border-slate-100 px-4 py-2 md:hidden">
                  <p className="text-sm font-medium text-slate-900">{displayName}</p>
                  <p className="truncate text-xs text-slate-500">{displayEmail}</p>
                </div>
                <button
                  onClick={handleLogout}
                  className="flex w-full items-center gap-2 px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                >
                  <LogOut className="h-4 w-4" />
                  Déconnexion
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </header>
  );
}