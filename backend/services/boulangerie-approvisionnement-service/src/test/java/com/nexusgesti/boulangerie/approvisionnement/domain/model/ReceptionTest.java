package com.nexusgesti.boulangerie.approvisionnement.domain.model;

import com.nexusgesti.boulangerie.approvisionnement.domain.ApprovisionnementException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReceptionTest {

    private LigneReception ligne(String article, String commandee, String recue, boolean conforme) {
        LigneReception ligne = new LigneReception();
        ligne.setId(UUID.randomUUID());
        ligne.setArticleCode(article);
        ligne.setQuantiteCommandee(new BigDecimal(commandee));
        ligne.setQuantiteRecue(new BigDecimal(recue));
        ligne.setPrixAchat(new BigDecimal("1.00"));
        ligne.setConforme(conforme);
        if (!conforme) {
            ligne.setMotifNonConformite("DLC trop courte");
        }
        return ligne;
    }

    private Reception receptionAttendue() {
        Reception reception = new Reception();
        reception.setIdReception(UUID.randomUUID());
        reception.setNumero("REC-00001");
        reception.setCommandeId(UUID.randomUUID());
        reception.ajouterLigne(ligne("FARINE", "10", "10", true));
        return reception;
    }

    @Test
    void controleExigeStatutAttendu() {
        Reception reception = receptionAttendue();

        reception.controler();
        assertEquals(StatutReception.EN_CONTROLE, reception.getStatut());

        assertThrows(ApprovisionnementException.class, reception::controler);
    }

    @Test
    void accepterSansControleEstRefuse() {
        Reception reception = receptionAttendue();

        ApprovisionnementException ex = assertThrows(ApprovisionnementException.class,
                reception::accepter);
        assertEquals("TRANSITION_INVALIDE", ex.getCode());
    }

    @Test
    void accepterReceptionConformeEtComplete() {
        Reception reception = receptionAttendue();
        reception.controler();

        reception.accepter();

        assertEquals(StatutReception.ACCEPTEE, reception.getStatut());
        assertTrue(reception.estHonoree());
    }

    @Test
    void accepterReceptionNonConformeEstRefusee() {
        Reception reception = receptionAttendue();
        reception.getLignes().clear();
        reception.ajouterLigne(ligne("FARINE", "10", "10", false));
        reception.controler();

        ApprovisionnementException ex = assertThrows(ApprovisionnementException.class,
                reception::accepter);
        assertEquals("ETAT_INVALIDE", ex.getCode());
        assertEquals(StatutReception.EN_CONTROLE, reception.getStatut());
    }

    @Test
    void accepterReceptionPartielleRestePartielle() {
        Reception reception = receptionAttendue();
        reception.getLignes().clear();
        reception.ajouterLigne(ligne("FARINE", "10", "6", true));
        reception.controler();

        reception.accepter();

        assertEquals(StatutReception.PARTIELLE, reception.getStatut());
        assertTrue(reception.estHonoree());
    }

    @Test
    void refuserExigeUnMotifEtLeControle() {
        Reception reception = receptionAttendue();

        assertThrows(ApprovisionnementException.class, () -> reception.refuser("abc"));

        reception.controler();
        assertThrows(ApprovisionnementException.class, () -> reception.refuser(" "));

        reception.refuser("Marchandise abimee");
        assertEquals(StatutReception.REFUSEE, reception.getStatut());
        assertEquals("Marchandise abimee", reception.getMotif());
        assertFalse(reception.estHonoree());
    }

    @Test
    void ligneNonConformeSansMotifEstRefusee() {
        LigneReception ligne = ligne("SEL", "1", "1", false);
        ligne.setMotifNonConformite(null);

        assertThrows(ApprovisionnementException.class, ligne::verifier);
    }

    @Test
    void ligneQuantiteRecueSuperieureAuCommandeEstRefusee() {
        LigneReception ligne = ligne("SEL", "1", "2", true);

        assertThrows(ApprovisionnementException.class, ligne::verifier);
    }

    @Test
    void ligneQuantiteNulleEstRefusee() {
        LigneReception ligne = ligne("SEL", "1", "0", true);

        assertThrows(ApprovisionnementException.class, ligne::verifier);
    }

    @Test
    void ligneValideNeLeveRien() {
        assertDoesNotThrow(() -> ligne("SEL", "5", "5", true).verifier());
    }
}
