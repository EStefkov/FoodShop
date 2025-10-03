package bg.emiliyan.acc_backend.controllers.v1;

import bg.emiliyan.acc_backend.dto.UserDTO;
import bg.emiliyan.acc_backend.entities.User;
import bg.emiliyan.acc_backend.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable){
        return ResponseEntity.ok(userService.getAllUser(pageable));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody User regUser){
        UserDTO user = userService.registerUser(regUser);
        return ResponseEntity.ok(user);
    }





}
