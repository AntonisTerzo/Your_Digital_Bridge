package dev.antonis.your_digital_bridge.transaction;

import dev.antonis.your_digital_bridge.entity.Transaction;
import dev.antonis.your_digital_bridge.entity.User;
import dev.antonis.your_digital_bridge.security.exceptions.InsufficientFundsException;
import dev.antonis.your_digital_bridge.security.exceptions.InvalidTransactionException;
import dev.antonis.your_digital_bridge.security.exceptions.UserNotFoundException;
import dev.antonis.your_digital_bridge.security.rateLimiting.RedisRateLimitingService;
import dev.antonis.your_digital_bridge.transaction.dto.TransactionRequestDto;
import dev.antonis.your_digital_bridge.transaction.dto.TransactionResponseDto;
import dev.antonis.your_digital_bridge.repository.TransactionRepository;
import dev.antonis.your_digital_bridge.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final RedisRateLimitingService redisRateLimitingService;
    private final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              RedisRateLimitingService redisRateLimitingService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.redisRateLimitingService = redisRateLimitingService;
    }

    @Transactional
    public void transferMoney(Integer senderId, TransactionRequestDto request) {
        // Check rate limit FIRST, before any expensive operations
        redisRateLimitingService.checkRateLimit(senderId);

        //Null checks
        if (request == null || request.amount() == null || request.receiverEmail() == null) {
            throw new InvalidTransactionException("Invalid transfer request");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException("Sender not found"));
        
        User receiver = userRepository.findByEmail(request.receiverEmail())
                .orElseThrow(() -> new UserNotFoundException("Receiver not found. Please add a valid receiver."));

        if (sender.getId().equals(receiver.getId())) {
            throw new InvalidTransactionException("Cannot transfer money to yourself");
        }

        BigDecimal amount = request.amount();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount must be positive");
        }

        if (sender.getBalance() == null || receiver.getBalance() == null) {
            throw new InvalidTransactionException("Account balances are unavailable");
        }

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        // Update balances
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setTimestamp(Instant.now());

        Transaction savedTransaction = transactionRepository.save(transaction);

        logger.info("Transfer successful: transactionId={}, from={} to ={} amount={}",
                savedTransaction.getId(), sender.getEmail(), receiver.getEmail(), amount);

        new TransactionResponseDto(
                savedTransaction.getId(),
                sender.getEmail(),
                receiver.getEmail(),
                amount,
                savedTransaction.getTimestamp()
        );
    }
} 