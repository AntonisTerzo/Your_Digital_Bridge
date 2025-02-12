package dev.antonis.your_digital_bridge.user.repository;

import dev.antonis.your_digital_bridge.entity.SocialLoginCredentials;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialLoginRepository extends ListCrudRepository<SocialLoginCredentials, Integer> {
    // Find user by githubId
    Optional<SocialLoginCredentials> findByGithubId(String githubId);

    // Find user by GitHub login (name)
    Optional<SocialLoginCredentials> findByName(String name);
}
