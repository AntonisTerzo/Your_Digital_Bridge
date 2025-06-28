package dev.antonis.your_digital_bridge.user;

import dev.antonis.your_digital_bridge.entity.SocialLoginCredential;
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

    public UserDetailsServiceImpl(UserCredentialRepository userCredentialRepository,
                                  SocialLoginRepository socialLoginRepository) {
        this.userCredentialRepository = userCredentialRepository;
        this.socialLoginRepository = socialLoginRepository;
    }
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Trim the username to remove any leading/trailing whitespace
        username = username.trim();
        // Look up the user using local credentials
        Optional<UserCredential> localUser = userCredentialRepository.findByUsername(username);
        if (localUser.isPresent()) {
            return  UserDetailsImpl.build(localUser.get());
        }
        // Look up the user using social login credentials
        Optional<SocialLoginCredential> socialUser = socialLoginRepository.findByProviderId(username);
        if(socialUser.isPresent()) {
            return UserDetailsImpl.socialBuild(socialUser.get());
        }

        throw new UsernameNotFoundException("User not found with username " + username);
    }
}
