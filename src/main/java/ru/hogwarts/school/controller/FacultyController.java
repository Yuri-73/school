package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.exception.NoFacultyColorException;
import ru.hogwarts.school.exception.NullEmptyColorException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping // POST http://localhost:8090/faculty
    /**
     * Для записи факультетов по телу запроса через свагер(постман)
     */
    public ResponseEntity<Faculty> createFaculty(@RequestBody Faculty faculty) {
        Faculty faculty1 = facultyService.createFaculty(faculty);
        return ResponseEntity.ok(faculty1);
    }

    @GetMapping("{id}") // GET http://localhost:8090/faculty/1
    /**
     * Для получения факультета по индексу через свагер(постман)
     */
    public ResponseEntity<Faculty> findFaculty(@PathVariable Long id) {
        if (facultyService.findFaculty(id) == null) {
            /**
             * Выводим 404 по варианту 1
             */
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(facultyService.findFaculty(id));
    }

    @PutMapping // PUT http://localhost:8090/faculty
    /**
     * Для редактирования факультетов через свагер(постман)
     */
//    @ApiResponses(code = 405, message = "Студент не найден")
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        /**
         * Если такого студента в Мапе нет, то 404:
         */
        if (facultyService.editFaculty(faculty) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(facultyService.editFaculty(faculty));
    }

    @DeleteMapping("{id}")  // DELETE http://localhost:8090/faculty/1
    /**
     * Для удаления факультета по id через Свагер
     */
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable Long id) {
        Faculty faculty = facultyService.deleteFaculty(id);
        if (faculty == null) {
            /**
             * Если факультета с этим Id нет, то выскочет 405. Вариант 3
             */
            return ResponseEntity.status(405).build();
        }
        /**
         * При удалении студента по выбранному Id по умолчанию пропишется 404.
         */
        return ResponseEntity.ok(faculty);
    }

    @GetMapping()
    /**
     * Для вывода всех факультетов через свагер(постман)
     */
    public ResponseEntity<Collection<Faculty>> getAllFaculty() {
        return ResponseEntity.ok(facultyService.getAllFaculty());
    }

    @GetMapping(path = "/get/color")
    /**
     * ДЗ-3.2 Сваггер (без репозитория)
     */
    String getFacultyByColor(@RequestParam(required = false) String color) {
        try {
            return "Факультеты с таким цветом найдены: " + facultyService.getFacultyByColor(color);
        } catch (NullEmptyColorException exc) {
            return "Цвет факультета не задан";
        } catch (NoFacultyColorException exception) {
            return "Факультеты с таким цветом отсутствуют";
        }
    }

    @GetMapping("/by-color")
    /**
     * ДЗ-3.4 Введение в SQL шаг 1.2(1) (нахождение фака по его цвету через стандартный метод репозитория)
     */
    public ResponseEntity<Faculty> findByColorIgnoreCase(@RequestParam String color) {
        return ResponseEntity.ok(facultyService.findByColorIgnoreCase(color));
    }

    @GetMapping("/by-name")
    /**
     * ДЗ-3.4 Введение в SQL шаг 1.2(2) (нахождение фака по его имени через стандартный метод репозитория)
     */
    public ResponseEntity<Faculty> findByName(@RequestParam String name) {
        return ResponseEntity.ok(facultyService.findByName(name));
    }


    @GetMapping("/by-nameAndColor")
    /**
     * ДЗ-3.4 SQL шаг 1.2(3) (нахождение фака по его имени и цвету через стандартный метод репозитория - доп. метод)
     */
    public ResponseEntity<Faculty> findByNameAndColor(@RequestParam String name,
                                                      @RequestParam String color) {
        return ResponseEntity.ok(facultyService.findByNameAndColor(name, color));
    }


    @GetMapping("/{id}/students")
    /**
     *  ДЗ-3.4 шаг 4.2 SQL (нахождение студентов по идентификатору факультета через метод репозитория по умолчанию)
     */
    public ResponseEntity<Collection<Student>> getStudentsOfFaculty(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.getStudentsOfFaculty(id));
    }

    @GetMapping("/long-name")
    /**
     *  ДЗ-4.5 (только шаг 3, остальные шаги в классе-сервисе студента)
     *  Вывод самого длинного имени факультета в БД факультета с помощью стрима:
     */
    public ResponseEntity<String> longestNameFaculty() {
        String name = facultyService.longestFacultyName();
        if ((name.isEmpty())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(name);
    }

    @GetMapping("/long-color")
    /**
     *  Вывод самого длинного цвета факультета в БД факультета с помощью стрима (дополнительный метод к 4.5):
     */
    public ResponseEntity<String> longestColorFaculty() {
        String color = facultyService.longestFacultyColor();
        if ((color.isEmpty())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(color);
    }
}

