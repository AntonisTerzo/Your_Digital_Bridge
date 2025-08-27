package dev.antonis.your_digital_bridge.pages;

import dev.antonis.your_digital_bridge.entity.User;
import dev.antonis.your_digital_bridge.entity.UserCredential;
import dev.antonis.your_digital_bridge.entity.SocialLoginCredential;
import dev.antonis.your_digital_bridge.transaction.TransactionService;
import dev.antonis.your_digital_bridge.transaction.dto.TransactionRequestDto;
import dev.antonis.your_digital_bridge.user.UserDetailsImpl;
import dev.antonis.your_digital_bridge.user.repository.UserCredentialRepository;
import dev.antonis.your_digital_bridge.user.repository.SocialLoginRepository;
import dev.antonis.your_digital_bridge.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Controller
public class PagesController {
    private final UserCredentialRepository userCredentialRepository;
    private final SocialLoginRepository socialLoginRepository;
    private final TransactionService transactionService;
    private final UserRepository userRepository;

    public PagesController(UserCredentialRepository userCredentialRepository,
                           SocialLoginRepository socialLoginRepository,
                           TransactionService transactionService, UserRepository userRepository) {
        this.userCredentialRepository = userCredentialRepository;
        this.socialLoginRepository = socialLoginRepository;
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    @GetMapping()
    public String getHomePage() {
        return "index";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/me")
    public String getUserPage(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 1. Load directly by ID (from the JWT -> UserDetails)
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found. Please try again")
                );
        model.addAttribute("fullName", user.getFullName());
        model.addAttribute("balance", user.getBalance());
        return "user-page";
    }

    @GetMapping("/transfer")
    public String getTransferPage(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found. Please try again")
                );
        model.addAttribute("balance", user.getBalance());
        return "transfer";
    }

    @PostMapping("/transfer")
    public String processTransfer(
            @RequestParam String receiverEmail,
            @RequestParam BigDecimal amount,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() ->
                            new UsernameNotFoundException("User not found. Please try again")
                    );
            TransactionRequestDto request = new TransactionRequestDto(receiverEmail, amount);
            transactionService.transferMoney(user.getId(), request);

            redirectAttributes.addFlashAttribute("success",
                    "Successfully transferred €" + amount + " to " + receiverEmail);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/transfer";
    }

    @GetMapping("/register")
    public String getRegisterPage() {
        return "register";
    }

    /**
     * Helper method to retrieve the User entity from the current Authentication.
     * Tries both regular and social login methods.
     */
    private Optional<User> getUserFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 1) Try regular login
        return userCredentialRepository.findByUsername(username)
                .map(UserCredential::getUser)
                // 2) Otherwise try social login
                .or(() -> {
                    if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
                        String provider = oauthToken.getAuthorizedClientRegistrationId();
                        Map<String, Object> attrs = oauthToken.getPrincipal().getAttributes();

                        if ("github".equals(provider) && attrs.containsKey("id")) {
                            String providerId = attrs.get("id").toString();
                            return socialLoginRepository
                                    .findByProviderAndProviderId(provider, providerId)
                                    .map(SocialLoginCredential::getUser);
                        }
                    }
                    return Optional.empty();
                });
    }
}

