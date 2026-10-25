package com.nexusgesti.assessment.adapter.in.web;

import java.math.BigDecimal;
import java.util.UUID;

public record SaisirNoteRequest(UUID eleveId, BigDecimal valeur) {
}
