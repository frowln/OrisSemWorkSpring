package kpfu.itis.kasimov.controllers;

import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.security.CustomUserDetails;
import kpfu.itis.kasimov.services.CourseService;
import kpfu.itis.kasimov.services.UserCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final CourseService courseService;
    private final UserCourseService userCourseService;

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        model.addAttribute("courses", courseService.findAll());
        if (principal != null) {
            Authentication authentication = (Authentication) principal;
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = customUserDetails.getPerson();  // <-- вот здесь
            model.addAttribute("enrolledCourses", userCourseService.getEnrolledCourses(user.getId()));
        }
        return "home";
    }

}
