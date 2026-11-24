package ua.kpi.comsys.streams;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

public class TransactionStreamAnalyzer {

    public Optional<Transaction> findFirstHighValueTransaction(Stream<Transaction> stream, BigDecimal threshold) {
        if (stream == null || threshold == null) {
            throw new IllegalArgumentException("Stream and threshold cannot be null");
        }
        return stream
                .filter(tx -> tx.amount().compareTo(threshold) > 0)
                .findFirst();
    }

    public List<Transaction> takeTransactionsUntilBudgetExceeded(Stream<Transaction> stream, BigDecimal maxBudget) {
        if (stream == null || maxBudget == null) {
            throw new IllegalArgumentException("Stream and maxBudget cannot be null");
        }
        
        final BigDecimal[] currentSum = {BigDecimal.ZERO};

        return stream
                .takeWhile(tx -> {
                    if (currentSum[0].add(tx.amount()).compareTo(maxBudget) > 0) {
                        return false;
                    }
                    currentSum[0] = currentSum[0].add(tx.amount());
                    return true;
                })
                .collect(Collectors.toList());
    }


    public List<List<Transaction>> processBatches(Stream<Transaction> stream, int batchSize, int maxBatches) {
        if (stream == null) {
            throw new IllegalArgumentException("Stream cannot be null");
        }
        if (batchSize <= 0 || maxBatches <= 0) {
            throw new IllegalArgumentException("batchSize and maxBatches must be greater than 0");
        }

        Gatherer<Transaction, ?, List<Transaction>> batchGatherer = Gatherer.ofSequential(
                () -> new ArrayList<Transaction>(),
                (state, element, downstream) -> {
                    state.add(element);
                    if (state.size() == batchSize) {
                        List<Transaction> batch = new ArrayList<>(state);
                        state.clear();
                        downstream.push(batch);
                    }
                    return !downstream.isRejecting();
                },
                (state, downstream) -> {
                    if (!state.isEmpty()) {
                        downstream.push(new ArrayList<>(state));
                    }
                }
        );

        return stream
                .gather(batchGatherer)
                .limit(maxBatches)
                .collect(Collectors.toList());
    }
}