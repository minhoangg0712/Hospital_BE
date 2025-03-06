package DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Integer userId;
    private String name;
    private String phone;
    private String email;
    private String gender;
    private String roleCode;
    private Integer departmentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

