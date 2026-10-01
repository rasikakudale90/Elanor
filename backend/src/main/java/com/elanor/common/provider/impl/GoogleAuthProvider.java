package com.elanor.common.provider.impl;

import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import com.elanor.common.provider.SocialAuthProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GoogleAuthProvider implements SocialAuthProvider {

    private static final Logger log = LoggerFactory.getLogger(GoogleAuthProvider.class);

    @Override
    public SocialUserProfile verifyToken(String idToken) {
        if (idToken == null || idToken.isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_CREDENTIALS, "Invalid Google ID token.");
        }

        log.info("[DEMO GOOGLE AUTH PROVIDER] Validating token [{}]", idToken);
        // For development/demo, allow simulated tokens or fallback to a demo profile
        String email = idToken.contains("@") ? idToken : "demo.user@elanor.com";
        return new SocialUserProfile(
                email,
                "Demo",
                "Customer",
                "https://images.unsplash.com/photo-1534528741775-53994a69daeb"
        );
    }
}
