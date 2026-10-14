package ua.kpi.comsys.dop;

public class OrderProcessor {
    public String process(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        return switch (status) {
            case Pending p -> """
                    Order Status: PENDING
                    Details: Order is currently pending processing.""";
            case Paid pa -> """
                    Order Status: PAID	
                    	Details: Payment ID: %s, Amount: $%s""".formatted(pa.paymentID(), pa.amount());
            case Shipped s -> """
                    Order Status: SHIPPED
                    	Details: Tracking Code: %s, Dispatch Date: %s""".formatted(s.trackingCode(), s.dispatchDate());
            case Cancelled c -> """
                    Order Status: CANCELLED
                    	Details: Reason: %s""".formatted(c.reason());
        };
    }
}