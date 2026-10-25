package com.nexusgesti.people.domain;

import java.util.List;
import java.util.UUID;

public class PeopleException extends RuntimeException {

    private final String code;
    private final List<String> violations;

    public PeopleException(String code, String message) {
        this(code, message, List.of());
    }

    public PeopleException(String code, String message, List<String> violations) {
        super(message);
        this.code = code;
        this.violations = violations == null ? List.of() : violations;
    }

    public String getCode() {
        return code;
    }

    public List<String> getViolations() {
        return violations;
    }

    public static PeopleException notFound(String type, UUID id) {
        return new PeopleException("NOT_FOUND", type + " introuvable : " + id);
    }

    public static PeopleException transitionInvalide(String from, String to) {
        return new PeopleException("TRANSITION_INVALIDE",
                "Transition de " + from + " vers " + to + " interdite");
    }

    public static PeopleException regleAffectation(List<String> violations) {
        return new PeopleException("REGLE_AFFECTATION",
                "Regle d affectation violee", violations);
    }

    public static PeopleException conflit(String code, String message) {
        return new PeopleException(code, message);
    }
}
