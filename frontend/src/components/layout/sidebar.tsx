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
  Layers,
  School,
  CalendarRange,
  BookOpen,
  BookMarked,
  DoorOpen,
  Clock,
  CalendarClock,
  Award,
  Boxes,
  UsersRound,
  HeartHandshake,
  Presentation,
  Briefcase,
  ClipboardList,
  ListChecks,
  UserPlus,
  CalendarCheck,
  ClipboardCheck,
  FileCheck,
  Activity,
  FileSpreadsheet,
  ScrollText,
  TrendingUp,
  Gavel,
  RotateCcw,
  FileSignature,
  Wallet,
  Receipt,
  CreditCard,
  Ticket,
  Coins,
  type LucideIcon,
} from "lucide-react";
import { cn } from "@/lib/utils";
import { useAuth } from "@/components/providers/auth-provider";

interface SidebarProps {
  isOpen?: boolean;
  onClose?: () => void;
}

interface NavItem {
  href: string;
  label: string;
  icon: LucideIcon;
}

interface NavGroup {
  title?: string;
  items: NavItem[];
}

const navItems: NavItem[] = [
  { href: "/dashboard", label: "Tableau de bord", icon: LayoutDashboard },
  { href: "/tenant", label: "Établissement", icon: Building2 },
  { href: "/annees", label: "Années scolaires", icon: CalendarDays },
  { href: "/grilles", label: "Grilles", icon: FileText },
  { href: "/referentiels", label: "Référentiels", icon: FolderOpen },
];

const structureItems: NavItem[] = [
  { href: "/pedagogie/niveaux", label: "Niveaux", icon: Layers },
  { href: "/pedagogie/classes", label: "Classes", icon: School },
  { href: "/pedagogie/promotions", label: "Promotions", icon: Award },
  { href: "/pedagogie/semestres", label: "Semestres", icon: CalendarRange },
  { href: "/pedagogie/ues", label: "Unités d'enseignement", icon: Boxes },
  { href: "/pedagogie/matieres", label: "Matières", icon: BookOpen },
  { href: "/pedagogie/cours", label: "Cours / ECUE", icon: BookMarked },
  { href: "/pedagogie/salles", label: "Salles", icon: DoorOpen },
  { href: "/pedagogie/creneaux", label: "Créneaux", icon: Clock },
  { href: "/pedagogie/regimes-horaires", label: "Régimes horaires", icon: CalendarClock },
  { href: "/pedagogie/emplois-du-temps", label: "Emplois du temps", icon: CalendarDays },
];

const peopleItems: NavItem[] = [
  { href: "/pedagogie/eleves", label: "Élèves", icon: UsersRound },
  { href: "/pedagogie/parents", label: "Parents", icon: HeartHandshake },
  { href: "/pedagogie/enseignants", label: "Enseignants", icon: Presentation },
  { href: "/pedagogie/personnel", label: "Personnel", icon: Briefcase },
];

const enrollmentItems: NavItem[] = [
  { href: "/pedagogie/inscriptions-ue", label: "Inscriptions UE", icon: ClipboardList },
  { href: "/pedagogie/groupes", label: "Groupes TD/TP", icon: Users },
  { href: "/pedagogie/regles-inscription", label: "Règles d'éligibilité", icon: ListChecks },
  { href: "/pedagogie/admissions", label: "Admissions", icon: UserPlus },
];

const attendanceItems: NavItem[] = [
  { href: "/pedagogie/seances", label: "Séances", icon: CalendarCheck },
  { href: "/pedagogie/appels", label: "Feuilles d'appel", icon: ClipboardCheck },
  { href: "/pedagogie/justificatifs", label: "Justificatifs", icon: FileCheck },
  { href: "/pedagogie/assiduite", label: "Assiduité", icon: Activity },
];

const assessmentItems: NavItem[] = [
  { href: "/pedagogie/evaluations", label: "Évaluations", icon: FileSpreadsheet },
  { href: "/pedagogie/bulletins", label: "Bulletins", icon: ScrollText },
  { href: "/pedagogie/moyennes", label: "Moyennes & classement", icon: TrendingUp },
];

const examItems: NavItem[] = [
  { href: "/pedagogie/sessions", label: "Sessions d'examen", icon: CalendarClock },
  { href: "/pedagogie/deliberations", label: "Délibérations", icon: Gavel },
  { href: "/pedagogie/rattrapages", label: "Rattrapages", icon: RotateCcw },
  { href: "/pedagogie/proces-verbaux", label: "Procès-verbaux", icon: FileSignature },
];

const feesItems: NavItem[] = [
  { href: "/pedagogie/grilles-tarifaires", label: "Grilles tarifaires", icon: Wallet },
  { href: "/pedagogie/avis-paiements", label: "Avis de paiement", icon: Receipt },
  { href: "/pedagogie/paiements", label: "Paiements", icon: CreditCard },
  { href: "/pedagogie/coupons-paiement", label: "Coupons", icon: Ticket },
  { href: "/pedagogie/journaliers", label: "Journaliers", icon: Coins },
];

const adminItems: NavItem[] = [
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

  const groups: NavGroup[] = [
    { items: navItems },
    { title: "Pédagogie · Structure", items: structureItems },
    { title: "Pédagogie · Personnes", items: peopleItems },
    { title: "Pédagogie · Inscriptions", items: enrollmentItems },
    { title: "Pédagogie · Assiduité", items: attendanceItems },
    { title: "Pédagogie · Évaluations", items: assessmentItems },
    { title: "Pédagogie · Examens", items: examItems },
    { title: "Pédagogie · Frais", items: feesItems },
  ];

  const renderItem = ({ href, label, icon: Icon }: NavItem) => {
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
          <Icon className={cn("h-4 w-4 shrink-0", active ? "text-blue-600" : "text-slate-400")} />
          {label}
        </Link>
      </li>
    );
  };

  return (
    <>
      {isOpen && <div className="fixed inset-0 z-40 bg-black/40 lg:hidden" onClick={onClose} />}

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
            {groups.map((group, groupIndex) => (
              <li key={group.title ?? `group-${groupIndex}`}>
                {group.title && (
                  <p className="px-3 pb-1 pt-5 text-[11px] font-semibold uppercase tracking-wider text-slate-400">
                    {group.title}
                  </p>
                )}
                <ul className="space-y-1">{group.items.map(renderItem)}</ul>
              </li>
            ))}

            {user?.isAdmin && (
              <li>
                <p className="px-3 pb-1 pt-5 text-[11px] font-semibold uppercase tracking-wider text-slate-400">
                  Plateforme
                </p>
                <ul className="space-y-1">{adminItems.map(renderItem)}</ul>
              </li>
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
