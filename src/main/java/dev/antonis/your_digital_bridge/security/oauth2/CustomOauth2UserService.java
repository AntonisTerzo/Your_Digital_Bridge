package dev.antonis.your_digital_bridge.security.oauth2;

import dev.antonis.your_digital_bridge.entity.SocialLoginCredential;
import dev.antonis.your_digital_bridge.entity.User;
import dev.antonis.your_digital_bridge.user.repository.SocialLoginRepository;
import dev.antonis.your_digital_bridge.user.repository.UserRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    private final SocialLoginRepository socialLoginRepository;
    private final UserRepository userRepository;

    public CustomOauth2UserService(SocialLoginRepository socialLoginRepository, UserRepository userRepository) {
        this.socialLoginRepository = socialLoginRepository;
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Let Spring fetch the user details from GitHub
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        // Create a mutable copy of the attributes map
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        // Get the provider
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = String.valueOf(attributes.get("id"));
        // Check if the email is missing or comes as "null"
        String email = attributes.get("email") != null && !"null".equals(attributes.get("email"))
                ? String.valueOf(attributes.get("email"))
                : null;
        String name = String.valueOf(attributes.get("login"));

        // If email is not available, call GitHub's /user/emails endpoint
        if (email == null || email.isEmpty()) {
            try {
                String accessToken = userRequest.getAccessToken().getTokenValue();
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(accessToken);
                HttpEntity<String> entity = new HttpEntity<>("", headers);

                ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                        "https://api.github.com/user/emails",
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<>() {
                        }
                );

                List<Map<String, Object>> emails = response.getBody();
                if (emails != null) {
                    email = emails.stream()
                            .filter(emailObj -> Boolean.TRUE.equals(emailObj.get("primary"))
                                    && Boolean.TRUE.equals(emailObj.get("verified")))
                            .map(emailObj -> (String) emailObj.get("email"))
                            .findFirst()
                            .orElse(null);

                    if (email != null) {
                        attributes.put("email", email);
                    }
                }
            } catch (Exception e) {
                // Log the exception as needed
                System.err.println("Failed to fetch email from GitHub API: " + e.getMessage());
            }
        }

        // Create a final variable for use in the lambda expression
        final String finalEmail = email;

        // Check if the user exists in the database, or create a new one
        User user = userRepository.findByEmail(finalEmail).orElseGet(() -> {
            //create new user
            User newUser = new User();
            newUser.setEmail(finalEmail);
            newUser.setFullName(name);
            newUser.setCreatedAt(Instant.now());
            newUser.setUpdatedAt(Instant.now());
            newUser.setBalance(new BigDecimal("100.00"));
            return userRepository.save(newUser);
        });
        // Check if there is an existing socialLoginCredentials
        Optional<SocialLoginCredential> socialLoginOpt = socialLoginRepository.findByProviderAndProviderId(provider, providerId);
        if (socialLoginOpt.isEmpty()) {
            SocialLoginCredential socialLogin = new SocialLoginCredential(user, provider, providerId);
            socialLogin.setCreatedAt(Instant.now());
            socialLogin.setUpdatedAt(Instant.now());
            socialLoginRepository.save(socialLogin);
        }
        return new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, "login");
    }
}
