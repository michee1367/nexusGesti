package com.nexusgesti.exam.domain.model;

public enum DecisionType {
    ADMIS,
    ADMIS_MENTION,
    RATTAPAGE,
    REDOUBLANT,
    REJETE;

    public boolean estFavorable() {
        return this == ADMIS || this == ADMIS_MENTION;
    }

    public DecisionCode code() {
        return switch (this) {
            case ADMIS -> DecisionCode.P;
            case ADMIS_MENTION -> DecisionCode.M;
            case RATTAPAGE -> DecisionCode.R;
            case REDOUBLANT -> DecisionCode.V;
            case REJETE -> DecisionCode.E;
        };
    }

    public String libelle() {
        return switch (this) {
            case ADMIS -> "Admis";
            case ADMIS_MENTION -> "Admis avec mention";
            case RATTAPAGE -> "Admis au rattrapage";
            case REDOUBLANT -> "Redoublant";
            case REJETE -> "Rejete";
        };
    }

    public String consequenceInscription() {
        return switch (this) {
            case ADMIS, ADMIS_MENTION -> "INSCRIPTION_NIVEAU_SUPERIEUR";
            case RATTAPAGE -> "INSCRIPTION_SOUS_CONDITION";
            case REDOUBLANT -> "REINSCRIPTION_MEME_NIVEAU";
            case REJETE -> "EXCLUSION";
        };
    }
}
