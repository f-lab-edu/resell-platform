package flab.resellPlatform.domain.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Builder
@AllArgsConstructor
public final class UserEntity {
    private long id;

    private String username;

    private String password;

    private String phoneNumber;

    private String name;

    private String nickname;

    private String email;

    private String shoeSize;

    private String role;

    /**
     * @param username
     * @param password
     * @param phoneNumber
     * @param name
     * @param nickname
     * @param email
     * @param shoeSize
     * @param role
     */
    public UserEntity(String username, String password, String phoneNumber, String name, String nickname, String email, String shoeSize, String role) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.shoeSize = shoeSize;
        this.role = role;
    }
}
