package kpfu.itis.kasimov.dto;

import lombok.Data;

@Data
public class ReviewCreateDTO {
    private Integer courseId;
    private Integer rating;
    private String comment;
}
