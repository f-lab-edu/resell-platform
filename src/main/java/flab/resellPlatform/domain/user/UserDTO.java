package flab.resellPlatform.domain.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static flab.resellPlatform.domain.user.UserDTO.errorMessage.emailFormError;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    public static class errorMessage {
        public static final String emailFormError = "write in email form";
        public static final String usernameDuplication = "username already exists";
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
    @Email(message = emailFormError)
    private String email;

    @NotBlank
    private String shoeSize;

}
