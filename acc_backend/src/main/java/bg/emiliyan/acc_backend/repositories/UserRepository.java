package bg.emiliyan.acc_backend.repositories;

import bg.emiliyan.acc_backend.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface UserRepository extends JpaRepository<User, Long>{

    Page<User> findAll(Pageable pageable);
    User findUserById(Long id);
    boolean existsByUsername(String username);
    User findByUsername(String username);
    User findByEmail(String email);
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.role = :roleName")
    long countByRoleName(@Param("roleName") String roleName);
    boolean existsByEmail(String email);
    User findByGoogleId(String googleId);

}
