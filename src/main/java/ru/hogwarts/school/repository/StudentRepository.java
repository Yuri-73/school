package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByAgeBetween(int min, int max); //Стандартный метод поиска студентов с отрезком нужных возрастов

    Collection<Student> findStudentsByFacultyName(String facultyName); //Стандартный метод поиска всех студентов по имени факультета

    public void deleteById(Long id);  //Для очищения интеграционных тестов, если не пользоваться тестовой базой

    //Методы SQL-запросов из приложения (3 шт.): шаг 1 ДЗ-4.1:
    @Query(value = "SELECT COUNT(*) FROM Student", nativeQuery = true)
    public Integer getAllStudentInSchool();

    @Query(value = "SELECT AVG(age) FROM Student", nativeQuery = true)
    public Integer getMidlAgeStudent();

    @Query(value = "SELECT * FROM Student ORDER BY id DESC LIMIT 5", nativeQuery = true)  //В обратном порядке последние 5 студентов
    public List<Student> getFiveLastBackStudents();
}


