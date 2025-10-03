package dev.antonis.your_digital_bridge.user.auth;

import dev.antonis.your_digital_bridge.entity.User;
import dev.antonis.your_digital_bridge.transaction.TransactionService;
import dev.antonis.your_digital_bridge.transaction.dto.TransactionRequestDto;
import dev.antonis.your_digital_bridge.repository.UserRepository;
import dev.antonis.your_digital_bridge.user.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;


@Controller
public class PagesController {
    private static final Logger logger = LoggerFactory.getLogger(PagesController.class);
    private final TransactionService transactionService;
    private final UserRepository userRepository;

    public PagesController(
                           TransactionService transactionService, UserRepository userRepository) {
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
        try {
            User user = getCurrentUser(userDetails);
            model.addAttribute("fullName", user.getFullName());
            model.addAttribute("balance", user.getBalance());
            return "user-page";
        } catch (Exception e) {
            logger.error("Error loading user page for user ID: {}", userDetails.getId(), e);
            model.addAttribute("error", "Unable to load user information. Please try again.");
            return "redirect:/login?error=true";
        }
    }

    @GetMapping("/transfer")
    public String getTransferPage(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            User user = getCurrentUser(userDetails);
            model.addAttribute("balance", user.getBalance());
            return "transfer";
        } catch (Exception e) {
            logger.error("Error loading transfer page for user ID: {}", userDetails.getId(), e);
            model.addAttribute("error", "Unable to load transfer page. Please try again.");
            return "redirect:/login?error=true";
        }
    }

    @PostMapping("/transfer")
    public String processTransfer(
            @RequestParam String receiverEmail,
            @RequestParam BigDecimal amount,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            RedirectAttributes redirectAttributes) {
            User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() ->
                            new UsernameNotFoundException("User not found. Please try again")
                    );
            TransactionRequestDto request = new TransactionRequestDto(receiverEmail, amount);
            transactionService.transferMoney(user.getId(), request);

            redirectAttributes.addFlashAttribute("success",
                    "Successfully transferred €" + amount + " to " + receiverEmail);
        return "redirect:/transfer";
    }

    @GetMapping("/register")
    public String getRegisterPage() {
        return "register";
    }


    /**
     * Helper method to safely retrieve the current user
     */
    private User getCurrentUser(UserDetailsImpl userDetails) {
        if (userDetails == null || userDetails.getId() == null) {
            logger.warn("Invalid user details provided");
            throw new SecurityException("Invalid authentication");
        }

        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> {
                    logger.error("User not found in database for authenticated user ID: {}", userDetails.getId());
                    return new SecurityException("User authentication invalid");
                });
    }
}

