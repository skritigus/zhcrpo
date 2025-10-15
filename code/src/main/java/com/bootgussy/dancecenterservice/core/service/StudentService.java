package com.bootgussy.dancecenterservice.core.service;

import com.bootgussy.dancecenterservice.core.model.Student;
import java.util.List;

public interface StudentService {
    Student findStudentById(Long id);

    List<Student> findAllStudents();

    Student createStudent(Student student);

    Student updateStudent(Student student);

    void deleteStudent(Long id);
}
