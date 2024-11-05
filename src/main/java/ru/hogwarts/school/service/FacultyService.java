package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.NoFacultyColorException;
import ru.hogwarts.school.exception.NullEmptyColorException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    /**
     * ДЗ-4.6 Включение логирования результатов для факультета
     */
    private final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    public FacultyService(FacultyRepository facultyRepository) {
        logger.info("Запуск конструктора класса FacultyService");
        this.facultyRepository = facultyRepository;
    }

    /**
     * Внесение носого факультета в БД:
     */
    public Faculty createFaculty(Faculty faculty) {
        logger.info("Старт метода createFaculty");
        return facultyRepository.save(faculty);
    }

    /**
     * Нахождение факультета по его id:
     */
    public Faculty findFaculty(Long id) {
       logger.info("Старт метода findFaculty с id = " + id);
       Faculty faculty = facultyRepository.findById(id).orElse(null);
       return faculty;
    }

    /**
     * Редактирование факультета:
     */
    public Faculty editFaculty(Faculty faculty) {
        logger.info("Старт метода editFaculty");
        return facultyRepository.findById(faculty.getId())
                .map(e -> facultyRepository.save(faculty))
                .orElse(null);
    }

    /**
     * Удаление факультета по его id:
     */
    public Faculty deleteFaculty(Long id) {
        logger.info("Старт метода deleteFaculty");
        var entity = facultyRepository.findById(id).orElse(null);
        if (entity != null) {
            facultyRepository.delete(entity);
        }
        return entity;
    }

    /**
     * Вывод всех факультетов из БД:
     */
    public Collection<Faculty> getAllFaculty() {
        logger.info("Старт метода getAllFaculty");
        return facultyRepository.findAll();
    }

    /**
     *  ДЗ-3.3: Нахождение факультета по его цвету:
     */
    public Collection<String> getFacultyByColor(String color) {
        logger.info("Старт метода getFacultyByColor");
        if (color == null || color.isEmpty()) {
            logger.error("There is no such color = " + color);
            throw new NullEmptyColorException();
        }
        Collection<String> facultyListByColor = getAllFaculty()
                .stream()
                .filter(e -> e.getColor().equals(color))
                .map(e -> e.getName())
                .collect(Collectors.toList());
        if (facultyListByColor.isEmpty()) {
            logger.error("Отсутствует факультет с color = " + color);
            throw new NoFacultyColorException();
        }
        return facultyListByColor;
    }

    /**
     *  ДЗ-3.4 шаг 1.2: Нахождение факультета по его цвету с игнорированием разряда color:
     */
    public Faculty findByColorIgnoreCase(String color) {
        logger.info("Старт метода findByColorIgnoreCase");
        return facultyRepository.findByColorIgnoreCase(color);
    }

    /**
     *  ДЗ-3.4 шаг 1.2(доп.): Нахождение факультета по его имени и цвету:
     */
    public Faculty findByNameAndColor(String name, String color) {
        logger.info("Старт метода findByNameAndColor");
        return facultyRepository.findByNameAndColor(name, color);
    }

     /**
     *  ДЗ-3.4 шаг 1.2(доп.): Нахождение факультета по его имени:
     */
    public Faculty findByName(String name) {
        logger.info("Старт метода findByName");
        return facultyRepository.findByName(name);
    }

     /**
     *  Нахождение студентов по id их факультета:
     */
    public Collection<Student> getStudentsOfFaculty(Long id) {
        logger.info("Старт метода getStudentsOfFaculty");
        return facultyRepository.findById(id)
                .map(Faculty::getStudents)
                .orElse(Collections.emptyList());
    }

     /**
     *  ДЗ-4.5 Параллельные и непараллельные стримы (только шаг 3, остальные шаги в классе-сервисе студента)
     *  Шаг 3. Вывод самого длинного имени факультета в БД факультета с помощью стрима (непараллельного):
     */
    public String longestFacultyName() {
        logger.info("Вывод самого длинного имени факультета в БД факультета с помощью стрима (непараллельного) - longestFacultyName():");
        String longestWord = facultyRepository.findAll()
                .stream().map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElse("");
         /**
         *  Если коллекция String пустая, то выдает не код ошибки, а пустую строку
         */
        return longestWord;
    }

    /**
     *  Дополнительный метод для шага 3 ДЗ-4.5 - самое длинное слово 'color':
     */
    public String longestFacultyColor() {
        logger.info("Вывод самого длинного имени цвета факультета в БД с помощью стрима (непараллельного) - longestFacultyColor():");
        Optional<String> max = facultyRepository.findAll()
                .stream().map(Faculty::getColor)
                .max(Comparator.comparingInt(String::length));
        //Метод определения слова в опшине String с максимальным количеством букв
        return max.orElse("");  //Если коллекция String пустая, то выдает не код ошибки, а пустую строку.
    }
}

