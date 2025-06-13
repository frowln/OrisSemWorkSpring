package kpfu.itis.kasimov.security;

import kpfu.itis.kasimov.models.User;
import lombok.Getter;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

@Getter
public class CustomOAuth2User
        extends CustomUserDetails           // ← наследуемся!
        implements OAuth2User {

    private final Map<String, Object> attributes;

    public CustomOAuth2User(User user, Map<String, Object> attrs) {
        super(user);                        // ← передаём в родителя
        this.attributes = attrs;
    }

    /* --------- OAuth2User --------- */
    @Override public Map<String, Object> getAttributes() { return attributes; }
    @Override public String getName() { return getPerson().getName(); }
}
