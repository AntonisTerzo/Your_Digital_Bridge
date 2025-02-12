package dev.antonis.your_digital_bridge.security.oauth2;

import dev.antonis.your_digital_bridge.entity.SocialLoginCredentials;
import dev.antonis.your_digital_bridge.user.repository.SocialLoginRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Service
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    private final SocialLoginRepository socialLoginRepository;

    public CustomOauth2UserService(SocialLoginRepository socialLoginRepository) {
        this.socialLoginRepository = socialLoginRepository;
    }


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Let Spring fetch the user details from GitHub
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // For GitHub, attributes like "id", "name", "email" are available
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String githubId = String.valueOf(attributes.get("id"));
        String email = String.valueOf(attributes.get("email"));
        String name = String.valueOf(attributes.get("login"));

        // Check if the user exists in the database
        SocialLoginCredentials userSocialLogin = socialLoginRepository.findByGithubId(githubId)
                .orElseGet(() -> {
                    // Create a new user if not exists
                    SocialLoginCredentials newUser = new SocialLoginCredentials();
                    newUser.setGithubId(githubId);
                    newUser.setName(name);
                    newUser.setEmail(email);
                    newUser.setCreatedAt(Instant.now());
                    newUser.setUpdatedAt(Instant.now());
                    newUser.setBalance(new BigDecimal("100.00"));
                    return socialLoginRepository.save(newUser);
                });

        return new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, "login");
    }
}
