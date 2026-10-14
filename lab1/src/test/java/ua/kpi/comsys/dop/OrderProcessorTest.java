package ua.kpi.comsys.dop;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class OrderProcessorTest {

    private final OrderProcessor processor = new OrderProcessor();

    @Test
    void testNullStatusThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.process(null);
        });
        assertEquals("Status cannot be null", exception.getMessage());
    }

    @Test
    void testPendingStatus() {
        OrderStatus status = new Pending();
        String result = processor.process(status);
        assertTrue(result.contains("PENDING"));
    }

    @Test
    void testPaidStatus() {
        OrderStatus status = new Paid("PAY-123", new BigDecimal("150.00"));
        String result = processor.process(status);
        assertTrue(result.contains("PAID"));
        assertTrue(result.contains("PAY-123"));
    }

    @Test
    void testPaidValidation() {
        assertThrows(IllegalArgumentException.class, () -> new Paid("", BigDecimal.TEN));
        assertThrows(IllegalArgumentException.class, () -> new Paid("123", BigDecimal.ZERO));
    }

    @Test
    void testShippedStatus() {
        OrderStatus status = new Shipped("TRK-999", LocalDate.of(2026, 10, 6));
        String result = processor.process(status);
        assertTrue(result.contains("SHIPPED"));
        assertTrue(result.contains("TRK-999"));
    }

    @Test
    void testShippedValidation() {
        assertThrows(IllegalArgumentException.class, () -> new Shipped(null, LocalDate.now()));
    }

    @Test
    void testCancelledStatus() {
        OrderStatus status = new Cancelled("Out of stock");
        String result = processor.process(status);
        assertTrue(result.contains("CANCELLED"));
        assertTrue(result.contains("Out of stock"));
    }

    @Test
    void testCancelledValidation() {
        assertThrows(IllegalArgumentException.class, () -> new Cancelled("   "));
    }
}