package flab.resellPlatform.domain.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordInquiryForm {
    private String username;
    private String phoneNumber;
    private String email;
}
