package com.nexusgesti.tenant.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GrilleNotation extends Grille {

    private List<Bareme> baremes = new ArrayList<>();
    private List<ReglePonderation> reglesPonderation = new ArrayList<>();
    private ConfigCascade configCascade;
    private List<SeuilDecision> seuilsDecision = new ArrayList<>();
    private List<RegleAssiduite> reglesAssiduite = new ArrayList<>();
    private List<Mention> mentions = new ArrayList<>();
    private List<RegleAppreciation> appreciations = new ArrayList<>();

    public Bareme getBaremeDefaut() {
        return baremes.stream().filter(Bareme::isDefaut).findFirst()
                .orElse(baremes.isEmpty() ? null : baremes.get(0));
    }

    public Mention getMention(BigDecimal note) {
        return mentions.stream()
                .filter(m -> m.getSeuilMinimum() != null && note != null
                        && note.compareTo(m.getSeuilMinimum()) >= 0)
                .max((a, b) -> a.getSeuilMinimum().compareTo(b.getSeuilMinimum()))
                .orElse(null);
    }

    public SeuilDecision getSeuilDecision(BigDecimal moyenne) {
        return seuilsDecision.stream()
                .filter(s -> s.contient(moyenne, null, null, null))
                .max((a, b) -> Integer.compare(a.getPriorite(), b.getPriorite()))
                .orElse(null);
    }

    public ReglePonderation resoudreRegle(CandidatPonderation candidat) {
        return reglesPonderation.stream()
                .filter(r -> r.sApplique(candidat))
                .max((a, b) -> Integer.compare(a.nbCriteresRenseignes(), b.nbCriteresRenseignes()))
                .orElse(null);
    }

    public boolean estAbsenteToleree(int absences) {
        return reglesAssiduite.stream()
                .anyMatch(r -> r.getAbsencesMaxParPeriode() != null
                        && absences <= r.getAbsencesMaxParPeriode());
    }

    public GrilleNotation nouvelleVersion() {
        GrilleNotation copie = new GrilleNotation();
        copierCommun(copie);
        copie.baremes = new ArrayList<>(this.baremes);
        copie.reglesPonderation = new ArrayList<>(this.reglesPonderation);
        copie.configCascade = this.configCascade;
        copie.seuilsDecision = new ArrayList<>(this.seuilsDecision);
        copie.reglesAssiduite = new ArrayList<>(this.reglesAssiduite);
        copie.mentions = new ArrayList<>(this.mentions);
        copie.appreciations = new ArrayList<>(this.appreciations);
        return copie;
    }

    public List<Bareme> getBaremes() { return baremes; }
    public void setBaremes(List<Bareme> baremes) { this.baremes = baremes == null ? new ArrayList<>() : baremes; }
    public List<ReglePonderation> getReglesPonderation() { return reglesPonderation; }
    public void setReglesPonderation(List<ReglePonderation> reglesPonderation) { this.reglesPonderation = reglesPonderation == null ? new ArrayList<>() : reglesPonderation; }
    public ConfigCascade getConfigCascade() { return configCascade; }
    public void setConfigCascade(ConfigCascade configCascade) { this.configCascade = configCascade; }
    public List<SeuilDecision> getSeuilsDecision() { return seuilsDecision; }
    public void setSeuilsDecision(List<SeuilDecision> seuilsDecision) { this.seuilsDecision = seuilsDecision == null ? new ArrayList<>() : seuilsDecision; }
    public List<RegleAssiduite> getReglesAssiduite() { return reglesAssiduite; }
    public void setReglesAssiduite(List<RegleAssiduite> reglesAssiduite) { this.reglesAssiduite = reglesAssiduite == null ? new ArrayList<>() : reglesAssiduite; }
    public List<Mention> getMentions() { return mentions; }
    public void setMentions(List<Mention> mentions) { this.mentions = mentions == null ? new ArrayList<>() : mentions; }
    public List<RegleAppreciation> getAppreciations() { return appreciations; }
    public void setAppreciations(List<RegleAppreciation> appreciations) { this.appreciations = appreciations == null ? new ArrayList<>() : appreciations; }
}