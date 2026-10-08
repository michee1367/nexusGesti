package com.nexusgesti.tenant.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GrilleNotationTest {

    private GrilleNotation grille() {
        GrilleNotation g = new GrilleNotation();
        g.setId(UUID.randomUUID());
        g.setEtablissementId(UUID.randomUUID());
        return g;
    }

    private ReglePonderation regle(String matiere, ProfilEtablissement profil,
                                   Integer criteres) {
        ReglePonderation r = new ReglePonderation();
        r.setIdRegle(UUID.randomUUID());
        r.setActive(true);
        r.setMatiere(matiere);
        r.setProfil(profil);
        return r;
    }

    @Test
    void resoudreRegleChoisitLaPlusSpecifique() {
        CandidatPonderation candidat = new CandidatPonderation();
        candidat.setProfil(ProfilEtablissement.SECONDAIRE);
        candidat.setMatiere("MATHS");

        ReglePonderation generique = regle(null, null, 0);
        ReglePonderation parProfil = regle(null, ProfilEtablissement.SECONDAIRE, 1);
        ReglePonderation parMatiere = regle("MATHS", ProfilEtablissement.SECONDAIRE, 2);
        GrilleNotation g = grille();
        g.getReglesPonderation().add(generique);
        g.getReglesPonderation().add(parMatiere);
        g.getReglesPonderation().add(parProfil);

        assertEquals(parMatiere, g.resoudreRegle(candidat));
    }

    @Test
    void resoudreRegleIgnoreLesInactivesEtRetourneNull() {
        ReglePonderation inactive = regle(null, null, 0);
        inactive.setActive(false);
        GrilleNotation g = grille();
        g.getReglesPonderation().add(inactive);
        assertNull(g.resoudreRegle(new CandidatPonderation()));
    }

    @Test
    void getMentionRetientLaPlusElevee() {
        GrilleNotation g = grille();
        Mention passable = new Mention();
        passable.setCode("PASSABLE");
        passable.setSeuilMinimum(BigDecimal.valueOf(10));
        Mention bien = new Mention();
        bien.setCode("BIEN");
        bien.setSeuilMinimum(BigDecimal.valueOf(14));
        g.getMentions().add(passable);
        g.getMentions().add(bien);
        assertEquals("BIEN", g.getMention(BigDecimal.valueOf(15)).getCode());
        assertNull(g.getMention(BigDecimal.valueOf(9)));
    }

    @Test
    void getSeuilDecisionRetientLaPlusPrioritaire() {
        GrilleNotation g = grille();
        SeuilDecision haute = new SeuilDecision();
        haute.setPortee(TypePortee.GLOBALE);
        haute.setMoyenneMinimale(BigDecimal.valueOf(10));
        haute.setDecisionResultante(Decision.ADMIS);
        haute.setPriorite(2);
        SeuilDecision basse = new SeuilDecision();
        basse.setPortee(TypePortee.GLOBALE);
        basse.setMoyenneMinimale(BigDecimal.valueOf(8));
        basse.setDecisionResultante(Decision.RATTAPAGE);
        basse.setPriorite(1);
        g.getSeuilsDecision().add(basse);
        g.getSeuilsDecision().add(haute);
        assertEquals(Decision.ADMIS, g.getSeuilDecision(BigDecimal.valueOf(12)).getDecisionResultante());
    }
}