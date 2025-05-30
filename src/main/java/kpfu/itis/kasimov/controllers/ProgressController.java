package kpfu.itis.kasimov.controllers;

import kpfu.itis.kasimov.services.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/progress")
@RequiredArgsConstructor
public class ProgressController {
    private final ProgressService progressService;

    @GetMapping("/{userId}/{courseId}")
    public String showProgress(@PathVariable Integer userId, @PathVariable Integer courseId, Model model) {
        model.addAttribute("progress", progressService.getProgress(userId, courseId));
        return "progress/show";
    }
}