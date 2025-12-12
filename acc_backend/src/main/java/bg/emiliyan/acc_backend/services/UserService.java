package bg.emiliyan.acc_backend.services;

import bg.emiliyan.acc_backend.dtos.GetAllUserDTO;
import bg.emiliyan.acc_backend.dtos.RegisterUserDTO;
import bg.emiliyan.acc_backend.dtos.UpdateUserDTO;
import bg.emiliyan.acc_backend.dtos.UserDTO;
import bg.emiliyan.acc_backend.entities.User;
import bg.emiliyan.acc_backend.exceptions.LastAdminException;
import bg.emiliyan.acc_backend.exceptions.UnauthorizedAccessException;
import bg.emiliyan.acc_backend.exceptions.UserNotFoundByIdException;
import bg.emiliyan.acc_backend.exceptions.UsernameAlreadyExistsException;
import bg.emiliyan.acc_backend.mappers.UserMapper;
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
    private final UserMapper userMapper;

    public Page<GetAllUserDTO> getAllUser(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> GetAllUserDTO.builder()
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
                .orElseThrow(() -> new UserNotFoundByIdException(id));
        return userMapper.userToUserDTO(user);
    }


    public UserDTO registerUser(RegisterUserDTO users){

        if(userRepository.existsByUsername(users.getUsername()) || userRepository.existsByEmail(users.getEmail())){
            throw new UsernameAlreadyExistsException();
        }
        User user = RegisterUserMapDTO(users);
        user.setPassword(passwordEncoder.encode(users.getPassword()));
            User savedUser = userRepository.save(user);
            return mapToDTO(savedUser);


    }

    public void deleteUser(Long id, String currentUserRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundByIdException(id));

        // Проверка: ако потребителят е админ
        if ("ADMIN".equals(user.getRole())) {
            // Проверка за последен админ
            long adminCount = userRepository.countByRole("ADMIN");
            if (adminCount <= 1) {
                throw new LastAdminException(currentUserRole);
            }

            // Ако текущият потребител не е админ, забраняваме
            if (!"ADMIN".equals(currentUserRole)) {
                throw new UnauthorizedAccessException();
            }
        }

        userRepository.deleteById(id);
    }


    public UserDTO updateUser(Long id, UpdateUserDTO dto) {
        User user = userRepository.findUserById(id);
        userMapper.updateUserFromDTO(dto, user);
        userRepository.save(user);
        return userMapper.userToUserDTO(user);
    }


    // helping method
    private  UserDTO mapToDTO(User user){
        return UserDTO.builder()

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

                .build();
    }

    private  User RegisterUserMapDTO(RegisterUserDTO user){
        return User.builder()
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
    }


}



