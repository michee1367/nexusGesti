package com.nexusgesti.fees.adapter.in.web;

import com.nexusgesti.fees.domain.model.ContexteReduction;

import java.util.UUID;

public record RemiseRequest(UUID reductionId, ContexteReduction contexte) {
}