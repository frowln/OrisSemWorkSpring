package kpfu.itis.kasimov.dto;

import kpfu.itis.kasimov.models.Course;
import kpfu.itis.kasimov.models.Review;
import kpfu.itis.kasimov.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Integer id;
    private Integer courseId;
    private Integer userId;
    private Integer rating;
    private String comment;
    private Timestamp createdAt;

    public static ReviewDTO valueOf(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getCourse().getId(), // Получаем ID курса
                review.getUser().getId(),   // Получаем ID пользователя
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }

    public Review toEntity() {
        Review review = new Review();
        review.setId(this.id);

        // Очень важно — только id для course и user (не надо полностью их загружать)
        Course course = new Course();
        course.setId(this.courseId);
        review.setCourse(course);

        User user = new User();
        user.setId(this.userId);
        review.setUser(user);

        review.setRating(this.rating);
        review.setComment(this.comment);
        review.setCreatedAt(this.createdAt);

        return review;
    }

}
