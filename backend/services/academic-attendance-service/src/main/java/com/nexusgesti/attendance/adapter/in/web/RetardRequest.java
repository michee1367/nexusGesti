package com.nexusgesti.attendance.adapter.in.web;

import java.util.UUID;

public record RetardRequest(UUID eleveId, Integer minutes) {
}
