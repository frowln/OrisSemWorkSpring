package kpfu.itis.kasimov.services;

import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.repositories.UserRepository;
import kpfu.itis.kasimov.util.GenerateDefaultIcon;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        String role = user.getRole();
        if (role != null && (role.equals("student") || role.equals("teacher"))) {
            user.setRole("ROLE_" + role.toUpperCase());
        } else {
            user.setRole("ROLE_USER");
        }

        if (user.getAvatarUrl() == null || user.getAvatarUrl().isEmpty()) {
            user.setAvatarUrl(GenerateDefaultIcon.generateDefaultIcon(user.getName()));
        }

        userRepository.save(user);
    }
}
