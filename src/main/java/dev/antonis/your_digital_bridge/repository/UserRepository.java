package dev.antonis.your_digital_bridge.repository;

import dev.antonis.your_digital_bridge.entity.User;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface UserRepository extends ListCrudRepository<User, Integer>{
    // Find user by email
    Optional<User> findByEmail(String email);
    // Find user by Id
    Optional<User> findById( Integer id);
}
