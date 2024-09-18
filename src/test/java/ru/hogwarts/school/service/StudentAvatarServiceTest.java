//package ru.hogwarts.school.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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

//@ExtendWith(MockitoExtension.class)
//class StudentAvatarServiceTest {
//
//    @Mock
//    private StudentAvatarRepository studentAvatarRepository;
//
//    @Mock
//    private StudentRepository studentRepository;
//
//
//    private StudentService studentService = new StudentService(studentRepository);
//
//    private StudentAvatarService out;
//
//    @BeforeEach
//    public void setUp() {
//        out = new StudentAvatarService(studentService, studentAvatarRepository);
//    }
//
//    @Test
//    void uploadAvatar() {
//        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some content".getBytes());
//        Mockito.when(studentRepository.findById(1l)).thenReturn(null);
//        //check
//        Assertions.assertThrows(StudentNotFoundException.class, () -> out.uploadAvatar(1l, file));
//    }
//
//    @Test
//    void findAvatar() {
//    }
//
//    @Test
//    void generateImagePreview() {
//    }
//}