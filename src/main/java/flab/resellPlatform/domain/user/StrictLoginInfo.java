package flab.resellPlatform.domain.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StrictLoginInfo {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    @Email
    private String email;
}
