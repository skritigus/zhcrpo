package com.bootgussy.dancecenterservice.service;

import com.bootgussy.dancecenterservice.core.config.CacheConfig;
import com.bootgussy.dancecenterservice.core.exception.AlreadyExistsException;
import com.bootgussy.dancecenterservice.core.exception.ResourceNotFoundException;
import com.bootgussy.dancecenterservice.core.model.Student;
import com.bootgussy.dancecenterservice.core.repository.StudentRepository;
import com.bootgussy.dancecenterservice.core.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StudentServiceImplTest {

    @InjectMocks
    private StudentServiceImpl studentService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CacheConfig cacheConfig;

    private Student student;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        student = new Student(1L, "Olya", "1234", null);
    }

    @Test
    void findStudentById_ExistingStudent_ReturnsStudent() {
        when(cacheConfig.getStudent(student.getId())).thenReturn(null);
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));

        Student foundStudent = studentService.findStudentById(student.getId());

        assertNotNull(foundStudent);
        assertEquals(student.getId(), foundStudent.getId());
        verify(cacheConfig).putStudent(student.getId(), student);
    }

    @Test
    void findStudentById_NonExistingStudent_ThrowsException() {
        when(cacheConfig.getStudent(student.getId())).thenReturn(null);
        when(studentRepository.findById(student.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> studentService.findStudentById(student.getId())
        );

        assertEquals("Student not found. ID: 1", exception.getMessage());
    }

    @Test
    void findAllStudents_ReturnsAllStudents() {
        when(studentRepository.findAll()).thenReturn(Collections.singletonList(student));

        var students = studentService.findAllStudents();

        assertNotNull(students);
        assertEquals(1, students.size());
        assertEquals(student, students.get(0));
    }

    @Test
    void createStudent_ValidStudent_CreatesStudent() {
        when(studentRepository.findByNameAndPhoneNumber(student.getName(), student.getPhoneNumber()))
                .thenReturn(Collections.emptyList());
        when(studentRepository.save(student)).thenReturn(student);

        Student createdStudent = studentService.createStudent(student);

        assertNotNull(createdStudent);
        assertEquals(student.getId(), createdStudent.getId());
        verify(cacheConfig).putStudent(student.getId(), createdStudent);
    }

    @Test
    void createStudent_StudentAlreadyExists_ThrowsException() {
        when(studentRepository.findByNameAndPhoneNumber(student.getName(), student.getPhoneNumber()))
                .thenReturn(Collections.singletonList(student));

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            studentService.createStudent(student);
        });

        assertEquals("Student already exists. Name: Olya, Phone number: 1234", exception.getMessage());
    }

    @Test
    void createStudent_NullName_ThrowsException() {
        student.setName(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            studentService.createStudent(student);
        });

        assertEquals("Incorrect JSON. All fields must be filled (name, phoneNumber).", exception.getMessage());
    }

    @Test
    void createStudent_NullPhoneNumber_ThrowsException() {
        student.setPhoneNumber(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            studentService.createStudent(student);
        });

        assertEquals("Incorrect JSON. All fields must be filled (name, phoneNumber).", exception.getMessage());
    }

    @Test
    void updateStudent_ValidStudent_UpdatesStudent() {
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(studentRepository.findByNameAndPhoneNumber(student.getName(), student.getPhoneNumber()))
                .thenReturn(Collections.emptyList());
        when(studentRepository.save(student)).thenReturn(student);

        Student updatedStudent = studentService.updateStudent(student);

        assertNotNull(updatedStudent);
        assertEquals(student.getId(), updatedStudent.getId());
        verify(cacheConfig).putStudent(student.getId(), updatedStudent);
    }

    @Test
    void updateStudent_StudentNotFound_ThrowsException() {
        when(studentRepository.findById(student.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            studentService.updateStudent(student);
        });

        assertEquals("The student does not exist. ID: 1, Name: Olya, Phone number: 1234", exception.getMessage());
    }

    @Test
    void updateStudent_StudentAlreadyExists_ThrowsException() {
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(studentRepository.findByNameAndPhoneNumber(student.getName(), student.getPhoneNumber()))
                .thenReturn(Collections.singletonList(student));

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            studentService.updateStudent(student);
        });

        assertEquals("Student already exists. Name: Olya, Phone number: 1234", exception.getMessage());
    }

    @Test
    void deleteStudent_ValidId_DeletesStudent() {
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));

        studentService.deleteStudent(student.getId());

        verify(studentRepository).delete(student);
        verify(cacheConfig).removeStudent(student.getId());
    }

    @Test
    void deleteStudent_NonExistingId_ThrowsException() {
        when(studentRepository.findById(student.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> studentService.deleteStudent(student.getId())
        );

        assertEquals("Student not found. ID: 1", exception.getMessage());
    }
}