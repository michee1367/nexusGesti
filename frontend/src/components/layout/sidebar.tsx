"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  LayoutDashboard,
  Building2,
  CalendarDays,
  FileText,
  FolderOpen,
  Users,
  UserCog,
  KeyRound,
  Bell,
  ShieldCheck,
  BarChart3,
  X,
  GraduationCap,
} from "lucide-react";
import { cn } from "@/lib/utils";
import { useAuth } from "@/components/providers/auth-provider";

interface SidebarProps {
  isOpen?: boolean;
  onClose?: () => void;
}

const navItems = [
  { href: "/dashboard", label: "Tableau de bord", icon: LayoutDashboard },
  { href: "/tenant", label: "Établissement", icon: Building2 },
  { href: "/annees", label: "Années scolaires", icon: CalendarDays },
  { href: "/grilles", label: "Grilles", icon: FileText },
  { href: "/referentiels", label: "Référentiels", icon: FolderOpen },
];

const adminItems = [
  { href: "/tenants", label: "Tenants", icon: Users },
  { href: "/utilisateurs", label: "Utilisateurs", icon: UserCog },
  { href: "/roles", label: "Rôles & permissions", icon: KeyRound },
  { href: "/notifications", label: "Notifications", icon: Bell },
  { href: "/audit", label: "Audit", icon: ShieldCheck },
  { href: "/rapports", label: "Rapports", icon: BarChart3 },
];

export function Sidebar({ isOpen, onClose }: SidebarProps) {
  const pathname = usePathname();
  const { user } = useAuth();

  return (
    <>
      {isOpen && (
        <div className="fixed inset-0 z-40 bg-black/40 lg:hidden" onClick={onClose} />
      )}

      <aside
        className={cn(
          "fixed left-0 top-0 z-50 flex h-screen w-64 flex-col border-r border-border bg-white transition-transform duration-300 ease-in-out",
          isOpen ? "translate-x-0" : "-translate-x-full",
          "lg:translate-x-0"
        )}
      >
        <div className="flex h-16 items-center justify-between border-b border-border bg-blue-600 px-4 text-white shadow-sm">
          <div className="flex items-center gap-2">
            <GraduationCap className="h-6 w-6" />
            <span className="text-sm font-bold tracking-tight">NexusGesti</span>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="rounded-md p-1 text-blue-100 hover:bg-blue-700 hover:text-white lg:hidden"
            aria-label="Fermer le menu"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <nav className="flex-1 overflow-y-auto p-4">
          <ul className="space-y-1">
            {navItems.map(({ href, label, icon: Icon }) => {
              const active = pathname === href || pathname.startsWith(href + "/");
              return (
                <li key={href}>
                  <Link
                    href={href}
                    onClick={onClose}
                    className={cn(
                      "flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors",
                      active
                        ? "bg-blue-50 font-semibold text-blue-600"
                        : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                    )}
                  >
                    <Icon
                      className={cn("h-4 w-4 shrink-0", active ? "text-blue-600" : "text-slate-400")}
                    />
                    {label}
                  </Link>
                </li>
              );
            })}

            {user?.isAdmin && (
              <>
                <li className="px-3 pb-1 pt-5">
                  <p className="text-[11px] font-semibold uppercase tracking-wider text-slate-400">
                    Plateforme
                  </p>
                </li>
                {adminItems.map(({ href, label, icon: Icon }) => {
                  const active = pathname === href || pathname.startsWith(href + "/");
                  return (
                    <li key={href}>
                      <Link
                        href={href}
                        onClick={onClose}
                        className={cn(
                          "flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors",
                          active
                            ? "bg-blue-50 font-semibold text-blue-600"
                            : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                        )}
                      >
                        <Icon
                          className={cn(
                            "h-4 w-4 shrink-0",
                            active ? "text-blue-600" : "text-slate-400"
                          )}
                        />
                        {label}
                      </Link>
                    </li>
                  );
                })}
              </>
            )}
          </ul>
        </nav>

        <div className="border-t border-border p-4">
          <p className="text-center text-xs text-slate-400">© NexusGesti</p>
        </div>
      </aside>
    </>
  );
}