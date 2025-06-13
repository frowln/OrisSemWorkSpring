package kpfu.itis.kasimov.util;

import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.security.CustomUserDetails;
import kpfu.itis.kasimov.security.CustomOidcUser;          // <-- новый principal
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    @ModelAttribute
    public void addUserInfo(Model model, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("userName",  null);
            model.addAttribute("avatarUrl", null);
            return;
        }

        Object principal = authentication.getPrincipal();

        /* -------- form-login -------- */
        if (principal instanceof CustomUserDetails cud) {
            User u = cud.getPerson();
            model.addAttribute("userName",  u.getName());
            model.addAttribute("avatarUrl", u.getAvatarUrl());
            return;
        }

        /* -------- Google OIDC -------- */
        if (principal instanceof CustomOidcUser coidc) {           // наш класс
            User u = coidc.getUser();
            model.addAttribute("userName",  u.getName());
            model.addAttribute("avatarUrl", u.getAvatarUrl());
            return;
        }

        /* -------- fallback: чужой OidcUser (маловероятно) -------- */
        if (principal instanceof OidcUser oidc) {
            String name   = oidc.getFullName();
            String email  = oidc.getEmail();
            String avatar = (String) oidc.getClaims().get("picture");

            model.addAttribute("userName",
                    (name != null && !name.isBlank()) ? name : email);
            model.addAttribute("avatarUrl", avatar);
        }
    }
}
