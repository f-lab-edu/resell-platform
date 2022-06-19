package flab.resellPlatform.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class StrictLoginInfo {
    private String username;
    private String password;
    private String phoneNumber;
    private String email;
}
