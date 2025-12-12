package bg.emiliyan.acc_backend.services;

import bg.emiliyan.acc_backend.dtos.LoginRequestDTO;
import bg.emiliyan.acc_backend.security.CookieUtils;
import bg.emiliyan.acc_backend.security.JwtUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Service layer handling authentication logic — login, Google OAuth2, and logout.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    /**
     * Standard login with username/email and password.
     */
    public ResponseEntity<LoginResponse> login(LoginRequestDTO request, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );

        String username = auth.getName();
        String token = jwtUtils.generateToken(username);
        ResponseCookie cookie = CookieUtils.createJwtCookie(token);
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new LoginResponse("Login successful", token, username));
    }

    /**
     * Google login using OAuth2 token verification.
     */
    public ResponseEntity<?> googleLogin(GoogleTokenRequest request, HttpServletResponse response) {
        try {
            var jsonFactory = JacksonFactory.getDefaultInstance();
            var transport = GoogleNetHttpTransport.newTrustedTransport();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.token());
            if (idToken == null) {
                return ResponseEntity.badRequest().body("Invalid Google token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            // TODO: Провери дали user-ът вече съществува и ако не, го създай.
            String jwt = jwtUtils.generateToken(email);

            ResponseCookie cookie = CookieUtils.createJwtCookie(jwt);
            response.addHeader("Set-Cookie", cookie.toString());

            return ResponseEntity.ok(new LoginResponse("Google login successful", jwt, email));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Google login failed: " + e.getMessage());
        }
    }

    /**
     * Logout by invalidating JWT cookie.
     */
    public ResponseEntity<String> logout(HttpServletResponse response) {
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

    // ✅ вътрешни записи — може и в отделен пакет bg.emiliyan.acc_backend.dto.auth
    public record GoogleTokenRequest(String token) {}
    public record LoginResponse(String message, String token, String username) {}
}
