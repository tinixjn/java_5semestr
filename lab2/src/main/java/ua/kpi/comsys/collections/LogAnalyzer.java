package ua.kpi.comsys.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogAnalyzer {

    private static final Pattern LOG_PATTERN = Pattern.compile("^(\\S+) \\S+ \\S+ \\[.*\\] \".*\" \\d+ \\d+.*$");

    public Set<String> getUniqueIps(List<String> logLines) {
        if (logLines == null) {
            throw new IllegalArgumentException("logLines cannot be null");
        }

        Set<String> uniqueIps = new HashSet<>();
        for (String line : logLines) {
            if (line != null) {
                Matcher matcher = LOG_PATTERN.matcher(line);
                if (matcher.matches()) {
                    uniqueIps.add(matcher.group(1));
                }
            }
        }
        return uniqueIps;
    }

    public List<String> getTopIps(List<String> logLines, int topN) {
        if (logLines == null) {
            throw new IllegalArgumentException("logLines cannot be null");
        }
        if (topN <= 0) {
            throw new IllegalArgumentException("topN must be greater than 0");
        }

        Map<String, Integer> ipCounts = new HashMap<>();
        for (String line : logLines) {
            if (line != null) {
                Matcher matcher = LOG_PATTERN.matcher(line);
                if (matcher.matches()) {
                    String ip = matcher.group(1);
                    ipCounts.put(ip, ipCounts.getOrDefault(ip, 0) + 1);
                }
            }
        }

        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(ipCounts.entrySet());

        Collections.sort(entryList, (e1, e2) -> {
            int freqCompare = e2.getValue().compareTo(e1.getValue());
            if (freqCompare != 0) {
                return freqCompare;
            }
            return e1.getKey().compareTo(e2.getKey());
        });

        List<String> result = new ArrayList<>();
        int count = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            if (count >= topN) {
                break;
            }
            result.add(entry.getKey());
            count++;
        }

        return result;
    }
}