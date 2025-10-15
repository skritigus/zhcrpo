package com.bootgussy.dancecenterservice.api.controller;

import com.bootgussy.dancecenterservice.api.dto.create.TrainerCreateDto;
import com.bootgussy.dancecenterservice.api.dto.response.TrainerResponseDto;
import com.bootgussy.dancecenterservice.core.mapper.TrainerMapper;
import com.bootgussy.dancecenterservice.core.model.Trainer;
import com.bootgussy.dancecenterservice.core.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainer")
@Tag(name = "Trainer", description = "API for managing trainers")
@RequiredArgsConstructor
public class TrainerController {
    private final TrainerService trainerService;
    private final TrainerMapper trainerMapper;

    @Operation(summary = "Get trainer by ID", description = "Retrieves a trainer by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TrainerResponseDto> findTrainerById(
            @Parameter(description = "Trainer's ID", example = "1") @PathVariable Long id) {
        Trainer trainer = trainerService.findTrainerById(id);
        return ResponseEntity.ok(trainerMapper.toResponseDto(trainer));
    }

    @Operation(summary = "Get all trainers", description = "Retrieves all trainers")
    @ApiResponse(responseCode = "200", description = "Trainers retrieved successfully")
    @GetMapping
    public ResponseEntity<List<TrainerResponseDto>> findAllTrainers() {
        List<Trainer> trainers = trainerService.findAllTrainers();
        return ResponseEntity.ok(trainerMapper.toResponseDtoList(trainers));
    }

    @Operation(summary = "Create a new trainer", description = "Creates a new trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainer successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "This trainer already exists")
    })
    @PostMapping
    public ResponseEntity<TrainerResponseDto> createTrainer(
            @Parameter(description = "Data to create the trainer")
            @Valid @RequestBody TrainerCreateDto createDto) {
        Trainer trainer = trainerMapper.toEntity(createDto);
        Trainer createdTrainer = trainerService.createTrainer(trainer);
        return new ResponseEntity<>(
                trainerMapper.toResponseDto(createdTrainer),
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Update trainer by ID", description = "Updates an existing trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "409", description = "This trainer already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TrainerResponseDto> updateTrainer(
            @Parameter(description = "Trainer's ID", example = "1") @PathVariable Long id,
            @Parameter(description = "Data to update the trainer")
            @Valid @RequestBody TrainerCreateDto createDto) {
        Trainer trainer = trainerMapper.toEntity(createDto);
        trainer.setId(id);
        Trainer updatedTrainer = trainerService.updateTrainer(trainer);
        return ResponseEntity.ok(trainerMapper.toResponseDto(updatedTrainer));
    }

    @Operation(summary = "Delete trainer by ID", description = "Deletes an existing trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trainer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainer(
            @Parameter(description = "Trainer's ID", example = "1") @PathVariable Long id) {
        trainerService.deleteTrainer(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}