package ru.hogwarts.school.repository;

import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    public Collection<Faculty> findByColorIgnoreCase(String color);

    public Faculty findFacultyByStudentsIs(Student student);

    public Faculty findByNameAndColor(String name, String color);

}
