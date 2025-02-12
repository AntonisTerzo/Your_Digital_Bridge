package dev.antonis.your_digital_bridge.user;

import dev.antonis.your_digital_bridge.entity.SocialLoginCredentials;
import dev.antonis.your_digital_bridge.entity.UserCredential;
import dev.antonis.your_digital_bridge.user.repository.SocialLoginRepository;
import dev.antonis.your_digital_bridge.user.repository.UserCredentialRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserCredentialRepository userCredentialRepository;
    private final SocialLoginRepository socialLoginRepository;

    public UserDetailsServiceImpl(UserCredentialRepository userCredentialRepository, SocialLoginRepository socialLoginRepository) {
        this.userCredentialRepository = userCredentialRepository;
        this.socialLoginRepository = socialLoginRepository;
    }
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Trim the username to remove any leading/trailing whitespace
        username = username.trim();
        // First try to look up the user using local credentials
        Optional<UserCredential> localUser = userCredentialRepository.findByUsername(username);
        if (localUser.isPresent()) {
            return UserDetailsImpl.build(localUser.get());
        }

        // Next, try finding a social user by GitHub login (name)
        Optional<SocialLoginCredentials> socialUser = socialLoginRepository.findByName(username);
        if (socialUser.isPresent()) {
            return UserDetailsImpl.build(socialUser.get());
        }

        // Fallback: if username is numeric (e.g. GitHub id), try looking up by githubId
        if (username.chars().allMatch(Character::isDigit)) {
            socialUser = socialLoginRepository.findByGithubId(username);
            if (socialUser.isPresent()) {
                return UserDetailsImpl.build(socialUser.get());
            }
        }

        throw new UsernameNotFoundException("User not found with username " + username);
    }
}
