package bg.emiliyan.acc_backend.repositories;

import bg.emiliyan.acc_backend.entities.Role;
import bg.emiliyan.acc_backend.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface RoleRepository extends Repository<Role, Long> {
    Optional<Role> findByRole(String role);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    List<Role> findByRoleIn(List<String> roles);

}
