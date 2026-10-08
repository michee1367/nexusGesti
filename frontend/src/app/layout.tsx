import type { Metadata } from "next";
import { Toaster } from "sonner";
import { AuthProvider } from "@/components/providers/auth-provider";
import { TenantProvider } from "@/components/providers/tenant-provider";
import "./globals.css";

export const metadata: Metadata = {
  title: "NexusGesti — Console",
  description: "Console socle de la plateforme NexusGesti",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="fr">
      <body className="antialiased">
        <AuthProvider>
          <TenantProvider>
            {children}
            <Toaster position="top-right" richColors />
          </TenantProvider>
        </AuthProvider>
      </body>
    </html>
  );
}