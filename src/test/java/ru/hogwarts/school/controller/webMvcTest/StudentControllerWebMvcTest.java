package ru.hogwarts.school.controller.webMvcTest;

import aj.org.objectweb.asm.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.exception.NoFacultyColorException;
import ru.hogwarts.school.exception.NoStudentAgeException;
import ru.hogwarts.school.exception.NullAgeException;
import ru.hogwarts.school.exception.NullEmptyColorException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;
import com.github.javafaker.Faker;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private StudentService studentService;

    @MockBean
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentController studentController;

    private ObjectMapper mapper = new ObjectMapper();  //Для сериализации объекта

    private static JSONObject studentObject;
    private static JSONObject studentObject2;
    private static Student student;
    private static Student student2;
    Faculty faculty;   //Для теста по проверке вывода факультета по студенту

    @BeforeEach
    void setup() throws JSONException {
        studentObject = new JSONObject();  //Создание и инициализация JSON-объекта для подачи в мок-URL тестов (сериализация 2-м способом)
        studentObject.put("name", "Bob");
        studentObject.put("age", 25);

        studentObject2 = new JSONObject();  //Создание и инициализация JSON-объекта для подачи в мок-URL тестов
        studentObject2.put("name", "Jhon");
        studentObject2.put("age", 26);

        student = new Student();
        student.setId(1l);
        student.setName("Bob");
        student.setAge(25);

        student2 = new Student();
        student2.setId(2l);
        student2.setName("Jhon");
        student2.setAge(26);
    }

    @Test
    public void createStudentMvcTest() throws Exception {
        //initial data:
        when(studentRepository.save(ArgumentMatchers.any(Student.class))).thenReturn(student);
        //test:
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(studentObject2.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //check:
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(student.getName()))
                .andExpect(jsonPath("$.age").value(student.getAge()));
        verify(studentRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void getStudentMvcTest() throws Exception {
        //initial data:
        when(studentRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(student));
        //test:
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/" + student2.getId())
                        .accept(MediaType.APPLICATION_JSON))
                //check:
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(student.getName()))
                .andExpect(jsonPath("$.age").value(student.getAge()))
                .andExpect(jsonPath("$.id").value(student.getId()));
        verify(studentRepository, times(2)).findById(any());
    }

    @Test
    public void removeStudentMvcTest() throws Exception {
        //initial data:
        when(studentRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(student2));
        //test:
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/" + student2.getId())
                        .accept(MediaType.APPLICATION_JSON))
                //check:
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(student2.getName()))
                .andExpect(jsonPath("$.age").value(student2.getAge()))
                .andExpect(jsonPath("$.id").value(student2.getId()));
    }

    @Test
    public void editStudentMvcTest() throws Exception {
        //initial data:
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        student.setName("Victor");

        //Создание тела запроса после изменения имени в виде JSON:
        JSONObject updateStudent = new JSONObject();
        updateStudent.put("id", student.getId());
        updateStudent.put("name", student.getName());
        updateStudent.put("color", student.getAge());

        when(studentRepository.save(ArgumentMatchers.any(Student.class))).thenReturn(student);
        //test:
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student/")
                        .content(updateStudent.toString())  //Передача тела объекта updateFacultyRq в контроллер в виде строки по заданному URL
                        .contentType(MediaType.APPLICATION_JSON) //Передача типа (контента JSON)
                        .accept(MediaType.APPLICATION_JSON))  //Получение тела объекта
                //check:
                .andExpect(status().isOk())  //Если тест проходит, то статус 200
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.name").value("Victor"))
                .andExpect(jsonPath("$.age").value(student.getAge()));
        verify(studentRepository, Mockito.times(2)).save(any());
    }

    @Test
    public void getAllStudentMvcTest() throws Exception {
        //initial data:
        when(studentRepository.findAll()).thenReturn(Collections.singletonList(student)); //Конструкция, позволяющая создать коллекцию из 1 элемента
        //test:
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student")
                        .accept(MediaType.APPLICATION_JSON)) //Получение тела объекта
                //check:
                .andExpect(status().isOk())
                //Выдаётся в виде JSON-коллекции:
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(Collections.singletonList(student))))
                .andExpect(jsonPath("$").isArray()) //Превращение JSON-Листа-одиночки в JSON-массив студентов из одного объекта
                .andExpect(jsonPath("$[0].name").value(student.getName())) //Пробежка по полученному массиву
                .andExpect(jsonPath("$[0].age").value(student.getAge()));


        //Вариант от Ильи (вебинар 3.6, время: 01.31.00)
        //Подготовка данных:
        when(studentRepository.findAll()).thenReturn(List.of(new Student(1l, "Name", 20), new Student(2l, "Name2", 21)));
        //Тестирование:
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student"))
                //Контроль:
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())  //Превращение JSON-Листа в JSON-массив студентов
                .andExpect(jsonPath("$[0].name").value("Name"))
                .andExpect(jsonPath("$[1].name").value("Name2"))
                .andExpect(jsonPath("$[0].age").value(20))
                .andExpect(jsonPath("$[1].age").value(21));
    }

    @Test
    public void getStudentByAgeMvcTest() throws Exception {
        //Подготовка данных:
        when(studentRepository.findAll()).thenReturn(List.of(new Student(1l, "Name", 20), new Student(2l, "Name2", 22)));
        //Тестирование:
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/get/by-age?age=22"))
                //Контроль:
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Name2"))
                .andExpect(jsonPath("$[0].age").value(22));
    }

    @Test
    public void findByAgeBetweenStudentMvcTest() throws Exception {  //Тест бесполезный, т.к. сервис логикой не обладает
        //Подготовка данных:
        when(studentRepository.findByAgeBetween(anyInt(), anyInt())).thenReturn(List.of(new Student(1l, "Name", 20),
                new Student(2l, "Name2", 22), new Student(3l, "Name3", 23)));
        //Тестирование:
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/age?min=20&max=23"))
                //Контроль:
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Name"))
                .andExpect(jsonPath("$[0].age").value(20))
                .andExpect(jsonPath("$[1].name").value("Name2"))
                .andExpect(jsonPath("$[1].age").value(22))
                .andExpect(jsonPath("$[2].name").value("Name3"))
                .andExpect(jsonPath("$[2].age").value(23));
    }

    @Test
    public void findStudentsByFacultyNameMvcTest() throws Exception {  //Тест бесполезный, т.к. сервис логикой не обладает
        //Подготовка данных:
        faculty = new Faculty(1l, "Hogward", "Red");
        when(studentRepository.findStudentsByFacultyName(anyString())).thenReturn(List.of(new Student(1l, "Name", 20), new Student(2l, "Name2", 22)));
        //Тестирование:
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/faculty?facultyName=" + faculty.getName()))
                //Контроль:
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Name"))
                .andExpect(jsonPath("$[0].age").value(20))
                .andExpect(jsonPath("$[1].name").value("Name2"))
                .andExpect(jsonPath("$[1].age").value(22));
    }

    @Test
    public void getFacultyOfStudentMvcTest() throws Exception {
        //Подготовка данных:
        faculty = new Faculty(1l, "Hogward", "Red");
        student.setFaculty(faculty);
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        //Тестирование:
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/" + student.getId() + "/faculty"))
                //Контроль:
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
    }
}
