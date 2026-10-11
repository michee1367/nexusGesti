package com.nexusgesti.fees.domain.model;

public class ConditionReduction {

    private String critere;
    private String operateur;
    private String valeur;
    private boolean cumulable;

    public boolean estSatisfaite(ContexteReduction contexte) {
        if (contexte == null) {
            return false;
        }
        String reel = switch (critere) {
            case "niveau" -> contexte.getNiveau();
            case "classe" -> contexte.getClasse() == null ? null : contexte.getClasse().toString();
            case "nombreEnfantsScolaires" -> contexte.getNombreEnfantsScolaires() == null
                    ? null : contexte.getNombreEnfantsScolaires().toString();
            case "statutSocial" -> contexte.getStatutSocial();
            case "bourse" -> Boolean.toString(contexte.isBourse());
            case "inscriptionTardive" -> Boolean.toString(contexte.isInscriptionTardive());
            case "eleveRedoublant" -> Boolean.toString(contexte.isEleveRedoublant());
            default -> null;
        };
        if (reel == null) {
            return false;
        }
        return comparer(reel);
    }

    private boolean comparer(String reel) {
        String attendu = valeur;
        String operateurEffectif = operateur == null ? "=" : operateur;
        if (estNumerique(reel) && estNumerique(attendu)) {
            java.math.BigDecimal gauche = new java.math.BigDecimal(reel);
            java.math.BigDecimal droite = new java.math.BigDecimal(attendu);
            int c = gauche.compareTo(droite);
            return switch (operateurEffectif) {
                case "=", "==" -> c == 0;
                case "!=" -> c != 0;
                case ">" -> c > 0;
                case ">=" -> c >= 0;
                case "<" -> c < 0;
                case "<=" -> c <= 0;
                default -> false;
            };
        }
        return switch (operateurEffectif) {
            case "=", "==" -> reel.equalsIgnoreCase(attendu);
            case "!=" -> !reel.equalsIgnoreCase(attendu);
            default -> false;
        };
    }

    private boolean estNumerique(String valeur) {
        if (valeur == null || valeur.isBlank()) {
            return false;
        }
        try {
            new java.math.BigDecimal(valeur);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public String getCritere() { return critere; }
    public void setCritere(String critere) { this.critere = critere; }
    public String getOperateur() { return operateur; }
    public void setOperateur(String operateur) { this.operateur = operateur; }
    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }
    public boolean isCumulable() { return cumulable; }
    public void setCumulable(boolean cumulable) { this.cumulable = cumulable; }
}
