package kpfu.itis.kasimov.security;

import kpfu.itis.kasimov.models.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.*;

/**
 * Универсальный principal: один и тот же класс работает
 * как для form-login, так и для OAuth2-login.
 */
@Getter
public class CustomUserDetails
        implements UserDetails, OAuth2User, Serializable {

    private static final long serialVersionUID = 1L;

    private final User user;                       // сущность из БД
    private Map<String, Object> attributes =       // атрибуты OAuth2 (пустые при form-login)
            Collections.emptyMap();

    /* ----------  КОНСТРУКТОРЫ  ---------- */

    /**
     * Используется провайдером form-login
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Используется CustomOAuth2UserService
     */
    public CustomUserDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        if (attributes != null) {
            this.attributes = attributes;
        }
    }

    /* ----------  UserDetails  ---------- */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /* ----------  OAuth2User  ---------- */

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * Как правило, имя показывается в UI; при отсутствии можно вернуть email
     */
    @Override
    public String getName() {
        return Optional.ofNullable(user.getName())
                .filter(n -> !n.isBlank())
                .orElse(user.getEmail());
    }

    /* ----------  Доп. методы для контроллеров / шаблонов ---------- */

    public String getAvatarUrl() {
        return user.getAvatarUrl();
    }

    // сохранённый геттер, чтобы не менять существующие вызовы
    public User getPerson() {
        return user;
    }

    // альтернативный геттер — вдруг где-то используется
    public User getUser() {
        return user;
    }
}
