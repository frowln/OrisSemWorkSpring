package kpfu.itis.kasimov.models;

import jakarta.persistence.*;
import kpfu.itis.kasimov.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String email;

    @Column(nullable = true)
    private String password;

    private String role;

    @Column(name = "avatar_url")
    private String avatarUrl;
    @ManyToMany(mappedBy = "students") // students — это имя поля в Course
    private List<Course> enrolledCourses;


}
