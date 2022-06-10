package flab.resellPlatform.common.response.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateAccountSuccess {

    @JsonProperty
    private String username;

    @JsonProperty
    private String name;

}
