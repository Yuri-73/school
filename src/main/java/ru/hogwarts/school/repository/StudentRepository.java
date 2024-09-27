package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByAgeBetween(int min, int max); //Стандартный метод поиска студентов с отрезком нужных возрастов

    Collection<Student> findStudentsByFacultyName(String facultyName); //Стандартный метод поиска всех студентов по имени факультета

    public void deleteById(Long id);
}
