package kpfu.itis.kasimov.controllers;

import kpfu.itis.kasimov.models.Course;
import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.security.CustomOAuth2User;
import kpfu.itis.kasimov.security.CustomUserDetails;
import kpfu.itis.kasimov.services.CourseService;
import kpfu.itis.kasimov.services.UserCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CourseService courseService;
    private final UserCourseService userCourseService;

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        return loadCoursesToModel(model, principal, courseService.findAll());
    }

    @GetMapping("/searchCourses")
    public String searchCourses(@RequestParam String query, Model model, Principal principal) {
        List<Course> results = courseService.searchCourses(query);
        return loadCoursesToModel(model, principal, results);
    }

    private String loadCoursesToModel(Model model, Principal principal, List<Course> courses) {
        model.addAttribute("courses", courses);

        if (principal instanceof Authentication authentication) {
            Object principalObj = authentication.getPrincipal();

            User user = null;

            if (principalObj instanceof CustomUserDetails userDetails) {
                user = userDetails.getPerson();
            } else if (principalObj instanceof CustomOAuth2User customOAuth2User) {
                user = customOAuth2User.getUser();
            }

            if (user != null) {
                model.addAttribute("userName", user.getName());
                model.addAttribute("avatarUrl", user.getAvatarUrl());

                List<Course> enrolledCourses = userCourseService.getEnrolledCourses(user.getId());
                model.addAttribute("enrolledCourses", enrolledCourses);
                model.addAttribute("enrolledCourseIds", enrolledCourses.stream()
                        .map(Course::getId)
                        .collect(Collectors.toList()));
            }
        }

        return "home";
    }

}
