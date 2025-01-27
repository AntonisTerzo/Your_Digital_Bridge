package dev.antonis.your_digital_bridge.user.auth;

import dev.antonis.your_digital_bridge.entity.User;
import dev.antonis.your_digital_bridge.entity.UserCredential;
import dev.antonis.your_digital_bridge.user.dto.RegisterUserResponseDto;
import dev.antonis.your_digital_bridge.user.repository.UserCredentialRepository;
import dev.antonis.your_digital_bridge.user.repository.UserRepository;
import dev.antonis.your_digital_bridge.user.dto.RegisterUserRequestDto;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, UserCredentialRepository userCredentialRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegisterUserResponseDto registerNewUser(@Valid RegisterUserRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.email()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }
        if (userCredentialRepository.findByUsername(requestDto.username()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setFirstName(requestDto.firstName());
        user.setLastName(requestDto.lastName());
        user.setEmail(requestDto.email());
        user.setAdress(requestDto.address());
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        User savedUser = userRepository.save(user);

        UserCredential userCredential = new UserCredential();
        userCredential.setUsername(requestDto.username());
        userCredential.setPassword(passwordEncoder.encode(requestDto.password()));
        userCredential.setUser(savedUser);
        userCredential.setCreatedAt(Instant.now());
        userCredential.setUpdatedAt(Instant.now());

        userCredentialRepository.save(userCredential);

        return new RegisterUserResponseDto(userCredential.getUsername(), user.getEmail());
    }
}
