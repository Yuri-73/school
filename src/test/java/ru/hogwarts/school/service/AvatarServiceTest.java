package ru.hogwarts.school.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
//import ru.hogwarts.school.models.domain.Avatar;
//import ru.hogwarts.school.models.domain.Student;
//import ru.hogwarts.school.models.dto.StudentDto;
import ru.hogwarts.school.repository.StudentAvatarRepository;
//import ru.hogwarts.school.services.repositories.AvatarRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  //https://github.com/Tavkel/hogwarts_db/blob/master/src/test/java/ru/hogwarts/school/services/implementations/AvatarServiceImplTest.java
class AvatarServiceTest {
    private StudentAvatarRepository studentAvatarRepository = mock(StudentAvatarRepository.class);
    private StudentService studentService = mock(StudentService.class);
    private final String path = "./src/test/resources";
    private StudentAvatarService out;
    byte[] image;
    private final Student studentDto = new Student(1L, "Harry", 11);

    public AvatarServiceTest() throws IOException {
        out = new StudentAvatarService(studentService, studentAvatarRepository, path);  //Теперь path  в конструкторе, чтобы был тестовый путь

        try (InputStream is = Files.newInputStream(Path.of(path + "/test.jpg"));  //сканируем из файла
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ) {
            bis.transferTo(baos);
            this.image = baos.toByteArray(); //укладываем в массив байт
        }
    }

    @BeforeEach
    private void resetDirectory() throws IOException {
        Files.deleteIfExists(Path.of(path + "/1.jpg"));  //каждый раз вначале удаляем созданный файл
    }

    @Test
    void shouldFindAvatar_WhenPositiveIdStudent_ThenReturnAvatar() {
        //Начальные условия:
        long studentId = 1L;
        Avatar avatar = new Avatar();
        when(studentService.findStudent(studentDto.getId())).thenReturn(studentDto);
        when(studentAvatarRepository.findByStudentId(studentDto.getId())).thenAnswer(p -> {
            Student s = new Student();
            s.setId(studentId);
            avatar.setStudent(s);
            return Optional.of(avatar);
        });

        //Тест:
        var actual = out.findAvatar(studentId);

        //Контроль:
        assertEquals(studentId, actual.getStudent().getId());
        assertEquals(avatar, actual);

        verify(studentService, times(1)).findStudent(studentId);
        verify(studentAvatarRepository, times(1)).findByStudentId(studentId);
    }

    @Test
    void shouldFindAvatar_WhenNoStudent_ThenReturnNoSuchElementException() {
        //Начальные условия:
        when(studentService.findStudent(anyLong())).thenThrow(new NoSuchElementException("Student not found!"));

        //Тест:
        var exception = assertThrows(NoSuchElementException.class, () -> out.findAvatar(422L));

        //Контроль:
        assertEquals("Student not found!", exception.getMessage());

        verify(studentService, times(1)).findStudent(anyLong());
        verify(studentAvatarRepository, never()).findByStudentId(anyLong());  //Убеждаемся, что до него не дошло
    }

    @Test
    void shouldUploadAvatar_WhenHadAvatar_ThenAvatarToDbAndDisk() throws IOException {
        //Начальные условия:
        MultipartFile file = new MockMultipartFile("filename.jpg",
                "filename.jpg", "jpg", image);
        long studentId = 1L;
        Avatar oldAvatar = new Avatar();
        when(studentService.findStudent(studentId)).thenReturn(studentDto);
        when(studentAvatarRepository.findByStudentId(studentId)).thenReturn(Optional.of(oldAvatar));

        //Тест:
        out.uploadAvatar(studentId, file);
        Path path1 = Path.of(path + "/" + studentId + ".jpg");

        //Контроль:
        assertTrue(Files.isReadable(path1));
//        assertTrue(Files.exists(path1));   //Можно и так

        verify(studentService, times(2)).findStudent(studentId);
        verify(studentAvatarRepository, times(1)).findByStudentId(studentId);
        verify(studentAvatarRepository, times(1)).save(any(Avatar.class));

        assertEquals(studentDto, studentAvatarRepository.findByStudentId(studentId).get().getStudent());
    }
}
