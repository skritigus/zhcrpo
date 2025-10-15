package com.bootgussy.dancecenterservice.api.controller;

import com.bootgussy.dancecenterservice.api.dto.create.HallCreateDto;
import com.bootgussy.dancecenterservice.api.dto.response.HallResponseDto;
import com.bootgussy.dancecenterservice.core.mapper.HallMapper;
import com.bootgussy.dancecenterservice.core.model.Hall;
import com.bootgussy.dancecenterservice.core.service.HallService;
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
@RequestMapping("/api/hall")
@Tag(name = "Hall", description = "API for managing halls")
@RequiredArgsConstructor
public class HallController {
    private final HallService hallService;
    private final HallMapper hallMapper;

    @Operation(summary = "Get hall by ID", description = "Retrieves a hall by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hall retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Hall not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<HallResponseDto> findHallById(
            @Parameter(description = "Hall's ID", example = "1") @PathVariable Long id) {
        Hall hall = hallService.findHallById(id);
        return ResponseEntity.ok(hallMapper.toResponseDto(hall));
    }

    @Operation(summary = "Get all halls", description = "Retrieves all halls")
    @ApiResponse(responseCode = "200", description = "Halls retrieved successfully")
    @GetMapping
    public ResponseEntity<List<HallResponseDto>> findAllHalls() {
        List<Hall> halls = hallService.findAllHalls();
        return ResponseEntity.ok(hallMapper.toResponseDtoList(halls));
    }

    @Operation(summary = "Create a new hall", description = "Creates a new hall")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Hall successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "This hall already exists")
    })
    @PostMapping
    public ResponseEntity<HallResponseDto> createHall(
            @Parameter(description = "Data to create the hall") @Valid @RequestBody HallCreateDto createDto) {
        Hall hall = hallMapper.toEntity(createDto);
        Hall createdHall = hallService.createHall(hall);
        return new ResponseEntity<>(hallMapper.toResponseDto(createdHall), HttpStatus.CREATED);
    }

    @Operation(summary = "Update hall by ID", description = "Updates an existing hall")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hall updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Hall not found"),
            @ApiResponse(responseCode = "409", description = "This hall already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<HallResponseDto> updateHall(
            @Parameter(description = "Hall's ID", example = "1") @PathVariable Long id,
            @Parameter(description = "Data to update the hall") @Valid @RequestBody HallCreateDto createDto) {
        Hall hall = hallMapper.toEntity(createDto);
        hall.setId(id);
        Hall updatedHall = hallService.updateHall(hall);
        return ResponseEntity.ok(hallMapper.toResponseDto(updatedHall));
    }

    @Operation(summary = "Delete hall by ID", description = "Deletes an existing hall")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Hall deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Hall not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHall(
            @Parameter(description = "Hall's ID", example = "1") @PathVariable Long id) {
        hallService.deleteHall(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}