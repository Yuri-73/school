package ru.hogwarts.school.controller.webMvcTest;

import aj.org.objectweb.asm.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.exception.NoFacultyColorException;
import ru.hogwarts.school.exception.NullEmptyColorException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;
import com.github.javafaker.Faker;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Этот род тестов называется компонентным, а не интеграционным. Полноценное URL не создаётся, в отличие от TestRestTemplate, а имитируется (мокируется)
@WebMvcTest(FacultyController.class)
public class FacultyControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private FacultyService facultyService;

    @MockBean
    private FacultyRepository facultyRepository;

    @InjectMocks
    private FacultyController facultyController;

    private ObjectMapper mapper = new ObjectMapper();  //Для продвинутых тестов, которые идут параллельно с основными, которые отправил на проверку по ветке hw-3.6

    private static Long id = 1L;
    private static String name = "Blowers";
    private static String color = "yellow";
    private static JSONObject facultyObject;
    private static Faculty faculty;
    private static Faculty faculty2;
    Student student;   //Для теста по проверке вывода коллекции студентов по id факультета

    @BeforeEach
    void setUp() throws JSONException {
        facultyObject = new JSONObject();  //Создание и инициализация JSON-объекта для подачи в мок-URL тестов
        facultyObject.put("name", name);
        facultyObject.put("color", color);

        faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor(color);

        faculty2 = new Faculty();
        faculty2.setId(2l);
        faculty2.setName("Bob");
        faculty2.setColor("blau");
    }

    private final Faker faker = new Faker();

    @Test
    public void createFacultyMvcTest() throws Exception {
        //initial data:
        when(facultyRepository.save(ArgumentMatchers.any(Faculty.class))).thenReturn(faculty);
        //test and check:
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  //then
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
        verify(facultyRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void createFacultyMvcMapperStreamTest() throws Exception {  //Ввод факультета продвинутым способом через mapper и stream (помощь от Громовой)
        //initial data:
        Faculty facultyFaker = generateFacultyFaker();  //
        faculty.setName(facultyFaker.getName());
        System.out.println("faculty.getName: " + faculty.getName());
        faculty.setColor(facultyFaker.getColor());
        System.out.println("faculty.getColor: " + faculty.getColor());
        when(facultyRepository.save(ArgumentMatchers.any())).thenReturn(faculty);
        //test and check:
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/faculty")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(facultyFaker))
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    Faculty facultyResult = mapper.readValue(
                            result.getResponse().getContentAsString(),
                            Faculty.class
                    );
                    assertThat(facultyResult).isNotNull();
                    assertThat(facultyResult.getId()).isEqualTo(1L);
                    assertThat(facultyResult.getColor()).isEqualTo(faculty.getColor());
                    assertThat(facultyResult.getName()).isEqualTo(faculty.getName());
                });
        verify(facultyRepository, new Times(1)).save(any());
    }

    @Test
    public void getFacultyMvcTest() throws Exception {
        //initial data:
        when(facultyRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(faculty));
        //test and check:
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + faculty.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
        verify(facultyRepository, times(2)).findById(any());
    }

    @Test
    public void getFacultyMvcMapperStreamTest() throws Exception {  //Нахождение факультета продвинутым способом через mapper и stream
        //initial data:
        Faculty facultyFaker = generateFacultyFaker();
        faculty.setName(facultyFaker.getName());
        System.out.println("faculty.getName: " + faculty.getName());
        faculty.setColor(facultyFaker.getColor());
        System.out.println("faculty.getColor: " + faculty.getColor());

        when(facultyRepository.findById(faculty.getId())).thenReturn(Optional.of(faculty));
        //test and check:
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/faculty/" + faculty.getId())
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    Faculty facultyResult = mapper.readValue(
                            result.getResponse().getContentAsString(),
                            Faculty.class
                    );
                    assertThat(facultyResult).isNotNull();
                    assertThat(facultyResult.getId()).isEqualTo(faculty.getId());
                    assertThat(facultyResult.getColor()).isEqualTo(faculty.getColor());
                    assertThat(facultyResult.getName()).isEqualTo(faculty.getName());
                });

        // not found checking:
        //initial data:
        when(facultyRepository.findById(eq(2L))).thenReturn(Optional.empty());
        //test and check:
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/faculties/2")
                ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertThat(responseString).isNotNull();
                });
        verify(facultyRepository, never()).save(any());
        verify(facultyRepository, never()).delete(any());
        verify(facultyRepository, times(2)).findById(any());
    }

    @Test
    public void removeFacultyMvcTest() throws Exception {
//                .andExpect(content().string(""));  //Убеждаемся, что ничего нет на выходе
        //initial data:
        when(facultyRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(faculty));
        doNothing().when(facultyRepository).deleteById(id); //Пустая заглушка вместо метода facultyRepository.deleteById()
        //test and check:
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
    }

    @Test
    public void removeFacultyMvcMapperStreamTest() throws Exception {  //Удаление факультета продвинутым способом через mapper и stream
        //initial data:
        Faculty facultyFaker = generateFacultyFaker();
        faculty.setName(facultyFaker.getName());
        System.out.println("faculty.getName: " + faculty.getName());
        faculty.setColor(facultyFaker.getColor());
        System.out.println("faculty.getColor: " + faculty.getColor());

        when(facultyRepository.findById(faculty.getId())).thenReturn(Optional.of(faculty));
        //test and check:
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/faculty/" + id)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    Faculty facultyResult = mapper.readValue(
                            result.getResponse().getContentAsString(),
                            Faculty.class
                    );
                    assertThat(facultyResult).isNotNull();
                    assertThat(facultyResult.getId()).isEqualTo(id);
                    assertThat(facultyResult.getColor()).isEqualTo(faculty.getColor());
                    assertThat(facultyResult.getName()).isEqualTo(faculty.getName());
                });
        verify(facultyRepository, Mockito.times(1)).delete(any());
        Mockito.reset(facultyRepository);

        // not found checking
        //initial data:
        when(facultyRepository.findById(eq(2L))).thenReturn(Optional.empty());
        //test and check:
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/facult/2")  //Специально испортил эндпоинт, чтобы выдало 404, т.к. у меня запрограммирована ошибка 405
                ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertThat(responseString).isNotNull();
                });
        verify(facultyRepository, never()).delete(any());
    }


    @Test
    public void editFacultyMvcTest() throws Exception {
        //initial data:
        when(facultyRepository.findById(id)).thenReturn(Optional.of(faculty));
        faculty.setName("mmmmmm");

        JSONObject updateFacultyRq = new JSONObject();
        updateFacultyRq.put("id", faculty.getId());
        updateFacultyRq.put("name", faculty.getName());
        updateFacultyRq.put("color", faculty.getColor());

        when(facultyRepository.save(ArgumentMatchers.any(Faculty.class))).thenReturn(faculty);
        //test and check:
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty/")
                        .content(updateFacultyRq.toString())  //Передача тела объекта updateFacultyRq в контроллер в виде строки по заданному URL
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  //Если тест проходит, то статус 200
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("mmmmmm"))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
        verify(facultyRepository, Mockito.times(2)).save(any());
    }

    @Test
    public void editFacultyMapperStreamTest() throws Exception {  //Редактирование факультета продвинутым способом через mapper и stream
        //initial data:
        Faculty facultyFaker = generateFacultyFaker();   //Создание случайного факультета для подскока
        //Пересоздание факультета по случ. свойствам:
        faculty.setName(facultyFaker.getName());
        System.out.println("faculty.getName: " + faculty.getName());
        faculty.setColor(facultyFaker.getColor());
        System.out.println("faculty.getColor: " + faculty.getColor());

        //Ставим заглушку на получение из базы, чтобы не получить в контроллере статус 404:
        when(facultyRepository.findById(faculty.getId())).thenReturn(Optional.of(faculty));

        //Пересоздание факультета опять по случ. свойствам, но чтобы получить заглушку на сохранение в БД::
        faculty.setColor(facultyFaker.getColor());
        faculty.setName(facultyFaker.getName());
        when(facultyRepository.save(any())).thenReturn(faculty);
        //test and check:
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/faculty")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(faculty))  //Отправляем через мокURL-шаблон в контроллер, откорректированное в JSON
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    Faculty facultyResult = mapper.readValue(
                            result.getResponse().getContentAsString(),
                            Faculty.class //Получаем результатом в контроллере (через мокURL-шаблон) факультет, отсериализованный из JSON
                    );
                    assertThat(facultyResult).isNotNull();
                    assertThat(facultyResult.getId()).isEqualTo(1L);
                    assertThat(facultyResult.getColor()).isEqualTo(faculty.getColor());
                    assertThat(facultyResult.getName()).isEqualTo(faculty.getName());
                });

        //not found checking:
        //initial data:
        when(facultyRepository.findById(2L)).thenReturn(Optional.ofNullable(null));
        //test and check:
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/faculty")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(faculty2))  //Отправляем факультет с id=2 для обеспечения работы заглушки
                ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertThat(responseString).isNotNull();
                });
        verify(facultyRepository, times(2)).save(any());  //Этот метод вызывается только в 1-й части теста
    }

    @Test
    public void findByColorIgnoreCaseTest() throws Exception {  //Тест поиска факультета по его цвету без учёта регистра
        //initial data:
        when(facultyRepository.findByColorIgnoreCase(anyString())).thenReturn(faculty2);
        //test and check:
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/by-color?color=blau")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2l))
                .andExpect(jsonPath("$.name").value(faculty2.getName()))
                .andExpect(jsonPath("$.color").value(faculty2.getColor()));
    }

    @Test
    public void findByNameTest() throws Exception {
        //initial data:
        when(facultyRepository.findByName(anyString())).thenReturn(faculty2);
        //test and check:
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/by-name?name=Bob")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2l))
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.color").value(faculty2.getColor()));
    }

    @Test
    public void findByNameAndColorTest() throws Exception {
        //initial data:
        when(facultyRepository.findByNameAndColor(anyString(), anyString())).thenReturn(faculty2);
        //test and check:
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/by-nameAndColor?name=Bob&color=blau")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2l))
                .andExpect(jsonPath("$.name").value(faculty2.getName()))
                .andExpect(jsonPath("$.color").value(faculty2.getColor()));
    }

    @Test
    public void getAllFacultyTest() throws Exception {
        //initial data:
        when(facultyRepository.findAll()).thenReturn(Collections.singletonList(faculty));
        //test and check:
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(Collections.singletonList(faculty))));
    }

    @Test
    public void getAllFacultyAndByColorIgnoreCaseMapperStreamTest() throws Exception { //Получение всей коллекции, а также факультета по цвету без регистра продвинутым способом через mapper
        //initial data:
        List<Faculty> faculties = Stream.iterate(1, id -> id + 1)
                .map(faculty -> generateFacultyFaker())
                .limit(15)
                .collect(Collectors.toList());

        when(facultyRepository.findAll()).thenReturn(faculties);
        //test and check:
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/faculty")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    List<Faculty> facultyOuts = mapper.readValue(
                            result.getResponse().getContentAsString(),
                            new com.fasterxml.jackson.core.type.TypeReference<List<Faculty>>() {
                                @Override
                                public int compareTo(com.fasterxml.jackson.core.type.TypeReference<List<Faculty>> o) {
                                    return super.compareTo(o);
                                }
                            }
                    );
                    assertThat(facultyOuts)
                            .isNotNull();
                    Stream.iterate(0, index -> index + 1)
                            .limit(facultyOuts.size())
                            .forEach(index -> {
                                Faculty facultyDtoOut = facultyOuts.get(index);
                                Faculty expected = faculties.get(index);
                                assertThat(facultyDtoOut.getId()).isEqualTo(expected.getId());
                                assertThat(facultyDtoOut.getColor()).isEqualTo(expected.getColor());
                                assertThat(facultyDtoOut.getName()).isEqualTo(expected.getName());
                            });
                });
        //initial data:
        String color = faculties.get(0).getColor();
        when(facultyRepository.findByColorIgnoreCase(eq(color))).thenReturn(faculty2);
        //test and check:
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/faculty/by-color?color=" + color)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    Faculty facultyExpect = mapper.readValue(
                            result.getResponse().getContentAsString(),
                            Faculty.class); //Получаем результатом в контроллере (через мокURL-шаблон) факультет, отсериализованный из JSON

                    assertThat(facultyExpect).isNotNull();
                    assertThat(facultyExpect.getId()).isEqualTo(faculty2.getId());
                    assertThat(facultyExpect.getColor()).isEqualTo(faculty2.getColor());
                    assertThat(facultyExpect.getName()).isEqualTo(faculty2.getName());
                });
    }

    @Test
    public void getStudentsOfIdFacultyTest() throws Exception {
        //initial data:
        Student student = new Student(1l, "Bob", 25);
        Student student2 = new Student(2l, "Sem", 21);
        faculty.setStudents(List.of(student, student2)); //Инициализируем факультет списком студентов

        when(facultyRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(faculty));
        //test and check:
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + faculty.getId() + "/students/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(faculty.getStudents())));
        assertEquals(facultyService.getStudentsOfFaculty(anyLong()), faculty.getStudents());
        assertEquals(faculty.getStudents().size(), 2);
    }

    @Test
    public void getStudentsOfIdFacultyMapperStreamTest() throws Exception {  //продвинутый тест через mapper и stream
        //initial data:
        List<Student> students = Stream.iterate(1, id -> id + 1)
                .map(student -> generateStudentFaker())
                .limit(5)
                .collect(Collectors.toList());

        faculty.setStudents(students);  //Инициализируем факультет списком студентов

        when(facultyRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(faculty));
        //initial data:
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/faculty/" + faculty.getId() + "/students/")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    List<Student> studentsActual = mapper.readValue(
                            result.getResponse().getContentAsString(),
                            new com.fasterxml.jackson.core.type.TypeReference<List<Student>>() {
                                @Override
                                public int compareTo(com.fasterxml.jackson.core.type.TypeReference<List<Student>> o) {
                                    return super.compareTo(o);
                                }
                            }
                    );
                    assertThat(studentsActual)
                            .isNotNull();
//                            .isNotEmpty(); //Почему-то краснеет. Разве коллекция не может быть пустой? MockMvc не любит пустоту.
                    Stream.iterate(0, index -> index + 1)
                            .limit(studentsActual.size())
                            .forEach(index -> {
                                Student studentActual = studentsActual.get(index);  //Выуживаем в итерации актуального студента
                                Student studentExpected = students.get(index);    //Выуживаем в итерации инспектируемого студента
                                assertThat(studentActual.getId()).isEqualTo(studentExpected.getId()); //Можно и не мудрить с usingRecursiveComparison()!
                                assertThat(studentActual.getAge()).usingRecursiveComparison().isEqualTo(studentExpected.getAge());
                                assertThat(studentActual.getName()).isEqualTo(studentExpected.getName());
                                assertThat(studentActual.getFaculty()).usingRecursiveComparison().isEqualTo(studentExpected.getFaculty());
                            });
                    assertThat(studentsActual.size()).isEqualTo(students.size());
                });
    }

    private Faculty generateFacultyFaker() {  //Вспомогательный метод c факерами для продвинутых тестов через mapper и stream
        Faculty facultyFaker = new Faculty();
        facultyFaker.setName(faker.harryPotter().house()); //Случайные имена из Поттера
        facultyFaker.setColor(faker.color().name()); //Случайные цвета
        return facultyFaker;
    }

    private Student generateStudentFaker() {  //Вспомогательный метод c факерами для продвинутых тестов через mapper и stream
        Student studentFaker = new Student();
        studentFaker.setName(faker.harryPotter().character());
        studentFaker.setAge(faker.random().nextInt(7, 18));
        return studentFaker;
    }
}
