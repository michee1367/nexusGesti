package com.nexusgesti.attendance.adapter.in.web;

import com.nexusgesti.attendance.domain.model.TypeAbsence;

import java.util.UUID;

public record AbsenceRequest(UUID eleveId, TypeAbsence typeAbsence) {
}
