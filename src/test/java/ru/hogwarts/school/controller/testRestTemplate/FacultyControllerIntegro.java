package ru.hogwarts.school.controller.testRestTemplate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.StudentAvatarService;

import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;


//Интеграционные тесты: проверка от реального URL в виде шаблона TestRestTemplate до работы БД.
//Добавил решение от Ильи с вебинара для теста getAllFacultiesTest() с помощью массива JSON
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerIntegro {

    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultyController;

    @MockBean
    private StudentAvatarService studentAvatarService;  //Без этого бина все тесты отказываются работать. Для чего-то тестовой БД он нужен!

    @Autowired
    private FacultyRepository repository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() throws Exception {
        Assertions.assertThat(facultyController).isNotNull();
    }

//    @AfterEach  //Нельзя использовать, т.к. БД не пуста изначально и существуют перекрёстные связи. А значит, придётся удалять содержимое всех трёх баз
//    void setUp() {
//        repository.deleteAll();
//    }

    @Test
    public void createFacultyTest() {
        //initial data:
        var request = faculty("f1", "anyColor");
        //test:
        var result = restTemplate.postForObject("/faculty", request, Faculty.class);
        //check:
        Assertions.assertThat(result.getName()).isEqualTo("f1");
        Assertions.assertThat(result.getColor()).isEqualTo("anyColor");
        Assertions.assertThat(result.getId()).isNotNull();
        Assertions.assertThat(result).isNotNull();
        //cleaning:
        repository.deleteById(result.getId());  //Очищение от тестовых данных. Но теперь не имеет смысла, т.к. тест-БД очищается сама!
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
        //initial data:
        var f = faculty("f1", "anyColor");
        var saved = restTemplate.postForObject("/faculty", f, Faculty.class);
        //test:
        var result = restTemplate.getForObject("/faculty/" + saved.getId(), Faculty.class);
        //check:
        Assertions.assertThat(result.getName()).isEqualTo("f1");
        Assertions.assertThat(result.getColor()).isEqualTo("anyColor");
        Assertions.assertThat(result).isNotNull();
        //cleaning:
        repository.deleteById(result.getId());  //Очищение от тестовых данных. Но теперь не имеет смысла, т.к. тест-БД очищается сама.
    }

    @Test
    public void findByNameOrColorTest() throws Exception {
        //initial data:
        var f = faculty("f1", "anyColor");
        var saved = restTemplate.postForObject("/faculty", f, Faculty.class);
        //test:
        Assertions
                .assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/faculty" + "/by-nameAndColor?name=f1&color=anyColor", Faculty.class))
                .isNotNull();
        //check:
        Assertions.assertThat(saved.getName()).isEqualTo("f1");
        Assertions.assertThat(saved.getColor()).isEqualTo("anyColor");

        Assertions
                .assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/faculty" + "/by-nameAndColor?name=f1&color=color", Faculty.class))
                .isNull();  //Заведомо отсутствующий цвет
        //cleaning:
        repository.deleteById(saved.getId());  //Очищение от тестовых данных. Но теперь не имеет смысла, т.к. тест-БД очищается сама.
    }

    @Test
    public void updateFacultyTest() {
        //initial data:
        var f = faculty("f1", "anyColor");
        var saved = restTemplate.postForObject("/faculty", f, Faculty.class);
        saved.setName("f2");
        //test:
        ResponseEntity<Faculty> facultyEntityPut = restTemplate.exchange(
                "/faculty", HttpMethod.PUT, new HttpEntity<>(saved), Faculty.class
        );
        //check:
        assertThat(facultyEntityPut.getBody().getName()).isEqualTo("f2");
        assertThat(facultyEntityPut.getBody().getColor()).isEqualTo("anyColor");

        Assertions.assertThat(facultyEntityPut.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(facultyEntityPut.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON));
        //cleaning:
        repository.deleteById(saved.getId());  //Очищение от тестовых данных. Но теперь не имеет смысла, т.к. тест-БД очищается сама.
    }

    @Test
    public void deleteFacultyTest() {
        //initial data:
        var f = faculty("DeletedF1", "anyColor");
        var saved = restTemplate.postForObject("/faculty", f, Faculty.class);
        //test:
        ResponseEntity<Faculty> facultyEntity = restTemplate.exchange(
                "/faculty/" + saved.getId(),
                HttpMethod.DELETE,
                null,
                Faculty.class);
        //check:
        Assertions.assertThat(facultyEntity.getBody().getName()).isEqualTo("DeletedF1");
        Assertions.assertThat(facultyEntity.getBody().getColor()).isEqualTo("anyColor");
        //test:
        var deletedF1 = restTemplate.getForObject("/faculty/" + saved.getId(), Faculty.class);
        //check:
        Assertions.assertThat(deletedF1).isNull();
        //test:
        ResponseEntity<Faculty> resultAfterDelete = restTemplate.exchange("/faculty/" + saved.getId(),
                HttpMethod.GET, null, Faculty.class);
        //check:
        assertThat(resultAfterDelete.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    void getAllFacultiesTest() {
        //initial data:
        var f1 = restTemplate.postForObject("/faculty", faculty("test1", "red"), Faculty.class);
        var f2 = restTemplate.postForObject("/faculty", faculty("test2", "green"), Faculty.class);
        var f3 = restTemplate.postForObject("/faculty", faculty("test3", "blue"), Faculty.class);
        //test:
        var result = restTemplate.exchange("/faculty",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Faculty>>() {
                });

        var faculties = result.getBody();
        //check:
        Assertions.assertThat(faculties).isNotNull();
        Assertions.assertThat(faculties.size()).isEqualTo(3);  //В тест-базе только тестовые объекты
//        Assertions.assertThat(faculties).contains(new Faculty(5l, "АО", "голубой"));
        Assertions.assertThat(faculties).contains(new Faculty(f1.getId(), "test1", "red"));

        //Очищение от тестовых данных. Но теперь не имеет смысла, т.к. тест-БД очищается сама.
        repository.deleteById(f1.getId());
        repository.deleteById(f2.getId());
        repository.deleteById(f3.getId());

        //Тест от Ильи Савинова из вебинара 3.6 [01:31:00] через массив JSON:
        //Начальные условия:
        ResponseEntity<Faculty> newFacultyResponse = restTemplate.postForEntity("http://localhost:" + port + "/faculty", new Faculty(1l, "Name", "Red"), Faculty.class);
        //Вызов тестируемого метода через массив JSON:
        ResponseEntity<Faculty[]> response = restTemplate.getForEntity("http://localhost:" + port + "/faculty", Faculty[].class);
        Проверка:
        assertTrue(response.getStatusCode().is2xxSuccessful());
        Assertions.assertThat(response.getBody()).isNotNull();
        Faculty[] faculties1 = response.getBody();
        assertThat(faculties1).contains(newFacultyResponse.getBody());
//        assertThat(faculties1[0].getName()).isEqualTo("Name"); //Не пройдёт, потому что не знаю место расположения newFacultyResponse.getBody() в массиве (он у меня изначально заполнен)

        //Очищение от тестовых данных. Но теперь не имеет смысла, т.к. тест-БД очищается сама.
        repository.deleteById(f3.getId() + 1l); //Не смог вывести id из newFacultyResponse (newFacultyResponse..getBody().getId не проходит)
    }

    @Test
    public void getStudentsOfFacultyTest() throws Exception {
        //initial data:
        Faculty f = restTemplate.postForObject("/faculty", faculty("Нормоконтроль", "green"), Faculty.class);
        Student s1 = student("Пётр", 44);
        s1.setFaculty(f);
        Student s2 = student("Борис", 46);
        s2.setFaculty(f);

        Student saved1 = restTemplate.postForObject("/student", s1, Student.class);
        Student saved2 = restTemplate.postForObject("/student", s2, Student.class);
        //test:
        ResponseEntity<Collection<Student>> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/" + f.getId() + "/students",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                }
        );
        Collection<Student> actual = responseEntity.getBody();
        //check:
        Assertions
                .assertThat(actual)
                .isNotNull();
        Assertions.assertThat(actual.size()).isEqualTo(2);
        Assertions.assertThat(actual).contains(saved1);
        Assertions.assertThat(actual).contains(saved2);
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
        repository.deleteById(f.getId());  //Очищение от тестовых данных. Но теперь не имеет смысла, т.к. тест-БД очищается сама.
    }

    @Test
    public void findByNameTest() {
        //initial data:
        var f = faculty("f1", "anyColor");
        var saved = restTemplate.postForObject("/faculty", f, Faculty.class);
        //test:
        var result = restTemplate.getForObject("/faculty/by-name?name=" + saved.getName(), Faculty.class);
        //check:
        Assertions.assertThat(result.getName()).isEqualTo("f1");
        Assertions.assertThat(result.getColor()).isEqualTo("anyColor");
        Assertions.assertThat(result).isNotNull();
        //cleaning:
        repository.deleteById(result.getId());  //Очищение от тестовых данных. Но здесь по логике оно нужно.
        //test:
        ResponseEntity<Faculty> resultAfterDelete = restTemplate.exchange("/faculty/" + result.getId(),
                HttpMethod.GET, null, Faculty.class);
        //check:
        assertThat(resultAfterDelete.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void findByColorIgnoreCaseTest() {
        //initial data:
        var f = faculty("f1", "anyColor");
        var saved = restTemplate.postForObject("/faculty", f, Faculty.class);
        //test:
        var result = restTemplate.getForObject("/faculty/by-color?color=AnyColor", Faculty.class);
        //check:
        Assertions.assertThat(result.getName()).isEqualTo("f1");
        Assertions.assertThat(result.getColor()).isEqualTo("anyColor");
        Assertions.assertThat(result).isNotNull();
        //cleaning:
        repository.deleteById(result.getId());  //Очищение от тестовых данных. Но здесь по логике оно нужно.
        //test:
        ResponseEntity<Faculty> resultAfterDelete = restTemplate.exchange("/faculty/" + result.getId(),
                HttpMethod.GET, null, Faculty.class);
        //check:
        assertThat(resultAfterDelete.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void testGetFacultyByColor() {
        //initial data:
        var f = faculty("f1", "green");
        var saved = restTemplate.postForObject("/faculty", f, Faculty.class);
        //test:
        var result = restTemplate.getForObject("/faculty//get/color?color=green", String.class);
        //check:
        Assertions.assertThat(result).isEqualTo("Факультеты с таким цветом найдены: [f1]");
        //cleaning:
        repository.deleteById(saved.getId());  //Очищение от тестовых данных. Но здесь по логике оно нужно.
        //test:
        ResponseEntity<Faculty> resultAfterDelete = restTemplate.exchange("/faculty/" + saved.getId(),
                HttpMethod.GET, null, Faculty.class);  //Почему работает только с Id?
        //check:
        assertThat(resultAfterDelete.getStatusCodeValue()).isEqualTo(404);
    }
}
