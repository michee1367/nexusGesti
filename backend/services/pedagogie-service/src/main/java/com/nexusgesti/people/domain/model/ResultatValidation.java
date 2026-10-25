package com.nexusgesti.people.domain.model;

import java.util.ArrayList;
import java.util.List;

public class ResultatValidation {

    private final boolean valide;
    private final List<String> violations;

    public ResultatValidation(boolean valide, List<String> violations) {
        this.valide = valide;
        this.violations = violations == null ? new ArrayList<>() : new ArrayList<>(violations);
    }

    public boolean estValide() {
        return valide;
    }

    public List<String> getViolations() {
        return violations;
    }
}
