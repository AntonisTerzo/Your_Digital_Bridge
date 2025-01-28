package dev.antonis.your_digital_bridge.pages;

import dev.antonis.your_digital_bridge.entity.User;
import dev.antonis.your_digital_bridge.entity.UserCredential;
import dev.antonis.your_digital_bridge.user.repository.UserCredentialRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagesController {
    private final UserCredentialRepository userCredentialRepository;

    public PagesController(UserCredentialRepository userCredentialRepository) {
        this.userCredentialRepository = userCredentialRepository;
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
        return "user-page";
    }
}
