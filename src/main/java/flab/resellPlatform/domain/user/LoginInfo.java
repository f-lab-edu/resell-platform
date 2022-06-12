package flab.resellPlatform.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class LoginInfo {
    @Size(min = 1, max = 20, message = "아이디 길이가 맞지 않습니다.")
    @NotNull
    String username;
    @NotBlank
    String password;
}