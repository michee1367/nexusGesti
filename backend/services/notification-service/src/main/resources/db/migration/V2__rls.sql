-- Isolation multi-tenant au niveau base.
ALTER TABLE notifications.notifications
    ADD COLUMN tenant_id UUID DEFAULT NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid;

CREATE INDEX IF NOT EXISTS idx_notifications_tenant ON notifications.notifications (tenant_id);

ALTER TABLE notifications.notifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE notifications.notifications FORCE ROW LEVEL SECURITY;
CREATE POLICY notifications_tenant ON notifications.notifications
    USING (tenant_id = NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid)
    WITH CHECK (tenant_id = NULLIF(COALESCE(current_setting('app.tenant_id', true), ''), '')::uuid);
