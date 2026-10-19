package com.nexusgesti.tenant.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.tenant.domain.TenantException;
import com.nexusgesti.tenant.domain.model.Etablissement;
import com.nexusgesti.tenant.domain.model.ProfilEtablissement;
import com.nexusgesti.tenant.domain.port.in.EtablissementUseCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserCreatedConsumerTest {

    private static final UUID TENANT_ID = UUID.fromString("11111111-2222-3333-4444-555555555556");

    private final EtablissementUseCase etablissementUseCase = mock(EtablissementUseCase.class);
    private final UserCreatedConsumer consumer =
            new UserCreatedConsumer(etablissementUseCase, new ObjectMapper());

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void creeEtEtablissementDepuisUserCreated() {
        String message = """
                {"eventType":"user.created","payload":{"tenantId":"11111111-2222-3333-4444-555555555556",
                "email":"owner@test.com","etablissement":{"denomination":"École Test",
                "code":"ECO-01","profil":"PRIMAIRE"}}}
                """;

        consumer.consume(message);

        ArgumentCaptor<Etablissement> captor = ArgumentCaptor.forClass(Etablissement.class);
        verify(etablissementUseCase).creer(eq(TENANT_ID), captor.capture());
        Etablissement e = captor.getValue();
        assertThat(e.getDenomination()).isEqualTo("École Test");
        assertThat(e.getCode()).isEqualTo("ECO-01");
        assertThat(e.getProfil()).isEqualTo(ProfilEtablissement.PRIMAIRE);
    }

    @Test
    void evenementRejoueIgnoreSansErreur() {
        when(etablissementUseCase.creer(any(), any()))
                .thenThrow(TenantException.tenantAlreadyExists(TENANT_ID));
        String message = """
                {"eventType":"user.created","payload":{"tenantId":"11111111-2222-3333-4444-555555555556",
                "etablissement":{"denomination":"École Test","code":"ECO-01","profil":"PRIMAIRE"}}}
                """;

        assertThatCode(() -> consumer.consume(message)).doesNotThrowAnyException();
    }

    @Test
    void autreEvenementIgnore() {
        consumer.consume("{\"eventType\":\"tenant.updated\",\"payload\":{}}");
        verify(etablissementUseCase, never()).creer(any(), any());
    }

    @Test
    void sansEtablissementAucuneCreation() {
        String message = """
                {"eventType":"user.created","payload":{"tenantId":"11111111-2222-3333-4444-555555555556"}}
                """;
        consumer.consume(message);
        verify(etablissementUseCase, never()).creer(any(), any());
    }
}
