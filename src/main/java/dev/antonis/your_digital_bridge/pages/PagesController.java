package dev.antonis.your_digital_bridge.pages;

import dev.antonis.your_digital_bridge.entity.User;
import dev.antonis.your_digital_bridge.entity.UserCredential;
import dev.antonis.your_digital_bridge.transaction.TransactionService;
import dev.antonis.your_digital_bridge.transaction.dto.TransactionRequestDto;
import dev.antonis.your_digital_bridge.user.repository.UserCredentialRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
public class PagesController {
    private final UserCredentialRepository userCredentialRepository;
    private final TransactionService transactionService;

    public PagesController(UserCredentialRepository userCredentialRepository, TransactionService transactionService) {
        this.userCredentialRepository = userCredentialRepository;
        this.transactionService = transactionService;
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
    public String getUserPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        UserCredential userCredential = userCredentialRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User user = userCredential.getUser();
        
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("balance", user.getBalance());
        return "user-page";
    }

    @GetMapping("/transfer")
    public String getTransferPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        UserCredential userCredential = userCredentialRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User user = userCredential.getUser();
        
        model.addAttribute("balance", user.getBalance());
        return "transfer";
    }

    @PostMapping("/transfer")
    public String processTransfer(
            @RequestParam String receiverEmail,
            @RequestParam BigDecimal amount,
            RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            UserCredential userCredential = userCredentialRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            TransactionRequestDto request = new TransactionRequestDto(receiverEmail, amount);
            transactionService.transferMoney(userCredential.getUser().getId(), request);
            
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
}
