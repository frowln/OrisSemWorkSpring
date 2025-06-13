package kpfu.itis.kasimov.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.repositories.UserRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public User findByName(String name) {
        return userRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User register(User user) {
        return userRepository.save(user);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public void updatePassword(Integer userId, String rawPassword) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByEmailOrNull(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
