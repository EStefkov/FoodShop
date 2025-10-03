package bg.emiliyan.acc_backend.services;

import bg.emiliyan.acc_backend.dto.UserDTO;
import bg.emiliyan.acc_backend.entities.User;
import bg.emiliyan.acc_backend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserDTO> getAllUser(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .number(user.getNumber())
                        .address(user.getAddress())
                        .city(user.getCity())
                        .country(user.getCountry())
                        .postalCode(user.getPostalCode())
                        .profilePicture(user.getProfilePicture())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .build()
                );
    }


    public UserDTO registerUser(User user){

        // Creating the new user
        User newUser = User.builder()
                .username(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .email(user.getEmail())
                .role(user.getRole())
                .number(user.getNumber())
                .address(user.getAddress())
                .city(user.getCity())
                .country(user.getCountry())
                .postalCode(user.getPostalCode())
                .profilePicture(user.getProfilePicture())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();

        // Save in DB
        User savedUser = userRepository.save(newUser);

        return UserDTO.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .number(savedUser.getNumber())
                .address(savedUser.getAddress())
                .city(savedUser.getCity())
                .country(savedUser.getCountry())
                .postalCode(savedUser.getPostalCode())
                .profilePicture(savedUser.getProfilePicture())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .build();
    }

}



