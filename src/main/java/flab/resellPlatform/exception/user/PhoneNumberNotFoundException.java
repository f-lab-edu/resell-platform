package flab.resellPlatform.exception.user;

public class PhoneNumberNotFoundException extends UserInfoNotFoundException{
    public PhoneNumberNotFoundException() {
    }

    public PhoneNumberNotFoundException(String message) {
        super(message);
    }
}
