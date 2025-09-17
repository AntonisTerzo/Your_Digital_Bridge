package dev.antonis.your_digital_bridge.repository;

import dev.antonis.your_digital_bridge.entity.UserCredential;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends ListCrudRepository<UserCredential, Integer> {
    // Find user by username
    Optional<UserCredential> findByUsername(String username);
}
