package kpfu.itis.kasimov.controllers;

import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.services.UserService;
import kpfu.itis.kasimov.util.CloudinaryUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService     userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String showProfile(
            Model model,
            @AuthenticationPrincipal(expression = "user") User current
    ) {
        model.addAttribute("user", current);
        return "profile/show";
    }

    @PostMapping
    public String updateProfile(
            @ModelAttribute("user") User form,
            @RequestParam("avatar") MultipartFile file,
            @RequestParam(value = "confirmPassword", required = false) String confirm,
            @AuthenticationPrincipal(expression = "user") User current,   // ***
            Model model
    ) {
        if (!file.isEmpty()) {
            try {
                current.setAvatarUrl(CloudinaryUtil.upload(file));
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Ошибка загрузки аватара: " + e.getMessage());
                model.addAttribute("user", current);
                return "profile/show";
            }
        }

        current.setName(form.getName());
        current.setEmail(form.getEmail());

        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            if (confirm == null || !form.getPassword().equals(confirm)) {
                model.addAttribute("errorMessage", "Пароль и подтверждение не совпадают");
                model.addAttribute("user", current);
                return "profile/show";
            }
            current.setPassword(passwordEncoder.encode(form.getPassword()));
        }

        userService.update(current);
        model.addAttribute("successMessage", "Профиль обновлён");
        model.addAttribute("user", current);
        return "profile/show";
    }
}
