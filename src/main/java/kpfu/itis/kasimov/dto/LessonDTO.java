package kpfu.itis.kasimov.dto;

import kpfu.itis.kasimov.models.Lesson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonDTO {
    private Integer id;
    private String name;
    private String body;
    private Integer courseId;

    public static LessonDTO valueOf(Lesson lesson) {
        return new LessonDTO(
                lesson.getId(),
                lesson.getName(),
                lesson.getBody(),
                lesson.getCourse().getId() // Получаем ID курса
        );
    }
}
