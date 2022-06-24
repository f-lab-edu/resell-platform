package flab.resellPlatform.domain.user;

public final class Role {
    public static final String USER = "hasRole('ROLE_USER') or hasRole('ADMIN')";
    public static final String ADMIN = "hasRole('ADMIN')";
}
