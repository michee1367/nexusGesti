package com.nexusgesti.exam.domain.model;

import com.nexusgesti.exam.domain.ExamException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProcesVerbalTest {

    @Test
    void cycleDeVieRedactionSoumissionSignatureHomologation() {
        ProcesVerbal pv = new ProcesVerbal();
        pv.setId(UUID.randomUUID());
        pv.setReference("PV-0001");

        UUID redacteur = UUID.randomUUID();
        pv.rediger(redacteur);
        assertEquals(StatutPV.BROUILLON, pv.getStatut());
        assertEquals(redacteur, pv.getRedigePar());
        assertNotNull(pv.getDateRedaction());

        pv.soumettre();
        assertEquals(StatutPV.SOUMIS, pv.getStatut());

        pv.signer(UUID.randomUUID(), "SECRETAIRE");
        assertEquals(StatutPV.SIGNE, pv.getStatut());
        assertEquals(1, pv.getSignatures().size());

        pv.homologuer();
        assertEquals(StatutPV.HOMOLOGUE, pv.getStatut());
        assertNotNull(pv.getDateCloture());
    }

    @Test
    void soumettreHorsBrouillonEstRefuse() {
        ProcesVerbal pv = new ProcesVerbal();
        pv.setId(UUID.randomUUID());
        pv.rediger(UUID.randomUUID());
        pv.soumettre();
        assertThrows(ExamException.class, pv::soumettre);
    }

    @Test
    void signatureApresHomologationEstRefusee() {
        ProcesVerbal pv = new ProcesVerbal();
        pv.setId(UUID.randomUUID());
        pv.setReference("PV-0001");
        pv.rediger(UUID.randomUUID());
        pv.soumettre();
        pv.signer(UUID.randomUUID(), "SECRETAIRE");
        pv.homologuer();
        assertThrows(ExamException.class, () -> pv.signer(UUID.randomUUID(), "DIRECTEUR"));
    }

    @Test
    void estCompletExigeDirecteurEtSecretaire() {
        ProcesVerbal pv = new ProcesVerbal();
        pv.setId(UUID.randomUUID());
        pv.setReference("PV-0001");
        pv.rediger(UUID.randomUUID());
        assertFalse(pv.estComplet());

        pv.signer(UUID.randomUUID(), "SECRETAIRE");
        assertFalse(pv.estComplet());

        pv.signer(UUID.randomUUID(), "DIRECTEUR");
        assertTrue(pv.estComplet());
    }

    @Test
    void contesterExigeUnMotif() {
        ProcesVerbal pv = new ProcesVerbal();
        pv.setId(UUID.randomUUID());
        pv.setReference("PV-0001");
        pv.rediger(UUID.randomUUID());
        assertThrows(ExamException.class, () -> pv.contester(null));
        pv.contester("erreur de saisie");
        assertEquals(StatutPV.CONTESTE, pv.getStatut());
    }
}
