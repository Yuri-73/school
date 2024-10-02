package ru.hogwarts.school.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.hogwarts.school.exception.NoStudentAgeException;
import ru.hogwarts.school.exception.NullAgeException;
import ru.hogwarts.school.exception.StudentNotFoundException;
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
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
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

    //ДЗ-3.3:
    public Collection<Student> getStudentByAge(Integer age) {
        if (age <= 0) {
            throw new NullAgeException(age);
        }
        Collection<Student> studentListByAge = getAllStudent()
                .stream()
                .filter(e -> e.getAge() == age)
                .collect(Collectors.toList());
        if (studentListByAge.isEmpty())
            throw new NoStudentAgeException(age);
        return studentListByAge;
    }

    //ДЗ-3.4 шаг 1.1:
    public List<Student> findByAgeBetween(Integer min, Integer max) {
        return studentRepository.findByAgeBetween(min, max);
    }

    //ДЗ-3.4 шаг 4.2* (по имени факультета):
    public Collection<Student> findStudentsByFacultyName(String facultyName) {
        return studentRepository.findStudentsByFacultyName(facultyName);
    }

    //ДЗ-3.4 шаг 4.2 (по Id студента):
    public Faculty getFacultyOfStudent(Long id) {
        return studentRepository.findById(id)
                .map(Student::getFaculty)
                .orElse(null);
    }
}

