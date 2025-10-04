package bg.emiliyan.acc_backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginRequest {
    private String login; // Can be username or email
    private String password;
}
