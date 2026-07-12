package bg.emiliyan.acc_backend.services;

import bg.emiliyan.acc_backend.dtos.LoginRequestDTO;
import bg.emiliyan.acc_backend.entities.Role;
import bg.emiliyan.acc_backend.entities.User;
import bg.emiliyan.acc_backend.exceptions.GoogleAccountLinkRequiredException;
import bg.emiliyan.acc_backend.exceptions.GoogleEmailNotVerifiedException;
import bg.emiliyan.acc_backend.exceptions.InvalidGoogleTokenException;
import bg.emiliyan.acc_backend.repositories.RoleRepository;
import bg.emiliyan.acc_backend.repositories.UserRepository;
import bg.emiliyan.acc_backend.security.cookie.CookieUtils;
import bg.emiliyan.acc_backend.security.jwt.JwtUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Set;

/**
 * Service layer handling authentication logic — login, Google OAuth2, and logout.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // Built once (verifier is thread-safe and relatively expensive to construct).
    private GoogleIdTokenVerifier googleIdTokenVerifier;

    @PostConstruct
    private void initGoogleVerifier() throws GeneralSecurityException, java.io.IOException {
        this.googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

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
     *
     * Security note: if a local account already exists with the same email but is
     * NOT yet linked to this Google identity, we deliberately do NOT auto-link it.
     * Auto-linking on email match is vulnerable to account pre-hijacking: an attacker
     * could register a normal account with the victim's email first, then silently
     * gain a parallel login path once the victim signs in with Google. Linking must
     * instead go through an authenticated flow (see UserService#linkGoogleAccount)
     * where the user proves ownership of the existing account first.
     *
     * Errors are surfaced as exceptions and handled centrally by GlobalExceptionHandler,
     * consistent with the rest of the codebase (see UserService).
     */
    public ResponseEntity<LoginResponse> googleLogin(GoogleTokenRequest request, HttpServletResponse response) {
        GoogleIdToken idToken;
        try {
            idToken = googleIdTokenVerifier.verify(request.token());
        } catch (Exception e) {
            // Never leak verifier internals (network/cert/parsing details) to the client.
            log.warn("Google token verification threw an exception", e);
            throw new InvalidGoogleTokenException();
        }

        if (idToken == null) {
            throw new InvalidGoogleTokenException();
        }

        GoogleIdToken.Payload payload = idToken.getPayload();

        if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
            throw new GoogleEmailNotVerifiedException();
        }

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");
        String googleId = payload.getSubject();

        User user;

        // 1. Known Google user — fastest path
        User byGoogleId = userRepository.findByGoogleId(googleId);
        if (byGoogleId != null) {
            user = byGoogleId;

        } else {
            // 2. An account with this email already exists but isn't linked to Google.
            //    Refuse to auto-link — see javadoc above.
            User byEmail = userRepository.findByEmail(email);
            if (byEmail != null) {
                throw new GoogleAccountLinkRequiredException();
            }

            // 3. Brand new user
            Role userRole = roleRepository.findByRole("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Default role not found"));

            String firstName = name != null ? name.split(" ")[0] : "";
            String lastName = name != null && name.contains(" ") ? name.split(" ", 2)[1] : "";

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

        String jwt = jwtUtils.generateToken(user.getUsername());
        response.addHeader("Set-Cookie", CookieUtils.createJwtCookie(jwt).toString());

        return ResponseEntity.ok(new LoginResponse("Google login successful", jwt, user.getUsername()));
    }

    /**
     * Logout by invalidating JWT cookie.
     */
    public ResponseEntity<String> logout(HttpServletResponse response) {
        response.addHeader("Set-Cookie", CookieUtils.clearJwtCookie().toString());
        return ResponseEntity.ok("Logged out");
    }

    // ✅ вътрешни записи — може и в отделен пакет bg.emiliyan.acc_backend.dto.auth
    public record GoogleTokenRequest(String token) {}
    public record LoginResponse(String message, String token, String username) {}
}