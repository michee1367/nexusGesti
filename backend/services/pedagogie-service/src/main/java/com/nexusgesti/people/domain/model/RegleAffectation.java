package com.nexusgesti.people.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class RegleAffectation {

    public static final String PRIMAIRE = "PRIMAIRE";
    public static final String SECONDAIRE = "SECONDAIRE";
    public static final String UNIVERSITAIRE = "UNIVERSITAIRE";

    public RegleAffectation() {
    }

    public ResultatValidation valider(String profil, UUID classeId, List<AffectationEnseignant> affectations) {
        List<String> violations = new ArrayList<>();
        List<AffectationEnseignant> affectationsClasse = pourClasse(classeId, affectations);
        long titulaires = affectationsClasse.stream().filter(AffectationEnseignant::isTitulaire).count();

        switch (normaliser(profil)) {
            case PRIMAIRE -> {
                if (affectationsClasse.size() != 1) {
                    violations.add("PRIMAIRE_EXIGE_UNE_SEULE_AFFECTATION");
                }
                if (titulaires != 1) {
                    violations.add("PRIMAIRE_EXIGE_UN_TITULAIRE");
                }
            }
            case SECONDAIRE -> {
                if (affectationsClasse.isEmpty()) {
                    violations.add("SECONDAIRE_EXIGE_AU_MOINS_UNE_AFFECTATION");
                }
                if (titulaires != 1) {
                    violations.add("SECONDAIRE_EXIGE_EXACTEMENT_UN_TITULAIRE");
                }
            }
            case UNIVERSITAIRE -> {
                if (titulaires != 0) {
                    violations.add("UNIVERSITAIRE_SANS_TITULAIRE_DE_CLASSE");
                }
            }
            default -> violations.add("PROFIL_INCONNU");
        }
        return new ResultatValidation(violations.isEmpty(), violations);
    }

    public boolean estTitulaireUnique(String profil, List<AffectationEnseignant> affectations) {
        long titulaires = affectations.stream().filter(AffectationEnseignant::isTitulaire).count();
        return titulaires == getNombreTitulairesRequis(profil);
    }

    public int getNombreTitulairesRequis(String profil) {
        return switch (normaliser(profil)) {
            case PRIMAIRE, SECONDAIRE -> 1;
            default -> 0;
        };
    }

    private List<AffectationEnseignant> pourClasse(UUID classeId, List<AffectationEnseignant> affectations) {
        if (affectations == null) {
            return List.of();
        }
        return affectations.stream()
                .filter(a -> classeId == null || classeId.equals(a.getClasseId()))
                .toList();
    }

    private String normaliser(String profil) {
        return profil == null ? "" : profil.trim().toUpperCase();
    }
}
