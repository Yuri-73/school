//package ru.hogwarts.school.service;
//
//import nonapi.io.github.classgraph.utils.FileUtils;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentAvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//@ExtendWith(MockitoExtension.class)
//class StudentAvatarServiceTest {
//
//    @Mock
//    private StudentAvatarRepository studentAvatarRepository;
//
//    @Mock
//    private StudentRepository studentRepository;
//
//    @Mock
//    private StudentService studentService;
//
//    private StudentAvatarService out;
//
//    Student student = new Student(1L, "Olga", 17);
//
//    String avatarsDir = "./src/test/resources/avatar";
//
//    @BeforeEach
//    public void setUp() {
//        out = new StudentAvatarService(avatarsDir,studentService, studentAvatarRepository);
//    }

//    @Test
//    void uploadAvatar_avatarSavedToDbAndDirectory() throws IOException {
//        String fileName = "1.jpeg";
//        MultipartFile file = new MockMultipartFile(
//                fileName, fileName,
//                "image/jpeg", new byte[]{});
////        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some content".getBytes());
//        when(studentService.findStudent(student.getId())).thenReturn(student);
//        when(studentAvatarRepository.findByStudentId(student.getId()))
//                .thenReturn(Optional.empty());
//
//        out.uploadAvatar(student.getId(), file);
//
////        verify(avatarRepository, times(1)).save(any());
//        assertTrue(FileUtils.canRead(new File(avatarsDir + "/" + student.getId() +
//                "." + fileName.substring(fileName.lastIndexOf(".") + 1))));
//    }
//   Тест uploadAvatar_avatarSavedToDbAndDirectory() проходит, если: 1. В аватар-сервисе занести в конструктор [@Value("${students.avatar.dir.path}") private String avatarsDir] третьим параметром
//   2. Вместо avatar.setData(generateImagePreview(filePath)) в том же аватар-сервисе вставить avatar.setData(file.getBytes()),
//   но почему-то пропадают картинки в файловой системе и Сваггер при гет-запросе картинки выдаёт статус 500, а при гет-запросе из БД 200 и картинку выдаёт!
//   Причину необходимости замены на avatar.setData(file.getBytes()) нашёл: почему-то в методе generateImagePreview(Path filePath) после строки
//   BufferedImage image = ImageIO.read(bis); и System.out.println("image = " +image); программа падает, т.к. image отчего-то равна null
// В помощь: https://github.com/Kseniia313/HogwartsSchool3.2 и https://github.com/Tavkel/hogwarts_db/blob/master/src/test/java/ru/hogwarts/school/services/implementations/AvatarServiceImplTest.java

//    @Test
//    void findAvatar() {
//    }

//    @Test
//    void generateImagePreview() {
//    }
//}