package kpfu.itis.kasimov.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLessonKey implements Serializable {
    private Integer user;
    private Integer lesson;
}

