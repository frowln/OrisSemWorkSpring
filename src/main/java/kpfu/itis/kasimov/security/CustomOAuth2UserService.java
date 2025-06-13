package kpfu.itis.kasimov.security;

import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService     userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) {
        OAuth2User oauth = new DefaultOAuth2UserService().loadUser(req);

        String email = oauth.getAttribute("email");
        if (email == null) throw new UsernameNotFoundException("Email not found");

        User user;
        try {
            user = userService.findByEmail(email);
        } catch (UsernameNotFoundException ex) {
            user = new User();
            user.setEmail(email);
            user.setName(oauth.getAttribute("name"));
            user.setAvatarUrl(oauth.getAttribute("picture"));
            user.setRole("ROLE_STUDENT");
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            user = userService.register(user);          // ← id гарантированно заполнен
        }

        return new CustomOAuth2User(user, oauth.getAttributes());
    }
}

