package bg.emiliyan.acc_backend.services;

import bg.emiliyan.acc_backend.dtos.GetAllUserDTO;
import bg.emiliyan.acc_backend.dtos.RegisterUserDTO;
import bg.emiliyan.acc_backend.dtos.UpdateUserDTO;
import bg.emiliyan.acc_backend.dtos.UserDTO;
import bg.emiliyan.acc_backend.entities.Role;
import bg.emiliyan.acc_backend.entities.User;
import bg.emiliyan.acc_backend.exceptions.*;
import bg.emiliyan.acc_backend.mappers.UserMapper;
import bg.emiliyan.acc_backend.repositories.RoleRepository;
import bg.emiliyan.acc_backend.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public Page<GetAllUserDTO> getAllUser(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> GetAllUserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRoleNames()) // <- всички роли
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

    public void deleteUser(Long id, String currentUserRole, HttpServletResponse response) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundByIdException(id));

        // Проверка за админ
        if (user.getRoleNames().contains("ROLE_ADMIN")) {
            long adminCount = userRepository.countByRoleName("ROLE_ADMIN");
            if (adminCount <= 1) {
                throw new LastAdminException(currentUserRole);
            }

            if (!"ROLE_ADMIN".equals(currentUserRole)) {
                throw new UnauthorizedAccessException();
            }
        }

        // Изтриване на потребителя
        userRepository.deleteById(id);

        // Изтриване на JWT cookie
        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }


    public UserDTO updateUser(Long id, UpdateUserDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateUserFromDTO(dto, user);
        userRepository.save(user);
        return userMapper.userToUserDTO(user);
    }




    // helping method
    private  UserDTO mapToDTO(User user){
        return UserDTO.builder()

                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRoleNames())
                .number(user.getNumber())
                .address(user.getAddress())
                .city(user.getCity())
                .country(user.getCountry())
                .postalCode(user.getPostalCode())
                .profilePicture(user.getProfilePicture())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .googleLinked(user.getGoogleId() != null)
                .build();
    }
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundByNameException(username);
        }
        return userMapper.userToUserDTO(user);
    }

    public UserDTO linkGoogleAccount(String username, String googleId, String picture) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundByNameException(username);
        }

        // Check googleId not already taken by another account
        User existingGoogle = userRepository.findByGoogleId(googleId);
        if (existingGoogle != null && !existingGoogle.getUsername().equals(username)) {
            throw new RuntimeException("This Google account is already linked to another user");
        }

        user.setGoogleId(googleId);
        if (user.getProfilePicture() == null) {
            user.setProfilePicture(picture);
        }
        return userMapper.userToUserDTO(userRepository.save(user));
    }

    private  User RegisterUserMapDTO(RegisterUserDTO user){

        Set<Role> roles = user.getRole().stream() // List<String>
                .map(roleName -> roleRepository.findByRole(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());
//
//        Role userRole = roleRepository.findByName("ROLE_USER")
//                .orElseThrow(() -> new RuntimeException("Default role USER not found"));

        return User.builder()
                .username(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .email(user.getEmail())
                .roles(roles)
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



