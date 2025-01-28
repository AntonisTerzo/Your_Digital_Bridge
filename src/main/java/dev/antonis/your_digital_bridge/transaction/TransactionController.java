package dev.antonis.your_digital_bridge.transaction;

import dev.antonis.your_digital_bridge.transaction.dto.TransactionRequestDto;
import dev.antonis.your_digital_bridge.transaction.dto.TransactionResponseDto;
import dev.antonis.your_digital_bridge.user.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDto> transferMoney(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody TransactionRequestDto request) {
        TransactionResponseDto response = transactionService.transferMoney(userDetails.getId(), request);
        return ResponseEntity.ok(response);
    }
} 