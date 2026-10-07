package com.nexusgesti.auth.domain.port.out;

import com.nexusgesti.auth.domain.model.RegisteredUser;
import com.nexusgesti.auth.domain.port.in.AuthUseCase;

public interface UserRegistrationEventPublisher {

    void publishUserRegistered(RegisteredUser user, AuthUseCase.RegisterCommand command);
}
