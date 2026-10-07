package com.nexusgesti.notification.domain.model;

public record NotificationTemplate(
        String code,
        Notification.Channel channel,
        String subject,
        String bodyTemplate
) {}
