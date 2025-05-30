package kpfu.itis.kasimov.dto;

import kpfu.itis.kasimov.models.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Integer id;
    private String name;
    private String description;
    private Integer teacherId;
    private Double averageRating;

    public static CourseDTO valueOf(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getTeacherId(),
                course.getAverageRating()
        );
    }
}