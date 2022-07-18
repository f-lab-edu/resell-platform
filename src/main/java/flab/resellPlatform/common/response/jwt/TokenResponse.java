package flab.resellPlatform.common.response.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TokenResponse {

    @JsonProperty
    private final Token accessToken;

    @JsonProperty
    private final Token refreshToken;

}
