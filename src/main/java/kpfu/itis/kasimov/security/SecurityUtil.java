package kpfu.itis.kasimov.security;

import kpfu.itis.kasimov.models.User;
import org.springframework.security.core.Authentication;

public final class SecurityUtil {

    private SecurityUtil() {}

    public static User currentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object p = auth.getPrincipal();
        if (p instanceof CustomUserDetails cud) return cud.getPerson();
        if (p instanceof CustomOidcUser cod)   return cod.getUser();
        return null;
    }
}
