package flab.resellPlatform.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JWTResponse {
    final private String accessToken;
    final private String tokenType;
    final private String expiresIn;
    final private String refreshToken;
}
