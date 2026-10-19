"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { ArrowLeft, Building2, CheckCircle2, Loader2 } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { authApi, tenantApi } from "@/services/api";
import type { ProfilEtablissement } from "@/types";

const schema = z.object({
  firstName: z.string().min(1, "Le prénom est requis").max(100),
  lastName: z.string().min(1, "Le nom est requis").max(100),
  email: z.string().email("Adresse email invalide"),
  phone: z
    .string()
    .regex(/^\+[1-9]\d{6,14}$/, "Téléphone international requis (ex. +243810000000)"),
  countryCode: z.string().length(2, "Code pays sur 2 lettres (ex. CD)"),
  password: z
    .string()
    .min(12, "Au moins 12 caractères")
    .regex(
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).+$/,
      "Majuscule, minuscule, chiffre et caractère spécial requis"
    ),
  acceptedTerms: z.boolean().refine((v) => v === true, {
    message: "Vous devez accepter les conditions",
  }),
  denomination: z.string().min(1, "La dénomination est requise").max(150),
  code: z.string().min(1, "Le code est requis").max(30),
  profil: z.enum(["PRIMAIRE", "SECONDAIRE", "UNIVERSITAIRE", "BOULANGERIE"]),
});

type FormData = z.infer<typeof schema>;

function randomUuid(): string {
  if (typeof crypto !== "undefined" && "randomUUID" in crypto) {
    return crypto.randomUUID();
  }
  return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0;
    const v = c === "x" ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}

async function waitForEtablissement(id: string, attempts = 20): Promise<boolean> {
  for (let i = 0; i < attempts; i += 1) {
    try {
      await tenantApi.get(id);
      return true;
    } catch {
      await new Promise((resolve) => setTimeout(resolve, 1000));
    }
  }
  return false;
}

export default function SignupPage() {
  const { login } = useAuth();
  const { setTenantId } = useTenant();
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [phase, setPhase] = useState<string>("");

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: {
      firstName: "",
      lastName: "",
      email: "",
      phone: "",
      countryCode: "CD",
      password: "",
      acceptedTerms: false,
      denomination: "",
      code: "",
      profil: "PRIMAIRE",
    },
  });

  const onSubmit = async (data: FormData) => {
    setLoading(true);
    try {
      const tenantId = randomUuid();
      setTenantId(tenantId);

      setPhase("Création du compte…");
      await authApi.register({
        email: data.email,
        phone: data.phone,
        password: data.password,
        firstName: data.firstName,
        lastName: data.lastName,
        countryCode: data.countryCode.toUpperCase(),
        acceptedTerms: data.acceptedTerms,
        etablissement: {
          denomination: data.denomination,
          code: data.code,
          profil: data.profil as ProfilEtablissement,
        },
      });

      setPhase("Connexion…");
      await login(data.email, data.password);

      setPhase("Préparation de votre espace…");
      await waitForEtablissement(tenantId);

      toast.success("Votre espace est prêt !");
      router.push("/dashboard");
    } catch (error) {
      toast.error(
        error instanceof Error ? error.message : "Inscription impossible. Réessayez."
      );
      setPhase("");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 py-10">
      <div className="mx-auto max-w-2xl px-4">
        <Link
          href="/"
          className="mb-6 inline-flex items-center gap-2 text-sm text-slate-600 hover:text-slate-900"
        >
          <ArrowLeft className="h-4 w-4" />
          Retour à l&apos;accueil
        </Link>

        <div className="mb-8 flex flex-col items-center">
          <div className="mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-blue-600 p-3 shadow-lg">
            <Building2 className="h-8 w-8 text-white" />
          </div>
          <h1 className="text-2xl font-bold text-slate-900">Créer votre établissement</h1>
          <p className="mt-1 text-center text-sm text-slate-500">
            Votre compte administrateur et votre espace sont créés en une seule étape.
          </p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="mb-4 text-sm font-semibold uppercase tracking-wide text-slate-500">
              Votre compte
            </h2>
            <div className="grid gap-4 sm:grid-cols-2">
              <div className="space-y-2">
                <Label htmlFor="firstName">Prénom</Label>
                <Input id="firstName" {...register("firstName")} />
                {errors.firstName && <p className="text-xs text-red-500">{errors.firstName.message}</p>}
              </div>
              <div className="space-y-2">
                <Label htmlFor="lastName">Nom</Label>
                <Input id="lastName" {...register("lastName")} />
                {errors.lastName && <p className="text-xs text-red-500">{errors.lastName.message}</p>}
              </div>
              <div className="space-y-2 sm:col-span-2">
                <Label htmlFor="email">Adresse email</Label>
                <Input id="email" type="email" autoComplete="email" {...register("email")} />
                {errors.email && <p className="text-xs text-red-500">{errors.email.message}</p>}
              </div>
              <div className="space-y-2">
                <Label htmlFor="phone">Téléphone</Label>
                <Input id="phone" placeholder="+243810000000" {...register("phone")} />
                {errors.phone && <p className="text-xs text-red-500">{errors.phone.message}</p>}
              </div>
              <div className="space-y-2">
                <Label htmlFor="countryCode">Code pays</Label>
                <Input id="countryCode" placeholder="CD" maxLength={2} {...register("countryCode")} />
                {errors.countryCode && (
                  <p className="text-xs text-red-500">{errors.countryCode.message}</p>
                )}
              </div>
              <div className="space-y-2 sm:col-span-2">
                <Label htmlFor="password">Mot de passe</Label>
                <Input id="password" type="password" autoComplete="new-password" {...register("password")} />
                {errors.password && <p className="text-xs text-red-500">{errors.password.message}</p>}
              </div>
            </div>
          </section>

          <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="mb-4 text-sm font-semibold uppercase tracking-wide text-slate-500">
              Votre établissement
            </h2>
            <div className="grid gap-4 sm:grid-cols-2">
              <div className="space-y-2 sm:col-span-2">
                <Label htmlFor="denomination">Dénomination</Label>
                <Input id="denomination" placeholder="École Les Palmiers" {...register("denomination")} />
                {errors.denomination && (
                  <p className="text-xs text-red-500">{errors.denomination.message}</p>
                )}
              </div>
              <div className="space-y-2">
                <Label htmlFor="code">Code</Label>
                <Input id="code" placeholder="ECO-001" {...register("code")} />
                {errors.code && <p className="text-xs text-red-500">{errors.code.message}</p>}
              </div>
              <div className="space-y-2">
                <Label htmlFor="profil">Profil</Label>
                <select
                  id="profil"
                  className="flex h-10 w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  {...register("profil")}
                >
                  <option value="PRIMAIRE">Primaire</option>
                  <option value="SECONDAIRE">Secondaire</option>
                  <option value="UNIVERSITAIRE">Universitaire</option>
                  <option value="BOULANGERIE">Boulangerie</option>
                </select>
                {errors.profil && <p className="text-xs text-red-500">{errors.profil.message}</p>}
              </div>
            </div>
          </section>

          <label className="flex items-start gap-3 text-sm text-slate-600">
            <input type="checkbox" className="mt-0.5 h-4 w-4" {...register("acceptedTerms")} />
            <span>
              J&apos;accepte les conditions d&apos;utilisation et la politique de confidentialité de NexusGesti.
            </span>
          </label>
          {errors.acceptedTerms && (
            <p className="-mt-3 text-xs text-red-500">{errors.acceptedTerms.message}</p>
          )}

          <Button type="submit" className="w-full" disabled={loading}>
            {loading ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                {phase || "Création…"}
              </>
            ) : (
              <>
                <CheckCircle2 className="mr-2 h-4 w-4" />
                Créer mon établissement
              </>
            )}
          </Button>

          <p className="text-center text-sm text-slate-500">
            Déjà inscrit ?{" "}
            <Link href="/login" className="font-medium text-blue-600 hover:underline">
              Se connecter
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
}
