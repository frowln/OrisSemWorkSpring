package kpfu.itis.kasimov.services;

import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.repositories.UserRepository;
import kpfu.itis.kasimov.util.GenerateDefaultIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Приведение роли к виду ROLE_STUDENT или ROLE_TEACHER
        String role = user.getRole();
        if (role != null && (role.equals("student") || role.equals("teacher"))) {
            user.setRole("ROLE_" + role.toUpperCase());
        } else {
            // Если что-то пошло не так, можно задать роль по умолчанию
            user.setRole("ROLE_USER");
        }

        // Генерация дефолтного аватара, если пусто
        if (user.getAvatarUrl() == null || user.getAvatarUrl().isEmpty()) {
            user.setAvatarUrl(GenerateDefaultIcon.generateDefaultIcon(user.getName()));
        }

        userRepository.save(user);
    }
}
