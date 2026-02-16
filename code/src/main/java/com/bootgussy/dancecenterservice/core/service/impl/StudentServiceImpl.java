package com.bootgussy.dancecenterservice.core.service.impl;

import com.bootgussy.dancecenterservice.core.config.CacheConfig;
import com.bootgussy.dancecenterservice.core.exception.AlreadyExistsException;
import com.bootgussy.dancecenterservice.core.exception.ResourceNotFoundException;
import com.bootgussy.dancecenterservice.core.model.Student;
import com.bootgussy.dancecenterservice.core.repository.StudentRepository;
import com.bootgussy.dancecenterservice.core.service.StudentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final CacheConfig cacheConfig;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository,
                              CacheConfig cacheConfig) {
        this.studentRepository = studentRepository;
        this.cacheConfig = cacheConfig;
    }

    @Override
    public Student findStudentById(Long id) {
        Student cachedStudent = cacheConfig.getStudent(id);
        if (cachedStudent != null) {
            return cachedStudent;
        }

        Student student = studentRepository.findById(id).orElse(null);

        if (student != null) {
            cacheConfig.putStudent(id, student);

            return student;
        } else {
            throw new ResourceNotFoundException("Student not found. ID: " + id);
        }
    }

    @Override
    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student createStudent(Student student) {
        Student savedStudent;

        if (
                student.getUser().getName() == null ||
                        student.getUser().getPhoneNumber() == null
        ) {
            throw new ResourceNotFoundException("Incorrect JSON. All fields must be filled " +
                    "(name, phoneNumber).");
        }

        if (studentRepository.findByNameAndPhoneNumber(student.getUser().getName(), student.getUser().getPhoneNumber())
                .isEmpty()) {
            savedStudent = studentRepository.save(student);
        } else {
            throw new AlreadyExistsException("Student already exists." +
                    " Name: " + student.getUser().getName() +
                    ", Phone number: " + student.getUser().getPhoneNumber());
        }

        cacheConfig.putStudent(savedStudent.getId(), savedStudent);

        return savedStudent;
    }

    @Override
    public Student updateStudent(Student student) {
        Student updatedStudent;

        if (
                student.getUser().getName() == null ||
                        student.getUser().getPhoneNumber() == null
        ) {
            throw new ResourceNotFoundException("Incorrect JSON. All fields must be filled " +
                    "(name, phoneNumber).");
        }

        if (!studentRepository.findByNameAndPhoneNumber(student.getUser().getName(), student.getUser().getPhoneNumber())
                .isEmpty()) {
            throw new AlreadyExistsException("Student already exists." +
                    " Name: " + student.getUser().getName() +
                    ", Phone number: " + student.getUser().getPhoneNumber());
        }

        if (studentRepository.findById(student.getId()).isPresent()) {
            updatedStudent = studentRepository.save(student);
        } else {
            throw new ResourceNotFoundException("The student does not exist." +
                    " ID: " + student.getId() +
                    ", Name: " + student.getUser().getName() +
                    ", Phone number: " + student.getUser().getPhoneNumber());
        }

        cacheConfig.putStudent(updatedStudent.getId(), updatedStudent);

        return updatedStudent;
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found. ID: " + id));

        cacheConfig.removeStudent(student.getId());

        studentRepository.delete(student);
    }
}