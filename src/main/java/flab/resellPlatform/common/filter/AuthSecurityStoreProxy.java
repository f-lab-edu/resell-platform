package flab.resellPlatform.common.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthSecurityStoreProxy implements AuthenticationStoreProxy {
    @Override
    public void storeAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
