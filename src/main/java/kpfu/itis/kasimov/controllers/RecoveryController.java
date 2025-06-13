package kpfu.itis.kasimov.controllers;

import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.services.EmailService;
import kpfu.itis.kasimov.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/auth/recovery")
@RequiredArgsConstructor
public class RecoveryController {

    private final UserService userService;
    private final EmailService emailService;

    @GetMapping
    public String recoveryForm() {
        return "auth/recovery";
    }

    @PostMapping
    public String processRecovery(@RequestParam String email, Model model) {
        try {
            User user = userService.findByEmail(email);

            // Генерируем временный пароль
            String tempPassword = UUID.randomUUID().toString().substring(0, 8);

            // Обновляем пароль в базе
            userService.updatePassword(user.getId(), tempPassword);

            // Шлём письмо
            String subject = "Восстановление пароля | Chill Study";
            String body = "Здравствуйте, " + user.getName() + "!\n\nВаш временный пароль: " + tempPassword +
                          "\n\nПожалуйста, войдите с этим паролем и смените его в профиле.";

            emailService.sendEmail(user.getEmail(), subject, body);

            model.addAttribute("message", "Временный пароль отправлен на вашу почту.");
        } catch (UsernameNotFoundException e) {
            model.addAttribute("error", "Пользователь с таким email не найден.");
        }
        return "auth/recovery";
    }
}
