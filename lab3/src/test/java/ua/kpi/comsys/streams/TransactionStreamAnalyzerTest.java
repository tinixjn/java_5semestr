package ua.kpi.comsys.streams;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TransactionStreamAnalyzerTest {

    private final TransactionStreamAnalyzer analyzer = new TransactionStreamAnalyzer();

    @Test
    void testValidationNullArguments() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.findFirstHighValueTransaction(null, BigDecimal.TEN));
        assertThrows(IllegalArgumentException.class, () -> analyzer.takeTransactionsUntilBudgetExceeded(null, BigDecimal.TEN));
        assertThrows(IllegalArgumentException.class, () -> analyzer.processBatches(null, 2, 2));
        assertThrows(IllegalArgumentException.class, () -> analyzer.processBatches(Stream.empty(), 0, 2));
    }

    @Test
    @Timeout(2)
    void testFindFirstHighValueTransactionInfiniteStream() {
        Stream<Transaction> infiniteStream = Stream.iterate(
                new Transaction("T1", "Food", new BigDecimal("50.00"), LocalDateTime.now()),
                tx -> new Transaction("T" + (Integer.parseInt(tx.transactionId().substring(1)) + 1), 
                                      "Food", 
                                      tx.amount().add(BigDecimal.TEN), 
                                      LocalDateTime.now())
        );

        Optional<Transaction> result = analyzer.findFirstHighValueTransaction(infiniteStream, new BigDecimal("75.00"));
        
        assertTrue(result.isPresent());
        assertEquals("T4", result.get().transactionId());
    }

    @Test
    @Timeout(2)
    void testTakeTransactionsUntilBudgetExceeded() {
        Stream<Transaction> stream = Stream.of(
                new Transaction("1", "Tech", new BigDecimal("100"), LocalDateTime.now()),
                new Transaction("2", "Tech", new BigDecimal("200"), LocalDateTime.now()),
                new Transaction("3", "Tech", new BigDecimal("500"), LocalDateTime.now())
        );

        List<Transaction> result = analyzer.takeTransactionsUntilBudgetExceeded(stream, new BigDecimal("350"));
        
        assertEquals(2, result.size()); 
    }

    @Test
    @Timeout(2)
    void testProcessBatchesInfiniteStream() {
        Stream<Transaction> infiniteStream = Stream.generate(
                () -> new Transaction("TX", "General", new BigDecimal("10.00"), LocalDateTime.now())
        );

        List<List<Transaction>> batches = analyzer.processBatches(infiniteStream, 2, 3);

        assertEquals(3, batches.size());
        for (List<Transaction> batch : batches) {
            assertEquals(2, batch.size());
        }
    }
}