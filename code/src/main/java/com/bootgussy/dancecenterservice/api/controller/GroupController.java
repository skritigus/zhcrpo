package com.bootgussy.dancecenterservice.api.controller;

import com.bootgussy.dancecenterservice.api.dto.create.GroupCreateDto;
import com.bootgussy.dancecenterservice.api.dto.response.GroupResponseDto;
import com.bootgussy.dancecenterservice.core.mapper.GroupMapper;
import com.bootgussy.dancecenterservice.core.model.Group;
import com.bootgussy.dancecenterservice.core.service.GroupService;
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
@RequestMapping("/api/group")
@Tag(name = "Group", description = "API for managing groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final GroupMapper groupMapper;

    @Operation(summary = "Get group by ID", description = "Retrieves a group by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Group not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponseDto> findGroupById(
            @Parameter(description = "Group's ID", example = "1") @PathVariable Long id) {
        Group group = groupService.findGroupById(id);
        return ResponseEntity.ok(groupMapper.toResponseDto(group));
    }

    @Operation(summary = "Get all groups", description = "Retrieves all groups")
    @ApiResponse(responseCode = "200", description = "Groups retrieved successfully")
    @GetMapping
    public ResponseEntity<List<GroupResponseDto>> findAllGroups() {
        List<Group> groups = groupService.findAllGroups();
        return ResponseEntity.ok(groupMapper.toResponseDtoList(groups));
    }

    @Operation(summary = "Get groups by dance style",
            description = "Retrieves groups based on the specified dance style")
    @ApiResponse(responseCode = "200", description = "Groups retrieved successfully")
    @GetMapping("/dance_style/{danceStyle}")
    public ResponseEntity<List<GroupResponseDto>> findAllGroupsByDanceStyle(
            @Parameter(description = "Dance style", example = "Ballet") @PathVariable String danceStyle) {
        List<Group> groups = groupService.findAllGroupsByDanceStyle(danceStyle);
        return ResponseEntity.ok(groupMapper.toResponseDtoList(groups));
    }

    @Operation(summary = "Create a new group", description = "Creates a new group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Group successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "This group already exists")
    })
    @PostMapping
    public ResponseEntity<GroupResponseDto> createGroup(
            @Parameter(description = "Data to create the group")
            @Valid @RequestBody GroupCreateDto createDto) {
        Group group = groupMapper.toEntity(createDto);
        Group createdGroup = groupService.createGroup(group);
        return new ResponseEntity<>(groupMapper.toResponseDto(createdGroup), HttpStatus.CREATED);
    }

    @Operation(summary = "Update group by ID", description = "Updates an existing group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Group not found"),
            @ApiResponse(responseCode = "409", description = "This group already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GroupResponseDto> updateGroup(
            @Parameter(description = "Group's ID", example = "1") @PathVariable Long id,
            @Parameter(description = "Data to update the group")
            @Valid @RequestBody GroupCreateDto createDto) {
        Group group = groupMapper.toEntity(createDto);
        group.setId(id);
        Group updatedGroup = groupService.updateGroup(group);
        return ResponseEntity.ok(groupMapper.toResponseDto(updatedGroup));
    }

    @Operation(summary = "Delete group by ID", description = "Deletes an existing group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Group deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Group not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(
            @Parameter(description = "Group's ID", example = "1") @PathVariable Long id) {
        groupService.deleteGroup(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}