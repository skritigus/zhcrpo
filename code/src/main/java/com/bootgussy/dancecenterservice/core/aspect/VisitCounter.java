package com.bootgussy.dancecenterservice.core.aspect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class VisitCounter {
    private final Map<String, AtomicLong> visitCounts = new ConcurrentHashMap<>();

    public void incrementVisit(String url) {
        visitCounts.computeIfAbsent(url, k -> new AtomicLong(0)).incrementAndGet();
    }

    public long getVisitCount(String url) {
        AtomicLong counter = visitCounts.get(url);
        return counter != null ? counter.get() : 0;
    }

    public Map<String, Long> getAllVisitCounts() {
        return visitCounts.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get()));
    }
}