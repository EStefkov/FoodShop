package bg.emiliyan.acc_backend.services;

import bg.emiliyan.acc_backend.dtos.RegisterUserDTO;
import bg.emiliyan.acc_backend.entities.User;
import bg.emiliyan.acc_backend.exceptions.UsernameAlreadyExistsException;
import bg.emiliyan.acc_backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserSuccessfully() {

        // 1️⃣ Създаваме DTO с валидни данни
        RegisterUserDTO dto = RegisterUserDTO.builder()
                .username("testuser")
                .password("Password1")  // трябва да мине regex-а
                .email("test@example.com")
                .role("USER")
                .firstName("Test")
                .lastName("User")
                .city("Sofia")
                .country("Bulgaria")
                .postalCode("1000")
                .build();

        Mockito.when(passwordEncoder.encode(Mockito.any(CharSequence.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // 2️⃣ Създаваме User entity, което ще върне репозитория
        User savedUser = new User();
        savedUser.setEmail(dto.getEmail());
        savedUser.setUsername(dto.getUsername());
        savedUser.setRole(dto.getRole());

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);

        // 3️⃣ Викаме метода
        var result = userService.registerUser(dto);

        // 4️⃣ Проверки
        assertNotNull(result);
        assertEquals(dto.getEmail(), result.getEmail());
        assertEquals(dto.getUsername(), result.getUsername());

        Mockito.verify(userRepository, times(1))
                .save(Mockito.any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterUserDTO dto = RegisterUserDTO.builder()
                .username("existinguser")
                .password("Password1")
                .email("test@example.com")
                .role("USER")
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
