package dev.antonis.your_digital_bridge.transaction.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponseDto(
    Integer id,
    String senderEmail,
    String receiverEmail,
    BigDecimal amount,
    Instant timestamp
) {} 