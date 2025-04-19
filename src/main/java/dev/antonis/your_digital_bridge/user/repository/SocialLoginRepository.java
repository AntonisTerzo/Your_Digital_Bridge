package dev.antonis.your_digital_bridge.user.repository;

import dev.antonis.your_digital_bridge.entity.SocialLoginCredential;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialLoginRepository extends ListCrudRepository<SocialLoginCredential, Integer> {
    // Find user by provider and providerID
    Optional<SocialLoginCredential> findByProviderAndProviderId(String provider, String providerID);
}
