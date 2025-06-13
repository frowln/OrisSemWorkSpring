package kpfu.itis.kasimov.security;

import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OidcUser loadUser(OidcUserRequest request) {
        // стандартный Spring-овский пользователь
        OidcUser googleUser = super.loadUser(request);

        /* ----- достаём, что нужно ----- */
        String email = googleUser.getEmail();
        String name = googleUser.getFullName();                 // null → можно fallback на email
        String pic = (String) googleUser.getClaims().get("picture");

        /* ----- создаём или берём из базы ----- */
        User user = userService.findByEmailOrNull(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setName(name != null ? name : email);
            user.setAvatarUrl(pic);
            user.setRole("ROLE_STUDENT");
            // ставим случайный пароль, всё равно человек логинится через Google
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            userService.save(user);
        }

        /* ----- оборачиваем в своего principal-а ----- */
        return new CustomOidcUser(user, googleUser.getIdToken(), googleUser.getUserInfo());
    }
}
