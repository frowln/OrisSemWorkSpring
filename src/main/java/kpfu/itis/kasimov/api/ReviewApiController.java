package kpfu.itis.kasimov.api;

import kpfu.itis.kasimov.dto.ReviewCreateDTO;
import kpfu.itis.kasimov.dto.ReviewDTO;
import kpfu.itis.kasimov.dto.ReviewUpdateDTO;
import kpfu.itis.kasimov.models.Course;
import kpfu.itis.kasimov.models.Review;
import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.security.CustomUserDetails;
import kpfu.itis.kasimov.services.CourseService;
import kpfu.itis.kasimov.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewApiController {

    private final ReviewService reviewService;
    private final CourseService courseService;

    // GET all reviews (optionally by courseId)
    @GetMapping
    @SecurityRequirement(name = "basicAuth")
    public List<ReviewDTO> getReviews(@RequestParam(required = false) Integer courseId) {
        List<Review> reviews;
        if (courseId != null) {
            reviews = reviewService.findByCourseId(courseId);
        } else {
            reviews = reviewService.findAll();
        }

        return reviews.stream()
                .map(ReviewDTO::valueOf)
                .collect(Collectors.toList());
    }

    // GET review by id
    @GetMapping("/{id}")
    @SecurityRequirement(name = "basicAuth")
    public ReviewDTO getReview(@PathVariable Integer id) {
        return reviewService.findById(id);
    }

    // POST new review
    @PostMapping
    @SecurityRequirement(name = "basicAuth")
    public void createReview(@RequestBody ReviewCreateDTO reviewDTO, Authentication authentication) {
        User currentUser = ((CustomUserDetails) authentication.getPrincipal()).getPerson();

        if (!currentUser.getRole().equals("ROLE_STUDENT")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can create reviews");
        }

        Course course = courseService.findById(reviewDTO.getCourseId());

        Review review = new Review();
        review.setCourse(course);
        review.setUser(currentUser);
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        reviewService.save(review);
    }

    // PUT update review
    @PutMapping("/{id}")
    @SecurityRequirement(name = "basicAuth")
    public void updateReview(@PathVariable Integer id,
                             @RequestBody ReviewUpdateDTO updatedReview,
                             Authentication authentication) {

        User currentUser = ((CustomUserDetails) authentication.getPrincipal()).getPerson();
        Review existingReview = reviewService.findReviewById(id).orElseThrow();

        // Проверяем права: либо админ, либо студент может править только свой отзыв
        if (currentUser.getRole().equals("ROLE_ADMIN")
            || (currentUser.getRole().equals("ROLE_STUDENT") && existingReview.getUser().getId().equals(currentUser.getId()))) {

            existingReview.setRating(updatedReview.getRating());
            existingReview.setComment(updatedReview.getComment());

            reviewService.save(existingReview);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this review");
        }
    }


    // DELETE review
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "basicAuth")
    public void deleteReview(@PathVariable Integer id, Authentication authentication) {
        User currentUser = ((CustomUserDetails) authentication.getPrincipal()).getPerson();

        Review review = reviewService.findReviewById(id).orElseThrow();

        // Админ — может всё
        if (currentUser.getRole().equals("ROLE_ADMIN")) {
            reviewService.delete(id);
            return;
        }

        // Студент — может удалить только свой отзыв
        if (currentUser.getRole().equals("ROLE_STUDENT") && review.getUser().getId().equals(currentUser.getId())) {
            reviewService.delete(id);
            return;
        }

        // Преподаватель и другие — запрещено
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this review");
    }
}
