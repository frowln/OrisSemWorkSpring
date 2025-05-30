package kpfu.itis.kasimov.dto;

import kpfu.itis.kasimov.models.Course;
import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.models.UserProgress;
import kpfu.itis.kasimov.repositories.CourseRepository;
import kpfu.itis.kasimov.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressDTO {
    private Long id;
    private Integer userId;
    private Integer courseId;
    private Integer completedLessons;
    private Integer totalLessons;
    private Timestamp lastUpdated;

    public static UserProgressDTO valueOf(UserProgress progress) {
        return new UserProgressDTO(
                progress.getId(),
                progress.getUser().getId(),       // Получаем ID пользователя
                progress.getCourse().getId(),      // Получаем ID курса
                progress.getCompletedLessons(),
                progress.getTotalLessons(),
                progress.getLastUpdated()
        );
    }

    public static UserProgress toEntity(UserProgressDTO dto, UserRepository userRepository, CourseRepository courseRepository) {
        return new UserProgress(
                dto.getId(),
                userRepository.findById(dto.getUserId()).orElseThrow(),
                courseRepository.findById(dto.getCourseId()).orElseThrow(),
                dto.getCompletedLessons(),
                dto.getTotalLessons(),
                dto.getLastUpdated()
        );
    }
}
