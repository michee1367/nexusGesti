package com.nexusgesti.tenant.domain.model;

import com.nexusgesti.tenant.domain.TenantException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GrilleVersionTest {

    private void grilleCommune(Grille g) {
        g.setId(UUID.randomUUID());
        g.setEtablissementId(UUID.randomUUID());
        g.setCode("G-NOTE");
        g.setLibelle("Grille");
        g.setProfil(ProfilEtablissement.SECONDAIRE);
        g.setVersion(3);
    }

    @Test
    void activerDepuisFigeeEstInterdit() {
        GrilleNotation g = new GrilleNotation();
        grilleCommune(g);
        g.figer();
        assertThrows(TenantException.class, g::activer);
    }

    @Test
    void activerPositionneLaDateEtLeDrapeau() {
        GrilleNotation g = new GrilleNotation();
        grilleCommune(g);
        g.activer();
        assertTrue(g.isActive());
        assertTrue(g.isModifiable());
        assertEquals(3, g.getVersion());
    }

    @Test
    void figerVerrouilleLaGrille() {
        GrilleNotation g = new GrilleNotation();
        grilleCommune(g);
        g.figer();
        assertTrue(g.estFigee());
        assertFalse(g.isModifiable());
        assertThrows(TenantException.class, g::figer);
    }

    @Test
    void nouvelleVersionIncrementeEtReactive() {
        GrilleNotation g = new GrilleNotation();
        grilleCommune(g);
        g.figer();
        GrilleNotation v = g.nouvelleVersion();
        assertEquals(4, v.getVersion());
        assertFalse(v.isActive());
        assertTrue(v.isModifiable());
        assertEquals(g.getCode(), v.getCode());
        assertEquals(g.getProfil(), v.getProfil());
    }

    @Test
    void nouvelleVersionDeStock() {
        GrilleStock stock = new GrilleStock();
        grilleCommune(stock);
        stock.setToleranceEcartInventairePct(BigDecimal.TEN);
        GrilleStock v = stock.nouvelleVersion();
        assertEquals(4, v.getVersion());
        assertEquals(0, BigDecimal.TEN.compareTo(v.getToleranceEcartInventairePct()));
        assertTrue(v.isModifiable());
    }

    @Test
    void nouvelleVersionDeConformite() {
        GrilleConformite c = new GrilleConformite();
        grilleCommune(c);
        c.setAccesTracable(true);
        GrilleConformite v = c.nouvelleVersion();
        assertEquals(4, v.getVersion());
        assertTrue(v.exigeAccesTrace());
    }
}