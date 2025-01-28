package dev.antonis.your_digital_bridge.transaction;

import dev.antonis.your_digital_bridge.entity.Transaction;
import dev.antonis.your_digital_bridge.entity.User;
import dev.antonis.your_digital_bridge.transaction.dto.TransactionRequestDto;
import dev.antonis.your_digital_bridge.transaction.dto.TransactionResponseDto;
import dev.antonis.your_digital_bridge.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TransactionResponseDto transferMoney(Integer senderId, TransactionRequestDto request) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        
        User receiver = userRepository.findByEmail(request.receiverEmail())
                .orElseThrow(() -> new RuntimeException("Receiver not found with email: " + request.receiverEmail()));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Cannot transfer money to yourself");
        }

        BigDecimal amount = request.amount();
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
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

        return new TransactionResponseDto(
            savedTransaction.getId(),
            sender.getEmail(),
            receiver.getEmail(),
            amount,
            savedTransaction.getTimestamp()
        );
    }
} 