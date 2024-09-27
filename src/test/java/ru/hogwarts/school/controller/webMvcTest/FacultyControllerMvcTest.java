package ru.hogwarts.school.controller.webMvcTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
public class FacultyControllerMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private FacultyService facultyService;

    @MockBean
    private FacultyRepository facultyRepository;

    @InjectMocks
    private FacultyController facultyController;

    private ObjectMapper mapper = new ObjectMapper();

    private static Long id = 1L;
    private static String name = "Blowers";
    private static String color = "yellow";
    private static JSONObject studentObject;
    private static Faculty faculty;
    private static Faculty faculty2;

    @BeforeEach
    void setUp() throws JSONException {
        studentObject = new JSONObject();
        studentObject.put("name", name);
        studentObject.put("color", color);

        faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor(color);

        faculty2 = new Faculty();
        faculty2.setId(2l);
        faculty2.setName("Bob");
        faculty2.setColor("blau");
    }

    @Test
    public void createFacultyMvcTest() throws Exception {
        //when and then:
        when(facultyRepository.save(ArgumentMatchers.any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  //then
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
    }

    @Test
    public void getFacultyMvcTest() throws Exception {
        //when and then:
        when(facultyRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + faculty.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
    }

    @Test
    public void removeFacultyMvcTest() throws Exception {
//                .andExpect(content().string(""));  //Убеждаемся, что ничего нет на выходе
        //when and then:
        when(facultyRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(faculty));
        doNothing().when(facultyRepository).deleteById(id); //Пустая заглушка вместо метода facultyRepository.deleteById()
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
    }

    @Test
    public void editFacultyMvcTest() throws Exception {
        //when and then:
        when(facultyRepository.findById(id)).thenReturn(Optional.of(faculty));
        faculty.setName("mmmmmm");

        JSONObject updateFacultyRq = new JSONObject();
        updateFacultyRq.put("id", faculty.getId());
        updateFacultyRq.put("name", faculty.getName());
        updateFacultyRq.put("color", faculty.getColor());

        when(facultyRepository.save(ArgumentMatchers.any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty/")
                        .content(updateFacultyRq.toString())  //Передача тела объекта updateFacultyRq в контроллер в виде строки по заданному URL
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  //Если тест проходит, то статус 200
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("mmmmmm"))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
    }

    @Test
    public void findByColorIgnoreCaseTest() throws Exception {  //Тест поиска факультета по его цвету без учёта регистра
        //when and then:
        when(facultyRepository.findByColorIgnoreCase(anyString())).thenReturn(faculty2);

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
        //when and then:
        when(facultyRepository.findByName(anyString())).thenReturn(faculty2);

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
        //when and then:
        when(facultyRepository.findByNameAndColor(anyString(), anyString())).thenReturn(faculty2);
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
        //when and then:
        when(facultyRepository.findAll()).thenReturn(Collections.singletonList(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(Collections.singletonList(faculty))));
    }

    @Test
    public void getStudentsOfFacultyTest() throws Exception {
        // given
        Student student = new Student(1l, "Bob", 25);
        Student student2 = new Student(2l, "Sem", 21);
        faculty.setStudents(List.of(student, student2));
        when(facultyRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + faculty.getId() + "/students/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(faculty.getStudents())));
        assertEquals(facultyService.getStudentsOfFaculty(anyLong()), faculty.getStudents());
    }
}
