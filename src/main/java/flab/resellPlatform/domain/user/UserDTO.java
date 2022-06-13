package flab.resellPlatform.domain.user;

import lombok.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import static flab.resellPlatform.domain.user.UserDTO.errorMessage.EMAIL_FORM_ERROR;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class UserDTO {
    public final static class errorMessage {
        public static final String EMAIL_FORM_ERROR = "write in email form";
        public static final String USERNAME_DUPLICATION = "username already exists";
    }

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String name;

    @NotBlank
    private String nickname;

    @NotBlank
    @Email(message = EMAIL_FORM_ERROR)
    private String email;

    @NotBlank
    private String shoeSize;

}
