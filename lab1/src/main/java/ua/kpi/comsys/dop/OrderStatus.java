package ua.kpi.comsys.dop;

import java.math.BigDecimal;
import java.time.LocalDate;

public sealed interface OrderStatus permits Pending, Paid, Shipped, Cancelled {
}

record Pending() implements OrderStatus {}

record Paid(String paymentID, BigDecimal amount) implements OrderStatus {
    public Paid {
        if (paymentID == null || paymentID.isBlank() || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid payment parameters");
        }
    }
}

record Shipped(String trackingCode, LocalDate dispatchDate) implements OrderStatus {
    public Shipped {
        if (trackingCode == null || trackingCode.isBlank() || dispatchDate == null) {
            throw new IllegalArgumentException("Invalid shipping parameters");
        }
    }
}

record Cancelled(String reason) implements OrderStatus {
    public Cancelled {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Invalid cancellation reason");
        }
    }
}
