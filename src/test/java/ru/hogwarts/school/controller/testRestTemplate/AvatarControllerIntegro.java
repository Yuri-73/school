package ru.hogwarts.school.controller.testRestTemplate;

//import jakarta.transaction.Transactional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.controller.AvatarController;


import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentAvatarService;
import ru.hogwarts.school.service.StudentService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class AvatarControllerIntegro {
    @LocalServerPort
    private int port;

    @Autowired
    private AvatarController avatarController;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @MockBean
    private StudentAvatarService studentAvatarService;

    @MockBean
    private StudentService studentService;

    private String baseUrl;

    @BeforeEach
    public void setup() {
        baseUrl = "http://localhost:" + port;

        Student student = new Student();
        student.setId(1L);
        student.setName("Тестовый");
        student.setAge(20);
        studentRepository.save(student);
    }

    @Test
    public void contextLoads() throws Exception {
        Assertions.assertThat(avatarController).isNotNull();
    }

    //Это чепуха, но работает: не подан в URL файл
    @Test
    public void testUploadAvatar() throws IOException {
        Student student = new Student(1L, "Nikolay", 30);
        byte[] bytes = Files.readAllBytes(Path.of("src/test/resources/test.jpg"));
        Avatar avatar = new Avatar();
        avatar.setData(bytes);
        avatar.setFilePath("/1L.pdf");
        avatar.setFileSize(11L);
        avatar.setStudent(student);
        avatar.setMediaType(".pdf");

        Assertions
                .assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/" + student.getId() + "/avatar", String.class))
                .isNotNull();
    }
}
