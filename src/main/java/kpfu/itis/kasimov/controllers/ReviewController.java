package kpfu.itis.kasimov.controllers;

import kpfu.itis.kasimov.models.Course;
import kpfu.itis.kasimov.models.Review;
import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.services.CourseService;
import kpfu.itis.kasimov.services.ReviewService;
import kpfu.itis.kasimov.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;
    private final CourseService courseService;

    @PostMapping
    public String addReview(@RequestParam Integer courseId, @RequestParam Integer rating, @RequestParam String comment, Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        Course course = courseService.findById(courseId);
        Review review = new Review();
        review.setCourse(course);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        reviewService.add(review);
        return "redirect:/courses/" + courseId;
    }
}
