package flab.resellPlatform.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private long id;
    private String username;
    private String password;
    private String phoneNumber;
    private String name;
    private String nickname;
    private String email;
    private String shoeSize;

    /**
     * @param username
     * @param password
     * @param phoneNumber
     * @param name
     * @param nickname
     * @param email
     * @param shoeSize
     */
    public User(String username, String password, String phoneNumber, String name, String nickname, String email, String shoeSize) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.shoeSize = shoeSize;
    }
}
