package bg.emiliyan.acc_backend.controllers.v1;

import bg.emiliyan.acc_backend.dtos.GetAllUserDTO;
import bg.emiliyan.acc_backend.dtos.RegisterUserDTO;
import bg.emiliyan.acc_backend.dtos.UpdateUserDTO;
import bg.emiliyan.acc_backend.dtos.UserDTO;
import bg.emiliyan.acc_backend.entities.User;
import bg.emiliyan.acc_backend.services.AuthService;
import bg.emiliyan.acc_backend.services.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;


    public UserController(UserService userService) {
        this.userService = userService;
    }





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
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication authentication, HttpServletResponse response) {
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");

        userService.deleteUser(id, role, response);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                              @RequestBody @Valid UpdateUserDTO updateUserDTO){
        return ResponseEntity.ok(userService.updateUser(id, updateUserDTO));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PostMapping("/me/link-google")
    public ResponseEntity<UserDTO> linkGoogle(
            @RequestBody AuthService.GoogleTokenRequest request,
            Authentication authentication) throws Exception {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(request.token());
        if (idToken == null) {
            return ResponseEntity.badRequest().build();
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String googleId = payload.getSubject();
        String picture  = (String) payload.get("picture");

        String username = authentication.getName();
        return ResponseEntity.ok(userService.linkGoogleAccount(username, googleId, picture));
    }




}
