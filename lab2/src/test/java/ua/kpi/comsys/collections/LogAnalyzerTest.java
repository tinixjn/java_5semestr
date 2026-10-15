package ua.kpi.comsys.collections;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LogAnalyzerTest {

    private final LogAnalyzer analyzer = new LogAnalyzer();

    @Test
    void testGetUniqueIpsNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.getUniqueIps(null));
    }

    @Test
    void testGetUniqueIpsValid() {
        List<String> logs = Arrays.asList(
            "127.0.0.1 ident alice [01/May/2025:07:20:10 +0000] \"GET /index.html HTTP/1.1\" 200 9481",
            "192.168.1.1 ident bob [01/May/2025:07:21:10 +0000] \"GET /about.html HTTP/1.1\" 200 1200",
            "127.0.0.1 ident alice [01/May/2025:07:22:10 +0000] \"GET /contact.html HTTP/1.1\" 200 500",
            "invalid log line format"
        );

        Set<String> uniqueIps = analyzer.getUniqueIps(logs);
        assertEquals(2, uniqueIps.size());
        assertTrue(uniqueIps.contains("127.0.0.1"));
        assertTrue(uniqueIps.contains("192.168.1.1"));
    }

    @Test
    void testGetTopIpsValidation() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.getTopIps(null, 5));
        assertThrows(IllegalArgumentException.class, () -> analyzer.getTopIps(Arrays.asList("some log"), 0));
        assertThrows(IllegalArgumentException.class, () -> analyzer.getTopIps(Arrays.asList("some log"), -2));
    }

    @Test
    void testGetTopIpsSortingAndTieBreaking() {
        List<String> logs = Arrays.asList(
            "10.0.0.1 - - [01/May/2025:00:00:00 +0000] \"GET /\" 200 100",
            "10.0.0.2 - - [01/May/2025:00:00:00 +0000] \"GET /\" 200 100",
            "10.0.0.2 - - [01/May/2025:00:00:00 +0000] \"GET /\" 200 100",
            "10.0.0.1 - - [01/May/2025:00:00:00 +0000] \"GET /\" 200 100"
        );

        List<String> top = analyzer.getTopIps(logs, 2);
        assertEquals(2, top.size());
        assertEquals("10.0.0.1", top.get(0));
        assertEquals("10.0.0.2", top.get(1));
    }

    @Test
    void testGetUniqueIpsEmptyList() {
        Set<String> uniqueIps = analyzer.getUniqueIps(List.of());
        assertTrue(uniqueIps.isEmpty());
    }

    @Test
    void testGetUniqueIpsNoValidLogs() {
        List<String> logs = Arrays.asList(
            "invalid line one",
            "another bad log format"
        );
        Set<String> uniqueIps = analyzer.getUniqueIps(logs);
        assertTrue(uniqueIps.isEmpty());
    }

    @Test
    void testGetTopIpsFewerThanTopN() {
        List<String> logs = Arrays.asList(
            "10.0.0.1 - - [01/May/2025:00:00:00 +0000] \"GET /\" 200 100"
        );
        List<String> top = analyzer.getTopIps(logs, 5);
        assertEquals(1, top.size());
        assertEquals("10.0.0.1", top.get(0));
    }
}