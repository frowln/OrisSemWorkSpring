package kpfu.itis.kasimov.dto;

import kpfu.itis.kasimov.models.UserLesson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLessonDTO {
    private Long id;
    private Integer userId;
    private Integer lessonId;
    private Boolean completed;
    private Timestamp lastUpdated;

    public boolean isCompleted() {
        return completed;
    }

    public static UserLessonDTO valueOf(UserLesson userLesson) {
        return new UserLessonDTO(
                userLesson.getId(),
                userLesson.getUser().getId(),     // Получаем ID пользователя
                userLesson.getLesson().getId(),    // Получаем ID урока
                userLesson.isCompleted(),
                userLesson.getLastUpdated()
        );
    }
}
