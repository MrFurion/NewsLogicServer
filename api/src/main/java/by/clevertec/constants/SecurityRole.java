package by.clevertec.constants;

public class SecurityRole {
    public static final String ROLE_ADMIN = "hasRole('ROLE_ADMIN')";
    public static final String ROLE_VIEWER = "hasRole('ROLE_VIEWER')";
    public static final String ROLE_JOURNALIST = "hasRole('ROLE_JOURNALIST')";
    public static final String ROLE_SUBSCRIBER = "hasRole('ROLE_SUBSCRIBER')";
    public static final String ROLE_ADMIN_OR_VIEWER = "hasAnyRole('ROLE_ADMIN', 'ROLE_VIEWER')";
    public static final String ROLE_ADMIN_OR_SUBSCRIBER = "hasAnyRole('ROLE_ADMIN', 'ROLE_SUBSCRIBER')";
    public static final String ROLE_ADMIN_OR_JOURNALIST = "hasAnyRole('ROLE_ADMIN', 'ROLE_JOURNALIST')";
    public static final String ROLE_ADMIN_OR_JOURNALIST_OR_SUBSCRIBER = "hasAnyRole('ROLE_ADMIN', 'ROLE_JOURNALIST', 'ROLE_SUBSCRIBER')";
}
