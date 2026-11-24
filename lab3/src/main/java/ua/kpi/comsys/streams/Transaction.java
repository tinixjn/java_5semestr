package ua.kpi.comsys.streams;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Transaction(String transactionId, String category, BigDecimal amount, LocalDateTime timestamp) {
    public Transaction {
        if (transactionId == null || category == null || amount == null || timestamp == null) {
            throw new IllegalArgumentException("Transaction fields cannot be null");
        }
    }
}