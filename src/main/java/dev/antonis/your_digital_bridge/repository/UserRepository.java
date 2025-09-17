package dev.antonis.your_digital_bridge.repository;

import dev.antonis.your_digital_bridge.entity.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface UserRepository extends ListCrudRepository<User, Integer>{
    // Find user by email
    Optional<User> findByEmail(String email);
    // Find user by Id
    @NonNull
    Optional<User> findById(@NonNull Integer id);
}
