package kpfu.itis.kasimov.util;

import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public UserValidator(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        try {
            customUserDetailsService.loadUserByUsername(user.getEmail());
        } catch (UsernameNotFoundException ignored) {
            return;
        }

        errors.rejectValue("email", "", "Человек с таким email пользователя уже существует");

    }
}
