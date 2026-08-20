package ute.edu.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ute.edu.model.UserAccount;
import ute.edu.repository.UserAccountRepository;

@Service
public class AuthService {
    private final UserAccountRepository userAccountRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserAccountRepository userAccountRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserAccount register(UserAccount user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userAccountRepository.save(user);
    }

    public UserAccount loginUser(String username, String rawPassword) {
        UserAccount user = userAccountRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
            return user;
        }
        return null;
    }

    public boolean login(String username, String rawPassword) {
        return loginUser(username, rawPassword) != null;
    }

    public UserAccount findByUsername(String username) {
        return userAccountRepository.findByUsername(username);
    }
}
