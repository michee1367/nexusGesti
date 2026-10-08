"use client";

import { useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select } from "@/components/ui/select";
import {
  PageHeader,
  LoadingView,
  ErrorState,
  EmptyState,
} from "@/components/common/async-state";
import { useApi } from "@/hooks/use-api";
import { useAuth } from "@/components/providers/auth-provider";
import { useTenant } from "@/components/providers/tenant-provider";
import { notificationApi } from "@/services/api";

const channelLabels: Record<string, string> = {
  EMAIL: "Email",
  SMS: "SMS",
  PUSH: "Push",
};

export default function NotificationsPage() {
  const { tenantId } = useTenant();
  const { isAdmin, user } = useAuth();
  const notifications = useApi(notificationApi.list, [tenantId]);

  const [form, setForm] = useState({
    userId: "",
    channel: "EMAIL",
    recipientAddress: "",
    subject: "",
    templateCode: "",
    priority: "NORMAL",
  });
  const [busy, setBusy] = useState(false);

  if (!isAdmin) {
    return (
      <div>
        <PageHeader title="Notifications" description={(user?.roles ?? []).join(", ")} />
        <p className="text-sm text-slate-500">Page réservée aux ADMIN (votre profil : CUSTOMER).</p>
      </div>
    );
  }

  const send = async () => {
    setBusy(true);
    try {
      await notificationApi.send({
        userId: form.userId,
        channel: form.channel as never,
        recipientAddress: form.recipientAddress,
        subject: form.subject || undefined,
        templateCode: form.templateCode || undefined,
        priority: form.priority as never,
      });
      toast.success("Notification envoyée");
      setForm({ ...form, userId: "", recipientAddress: "", subject: "", templateCode: "" });
      notifications.reload();
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Une erreur est survenue");
    } finally {
      setBusy(false);
    }
  };

  return (
    <div>
      <PageHeader title="Notifications" description="Envoi manuel (ADMIN) et liste de vos notifications." />

      <Card className="mb-6">
        <CardHeader>
          <CardTitle className="text-base">Envoyer une notification</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-2 sm:grid-cols-3">
            <div className="space-y-1">
              <Label htmlFor="userId">Utilisateur (UUID)</Label>
              <Input
                id="userId"
                placeholder="UUID du destinataire"
                value={form.userId}
                onChange={(e) => setForm({ ...form, userId: e.target.value })}
              />
            </div>
            <div className="space-y-1">
              <Label htmlFor="channel">Canal</Label>
              <Select
                id="channel"
                value={form.channel}
                onChange={(e) => setForm({ ...form, channel: e.target.value })}
              >
                <option value="EMAIL">Email</option>
                <option value="SMS">SMS</option>
                <option value="PUSH">Push</option>
              </Select>
            </div>
            <div className="space-y-1">
              <Label htmlFor="priority">Priorité</Label>
              <Select
                id="priority"
                value={form.priority}
                onChange={(e) => setForm({ ...form, priority: e.target.value })}
              >
                <option value="LOW">LOW</option>
                <option value="NORMAL">NORMAL</option>
                <option value="HIGH">HIGH</option>
                <option value="CRITICAL">CRITICAL</option>
              </Select>
            </div>
            <div className="space-y-1 sm:col-span-2">
              <Label htmlFor="recipientAddress">Adresse du destinataire</Label>
              <Input
                id="recipientAddress"
                placeholder="email@exemple.com ou +243..."
                value={form.recipientAddress}
                onChange={(e) => setForm({ ...form, recipientAddress: e.target.value })}
              />
            </div>
            <div className="space-y-1">
              <Label htmlFor="templateCode">Template (optionnel)</Label>
              <Input
                id="templateCode"
                placeholder="welcome"
                value={form.templateCode}
                onChange={(e) => setForm({ ...form, templateCode: e.target.value })}
              />
            </div>
            <div className="space-y-1 sm:col-span-2">
              <Label htmlFor="subject">Sujet</Label>
              <Input
                id="subject"
                placeholder="Sujet du message"
                value={form.subject}
                onChange={(e) => setForm({ ...form, subject: e.target.value })}
              />
            </div>
          </div>
          <Button
            onClick={send}
            disabled={busy || !form.userId || !form.recipientAddress}
          >
            Envoyer
          </Button>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Vos notifications</CardTitle>
        </CardHeader>
        <CardContent>
          {notifications.loading ? (
            <LoadingView label="Chargement…" />
          ) : notifications.error ? (
            <ErrorState error={notifications.error} onRetry={notifications.reload} />
          ) : notifications.data?.length ? (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border text-left text-xs uppercase text-slate-500">
                    <th className="px-3 py-2">Canal</th>
                    <th className="px-3 py-2">Sujet</th>
                    <th className="px-3 py-2">Statut</th>
                    <th className="px-3 py-2">Priorité</th>
                    <th className="px-3 py-2">Créée le</th>
                  </tr>
                </thead>
                <tbody>
                  {notifications.data.map((notification) => (
                    <tr key={notification.id} className="border-b border-border">
                      <td className="px-3 py-2">
                        <Badge variant="secondary">{channelLabels[notification.channel] ?? notification.channel}</Badge>
                      </td>
                      <td className="px-3 py-2 font-medium text-slate-900">
                        {notification.subject || "—"}
                      </td>
                      <td className="px-3 py-2">
                        <Badge
                          variant={
                            notification.status === "SENT" || notification.status === "DELIVERED"
                              ? "default"
                              : notification.status === "FAILED"
                                ? "danger"
                                : "outline"
                          }
                        >
                          {notification.status}
                        </Badge>
                      </td>
                      <td className="px-3 py-2">{notification.priority}</td>
                      <td className="px-3 py-2 text-slate-500">
                        {new Date(notification.createdAt).toLocaleString("fr-FR")}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <EmptyState label="Aucune notification" />
          )}
        </CardContent>
      </Card>
    </div>
  );
}