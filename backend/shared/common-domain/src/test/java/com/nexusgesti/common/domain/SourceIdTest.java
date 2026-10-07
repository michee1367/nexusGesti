package com.nexusgesti.common.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SourceIdTest {

    private static final UUID EXAMEN_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test
    void cleCompositePourHopitalActeFacturable() {
        SourceId sourceId = SourceId.of("hopital-plateau-technique-service", EXAMEN_ID);
        String cle = sourceId.compositeWith(CodeActe.of("LABO_GLYCEMIE"));

        assertThat(cle).isEqualTo(
                "hopital-plateau-technique-service:11111111-1111-1111-1111-111111111111:LABO_GLYCEMIE");
    }

    @Test
    void refuseCodeActeVide() {
        SourceId sourceId = SourceId.of("hopital-plateau-technique-service", EXAMEN_ID);

        assertThatThrownBy(() -> sourceId.compositeWith(CodeActe.of("")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void refuseSourceIdIncomplet() {
        assertThatThrownBy(() -> SourceId.of(" ", EXAMEN_ID))
                .isInstanceOf(IllegalArgumentException.class);
    }
}