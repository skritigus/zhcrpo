package com.bootgussy.dancecenterservice.api.controller;

import com.bootgussy.dancecenterservice.core.aspect.VisitCounter;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/visits")
public class VisitCounterController {
    private final VisitCounter visitCounter;

    public VisitCounterController(VisitCounter visitCounter) {
        this.visitCounter = visitCounter;
    }

    @Operation(summary = "Get the number of visits for a specific URL")
    @GetMapping
    public ResponseEntity<Long> getVisitCount(@RequestParam String url) {
        long count = visitCounter.getVisitCount(url);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Get all visited URLs and their visit counts")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Long>> getAllVisitCounts() {
        Map<String, Long> allVisitCounts = visitCounter.getAllVisitCounts();
        return ResponseEntity.ok(allVisitCounts);
    }
}