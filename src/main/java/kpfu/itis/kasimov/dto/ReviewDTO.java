package kpfu.itis.kasimov.dto;

import kpfu.itis.kasimov.models.Review;
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
}
