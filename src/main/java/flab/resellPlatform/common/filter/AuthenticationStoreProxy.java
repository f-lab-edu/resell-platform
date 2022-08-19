package flab.resellPlatform.common.filter;

import org.springframework.security.core.Authentication;

public interface AuthenticationStoreProxy {

    public void storeAuthentication(Authentication authentication);
}
