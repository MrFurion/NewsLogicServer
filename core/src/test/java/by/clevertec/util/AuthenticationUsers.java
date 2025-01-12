package by.clevertec.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationUsers {

    public static void authenticationUsers(String userName) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(userName);

        org.springframework.security.core.context.SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }
}
