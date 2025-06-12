package kpfu.itis.kasimov.controllers;

import kpfu.itis.kasimov.models.Course;
import kpfu.itis.kasimov.models.Review;
import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.security.CustomUserDetails;
import kpfu.itis.kasimov.services.CourseService;
import kpfu.itis.kasimov.services.ReviewService;
import kpfu.itis.kasimov.services.UserCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final CourseService courseService;
    private final UserCourseService userCourseService;

    @PostMapping
    public String addReview(@RequestParam Integer courseId,
                            @RequestParam Integer rating,
                            @RequestParam String comment,
                            Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return "redirect:/login";
        }

        User user = userDetails.getPerson();

        // Проверяем, записан ли пользователь
        if (!userCourseService.isEnrolled(user.getId(), courseId)) {
            return "redirect:/courses/" + courseId + "?error=notEnrolled";
        }

        Course course = courseService.findById(courseId);

        Review review = new Review();
        review.setCourse(course);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        reviewService.save(review);

        return "redirect:/courses/" + courseId;
    }
}
