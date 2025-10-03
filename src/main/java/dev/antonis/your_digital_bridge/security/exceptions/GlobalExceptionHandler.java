package dev.antonis.your_digital_bridge.security.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(UserNotFoundException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("error", "Account not found. Please log in.");
        return "redirect:/login";
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public String handleInsufficient(InsufficientFundsException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("error", "You have insufficient funds for this transfer.");
        return "redirect:/transfer";
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public String handleInvalid(InvalidTransactionException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("error", ex.getMessage());
        return "redirect:/transfer";
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public String handleRateLimitExceeded(RateLimitExceededException ex, RedirectAttributes ra) {
        logger.warn("Rate limit exceeded: {}", ex.getMessage());
        ra.addFlashAttribute("error", ex.getMessage());
        return "redirect:/transfer";
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpected(Exception ex, RedirectAttributes ra) {
        logger.error("Unexpected error in controller", ex);
        ra.addFlashAttribute("error", "An unexpected error occurred. Please try again later.");
        return "redirect:/transfer";
    }
}
