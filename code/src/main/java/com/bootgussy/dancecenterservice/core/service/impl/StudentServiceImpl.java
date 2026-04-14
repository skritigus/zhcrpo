package com.bootgussy.dancecenterservice.core.service.impl;

import com.bootgussy.dancecenterservice.core.config.CacheConfig;
import com.bootgussy.dancecenterservice.core.exception.AlreadyExistsException;
import com.bootgussy.dancecenterservice.core.exception.ResourceNotFoundException;
import com.bootgussy.dancecenterservice.core.model.Role;
import com.bootgussy.dancecenterservice.core.model.Student;
import com.bootgussy.dancecenterservice.core.model.User;
import com.bootgussy.dancecenterservice.core.repository.RoleRepository;
import com.bootgussy.dancecenterservice.core.repository.StudentRepository;
import com.bootgussy.dancecenterservice.core.repository.UserRepository;
import com.bootgussy.dancecenterservice.core.service.StudentService;
import java.util.List;
import java.util.Collections;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CacheConfig cacheConfig;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository,
                              UserRepository userRepository,
                              RoleRepository roleRepository,
                              PasswordEncoder passwordEncoder,
                              CacheConfig cacheConfig) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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
    @Transactional
    public List<Student> findAllStudents() {
        // Sync any users who have the role but no entity
        List<User> studentsWithRole = userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName().equals("STUDENT")))
                .toList();
        
        for (User user : studentsWithRole) {
            if (studentRepository.findByUserId(user.getId()).isEmpty()) {
                Student student = new Student();
                student.setUser(user);
                studentRepository.save(student);
            }
        }
        
        return studentRepository.findAll().stream()
                .filter(s -> s.getUser().getRoles().stream().anyMatch(r -> r.getName().equals("STUDENT")))
                .toList();
    }

    @Override
    @Transactional
    public Student createStudent(Student student) {
        if (student.getUser() == null) {
            throw new ResourceNotFoundException("User data is missing in Student object");
        }

        User userData = student.getUser();
        String phone = userData.getPhoneNumber();
        
        // Find existing user or create new one
        User user = userRepository.findByPhoneNumber(phone).orElseGet(() -> {
            User newUser = new User();
            newUser.setPhoneNumber(phone);
            newUser.setName(userData.getName());
            if (userData.getPassword() != null) {
                newUser.setPassword(passwordEncoder.encode(userData.getPassword()));
            }
            return newUser;
        });

        // Ensure STUDENT role exists
        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new ResourceNotFoundException("Role STUDENT not found"));
        
        if (user.getRoles() == null) {
            user.setRoles(new java.util.ArrayList<>(Collections.singletonList(studentRole)));
        } else if (!user.getRoles().contains(studentRole)) {
            user.getRoles().add(studentRole);
        }

        userRepository.save(user);
        student.setUser(user);

        if (studentRepository.findByUserId(user.getId()).isPresent()) {
            throw new AlreadyExistsException("Student record already exists for this user.");
        }

        Student savedStudent = studentRepository.save(student);
        cacheConfig.putStudent(savedStudent.getId(), savedStudent);

        return savedStudent;
    }

    @Override
    @Transactional
    public Student updateStudent(Student student) {
        Student existingStudent = studentRepository.findById(student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found. ID: " + student.getId()));

        User user = existingStudent.getUser();
        user.setName(student.getUser().getName());
        user.setPhoneNumber(student.getUser().getPhoneNumber());
        
        if (student.getUser().getPassword() != null && !student.getUser().getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(student.getUser().getPassword()));
        }

        Student updated = studentRepository.save(existingStudent);
        cacheConfig.putStudent(updated.getId(), updated);

        return updated;
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found. ID: " + id));

        User user = student.getUser();
        if (user != null) {
            Role studentRole = roleRepository.findByName("STUDENT").orElse(null);
            if (studentRole != null && user.getRoles() != null) {
                user.getRoles().remove(studentRole);
                userRepository.save(user);
            }
        }

        cacheConfig.removeStudent(student.getId());
        studentRepository.delete(student);
    }

    @Override
    @Transactional
    public Student findStudentByUserId(Long userId) {
        return studentRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
                    if (user.getRoles().stream().anyMatch(r -> r.getName().equals("STUDENT"))) {
                        Student s = new Student();
                        s.setUser(user);
                        return studentRepository.save(s);
                    }
                    throw new ResourceNotFoundException("Student entity not found and user doesn't have Student role");
                });
    }
}