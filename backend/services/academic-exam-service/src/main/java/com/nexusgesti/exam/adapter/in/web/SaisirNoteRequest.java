package com.nexusgesti.exam.adapter.in.web;

import java.math.BigDecimal;
import java.util.UUID;

public record SaisirNoteRequest(UUID eleveId, BigDecimal valeur, UUID correcteur) {
}
