package com.bootgussy.dancecenterservice.api.controller;

import com.bootgussy.dancecenterservice.api.dto.create.ScheduleItemCreateDto;
import com.bootgussy.dancecenterservice.api.dto.response.GroupResponseDto;
import com.bootgussy.dancecenterservice.api.dto.response.ScheduleItemResponseDto;
import com.bootgussy.dancecenterservice.core.mapper.ScheduleItemMapper;
import com.bootgussy.dancecenterservice.core.model.Group;
import com.bootgussy.dancecenterservice.core.model.ScheduleItem;
import com.bootgussy.dancecenterservice.core.service.ScheduleItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
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
@RequestMapping("/api/schedule_item")
@Tag(name = "Schedule Item", description = "API for managing schedule items")
@RequiredArgsConstructor
public class ScheduleItemController {
    private final ScheduleItemService scheduleItemService;
    private final ScheduleItemMapper scheduleItemMapper;

    @Operation(summary = "Get schedule item by ID", description = "Retrieves a schedule item by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schedule item retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Schedule item not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleItemResponseDto> findScheduleItemById(
            @Parameter(description = "Schedule item's ID", example = "1") @PathVariable Long id) {
        ScheduleItem scheduleItem = scheduleItemService.findScheduleItemById(id);
        return ResponseEntity.ok(scheduleItemMapper.toResponseDto(scheduleItem));
    }

    @Operation(summary = "Get all schedule items", description = "Retrieves all schedule items")
    @ApiResponse(responseCode = "200", description = "Schedule items retrieved successfully")
    @GetMapping
    public ResponseEntity<List<ScheduleItemResponseDto>> findAllScheduleItems() {
        List<ScheduleItem> scheduleItems = scheduleItemService.findAllScheduleItems();
        return ResponseEntity.ok(scheduleItemMapper.toResponseDtoList(scheduleItems));
    }

    @Operation(summary = "Get schedule items by group",
            description = "Retrieves schedule items based on the specified group")
    @ApiResponse(responseCode = "200", description = "Schedule items retrieved successfully")
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<ScheduleItemResponseDto>> findAllScheduleItemsByGroup(
            @Parameter(description = "Group id", example = "0") @PathVariable Long groupId) {
        List<ScheduleItem> scheduleItems = scheduleItemService.findAllScheduleItemsByGroup(groupId);
        return ResponseEntity.ok(scheduleItemMapper.toResponseDtoList(scheduleItems));
    }

    @Operation(summary = "Create a new schedule item", description = "Creates a new schedule item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Schedule item successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "This schedule item already exists")
    })
    @PostMapping
    public ResponseEntity<ScheduleItemResponseDto> createScheduleItem(
            @Parameter(description = "Data to create the schedule item")
            @Valid @RequestBody ScheduleItemCreateDto createDto) {
        ScheduleItem scheduleItem = scheduleItemMapper.toEntity(createDto);
        ScheduleItem createdScheduleItem = scheduleItemService.createScheduleItem(scheduleItem);
        return new ResponseEntity<>(
                scheduleItemMapper.toResponseDto(createdScheduleItem),
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Create multiple schedule items", description = "Creates multiple schedule items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Schedule items successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Some schedule items already exist")
    })
    @PostMapping("/bulk")
    public ResponseEntity<List<ScheduleItemResponseDto>> createMultipleScheduleItems(
            @Parameter(description = "Data to create multiple schedule items")
            @Valid @RequestBody List<ScheduleItemCreateDto> createDtos) {
        List<ScheduleItem> scheduleItems = createDtos.stream()
                .map(scheduleItemMapper::toEntity)
                .toList();

        List<ScheduleItem> createdScheduleItems = scheduleItemService
                .createMultipleScheduleItems(scheduleItems);

        return new ResponseEntity<>(
                scheduleItemMapper.toResponseDtoList(createdScheduleItems),
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Update schedule item by ID", description = "Updates an existing schedule item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schedule item updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Schedule item not found"),
            @ApiResponse(responseCode = "409", description = "This schedule item already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ScheduleItemResponseDto> updateScheduleItem(
            @Parameter(description = "Schedule item's ID", example = "1") @PathVariable Long id,
            @Parameter(description = "Data to update the schedule item")
            @Valid @RequestBody ScheduleItemCreateDto createDto) {
        ScheduleItem scheduleItem = scheduleItemMapper.toEntity(createDto);
        scheduleItem.setId(id);
        ScheduleItem updatedScheduleItem = scheduleItemService.updateScheduleItem(scheduleItem);
        return ResponseEntity.ok(scheduleItemMapper.toResponseDto(updatedScheduleItem));
    }

    @Operation(summary = "Delete schedule item by ID", description = "Deletes an existing schedule item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Schedule item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Schedule item not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduleItem(
            @Parameter(description = "Schedule item's ID", example = "1") @PathVariable Long id) {
        scheduleItemService.deleteScheduleItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}