package bg.emiliyan.acc_backend.services;

import bg.emiliyan.acc_backend.dtos.RegisterUserDTO;
import bg.emiliyan.acc_backend.entities.Role;
import bg.emiliyan.acc_backend.entities.User;
import bg.emiliyan.acc_backend.exceptions.UsernameAlreadyExistsException;
import bg.emiliyan.acc_backend.repositories.RoleRepository;
import bg.emiliyan.acc_backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserSuccessfully() {
        // DTO
        RegisterUserDTO dto = RegisterUserDTO.builder()
                .username("testuser")
                .password("Password1")
                .email("test@example.com")
                .role(List.of("ROLE_USER"))
                .firstName("Test")
                .lastName("User")
                .city("Sofia")
                .country("Bulgaria")
                .postalCode("1000")
                .build();

        // mock за password encoder
        Mockito.when(passwordEncoder.encode(Mockito.any(CharSequence.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // mock за roleRepository
        Role userRole = new Role();
        userRole.setRole("ROLE_USER");
        Mockito.when(roleRepository.findByRole("ROLE_USER"))
                .thenReturn(Optional.of(userRole));

        // mock за userRepository
        User savedUser = new User();
        savedUser.setUsername(dto.getUsername());
        savedUser.setEmail(dto.getEmail());
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);

        // call
        var result = userService.registerUser(dto);

        // assertions
        assertNotNull(result);
        assertEquals(dto.getUsername(), result.getUsername());
        assertEquals(dto.getEmail(), result.getEmail());

        Mockito.verify(userRepository, times(1)).save(Mockito.any(User.class));
    }


    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterUserDTO dto = RegisterUserDTO.builder()
                .username("existinguser")
                .password("Password1")
                .email("test@example.com")
                .role(List.of("ROLE_USER"))
                .firstName("Existing")
                .lastName("User")
                .city("Sofia")
                .country("Bulgaria")
                .postalCode("1000")
                .build();


        // Мокваме метода, който реално се вика в UserService
        Mockito.when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);
        Mockito.when(userRepository.existsByUsername(dto.getUsername())).thenReturn(false);

        // Тук очакваме точния exception
        UsernameAlreadyExistsException exception = assertThrows(
                UsernameAlreadyExistsException.class,
                () -> userService.registerUser(dto)
        );

        assertTrue(exception.getMessage().contains("Registration failed"));
    }





}
