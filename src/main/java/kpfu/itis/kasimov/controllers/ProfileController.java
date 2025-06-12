package kpfu.itis.kasimov.controllers;

import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.security.CustomUserDetails;
import kpfu.itis.kasimov.services.UserService;
import kpfu.itis.kasimov.util.CloudinaryUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String showProfile(Model model, Principal principal) {
        CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        User user = userDetails.getPerson();
        model.addAttribute("user", user);
        return "profile/show";
    }

    @PostMapping
    public String updateProfile(
            @ModelAttribute("user") User updated,
            @RequestParam("avatar") MultipartFile file,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            Principal principal,
            Model model
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        User user = userDetails.getPerson();

        // Обновляем аватарку
        if (!file.isEmpty()) {
            try {
                String avatarUrl = CloudinaryUtil.upload(file);
                user.setAvatarUrl(avatarUrl);
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Ошибка загрузки аватара: " + e.getMessage());
                model.addAttribute("user", user);
                return "profile/show";
            }
        }

        // Обновляем имя/email
        user.setName(updated.getName());
        user.setEmail(updated.getEmail());

        // Обновляем пароль — только если пользователь его ввёл
        if (updated.getPassword() != null && !updated.getPassword().isEmpty()) {
            if (confirmPassword == null || !updated.getPassword().equals(confirmPassword)) {
                model.addAttribute("errorMessage", "Пароль и подтверждение не совпадают");
                model.addAttribute("user", user);
                return "profile/show";
            }
            String hashedPassword = passwordEncoder.encode(updated.getPassword());
            user.setPassword(hashedPassword);
        }

        // Сохраняем изменения
        userService.update(user);

        model.addAttribute("successMessage", "Профиль обновлён");
        model.addAttribute("user", user);
        return "profile/show";
    }

}
