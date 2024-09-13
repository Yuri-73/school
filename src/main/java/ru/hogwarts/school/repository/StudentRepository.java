package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByAgeBetween(int min, int max); //Стандартный метод поиска студентов с отрезком нужных возрастов

    Collection<Student> findStudentsByFaculty_name(String faculty_name); //Стандартный метод поиска всех студентов с нужного имени факультета
}
