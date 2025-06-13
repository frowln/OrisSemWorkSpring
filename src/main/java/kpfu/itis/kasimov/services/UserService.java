package kpfu.itis.kasimov.services;

import kpfu.itis.kasimov.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public User findByName(String name) {
        return userRepository.findByName(name);
    }

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

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(UserDTO::valueOf).toList();
    }

    @Transactional
    public void updatePassword(Integer userId, String rawPassword) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    }

    public User findByEmailOrNull(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    // 2. универсальный save (можно просто делегировать register/update)
    public User save(User user) {
        return userRepository.save(user);
    }
}
