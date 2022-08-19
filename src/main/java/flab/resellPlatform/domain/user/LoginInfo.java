package flab.resellPlatform.domain.user;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class LoginInfo {
    @Size(min = 1, max = 20, message = "아이디 길이는 최소 {min}자리, 최대 {max}자리로 구성돼야 합니다.")
    @NotNull
    String username;
    @NotBlank
    String password;
}