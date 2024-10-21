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
    public ResponseEntity<Faculty> createFaculty(@RequestBody Faculty faculty) { //Для записи факультетов по телу через свагер(постман)
        Faculty faculty1 = facultyService.createFaculty(faculty);
        return ResponseEntity.ok(faculty1);
    }

    @GetMapping("{id}") // GET http://localhost:8090/faculty/1
    public ResponseEntity<Faculty> findFaculty(@PathVariable Long id) { //Для получения факультета по индексу через свагер(постман)
        if (facultyService.findFaculty(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); //Выводим 404 по варианту 1
        }
        return ResponseEntity.ok(facultyService.findFaculty(id));  //В свагере увидим выбранный объект в JSON
    }

    @PutMapping // PUT http://localhost:8090/faculty
//    @ApiResponses(code = 405, message = "Студент не найден")
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) { //Для редактирования факультетов через свагер(постман)
        // Если такого студента в Мапе нет, то выйдет 404
        if (facultyService.editFaculty(faculty) == null) {
            return ResponseEntity.notFound().build(); //Если факультет с этим Id не найден, то выскочит по умолчанию 404. Вариант 2
        }
        return ResponseEntity.ok(facultyService.editFaculty(faculty)); //В свагере увидим отредактированный объект в JSON
    }

    @DeleteMapping("{id}")  // DELETE http://localhost:8090/faculty/1
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable Long id) { //Для удаления факультета по id через Свагер
        Faculty faculty = facultyService.deleteFaculty(id);
        if (faculty == null) {
            return ResponseEntity.status(405).build(); //Если факультета с этим Id нет, то выскочит 405. Вариант 3
        }
        return ResponseEntity.ok(faculty); //При удалении студента по выбранному Id по умолчанию пропишется 404.
    }

    @GetMapping() // GET http://localhost:8090/faculty
    public ResponseEntity<Collection<Faculty>> getAllFaculty() { //Для вывода всех факультетов через свагер(постман)
        return ResponseEntity.ok(facultyService.getAllFaculty());
    }

    // ДЗ-3.2 Сваггер (без репозитория)
    @GetMapping(path = "/get/color")
    //localhost:8090/faculty/get/color?color=green
    String getFacultyByColor(@RequestParam(required = false) String color) {
        try {
            return "Факультеты с таким цветом найдены: " + facultyService.getFacultyByColor(color);
        } catch (NullEmptyColorException exc) {
            return "Цвет факультета не задан";
        } catch (NoFacultyColorException exception) {
            return "Факультеты с таким цветом отсутствуют";
        }
    }

    //ДЗ-3.4 Введение в SQL шаг 1.2(1) (нахождение фака по его цвету через стандартный метод репозитория)
    @GetMapping("/by-color")
    public ResponseEntity<Faculty> findByColorIgnoreCase(@RequestParam String color) {
        return ResponseEntity.ok(facultyService.findByColorIgnoreCase(color));
    }

    //ДЗ-3.4 Введение в SQL шаг 1.2(2) (нахождение фака по его имени через стандартный метод репозитория)
    @GetMapping("/by-name")
    public ResponseEntity<Faculty> findByName(@RequestParam String name) {
        return ResponseEntity.ok(facultyService.findByName(name));
    }

    // ДЗ-3.4 SQL шаг 1.2(3) (нахождение фака по его имени и цвету через стандартный метод репозитория - доп. метод)
    @GetMapping("/by-nameAndColor")
    public ResponseEntity<Faculty> findByNameAndColor(@RequestParam String name,
                                                      @RequestParam String color) {
        return ResponseEntity.ok(facultyService.findByNameAndColor(name, color));
    }

    // ДЗ-3.4 шаг 4.2 SQL (нахождение студентов по идентификатору факультета через метод репозитория по умолчанию)
    @GetMapping("/{id}/students")
    public ResponseEntity<Collection<Student>> getStudentsOfFaculty(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.getStudentsOfFaculty(id));
    }

    //ДЗ-4.5 (только шаг 3, остальные шаги в классе-сервисе студента)
    //Вывод самого длинного имени факультета в БД факультета с помощью стрима:
    @GetMapping("/long-name")
    public ResponseEntity<String> longestNameFaculty() {
        String name = facultyService.longestFacultyName();
        if ((name.isEmpty())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(name);
    }

    //Вывод самого длинного цвета факультета в БД факультета с помощью стрима (дополнительный метод):
    @GetMapping("/long-color")
    public ResponseEntity<String> longestColorFaculty() {
        String color = facultyService.longestFacultyColor();
        if ((color.isEmpty())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(color);
    }
}

