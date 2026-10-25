package com.nexusgesti.attendance.adapter.in.web;

import com.nexusgesti.attendance.domain.model.StatutPresence;

import java.util.UUID;

public record PointerRequest(UUID eleveId, StatutPresence statut) {
}
