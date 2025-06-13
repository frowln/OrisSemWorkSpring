package kpfu.itis.kasimov.controllers;

import kpfu.itis.kasimov.models.Course;
import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.security.SecurityUtil;
import kpfu.itis.kasimov.services.CourseService;
import kpfu.itis.kasimov.services.UserCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/my-chats")
@RequiredArgsConstructor
public class MyChatsController {

    private final CourseService courseService;
    private final UserCourseService userCourseService;

    @GetMapping
    public String myChats(Model model, Authentication authentication) {

        User user = SecurityUtil.currentUser(authentication);
        if (user == null) return "redirect:/auth/login";

        model.addAttribute("user", user);

        List<Course> chatCourses;
        if ("ROLE_TEACHER".equalsIgnoreCase(user.getRole())) {
            chatCourses = courseService.findByTeacherId(user.getId())
                    .stream()
                    .map(dto -> courseService.findById(dto.getId()))
                    .collect(Collectors.toList());
        } else {
            chatCourses = userCourseService.getEnrolledCourses(user.getId());
        }

        model.addAttribute("chatCourses", chatCourses);
        return "chat/my-chats";
    }
}
