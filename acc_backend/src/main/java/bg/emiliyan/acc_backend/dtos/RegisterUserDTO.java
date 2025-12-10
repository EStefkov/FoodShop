package bg.emiliyan.acc_backend.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

@Data
@Builder
@Valid
public class RegisterUserDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank
    @Size(min = 8,max = 255, message = "Password must be between 8 and 255 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one number"
    )
    private String password;

    @NotBlank
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank
    private String role;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String number;

    private String address;
    @NotBlank
    private String city;
    @NotBlank
    private String country;
    @Pattern(regexp = "\\d{4}", message = "Postal code must be 4 digits")
    private String postalCode;
    private String profilePicture;
}
