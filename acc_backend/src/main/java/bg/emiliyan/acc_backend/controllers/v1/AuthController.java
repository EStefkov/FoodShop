package bg.emiliyan.acc_backend.controllers.v1;

import bg.emiliyan.acc_backend.dtos.LoginRequestDTO;
import bg.emiliyan.acc_backend.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request, HttpServletResponse response) {
        return authService.login(request, response);
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody AuthService.GoogleTokenRequest request, HttpServletResponse response) {
        return authService.googleLogin(request, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        return authService.logout(response);
    }
}
