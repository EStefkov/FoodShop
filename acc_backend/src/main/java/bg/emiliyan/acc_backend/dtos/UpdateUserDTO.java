package bg.emiliyan.acc_backend.dtos;

import bg.emiliyan.acc_backend.configs.AuthProvider;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateUserDTO {

    @Size(min = 3, max = 50)
    private String username;

    @Email
    private String email;

    private List<String> role;

    private String number;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private String profilePicture;
    private String firstName;
    private String lastName;
    private String location;
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;
}
