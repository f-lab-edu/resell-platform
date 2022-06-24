package flab.resellPlatform.exception.user;

public class UserInfoNotFoundException extends RuntimeException{
    public UserInfoNotFoundException() {
    }

    public UserInfoNotFoundException(String message) {
        super(message);
    }
}
