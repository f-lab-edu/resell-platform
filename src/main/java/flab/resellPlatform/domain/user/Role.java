package flab.resellPlatform.domain.user;

public final class Role {
    public static final String USER = "hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')";
    public static final String ADMIN = "hasRole('ROLE_ADMIN')";
}
