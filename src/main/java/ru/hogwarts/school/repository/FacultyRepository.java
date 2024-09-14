package ru.hogwarts.school.repository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    public Faculty findByColorIgnoreCase(String color);

    public Faculty findByNameAndColor(String name, String color);

    public Faculty findByName(String name);
}
