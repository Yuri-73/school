package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.NoStudentAgeException;
import ru.hogwarts.school.exception.NullAgeException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student findStudent(Long id) {
        Student student = studentRepository.findById(id).orElse(null);
        return student;
    }

    public Student editStudent(Student student) {
        return studentRepository.findById(student.getId())
                .map(e -> studentRepository.save(student))
                .orElse(null);
    }

    public Student deleteStudent(Long id) {
        var entity = studentRepository.findById(id).orElse(null);
        if (entity != null) {
            studentRepository.delete(entity);
        }
        return entity;
    }

    public Collection<Student> getAllStudent() {
        return studentRepository.findAll();
    }

    public Collection<String> getStudentByAge(Integer age) {
        if (age <= 0) {
            throw new NullAgeException();
        }
        Collection<String> studentListByAge = getAllStudent()
                .stream()
                .filter(e -> e.getAge() == age)
                .map(e -> e.getName())
                .collect(Collectors.toList());
        if (studentListByAge.isEmpty())
            throw new NoStudentAgeException();
        return studentListByAge;
    }
}

