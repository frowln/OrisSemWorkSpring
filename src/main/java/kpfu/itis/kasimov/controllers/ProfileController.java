package kpfu.itis.kasimov.controllers;

import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.services.UserService;
import kpfu.itis.kasimov.util.CloudinaryUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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

    @GetMapping
    public String showProfile(Model model, Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        model.addAttribute("user", user);
        return "profile/show";
    }

    @PostMapping
    public String updateProfile(@ModelAttribute("user") User updated, @RequestParam("avatar") MultipartFile file, Principal principal, Model model) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if (!file.isEmpty()) {
            String avatarUrl = CloudinaryUtil.upload(file);
            user.setAvatarUrl(avatarUrl);
        }
        user.setName(updated.getName());
        user.setEmail(updated.getEmail());
        if (!updated.getPassword().isEmpty()) {
            user.setPassword(updated.getPassword());
        }
        userService.update(user);
        model.addAttribute("success", "Профиль обновлён");
        return "profile/show";
    }
}

