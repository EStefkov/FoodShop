package bg.emiliyan.acc_backend.repositories;

import bg.emiliyan.acc_backend.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    Page<User> findAll(Pageable pageable);
    User findUserById(Long id);
    boolean existsByUsername(String username);
    User findByUsername(String username);
    User findByEmail(String email);
}
