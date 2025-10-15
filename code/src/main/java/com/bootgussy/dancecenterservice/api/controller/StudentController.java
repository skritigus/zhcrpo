
package com.bootgussy.dancecenterservice.api.controller;

import com.bootgussy.dancecenterservice.api.dto.create.StudentCreateDto;
import com.bootgussy.dancecenterservice.api.dto.response.StudentResponseDto;
import com.bootgussy.dancecenterservice.core.mapper.StudentMapper;
import com.bootgussy.dancecenterservice.core.model.Student;
import com.bootgussy.dancecenterservice.core.service.StudentService;
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
@RequestMapping("/api/student")
@Tag(name = "Student", description = "API for managing students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;
    private final StudentMapper studentMapper;

    @Operation(summary = "Get student by ID", description = "Retrieves a student by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> findStudentById(
            @Parameter(description = "Student's ID", example = "1") @PathVariable Long id) {
        Student student = studentService.findStudentById(id);
        return ResponseEntity.ok(studentMapper.toResponseDto(student));
    }

    @Operation(summary = "Get all students", description = "Retrieves all students")
    @ApiResponse(responseCode = "200", description = "Students retrieved successfully")
    @GetMapping
    public ResponseEntity<List<StudentResponseDto>> findAllStudents() {
        List<Student> students = studentService.findAllStudents();
        return ResponseEntity.ok(studentMapper.toResponseDtoList(students));
    }

    @Operation(summary = "Create a new student", description = "Creates a new student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Student successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "This student already exists")
    })
    @PostMapping
    public ResponseEntity<StudentResponseDto> createStudent(
            @Parameter(description = "Data to create the student")
            @Valid @RequestBody StudentCreateDto createDto) {
        Student student = studentMapper.toEntity(createDto);
        Student createdStudent = studentService.createStudent(student);
        return new ResponseEntity<>(
                studentMapper.toResponseDto(createdStudent),
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Update student by ID", description = "Updates an existing student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "409", description = "This student already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> updateStudent(
            @Parameter(description = "Student's ID", example = "1") @PathVariable Long id,
            @Parameter(description = "Data to update the student")
            @Valid @RequestBody StudentCreateDto createDto) {
        Student student = studentMapper.toEntity(createDto);
        student.setId(id);
        Student updatedStudent = studentService.updateStudent(student);
        return ResponseEntity.ok(studentMapper.toResponseDto(updatedStudent));
    }

    @Operation(summary = "Delete student by ID", description = "Deletes an existing student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Student deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @Parameter(description = "Student's ID", example = "1") @PathVariable Long id) {
        studentService.deleteStudent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}