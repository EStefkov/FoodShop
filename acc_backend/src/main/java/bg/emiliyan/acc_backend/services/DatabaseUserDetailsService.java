package bg.emiliyan.acc_backend.services;

import bg.emiliyan.acc_backend.entities.User;
import bg.emiliyan.acc_backend.exceptions.UserNotFoundByNameException;
import bg.emiliyan.acc_backend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@AllArgsConstructor
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(login);
        if (user == null) {
            // Try email if username lookup failed
            user = userRepository.findByEmail(login);
        }
        if (user == null) {
            throw new UserNotFoundByNameException(login);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
