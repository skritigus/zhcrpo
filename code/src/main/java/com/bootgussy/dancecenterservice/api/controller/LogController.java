package com.bootgussy.dancecenterservice.api.controller;

import com.bootgussy.dancecenterservice.core.aspect.LogFileId;
import com.bootgussy.dancecenterservice.core.model.LogFileTask;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogController {
    private final LogFileId logFileId;
    private static final Logger LOGGER = LoggerFactory.getLogger(LogController.class);

    public LogController(LogFileId logFileId) {
        this.logFileId = logFileId;
    }

    @Operation(summary = "Request log file sorted by date and logging level")
    @GetMapping
    public ResponseEntity<byte[]> getLogFile(
            @RequestParam String date,
            @RequestParam(required = false, defaultValue = "all") String level) {
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            LOGGER.warn("Invalid date format: {}", date);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String logFileName = "logs/dance_center-" + date + ".0.log";
        Path logFilePath = Path.of(logFileName).normalize();
        LOGGER.info("Checking for log file at: {}", logFilePath);

        if (Files.exists(logFilePath)) {
            try (var linesStream = Files.lines(logFilePath, StandardCharsets.UTF_8)) {
                List<String> lines;

                if (!"all".equalsIgnoreCase(level)) {
                    String logLevel = level.toUpperCase();
                    var logPattern = Pattern.compile(
                            "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} \\[.*?\\] " +
                                    logLevel + " ");

                    lines = linesStream.filter(line -> logPattern.matcher(line).find()).toList();
                } else {
                    lines = linesStream.toList();
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.TEXT_PLAIN);
                headers.setContentDispositionFormData("attachment", "dance_center-" + date +
                        "-" + level + ".log");

                byte[] logFileBytes = String.join("\n", lines).getBytes(StandardCharsets.UTF_8);

                LOGGER.info("Log file retrieved successfully: {}", logFileName);
                return new ResponseEntity<>(logFileBytes, headers, HttpStatus.OK);
            } catch (IOException e) {
                LOGGER.error("Error reading log file: {}", e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            LOGGER.warn("Log file not found: {}", logFileName);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Create a task to generate a log file asynchronously")
    @PostMapping("/generate")
    public ResponseEntity<String> createLogFileTask(
            @RequestParam String date,
            @RequestParam(required = false, defaultValue = "all") String level) {
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            LOGGER.warn("Invalid date format for task creation: {}", date);
            return new ResponseEntity<>("Invalid date format", HttpStatus.BAD_REQUEST);
        }

        String taskId = logFileId.createLogFileTask(date, level);
        LOGGER.info("Log file generation task created with ID: {}", taskId);
        return new ResponseEntity<>(taskId, HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Get the status of a log file generation task")
    @GetMapping("/status/{taskId}")
    public ResponseEntity<LogFileTask> getTaskStatus(@PathVariable String taskId) {
        LogFileTask task = logFileId.getTaskStatus(taskId);
        if (task == null) {
            LOGGER.warn("Task not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        LOGGER.info("Task status retrieved");
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @Operation(summary = "Download the generated log file by task ID")
    @GetMapping("/download/{taskId}")
    public ResponseEntity<byte[]> downloadLogFile(@PathVariable String taskId) {
        Path filePath = logFileId.getLogFilePath(taskId);
        if (filePath == null) {
            LOGGER.warn("Log file not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", filePath.getFileName().toString());
            LOGGER.info("Log file downloaded successfully: {}", filePath.getFileName());
            byte[] fileBytes = Files.readAllBytes(filePath);
            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            LOGGER.error("Error downloading log file: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}