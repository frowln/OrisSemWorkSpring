package kpfu.itis.kasimov.security;

import kpfu.itis.kasimov.models.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.*;

@Getter
public class CustomUserDetails
        implements UserDetails, OAuth2User, Serializable {

    private static final long serialVersionUID = 1L;

    private final User user;                       // сущность из БД
    private Map<String, Object> attributes =       // атрибуты OAuth2 (пустые при form-login)
            Collections.emptyMap();

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public CustomUserDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        if (attributes != null) {
            this.attributes = attributes;
        }
    }

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

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return Optional.ofNullable(user.getName())
                .filter(n -> !n.isBlank())
                .orElse(user.getEmail());
    }

    public String getAvatarUrl() {
        return user.getAvatarUrl();
    }

    public User getPerson() {
        return user;
    }

    public User getUser() {
        return user;
    }
}
