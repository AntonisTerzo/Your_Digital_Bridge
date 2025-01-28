package dev.antonis.your_digital_bridge.user.auth;

import dev.antonis.your_digital_bridge.user.dto.LoginRequestDto;
import dev.antonis.your_digital_bridge.user.dto.LoginResponseDto;
import dev.antonis.your_digital_bridge.user.dto.RegisterUserRequestDto;
import dev.antonis.your_digital_bridge.user.dto.RegisterUserResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDto> registerNewUser(@RequestBody @Valid RegisterUserRequestDto registerUserRequestDto) {
        RegisterUserResponseDto response = authService.registerNewUser(registerUserRequestDto);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {
        LoginResponseDto response = authService.authenticateUser(loginRequest);
        ResponseCookie jwtCookie = authService.generateJwtCookie(loginRequest.username());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(response);
    }
}
