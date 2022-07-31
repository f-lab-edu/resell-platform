package flab.resellPlatform.common.utils;

public final class UserUtils {
    private UserUtils() {}
    public static final String normalizePhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("[-]", "");
    }
}
