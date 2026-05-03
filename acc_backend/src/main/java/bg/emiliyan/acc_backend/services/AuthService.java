package bg.emiliyan.acc_backend.services;

import bg.emiliyan.acc_backend.dtos.LoginRequestDTO;
import bg.emiliyan.acc_backend.entities.Role;
import bg.emiliyan.acc_backend.entities.User;
import bg.emiliyan.acc_backend.repositories.RoleRepository;
import bg.emiliyan.acc_backend.repositories.UserRepository;
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
import java.util.Set;

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
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

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
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.token());
            if (idToken == null) {
                return ResponseEntity.badRequest().body("Invalid Google token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            if (!payload.getEmailVerified()) {
                return ResponseEntity.badRequest().body("Google email not verified");
            }

            String email    = payload.getEmail();
            String name     = (String) payload.get("name");
            String picture  = (String) payload.get("picture");
            String googleId = payload.getSubject();

            User user;

            // 1. Known Google user — fastest path
            User byGoogleId = userRepository.findByGoogleId(googleId);
            if (byGoogleId != null) {
                user = byGoogleId;

                // 2. Email already exists — link Google to that account
            } else {
                User byEmail = userRepository.findByEmail(email);
                if (byEmail != null) {
                    byEmail.setGoogleId(googleId);
                    if (byEmail.getProfilePicture() == null) {
                        byEmail.setProfilePicture(picture);
                    }
                    user = userRepository.save(byEmail);

                    // 3. Brand new user
                } else {
                    Role userRole = roleRepository.findByRole("ROLE_USER")
                            .orElseThrow(() -> new RuntimeException("Default role not found"));

                    String firstName = name != null ? name.split(" ")[0] : "";
                    String lastName  = name != null && name.contains(" ") ? name.split(" ", 2)[1] : "";

                    User newUser = User.builder()
                            .email(email)
                            .username(email)
                            .googleId(googleId)
                            .firstName(firstName)
                            .lastName(lastName)
                            .profilePicture(picture)
                            .password("")
                            .roles(Set.of(userRole))
                            .build();

                    user = userRepository.save(newUser);
                }
            }

            String jwt = jwtUtils.generateToken(user.getUsername());
            response.addHeader("Set-Cookie", CookieUtils.createJwtCookie(jwt).toString());

            return ResponseEntity.ok(new LoginResponse("Google login successful", jwt, user.getUsername()));

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
