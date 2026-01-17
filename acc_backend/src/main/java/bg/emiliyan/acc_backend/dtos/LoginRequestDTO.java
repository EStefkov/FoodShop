package bg.emiliyan.acc_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequestDTO {

    @NotBlank
    private String login; // Can be username or email
    @NotBlank
    private String password;
}
