package kpfu.itis.kasimov.dto;

import kpfu.itis.kasimov.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String name;
    private String email;
    private String role; // Например: "teacher", "student"
    private String avatarUrl;

    public static UserDTO valueOf(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getAvatarUrl()
        );
    }
}
