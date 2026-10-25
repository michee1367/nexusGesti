package com.nexusgesti.identity.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.identity.domain.port.in.UtilisateurUseCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class UserCreatedConsumerTest {

    private final UtilisateurUseCase utilisateurUseCase = mock(UtilisateurUseCase.class);
    private final UserCreatedConsumer consumer =
            new UserCreatedConsumer(utilisateurUseCase, new ObjectMapper());

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void creeLigneIdentiteDepuisUserCreated() {
        String message = """
                {"eventType":"user.created","payload":{"userId":"b9c247d6-65e9-4350-8a6d-26e55f2afbbb",
                "email":"owner@test.com","firstName":"Jean","lastName":"Dupont",
                "tenantId":"11111111-2222-3333-4444-555555555556"}}
                """;

        consumer.consume(message);

        ArgumentCaptor<UtilisateurUseCase.EnregistrerUtilisateurCommand> captor =
                ArgumentCaptor.forClass(UtilisateurUseCase.EnregistrerUtilisateurCommand.class);
        verify(utilisateurUseCase).enregistrerDepuisEvenement(captor.capture());
        var command = captor.getValue();
        assertThat(command.keycloakUserId()).isEqualTo(UUID.fromString("b9c247d6-65e9-4350-8a6d-26e55f2afbbb"));
        assertThat(command.email()).isEqualTo("owner@test.com");
        assertThat(command.nom()).isEqualTo("Dupont");
        assertThat(command.prenom()).isEqualTo("Jean");
    }

    @Test
    void autreEvenementIgnore() {
        consumer.consume("{\"eventType\":\"tenant.updated\",\"payload\":{}}");
        verify(utilisateurUseCase, never()).enregistrerDepuisEvenement(any());
    }

    @Test
    void sansUserIdAucuneCreation() {
        consumer.consume("""
                {"eventType":"user.created","payload":{"tenantId":"11111111-2222-3333-4444-555555555556"}}
                """);
        verify(utilisateurUseCase, never()).enregistrerDepuisEvenement(any());
    }
}
