import Link from "next/link";
import {
  Building2,
  GraduationCap,
  HeartPulse,
  Landmark,
  ShieldCheck,
  Users,
  BarChart3,
  Bell,
  CheckCircle2,
  ArrowRight,
} from "lucide-react";

const DOMAINES = [
  {
    icon: GraduationCap,
    titre: "Pédagogie",
    desc: "Primaire, secondaire et université : structure, inscriptions, assiduité, évaluations, examens et frais scolaires.",
    points: ["Grilles de notation et délibérations", "Bulletins, coupons et paiements", "Emplois du temps et salles"],
  },
  {
    icon: HeartPulse,
    titre: "Hôpital",
    desc: "Patients, rendez-vous, consultations, hospitalisation, pharmacie et plateau technique (labo & imagerie).",
    points: ["Dossier patient unifié", "Prescriptions et résultats validés", "Traçabilité des lots et actes"],
  },
  {
    icon: Landmark,
    titre: "Finance",
    desc: "Comptabilité OHADA / SYSCOHADA : écritures, facturation, stocks, immobilisations et états certifiés.",
    points: ["Devise fonctionnelle USD, affichage CDF", "États certifiés et export FEC", "Facturation reliée aux actes"],
  },
];

const SOCLE = [
  { icon: Users, titre: "Identité & accès", desc: "Utilisateurs, rôles et permissions synchronisés avec Keycloak." },
  { icon: ShieldCheck, titre: "Multi-tenant étanche", desc: "Isolation des données par établissement (RLS PostgreSQL)." },
  { icon: Bell, titre: "Notification & audit", desc: "Communication aux usagers et journalisation de chaque action." },
  { icon: BarChart3, titre: "Reporting", desc: "Tableaux de bord et rapports consolidés par périmètre." },
];

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-white">
      <header className="sticky top-0 z-10 border-b border-slate-200 bg-white/80 backdrop-blur">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
          <div className="flex items-center gap-2">
            <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-blue-600">
              <Building2 className="h-5 w-5 text-white" />
            </div>
            <span className="text-lg font-bold text-slate-900">NexusGesti</span>
          </div>
          <nav className="flex items-center gap-3">
            <Link
              href="/login"
              className="rounded-lg px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-100"
            >
              Connexion
            </Link>
            <Link
              href="/signup"
              className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-blue-700"
            >
              Créer mon établissement
            </Link>
          </nav>
        </div>
      </header>

      <section className="mx-auto max-w-6xl px-6 pb-16 pt-20 text-center">
        <span className="inline-block rounded-full bg-blue-50 px-4 py-1.5 text-xs font-semibold uppercase tracking-wide text-blue-700">
          Plateforme SaaS multi-tenant
        </span>
        <h1 className="mx-auto mt-6 max-w-3xl text-4xl font-bold leading-tight text-slate-900 sm:text-5xl">
          Gérez votre établissement, de la pédagogie à la comptabilité
        </h1>
        <p className="mx-auto mt-6 max-w-2xl text-lg text-slate-600">
          NexusGesti réunit sur un socle unique la gestion scolaire, hospitalière et financière.
          Chaque établissement dispose de son espace isolé, sécurisé et prêt à l&apos;emploi.
        </p>
        <div className="mt-10 flex flex-wrap items-center justify-center gap-4">
          <Link
            href="/signup"
            className="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-6 py-3 text-sm font-semibold text-white transition hover:bg-blue-700"
          >
            Démarrer gratuitement
            <ArrowRight className="h-4 w-4" />
          </Link>
          <Link
            href="/login"
            className="inline-flex items-center gap-2 rounded-lg border border-slate-300 px-6 py-3 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
          >
            J&apos;ai déjà un compte
          </Link>
        </div>
      </section>

      <section className="border-y border-slate-200 bg-slate-50 py-20">
        <div className="mx-auto max-w-6xl px-6">
          <h2 className="text-center text-3xl font-bold text-slate-900">Trois métiers, une plateforme</h2>
          <p className="mx-auto mt-3 max-w-2xl text-center text-slate-600">
            Un socle technique commun, des modules métier activables selon votre profil.
          </p>
          <div className="mt-12 grid gap-6 md:grid-cols-3">
            {DOMAINES.map((d) => (
              <div
                key={d.titre}
                className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm transition hover:shadow-md"
              >
                <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-blue-50">
                  <d.icon className="h-6 w-6 text-blue-600" />
                </div>
                <h3 className="mt-4 text-lg font-semibold text-slate-900">{d.titre}</h3>
                <p className="mt-2 text-sm leading-relaxed text-slate-600">{d.desc}</p>
                <ul className="mt-4 space-y-2">
                  {d.points.map((p) => (
                    <li key={p} className="flex items-start gap-2 text-sm text-slate-600">
                      <CheckCircle2 className="mt-0.5 h-4 w-4 shrink-0 text-emerald-500" />
                      {p}
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="py-20">
        <div className="mx-auto max-w-6xl px-6">
          <h2 className="text-center text-3xl font-bold text-slate-900">Inclus dans le socle</h2>
          <div className="mt-12 grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
            {SOCLE.map((s) => (
              <div key={s.titre} className="rounded-2xl border border-slate-200 p-6">
                <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-slate-100">
                  <s.icon className="h-5 w-5 text-slate-700" />
                </div>
                <h3 className="mt-4 font-semibold text-slate-900">{s.titre}</h3>
                <p className="mt-2 text-sm leading-relaxed text-slate-600">{s.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="bg-blue-600 py-16">
        <div className="mx-auto max-w-4xl px-6 text-center">
          <h2 className="text-3xl font-bold text-white">Prêt à commencer ?</h2>
          <p className="mx-auto mt-3 max-w-xl text-blue-100">
            Créez votre compte et votre établissement en quelques minutes. Aucune installation requise.
          </p>
          <Link
            href="/signup"
            className="mt-8 inline-flex items-center gap-2 rounded-lg bg-white px-6 py-3 text-sm font-semibold text-blue-700 transition hover:bg-blue-50"
          >
            Créer mon établissement
            <ArrowRight className="h-4 w-4" />
          </Link>
        </div>
      </section>

      <footer className="border-t border-slate-200 py-8">
        <div className="mx-auto max-w-6xl px-6 text-center text-sm text-slate-500">
          © {new Date().getFullYear()} NexusGesti — Plateforme de gestion multi-tenant.
        </div>
      </footer>
    </div>
  );
}
