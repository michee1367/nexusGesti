package com.nexusgesti.assessment.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MoteurCalculMoyenne {

    public ResultatMoyenne calculer(List<Note> notes) {
        ResultatMoyenne resultat = new ResultatMoyenne();
        if (notes == null || notes.isEmpty()) {
            resultat.setNbNotes(0);
            resultat.setEffectif(0);
            return resultat;
        }
        resultat.setEffectif((int) notes.stream().map(Note::getEleveId).distinct().count());
        List<BigDecimal> valeurs = notes.stream()
                .filter(Note::comptePourMoyenne)
                .map(Note::getValeur)
                .toList();
        resultat.setNbNotes(valeurs.size());
        if (valeurs.isEmpty()) {
            return resultat;
        }
        BigDecimal somme = valeurs.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        resultat.setMoyenne(somme.divide(BigDecimal.valueOf(valeurs.size()), 2, RoundingMode.HALF_UP));
        resultat.setNoteMin(valeurs.stream().min(Comparator.naturalOrder()).orElse(null));
        resultat.setNoteMax(valeurs.stream().max(Comparator.naturalOrder()).orElse(null));
        return resultat;
    }

    public List<Classement> classer(Map<UUID, List<Note>> notesParEleve) {
        List<Classement> classement = new ArrayList<>();
        if (notesParEleve == null) {
            return classement;
        }
        notesParEleve.forEach((eleveId, notes) -> {
            BigDecimal moyenne = calculer(notes).getMoyenne();
            if (moyenne != null) {
                classement.add(new Classement(0, eleveId, moyenne));
            }
        });
        classement.sort(Comparator.comparing(Classement::getMoyenne).reversed()
                .thenComparing(c -> c.getEleveId().toString()));
        int rang = 1;
        for (Classement ligne : classement) {
            ligne.setRang(rang++);
        }
        return classement;
    }
}
