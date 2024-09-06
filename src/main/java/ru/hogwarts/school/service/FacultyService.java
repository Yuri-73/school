package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.NoFacultyColorException;
import ru.hogwarts.school.exception.NullEmptyColorException;
import ru.hogwarts.school.model.Faculty;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacultyService {
    private final Map<Long, Faculty> allFaculty = new HashMap<>(); //Пустая пополняемая Мапа
    private Long countId = 0L; //Создаем поле идентификатора и инициализируем нулём, чтобы в первом же ключе инкриментировать

    public Faculty createFaculty(Faculty faculty) { //Вносим в коллекцию-Мапу факультеты по одному
        faculty.setId(++countId);
        allFaculty.put(countId, faculty);
        return faculty;
    }

    public Faculty findFaculty(Long id) { //Находим в коллекции факультет по ID
        if (allFaculty.containsKey(id)) {
            return allFaculty.get(id);
        }
        return null;
    }

    public Faculty editFaculty(Faculty faculty) { //Редактируем объект в коллекции-Мапе по ID
        if (allFaculty.containsKey(faculty.getId())) {
            allFaculty.put(faculty.getId(), faculty);
            return faculty;
        }
        return null;
    }

    public Faculty deleteFaculty(Long id) { //Удаляем из коллекции-Мапы факультет по ID
        if (allFaculty.get(id) != null) {
            Faculty faculty = allFaculty.remove(id);
            System.out.println("faculty: " + faculty);
            return faculty;
        }
        return null;
    }

    public Collection<Faculty> getAllFaculty() { //Выводим из коллекции-Мапы всех факультетов
        return allFaculty.values(); //Превращение Мапы в Лист
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

