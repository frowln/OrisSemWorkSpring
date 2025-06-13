package kpfu.itis.kasimov.controllers;

import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.security.SecurityUtil;
import kpfu.itis.kasimov.services.UserCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/enroll")
@RequiredArgsConstructor
public class EnrollmentController {

    private final UserCourseService userCourseService;

    @PostMapping
    public String enroll(@RequestParam Integer courseId,
                         Authentication authentication) {

        User user = SecurityUtil.currentUser(authentication);
        if (user == null) return "redirect:/auth/login";

        userCourseService.enroll(user.getId(), courseId);
        return "redirect:/courses/" + courseId;
    }

    @PostMapping("/leave")
    public String leave(@RequestParam Integer courseId,
                        Authentication authentication) {

        User user = SecurityUtil.currentUser(authentication);
        if (user == null) return "redirect:/auth/login";

        userCourseService.leave(user.getId(), courseId);
        return "redirect:/courses/" + courseId;
    }

    /* «выгнать студента» вызывается учителем,
       личность здесь не проверяем — логика осталась прежней */
    @PostMapping("/remove")
    public String removeStudent(@RequestParam Integer courseId,
                                @RequestParam Integer studentId) {

        userCourseService.removeStudent(studentId, courseId);
        return "redirect:/courses/" + courseId;
    }
}
