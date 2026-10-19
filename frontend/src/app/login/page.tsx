"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { ArrowLeft, Eye, EyeOff, GraduationCap, Loader2 } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { ApiError } from "@/types";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { authApi, isValidUuid, tenantApi, type IdentiteEtablissement } from "@/services/api";

const schema = z.object({
  email: z.string().email("Adresse email invalide"),
  password: z.string().min(1, "Le mot de passe est requis"),
  tenantId: z.string().min(1, "Identifiant tenant requis"),
});

type FormData = z.infer<typeof schema>;

export default function LoginPage() {
  const { login } = useAuth();
  const { setTenantId } = useTenant();
  const router = useRouter();
  const [step, setStep] = useState<"email" | "password">("email");
  const [manualMode, setManualMode] = useState(false);
  const [resolving, setResolving] = useState(false);
  const [etablissement, setEtablissement] = useState<IdentiteEtablissement | null>(null);
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
    getValues,
    trigger,
    formState: { errors },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    mode: "onSubmit",
    defaultValues: { email: "", password: "", tenantId: "" },
  });

  const loadIdentite = async (id: string) => {
    try {
      setEtablissement(await tenantApi.identite(id));
    } catch {
      setEtablissement(null);
    }
  };

  const handleContinue = async () => {
    if (!(await trigger("email"))) return;
    setResolving(true);
    try {
      const { tenantId } = await authApi.resolveTenant(getValues("email"));
      setTenantId(tenantId);
      setValue("tenantId", tenantId);
      await loadIdentite(tenantId);
      setStep("password");
    } catch (error) {
      const notFound = error instanceof ApiError && error.code === "AUTH_TENANT_NOT_FOUND";
      toast.error(
        notFound
          ? "Aucun établissement n'est associé à cette adresse email."
          : error instanceof Error
            ? error.message
            : "Impossible de retrouver l'établissement"
      );
      setManualMode(true);
    } finally {
      setResolving(false);
    }
  };

  const handleManualContinue = async () => {
    if (!(await trigger("tenantId"))) return;
    const id = getValues("tenantId");
    setTenantId(id);
    await loadIdentite(id);
    setStep("password");
  };

  const onSubmit = async (data: FormData) => {
    setLoading(true);
    try {
      setTenantId(data.tenantId);
      const user = await login(data.email, data.password);
      toast.success(`Bienvenue, ${user.name || user.email}`);
      router.push("/dashboard");
    } catch (error) {
      toast.error(
        error instanceof Error ? error.message : "Identifiants incorrects ou compte inexistant"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-background p-4">
      <div className="w-full max-w-md">
        <div className="mb-8 flex flex-col items-center">
          <div className="mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-blue-600 p-3 shadow-lg">
            <GraduationCap className="h-8 w-8 text-white" />
          </div>
          <h1 className="text-2xl font-bold text-slate-900">NexusGesti</h1>
          <p className="mt-1 text-sm text-slate-500">Console socle</p>
        </div>

        <Card className="shadow-lg">
          <CardHeader>
            <CardTitle>Connexion</CardTitle>
            <CardDescription>
              {step === "email"
                ? "Renseignez votre email pour retrouver votre établissement."
                : "Saisissez votre mot de passe pour vous connecter."}
            </CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
              {step === "email" ? (
                <>
                  <div className="space-y-2">
                    <Label htmlFor="email">Adresse email</Label>
                    <Input
                      id="email"
                      type="email"
                      placeholder="demo.admin@nexusgesti.com"
                      autoComplete="email"
                      {...register("email")}
                    />
                    {errors.email && (
                      <p className="text-xs text-red-500">{errors.email.message}</p>
                    )}
                  </div>

                  {manualMode ? (
                    <div className="space-y-2">
                      <Label htmlFor="tenantId">Identifiant établissement (UUID)</Label>
                      <Input
                        id="tenantId"
                        type="text"
                        placeholder="11111111-2222-3333-4444-555555555570"
                        {...register("tenantId", {
                          validate: (value) =>
                            isValidUuid(value) || "Identifiant tenant (UUID) invalide",
                        })}
                      />
                      {errors.tenantId && (
                        <p className="text-xs text-red-500">{errors.tenantId.message}</p>
                      )}
                    </div>
                  ) : null}

                  <Button
                    type="button"
                    className="w-full"
                    disabled={resolving}
                    onClick={manualMode ? handleManualContinue : handleContinue}
                  >
                    {resolving && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                    Continuer
                  </Button>

                  <button
                    type="button"
                    onClick={() => setManualMode((value) => !value)}
                    className="block w-full text-center text-xs text-slate-400 hover:text-slate-600"
                  >
                    {manualMode
                      ? "Rechercher mon établissement par email"
                      : "Saisir l'identifiant de l'établissement manuellement"}
                  </button>
                </>
              ) : (
                <>
                  <div className="flex items-center justify-between rounded-lg border border-slate-200 bg-slate-50 px-3 py-2">
                    <div className="min-w-0">
                      <p className="truncate text-sm font-medium text-slate-900">
                        {etablissement?.denomination ?? "Établissement"}
                      </p>
                      <p className="truncate text-xs text-slate-500">
                        {etablissement?.code ?? getValues("tenantId")}
                        {etablissement?.profil ? ` · ${etablissement.profil}` : ""}
                      </p>
                    </div>
                    <Button
                      type="button"
                      variant="ghost"
                      size="sm"
                      onClick={() => {
                        setStep("email");
                        setEtablissement(null);
                      }}
                    >
                      <ArrowLeft className="mr-1 h-3.5 w-3.5" />
                      Changer
                    </Button>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="email-display">Adresse email</Label>
                    <Input id="email-display" type="email" value={getValues("email")} disabled />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="password">Mot de passe</Label>
                    <div className="relative">
                      <Input
                        id="password"
                        type={showPassword ? "text" : "password"}
                        placeholder="••••••••"
                        autoComplete="current-password"
                        {...register("password")}
                      />
                      <button
                        type="button"
                        onClick={() => setShowPassword(!showPassword)}
                        className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
                      >
                        {showPassword ? (
                          <EyeOff className="h-4 w-4" />
                        ) : (
                          <Eye className="h-4 w-4" />
                        )}
                      </button>
                    </div>
                    {errors.password && (
                      <p className="text-xs text-red-500">{errors.password.message}</p>
                    )}
                  </div>

                  <Button type="submit" className="w-full" disabled={loading}>
                    {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                    Se connecter
                  </Button>
                </>
              )}
            </form>

            <p className="mt-6 text-center text-xs leading-relaxed text-slate-400">
              Comptes de test : <br />
              ADMIN — demo.admin@nexusgesti.com · CUSTOMER — demo.customer@nexusgesti.com
            </p>
            <p className="mt-4 text-center text-sm text-slate-500">
              Pas encore de compte ?{" "}
              <Link href="/signup" className="font-medium text-blue-600 hover:underline">
                Créer un établissement
              </Link>
            </p>
          </CardContent>
        </Card>

        <p className="mt-6 text-center text-xs text-slate-400">
          <Link href="/" className="hover:text-slate-600">
            ← Retour à l&apos;accueil
          </Link>
        </p>
      </div>
    </div>
  );
}
