package dev.antonis.your_digital_bridge.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransactionRequestDto(
    @NotNull @Email String receiverEmail,
    @NotNull @DecimalMin("0.01") BigDecimal amount
) {} 