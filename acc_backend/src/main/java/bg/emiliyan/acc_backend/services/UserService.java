package bg.emiliyan.acc_backend.services;

import bg.emiliyan.acc_backend.dto.UserDTO;
import bg.emiliyan.acc_backend.entities.User;
import bg.emiliyan.acc_backend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;


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
                        .createdAt(user.getCreatedAt())
                        .build()
                );
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id: " + id + " does not exist!"));
        return mapToDTO(user);
    }


    public UserDTO registerUser(User user){

        if (userRepository.existsByUsername(user.getUsername())){
            throw new IllegalArgumentException("Username '" + user.getUsername() + "' already exists");
        }

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
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        // Save in DB
        User savedUser = userRepository.save(newUser);

        return mapToDTO(savedUser);
    }

    public void deleteUser(Long id){
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
        }else {
            throw new IllegalArgumentException("User with id: " + id + " does not exist!");
        }
    }

    public UserDTO updateUser(Long id, User userDTO) {

        User user = userRepository.findUserById(id);

        if (userDTO.getUsername() != null) user.setUsername(userDTO.getUsername());
        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        if (userDTO.getRole() != null) user.setRole(userDTO.getRole());
        if (userDTO.getNumber() != null) user.setNumber(userDTO.getNumber());
        if (userDTO.getAddress() != null) user.setAddress(userDTO.getAddress());
        if (userDTO.getCity() != null) user.setCity(userDTO.getCity());
        if (userDTO.getCountry() != null) user.setCountry(userDTO.getCountry());
        if (userDTO.getPostalCode() != null) user.setPostalCode(userDTO.getPostalCode());
        if (userDTO.getProfilePicture() != null) user.setProfilePicture(userDTO.getProfilePicture());
        if (userDTO.getFirstName() != null) user.setFirstName(userDTO.getFirstName());
        if (userDTO.getLastName() != null) user.setLastName(userDTO.getLastName());

        userRepository.save(user);

        return mapToDTO(user);
    }






    // helping method
    private  UserDTO mapToDTO(User user){
        return UserDTO.builder()
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
                .createdAt(user.getCreatedAt())
                .build();
    }


}



