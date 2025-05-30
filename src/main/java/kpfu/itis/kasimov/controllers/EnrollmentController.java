package kpfu.itis.kasimov.controllers;

import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.services.UserCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/enroll")
@RequiredArgsConstructor
public class EnrollmentController {
    private final UserCourseService userCourseService;

    @PostMapping
    public String enroll(@RequestParam Integer courseId, Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        userCourseService.enroll(user.getId(), courseId);
        return "redirect:/courses/" + courseId;
    }

    @PostMapping("/leave")
    public String leave(@RequestParam Integer courseId, Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        userCourseService.leave(user.getId(), courseId);
        return "redirect:/courses/" + courseId;
    }

    @PostMapping("/remove")
    public String removeStudent(@RequestParam Integer courseId, @RequestParam Integer studentId) {
        userCourseService.removeStudent(studentId, courseId);
        return "redirect:/courses/" + courseId;
    }
}
