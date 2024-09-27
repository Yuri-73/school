package ru.hogwarts.school.controller.testRestTemplate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.exception.NoFacultyColorException;
import ru.hogwarts.school.exception.NullEmptyColorException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static jdk.dynalink.linker.support.Guards.isNotNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private FacultyRepository repository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() throws Exception {
        Assertions.assertThat(facultyController).isNotNull();
    }

//    @AfterEach  //Нельзя использовать, т.к. БД не пуста изначально и существуют перекрёстные связи.
//    void setUp() {
//        repository.deleteAll();
//    }

    @Test
    public void createFacultyTest() {
        //given
        var request = faculty("f1", "anyColor");
        //when:
        var result = restTemplate.postForObject("/faculty", request, Faculty.class);
        //then:
        Assertions.assertThat(result.getName()).isEqualTo("f1");
        Assertions.assertThat(result.getColor()).isEqualTo("anyColor");
        Assertions.assertThat(result.getId()).isNotNull();
        Assertions.assertThat(result).isNotNull();

        repository.deleteById(result.getId());  //Очищение БД факультета от тестовых данных
    }

    static Faculty faculty(String name, String color) {
        var f = new Faculty();
        f.setName(name);
        f.setColor(color);
        return f;
    }

    private static Student student(String name, int age) {
        var s = new Student();
        s.setName(name);
        s.setAge(age);
        return s;
    }

    @Test
    public void getFacultyTest() {
        var f = faculty("f1", "anyColor");
        var saved = restTemplate.postForObject("/faculty", f, Faculty.class);

        var result = restTemplate.getForObject("/faculty/" + saved.getId(), Faculty.class);
        Assertions.assertThat(result.getName()).isEqualTo("f1");
        Assertions.assertThat(result.getColor()).isEqualTo("anyColor");
        Assertions.assertThat(result).isNotNull();
        repository.deleteById(result.getId());
    }

    @Test
    public void findByNameOrColorTest() throws Exception {
        var f = faculty("f1", "anyColor");
        var saved = restTemplate.postForObject("/faculty", f, Faculty.class);
        Assertions
                .assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/faculty" + "/by-nameAndColor?name=f1&color=anyColor", Faculty.class))
                .isNotNull();
        Assertions.assertThat(saved.getName()).isEqualTo("f1");
        Assertions.assertThat(saved.getColor()).isEqualTo("anyColor");

        Assertions
                .assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/faculty" + "/by-nameAndColor?name=f1&color=color", Faculty.class))
                .isNull();  //Заведомо отсутствующий цвет
        repository.deleteById(saved.getId());
    }

    @Test
    public void updateFacultyTest() {
        var f = faculty("f1", "anyColor");
        var saved = restTemplate.postForObject("/faculty", f, Faculty.class);
        saved.setName("f2");

        ResponseEntity<Faculty> facultyEntityPut = restTemplate.exchange(
                "/faculty", HttpMethod.PUT, new HttpEntity<>(saved), Faculty.class
        );
        assertThat(facultyEntityPut.getBody().getName()).isEqualTo("f2");
        assertThat(facultyEntityPut.getBody().getColor()).isEqualTo("anyColor");

        Assertions.assertThat(facultyEntityPut.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(facultyEntityPut.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON));

        repository.deleteById(saved.getId());
    }

    @Test
    public void deleteFacultyTest() {
        var f = faculty("DeletedF1", "anyColor");
        var saved = restTemplate.postForObject("/faculty", f, Faculty.class);

        ResponseEntity<Faculty> facultyEntity = restTemplate.exchange(
                "/faculty/" + saved.getId(),
                HttpMethod.DELETE,
                null,
                Faculty.class);

        Assertions.assertThat(facultyEntity.getBody().getName()).isEqualTo("DeletedF1");
        Assertions.assertThat(facultyEntity.getBody().getColor()).isEqualTo("anyColor");

        var deletedF1 = restTemplate.getForObject("/faculty/" + saved.getId(), Faculty.class);
        Assertions.assertThat(deletedF1).isNull();

        ResponseEntity<Faculty> resultAfterDelete = restTemplate.exchange("/faculty/" + saved.getId(),
                HttpMethod.GET, null, Faculty.class);
        assertThat(resultAfterDelete.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    void getAllFacultiesTest() {
        var f1 = restTemplate.postForObject("/faculty", faculty("test1", "red"), Faculty.class);
        var f2 = restTemplate.postForObject("/faculty", faculty("test2", "green"), Faculty.class);
        var f3 = restTemplate.postForObject("/faculty", faculty("test3", "blue"), Faculty.class);

        var result = restTemplate.exchange("/faculty",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Faculty>>() {
                });

        var faculties = result.getBody();

        Assertions.assertThat(faculties).isNotNull();
        Assertions.assertThat(faculties.size()).isEqualTo(9);  //6 факультетов уже имеется в БД, к ним добаляем 3
        Assertions.assertThat(faculties).contains(new Faculty(5l, "АО", "голубой"));
        Assertions.assertThat(faculties).contains(new Faculty(f1.getId(), "test1", "red"));

        repository.deleteById(f1.getId());
        repository.deleteById(f2.getId());
        repository.deleteById(f3.getId());
    }

    @Test
    public void getStudentsOfFacultyTest() throws Exception {
        Faculty f = restTemplate.postForObject("/faculty", faculty("Нормоконтроль", "green"), Faculty.class);
        Student s1 = student("Пётр", 44);
        s1.setFaculty(f);
        Student s2 = student("Борис", 46);
        s2.setFaculty(f);

        Student saved1 = restTemplate.postForObject("/student", s1, Student.class);
        Student saved2 = restTemplate.postForObject("/student", s2, Student.class);

        ResponseEntity<Collection<Student>> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/" + f.getId() + "/students",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                }
        );

        Collection<Student> actual = responseEntity.getBody();
        Assertions
                .assertThat(actual)
                .isNotNull();
        Assertions.assertThat(actual.size()).isEqualTo(2);
        Assertions.assertThat(actual).contains(saved1);
        Assertions.assertThat(actual).contains(saved2);

        ResponseEntity<Student> studentEntity1 = restTemplate.exchange(
                "/student/" + saved1.getId(),
                HttpMethod.DELETE, null, Student.class
        );
        ResponseEntity<Student> studentEntity2 = restTemplate.exchange(
                "/student/" + saved2.getId(),
                HttpMethod.DELETE, null, Student.class
        );
        repository.deleteById(f.getId());
    }

    @Test
    public void findByNameTest() {
        var f = faculty("f1", "anyColor");
        var saved = restTemplate.postForObject("/faculty", f, Faculty.class);

        var result = restTemplate.getForObject("/faculty/by-name?name=" + saved.getName(), Faculty.class);
        Assertions.assertThat(result.getName()).isEqualTo("f1");
        Assertions.assertThat(result.getColor()).isEqualTo("anyColor");
        Assertions.assertThat(result).isNotNull();
        repository.deleteById(result.getId());

        ResponseEntity<Faculty> resultAfterDelete = restTemplate.exchange("/faculty/" + result.getId(),
                HttpMethod.GET, null, Faculty.class);
        assertThat(resultAfterDelete.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void findByColorIgnoreCaseTest() {
        var f = faculty("f1", "anyColor");
        var saved = restTemplate.postForObject("/faculty", f, Faculty.class);

        var result = restTemplate.getForObject("/faculty/by-color?color=AnyColor", Faculty.class);
        Assertions.assertThat(result.getName()).isEqualTo("f1");
        Assertions.assertThat(result.getColor()).isEqualTo("anyColor");
        Assertions.assertThat(result).isNotNull();
        repository.deleteById(result.getId());

        ResponseEntity<Faculty> resultAfterDelete = restTemplate.exchange("/faculty/" + result.getId(),
                HttpMethod.GET, null, Faculty.class);
        assertThat(resultAfterDelete.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void testGetFacultyByColor() {
        var f = faculty("f1", "green");
        var saved = restTemplate.postForObject("/faculty", f, Faculty.class);

        var result = restTemplate.getForObject("/faculty//get/color?color=green", String.class);
        Assertions.assertThat(result).isEqualTo("Факультеты с таким цветом найдены: [f1]");

        repository.deleteById(saved.getId());

        ResponseEntity<Faculty> resultAfterDelete = restTemplate.exchange("/faculty/" + saved.getId(),
                HttpMethod.GET, null, Faculty.class);  //Почему работает только с Id?
        assertThat(resultAfterDelete.getStatusCodeValue()).isEqualTo(404);
    }
}
