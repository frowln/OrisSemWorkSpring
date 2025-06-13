package kpfu.itis.kasimov.util;

import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    @ModelAttribute
    public void addUserInfo(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomUserDetails userDetails) {
                User user = userDetails.getPerson();
                model.addAttribute("userName", user.getName());
                model.addAttribute("avatarUrl", user.getAvatarUrl());
            } else if (principal instanceof OAuth2User oAuth2User) {
                // Безопасно достаем name/email
                String name = oAuth2User.getAttribute("name");
                String email = oAuth2User.getAttribute("email");
                String avatar = oAuth2User.getAttribute("picture");

                // fallback — если name нет, то email покажем
                if (name == null || name.isBlank()) {
                    name = email != null ? email : "Unknown User";
                }

                model.addAttribute("userName", name);
                model.addAttribute("avatarUrl", avatar);
            }
        } else {
            // Для неавторизованных
            model.addAttribute("userName", null);
            model.addAttribute("avatarUrl", null);
        }
    }
}
