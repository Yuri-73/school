package ru.hogwarts.school.controller.webMvcTest;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.AvatarController;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentAvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentAvatarService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AvatarController.class)
public class AvatarControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private StudentAvatarService studentAvatarService;

    @SpyBean
    private StudentService studentService;

    @MockBean
    private StudentAvatarRepository studentAvatarRepository;

    @MockBean
    private StudentRepository studentRepository;

    @InjectMocks
    private AvatarController avatarController;

//    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void getAllAvatarTest() throws Exception {
        //Начальные условия:
        Integer pageNumber = 1;
        Integer pageSize = 1;

        Avatar avatar = new Avatar();
        avatar.setData(new byte[]{});
        avatar.setFilePath("/1L.pdf");
        avatar.setFileSize(11L);
        avatar.setStudent(new Student(1l, "Bob", 22));
        avatar.setMediaType(".pdf");

        Avatar avatar2 = new Avatar();
        avatar2.setData(new byte[]{});
        avatar2.setFilePath("/2L.pdf");
        avatar2.setFileSize(12L);
        avatar2.setStudent(new Student(2l, "Bill", 23));
        avatar2.setMediaType(".pdf");

        //Формирование объекта page:
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        List<Avatar> list = List.of(avatar, avatar2);
        Page<Avatar> avatarPage = new PageImpl<>(list, pageable, 0);
        when(studentAvatarRepository.findAll(pageable)).thenReturn(avatarPage);

        //Тест:
        mockMvc.perform(MockMvcRequestBuilders
                .get("/page-avatars/")
                .param("page", "" + pageNumber)
                .param("size", "" + pageSize)
                .accept(MediaType.APPLICATION_JSON))
                //Контроль:
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].fileSize").value(11l))
                .andExpect(jsonPath("$[1].fileSize").value(12l))
                .andExpect(jsonPath("$[0].filePath").value("/1L.pdf"))
                .andExpect(jsonPath("$[1].filePath").value("/2L.pdf"))
                .andExpect(jsonPath("$[0].student.id").value(1l))
                .andExpect(jsonPath("$[1].student.id").value(2l))
        ;


    }
}
