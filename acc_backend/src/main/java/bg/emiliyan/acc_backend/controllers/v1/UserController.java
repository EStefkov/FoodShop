package bg.emiliyan.acc_backend.controllers.v1;

import bg.emiliyan.acc_backend.dtos.GetAllUserDTO;
import bg.emiliyan.acc_backend.dtos.RegisterUserDTO;
import bg.emiliyan.acc_backend.dtos.UpdateUserDTO;
import bg.emiliyan.acc_backend.dtos.UserDTO;
import bg.emiliyan.acc_backend.entities.User;
import bg.emiliyan.acc_backend.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;



    @GetMapping
    public ResponseEntity<Page<GetAllUserDTO>> getAllUsers(Pageable pageable){
        return ResponseEntity.ok(userService.getAllUser(pageable));
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserDetails(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody @Valid  RegisterUserDTO regUser){
        UserDTO user = userService.registerUser(regUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    private record ErrorResponse(String message) {}

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication authentication) {
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");

        userService.deleteUser(id, role);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                              @RequestBody @Valid UpdateUserDTO updateUserDTO){
        return ResponseEntity.ok(userService.updateUser(id, updateUserDTO));
    }




}
