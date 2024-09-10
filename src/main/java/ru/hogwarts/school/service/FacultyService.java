package ru.hogwarts.school.service;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.NoFacultyColorException;
import ru.hogwarts.school.exception.NullEmptyColorException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        facultyRepository.save(faculty);
        return faculty;
    }

    public Faculty findFaculty(Long id) {
       Faculty faculty = facultyRepository.findById(id).orElse(null);
       return faculty;
    }

    public Faculty editFaculty(Faculty faculty) {
        return facultyRepository.findById(faculty.getId())
                .map(e -> facultyRepository.save(faculty))
                .orElse(null);
    }

    public Faculty deleteFaculty(Long id) {
        var entity = facultyRepository.findById(id).orElse(null);
        if (entity != null) {
            facultyRepository.delete(entity);
        }
        return entity;
    }

    public Collection<Faculty> getAllFaculty() {
        return facultyRepository.findAll();
    }

    public Collection<String> getFacultyByColor(String color) { //Ищем в коллекции-Мапе факультет по цвету на входе
        if (color == null || color.isEmpty())
            throw new NullEmptyColorException();
        Collection<String> facultyListByColor = getAllFaculty()
                .stream()
                .filter(e -> e.getColor().equals(color))
                .map(e -> e.getName())
                .collect(Collectors.toList());
        if (facultyListByColor.isEmpty())
            throw new NoFacultyColorException();
        return facultyListByColor;
    }
}

