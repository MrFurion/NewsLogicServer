package by.clevertec.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContext {
    public static String getUserNameFromContext() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
