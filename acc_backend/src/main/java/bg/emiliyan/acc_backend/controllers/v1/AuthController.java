package bg.emiliyan.acc_backend.controllers.v1;

import bg.emiliyan.acc_backend.security.JwtUtils;
import bg.emiliyan.acc_backend.security.CookieUtils;
import bg.emiliyan.acc_backend.dto.LoginRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );

        // Use the username from the authenticated user (in case they logged in with email)
        String username = auth.getName();
        String token = jwtUtils.generateToken(username);
        ResponseCookie cookie = CookieUtils.createJwtCookie(token);
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new LoginResponse(
            "Login successful",
            token,
                username
        ));
    }

    private record LoginResponse(
        String message,
        String token,
        String username
    ) {}

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
    boolean secure = false;
    String profile = System.getProperty("spring.profiles.active");
    if (profile == null) profile = System.getenv("SPRING_PROFILES_ACTIVE");
    if (profile != null && (profile.contains("prod") || profile.contains("production"))) {
        secure = true;
    }

    ResponseCookie expiredCookie = ResponseCookie.from("access_token", "")
        .httpOnly(true)
        .secure(secure)
        .path("/")
        .maxAge(0)
        .build();

        response.addHeader("Set-Cookie", expiredCookie.toString());
        return ResponseEntity.ok("Logged out");
    }
}
