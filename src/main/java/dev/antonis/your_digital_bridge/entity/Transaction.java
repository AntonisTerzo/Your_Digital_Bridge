package dev.antonis.your_digital_bridge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions", schema = "mydatabase")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private dev.antonis.your_digital_bridge.entity.User sender;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private dev.antonis.your_digital_bridge.entity.User receiver;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public dev.antonis.your_digital_bridge.entity.User getSender() {
        return sender;
    }

    public void setSender(dev.antonis.your_digital_bridge.entity.User sender) {
        this.sender = sender;
    }

    public dev.antonis.your_digital_bridge.entity.User getReceiver() {
        return receiver;
    }

    public void setReceiver(dev.antonis.your_digital_bridge.entity.User receiver) {
        this.receiver = receiver;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

}