package com.nexusgesti.reporting.domain.port.out;

import com.nexusgesti.reporting.domain.model.ReportRequest;

public interface ReportGeneratorPort {

    String generate(ReportRequest request);
}
