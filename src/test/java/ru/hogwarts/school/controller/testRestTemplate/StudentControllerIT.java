package ru.hogwarts.school.controller.testRestTemplate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerIT {
    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private StudentRepository repository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestRestTemplate postTemplate;
    @Autowired
    private TestRestTemplate putTemplate;
    @Autowired
    private StudentService studentService;

    private static Long id = 164L;
    private static String name = "Bob";
    private static int age = 22;
    private static String faculty_id = "green";

    @Test
    void contextLoads() throws Exception { // Инициализация бина контроллера студента
        Assertions.assertThat(studentController).isNotNull();
    }

    @Test
    public void createStudentTest() {
        //initial data:
        var s = student(name, age);
        //test:
        var result = restTemplate.postForObject("/student", s, Student.class);
        //check:
        Assertions.assertThat(result.getName()).isEqualTo(name);
        Assertions.assertThat(result.getAge()).isEqualTo(age);
        Assertions.assertThat(result.getId()).isNotNull();
        Assertions.assertThat(result).isNotNull();
        //cleaning:
        repository.deleteById(result.getId());  //Очищение от тестовых данных
    }

    @Test
    public void findStudentTest() {
        //initial data:
        var s = student(name, age);
        //test:
        var saved = restTemplate.postForObject("/student", s, Student.class);
        var result = restTemplate.getForObject("/student/" + saved.getId(), Student.class);
        //check:
        Assertions.assertThat(result.getName()).isEqualTo(name);
        Assertions.assertThat(result.getAge()).isEqualTo(age);
        Assertions.assertThat(result).isNotNull();
        //cleaning:
        repository.deleteById(result.getId());
    }

    @Test
    public void updateStudentTest() {
        //initial data:
        var s = student(name, age);
        //test:
        var saved = restTemplate.postForObject("/student", s, Student.class);  //работа метода контроллера через шаблон TestRestTemplate
        saved.setName("name2");

        ResponseEntity<Student> studentEntityPut = restTemplate.exchange(
                "/student", HttpMethod.PUT, new HttpEntity<>(saved), Student.class
        );
        //check:
        assertThat(studentEntityPut.getBody().getName()).isEqualTo("name2");
        assertThat(studentEntityPut.getBody().getAge()).isEqualTo(age);
        //cleaning:
        repository.deleteById(saved.getId());
    }

    @Test
    public void deleteStudentTest() {
        //initial data:
        var s = student(name, age);
        var saved = restTemplate.postForObject("/student", s, Student.class);
        //test:
        ResponseEntity<Student> studentEntity = restTemplate.exchange(
                "/student/" + saved.getId(),
                HttpMethod.DELETE,
                null,
                Student.class);
        //check:
        Assertions.assertThat(studentEntity.getBody().getName()).isEqualTo(name);
        Assertions.assertThat(studentEntity.getBody().getAge()).isEqualTo(age);
        //test:
        var deletedS1 = restTemplate.getForObject("/student/" + saved.getId(), Student.class);
        //check:
        Assertions.assertThat(deletedS1.getName()).isEqualTo(null);
        Assertions.assertThat(deletedS1.getAge()).isEqualTo(0);
        Assertions.assertThat(deletedS1.getId()).isEqualTo(null);
        //test:
        ResponseEntity<Student> resultAfterDelete = restTemplate.exchange("/student/" + saved.getId(),
                HttpMethod.GET, null, Student.class);
        //check:
        assertThat(resultAfterDelete.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    void getAllStudentTest() {
        //initial data:
        var s1 = restTemplate.postForObject("/student", student("test1", 24), Student.class);
        var s2 = restTemplate.postForObject("/student", student("test2", 25), Student.class);
        var s3 = restTemplate.postForObject("/student", student("test3", 26), Student.class);
        //test:
        var result = restTemplate.exchange("/student",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                });

        var students = result.getBody();
        //check:
        Assertions.assertThat(students).isNotNull();
        Assertions.assertThat(students.size()).isEqualTo(7);  //4 студента уже имеется в БД, к ним добаляем 3
        Assertions.assertThat(students).contains(new Student(1l, "Валерий", 20));
        Assertions.assertThat(students).contains(new Student(s1.getId(), "test1", 24));
        Assertions.assertThat(students).contains(new Student(s2.getId(), "test2", 25));
        //cleaning:
        repository.deleteById(s1.getId());  //Очищение от тестовых данных
        repository.deleteById(s2.getId());
        repository.deleteById(s3.getId());
    }

    @Test
    public void getStudentByAgeTest() {
        //initial data:
        var s = student(name, age);
        var saved = restTemplate.postForObject("/student", s, Student.class);
        //test:
        var result = restTemplate.getForObject("/student/get/by-age?age=22", String.class);
        //check:
        Assertions.assertThat(result).isEqualTo("Студенты с таким возрастом: [Bob]");
        Assertions.assertThat(result).isNotNull();
        //cleaning:
        repository.deleteById(saved.getId());
        //test:
        ResponseEntity<Student> resultAfterDelete = restTemplate.exchange("/student/" + saved.getId(),
                HttpMethod.GET, null, Student.class);
        //check:
        assertThat(resultAfterDelete.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void findByAgeBetweenStudentTest() { //Тест по промежутку возраста
        //initial data:
        var s1 = restTemplate.postForObject("/student", student("test1", 16), Student.class);
        var s2 = restTemplate.postForObject("/student", student("test2", 17), Student.class);
        var s3 = restTemplate.postForObject("/student", student("test3", 18), Student.class);
        var s4 = restTemplate.postForObject("/student", student("test4", 19), Student.class);
        var s5 = restTemplate.postForObject("/student", student("test5", 18), Student.class);
        //test:
        ResponseEntity<Collection<Student>> result = restTemplate.exchange("/student/age?min=16&max=17",
                HttpMethod.GET, null, new ParameterizedTypeReference<Collection<Student>>() {
                });
        var students = result.getBody(); //Выуживание коллекции students из ResponseEntity
        //check:
        Assertions.assertThat(students).isNotNull();
        Assertions.assertThat(students.size()).isEqualTo(2);
        Assertions.assertThat(students).containsExactly(s1, s2);
        //cleaning:
        ResponseEntity<Student> studentEntity1 = restTemplate.exchange(
                "/student/" + s1.getId(),
                HttpMethod.DELETE, null, Student.class
        );
        ResponseEntity<Student> studentEntity2 = restTemplate.exchange(
                "/student/" + s2.getId(),
                HttpMethod.DELETE, null, Student.class
        );
        ResponseEntity<Student> studentEntity3 = restTemplate.exchange(
                "/student/" + s3.getId(),
                HttpMethod.DELETE, null, Student.class
        );
        ResponseEntity<Student> studentEntity4 = restTemplate.exchange(
                "/student/" + s4.getId(),
                HttpMethod.DELETE, null, Student.class
        );
        ResponseEntity<Student> studentEntity5 = restTemplate.exchange(
                "/student/" + s5.getId(),
                HttpMethod.DELETE, null, Student.class
        );
    }

    @Test
    public void getFacultyOfStudentTest() {
        //initial data:
        Faculty savedFaculty = restTemplate.postForObject("/faculty", faculty("ppp", "green"), Faculty.class);
        Student s = student(name, age);
        s.setFaculty(savedFaculty);
        Student saved = restTemplate.postForObject("/student", s, Student.class);

//        Faculty result = restTemplate.getForObject("http://localhost:" + port + "/student/" + saved.getId() + "/faculty", Faculty.class); //Можно и так
        //test:
        ResponseEntity<Faculty> responseEntity = restTemplate.exchange(
                "/student/" + saved.getId() + "/faculty",
                HttpMethod.GET,
                null,
                Faculty.class
        );
        //check:
        Assertions
                .assertThat(responseEntity)
                .isNotNull();
        Assertions.assertThat(responseEntity.getBody().getName()).isEqualTo("ppp");
        Assertions.assertThat(responseEntity.getBody().getColor()).isEqualTo("green");

        Assertions.assertThat(saved.getName()).isEqualTo(name);
        Assertions.assertThat(saved.getAge()).isEqualTo(age);
        //cleaning:
        repository.deleteById(saved.getId());  //Удаляем тестового студента
        facultyRepository.deleteById(savedFaculty.getId());  //Удаляем тестовый факультет
    }

    @Test
    public void findStudentsByFacultyNameTest() {
        //initial data:
        Faculty f = restTemplate.postForObject("/faculty", faculty("Нормоконтроль", "green"), Faculty.class);
        Student s1 = student("Пётр", 44);
        s1.setFaculty(f);
        Student s2 = student("Борис", 46);
        s2.setFaculty(f);
        //test:
        Student saved1 = restTemplate.postForObject("/student", s1, Student.class);
        Student saved2 = restTemplate.postForObject("/student", s2, Student.class);

        ResponseEntity<Collection<Student>> result = restTemplate.exchange("/student/faculty?facultyName=Нормоконтроль",
                HttpMethod.GET, null, new ParameterizedTypeReference<Collection<Student>>() {
                });
        var students = result.getBody();
        //check:
        Assertions.assertThat(students).isNotNull();
        Assertions.assertThat(students.size()).isEqualTo(2);
        Assertions.assertThat(students).containsExactly(saved1, saved2);
        //cleaning students:
        ResponseEntity<Student> studentEntity1 = restTemplate.exchange(
                "/student/" + saved1.getId(),
                HttpMethod.DELETE, null, Student.class
        );
        ResponseEntity<Student> studentEntity2 = restTemplate.exchange(
                "/student/" + saved2.getId(),
                HttpMethod.DELETE, null, Student.class
        );
        //cleaning faculty:
        facultyRepository.deleteById(f.getId());
    }

    private static Student student(String name, int age) {
        var s = new Student();
        s.setName(name);
        s.setAge(age);
        return s;
    }

    static Faculty faculty(String name, String color) {
        var f = new Faculty();
        f.setName(name);
        f.setColor(color);
        return f;
    }
}