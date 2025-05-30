package kpfu.itis.kasimov.dto;

import kpfu.itis.kasimov.models.UserCourse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCourseDTO {
    private Integer userId;
    private Integer courseId;

    public static UserCourseDTO valueOf(UserCourse userCourse) {
        return new UserCourseDTO(
                userCourse.getUser().getId(),     // Получаем ID пользователя
                userCourse.getCourse().getId()    // Получаем ID курса
        );
    }
}
