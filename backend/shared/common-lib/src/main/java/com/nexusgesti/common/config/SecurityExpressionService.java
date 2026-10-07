package com.nexusgesti.common.config;

import com.nexusgesti.common.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("securityUtils")
public class SecurityExpressionService {

    @Value("${nexusgesti.security.transfer-mfa-required:true}")
    private boolean transferMfaRequired;

    public boolean isMfaVerified() {
        try {
            return SecurityUtils.isMfaVerified();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /** MFA pour transferts P2P — désactivable via nexusgesti.security.transfer-mfa-required */
    public boolean isTransferMfaVerified() {
        if (!transferMfaRequired) {
            return true;
        }
        return isMfaVerified();
    }
}
