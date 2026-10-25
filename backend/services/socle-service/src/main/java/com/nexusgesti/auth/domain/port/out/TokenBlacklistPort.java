package com.nexusgesti.auth.domain.port.out;

public interface TokenBlacklistPort {

    void blacklist(String jti, long ttlSeconds);

    boolean isBlacklisted(String jti);
}
