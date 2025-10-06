package bg.emiliyan.acc_backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String number;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private String profilePicture;
    private String firstName;
    private String lastName;
    private java.sql.Timestamp createdAt;
}
