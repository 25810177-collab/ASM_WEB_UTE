package ute.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ute.edu.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    UserAccount findByUsername(String username);
    UserAccount findByEmail(String email);
}
