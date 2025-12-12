package bg.emiliyan.acc_backend.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequestDTO {

    private String login; // Can be username or email
    private String password;
}
