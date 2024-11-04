package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.exception.NoStudentAgeException;
import ru.hogwarts.school.exception.NullAgeException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping // POST http://localhost:8090/student
    /**
     * Для записи студентов по телу запроса через свагер(постман)
     */
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student student1 = studentService.createStudent(student);
        return ResponseEntity.ok(student1);
    }

    @GetMapping("{id}") // GET http://localhost:8090/student/1
    /**
     * Для получения студента из по индексу через свагер(постман)
     */
    public ResponseEntity<Student> findStudent(@PathVariable Long id) {
        if (studentService.findStudent(id) == null) {
            /**
             * Вывод 404 по варианту 1
             */
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(studentService.findStudent(id));
    }

    @PutMapping // PUT http://localhost:8090/student
    /**
     * Для редактирования студентов через свагер(постман).
     */
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        if (studentService.editStudent(student) == null) {
            /**
             * Вывод 404 по варианту 2а
             */
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(studentService.editStudent(student));
    }

    @DeleteMapping("{id}")  // DELETE http://localhost:8090/student/1
    /**
     * Для удаления студента по id через Свагер
     */
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id) {
        Student student = studentService.deleteStudent(id);
        if (student == null) {
            /**
             * Вывод 405 по варианту 3
             */
            return ResponseEntity.status(405).build();
        }
        return ResponseEntity.ok(student);
    }

    @GetMapping() // GET http://localhost:8090/student
    /**
     * Для вывода всех студентов через свагер(постман)
     */
    public ResponseEntity<Collection<Student>> getAllStudent() {
        return ResponseEntity.ok(studentService.getAllStudent());
    }

    @GetMapping(path = "/get/by-age")
    /**
     * ДЗ-3.2 изначально без репозитория, но теперь работает через getAllStudent()
     */
    public ResponseEntity<Collection<Student>> getStudentByAge(@RequestParam(required = false) Integer age) {
            return ResponseEntity.ok(studentService.getStudentByAge(age));
    }

    @GetMapping("/age") // GET http://localhost:8090/student/age?min=22&max=23
    /**
     * Вызов стандартногот метода поиска студентов по отрезку возраста (ДЗ-3.4, шаг 1.1)
     */
    public ResponseEntity<List<Student>> findByAgeBetweenStudent(@RequestParam Integer min, @RequestParam(required = false) Integer max) {
        if (studentService.findByAgeBetween(min, max).isEmpty()) {
            /**
             * Вывод 404 по варианту 1
             */
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(studentService.findByAgeBetween(min, max));
    }

    @GetMapping("/faculty") // GET http://localhost:8082/student/faculty?facultyName=АО
    /**
     * ДЗ-3.4, шаг 4.2*(по имени факультета - по своей инициативе, в условии нет; не через геттер students'а в faculty, а через функционал БД:
     */
    public ResponseEntity<Collection<Student>> findStudentsByFacultyName(String facultyName) {
        return ResponseEntity.ok(studentService.findStudentsByFacultyName(facultyName));
    }

    @GetMapping("/{id}/faculty")
    /**
     * ДЗ-3.4 шаг 4.1 (SQL) Получение факультета по Id его студента
     */
    public ResponseEntity<Faculty> getFacultyOfStudent(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getFacultyOfStudent(id));
    }

    @GetMapping("/all-count-students")
    /**
     * ДЗ-4.1 шаг 1 Получение общего количества студентов из БД через @Query:
     */
    public Integer getCountAllStudents() {
        return studentService.getCountAllStudentInSchool();
    }

    @GetMapping("/midl-age-students")
    /**
     * ДЗ-4.1 шаг 1 Получение среднего возраста всех студентов из БД через @Query:
     */
    public Integer getMidlAgeStudents() {
        return studentService.getMidlAgeStudent();
    }

    @GetMapping("/last-five-students")
    /**
     * ДЗ-4.1 шаг 1 Получение 5 последних студентов из БД через @Query в обратном порядке:
     */
    public List<Student> getFiveLastBackStudents() {
        return studentService.getFiveLastBackStudents();
    }

    @GetMapping("/all-starts-name/{letter}")
    /**
     * ДЗ-4.5: Параллельные стримы
     * Шаг 1. Вывод всех имён студентов, начинающихся с одной и той же буквы, а также отсортированных в алфавитном порядке и находящихся в верхнем регистре:
     */
    public ResponseEntity<List<String>> getAllNameStartsWithA(@PathVariable String letter) {
        List<String> allNameStartsWithLetter = studentService.getAllNameStartsWithLetter(letter);
        if (allNameStartsWithLetter.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        System.out.println("letter: " + letter);
        return ResponseEntity.ok(allNameStartsWithLetter);
    }

    @GetMapping("/average/age")
    /**
     * Шаг 2 ДЗ-4.5 Вывод среднего возраста всех студентов, находящихся в БД студентов:
     */
    public Integer getMidlAgeAllStudents() {
        return studentService.getMidlAgeAllStudents();
    }


    @GetMapping("/sum-parallel")
    /**
     * Шаг 4. Вывод целого числа, полученного суммой всех индексов итерации от 1 до 1000000 с помощью параллельного стрима:
     */
    public Integer getSumStreamParallel() {
        return studentService.getIntegerParallelStream();
    }

    @GetMapping("/print-parallel")
    /**
     * ДЗ-4.6: Потоки
     * Шаг 1: Несинхронизированный вывод студентов в 3-х параллельных потоках (вперемешку):
     */
    public ResponseEntity<String> printStudentNamesThread() {
        String getNameAllStudentsThread = studentService.getNameAllStudentsThread();
        if (getNameAllStudentsThread == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Старт процесса без синхронизатора");
    }

    @GetMapping("/print-synchronized")
    /**
     * Шаг 2: Синхронизированный вывод студентов в 3-х параллельных потоках:
     */
    public ResponseEntity<String> printStudentNamesThreadSynchronization() {
        String getNameAllStudentsThreadSynchronization = studentService.getNameAllStudentsThreadSynchronization();
        if (getNameAllStudentsThreadSynchronization == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Старт процесса с синхронизатором");
    }
}

