package bg.emiliyan.acc_backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserDTO {

    @Size(min = 3, max = 50)
    private String username;

    @Email
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
}
