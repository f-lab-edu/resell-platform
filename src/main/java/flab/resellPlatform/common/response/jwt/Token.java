package flab.resellPlatform.common.response.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@RequiredArgsConstructor
public class Token {

    @JsonProperty
    private final String value;

    @JsonProperty
    private final Date expiration;

}
