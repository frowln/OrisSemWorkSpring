package kpfu.itis.kasimov.security;

import kpfu.itis.kasimov.models.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomOidcUser implements OidcUser, UserDetails {

    private final User user;      // сущность из БД
    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;

    public CustomOidcUser(User user, OidcIdToken idToken, OidcUserInfo userInfo) {
        this.user = user;
        this.idToken = idToken;
        this.userInfo = userInfo;
    }

    /* ----------  OidcUser  ---------- */

    /**
     * обязательный из OAuth2User
     */
    @Override
    public Map<String, Object> getAttributes() {
        return getClaims();              // подойдёт то же самое
    }

    @Override
    public Map<String, Object> getClaims() {
        return idToken.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }

    @Override
    public String getName() {
        String name = user.getName();
        return (name != null && !name.isBlank()) ? name : user.getEmail();
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

    /* ----------  Удобные геттеры для шаблонов ---------- */
    public String getAvatarUrl() {
        return user.getAvatarUrl();
    }
}
