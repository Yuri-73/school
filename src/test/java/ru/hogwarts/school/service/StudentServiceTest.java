package ru.hogwarts.school.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.exception.NoFacultyColorException;
import ru.hogwarts.school.exception.NoStudentAgeException;
import ru.hogwarts.school.exception.NullAgeException;
import ru.hogwarts.school.exception.NullEmptyColorException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepositoryMock;

    private StudentService out;

    @BeforeEach
    public void setUp() {
        out = new StudentService(studentRepositoryMock);
    }

    @Test
    void shouldCreateStudent_WhenStudent_ThenCorrectResult() {
        //test
        Student student1 = new Student(1L, "Юрий", 24);
        Mockito.when(studentRepositoryMock.save(student1)).thenReturn(student1);
        //check
        assertEquals(out.createStudent(student1), student1);

    }

    @Test
    void shouldFindStudent_WhenCorrectId_ThenCorrectResult() {
        //test
        Student student1 = new Student(1L, "Юрий", 24);
        Mockito.when(studentRepositoryMock.findById(student1.getId())).thenReturn(Optional.of((student1)));
        //check
        assertEquals(out.findStudent(student1.getId()), student1);
    }

    @Test
    void shouldFindStudent_WhenNotCorrectId_ThenNull() {
        //test
        Mockito.when(studentRepositoryMock.findById(any())).thenReturn(Optional.ofNullable(null));
        //check
        assertEquals(out.findStudent(2l), null);
    }

    @Test
    void shouldEditStudent_WhenCorrectStudent_ThenCorrectResult() {
        //test
        Student student1 = new Student(1L, "Юрий", 24);
        Student student2 = new Student(1L, "Аркадий", 29);
        Mockito.when(studentRepositoryMock.findById(student1.getId())).thenReturn(Optional.of((student1)));
        Mockito.when(studentRepositoryMock.save(student2)).thenReturn(student2);
        //check
        assertEquals(out.editStudent(student2), student2);
    }

    @Test
    void shouldEditStudent_WhenNotCorrectStudent_ThenNull() {
        //test
        Student student1 = new Student(1L, "Юрий", 24);
        Student student2 = new Student(1L, "Аркадий", 29);
        Mockito.when(studentRepositoryMock.findById(student1.getId())).thenReturn(Optional.ofNullable(null));
        //check
        assertEquals(out.editStudent(student2), null);
    }

    @Test
    void shouldDeleteStudent_WhenCorrectId_ThenRemoveStudent() {
        //test
        Student student1 = new Student(1L, "Юрий", 24);
        Mockito.when(studentRepositoryMock.findById(student1.getId())).thenReturn(Optional.of((student1)));
        //check
        assertEquals(out.deleteStudent(1l), student1);
    }

    @Test
    void shouldDeleteStudent_WhenNotCorrectId_ThenNull() {
        //test
        Student student1 = new Student(1L, "Юрий", 24);
        Mockito.when(studentRepositoryMock.findById(student1.getId())).thenReturn(Optional.ofNullable(null));
        //check
        assertEquals(out.deleteStudent(student1.getId()), null);
    }

    @Test
    @DisplayName("Возвращает список из студентов")
    void shouldGetAllStudent() {
        //test:
        Student student1 = new Student(1L, "Юрий", 24);
        Student student2 = new Student(2L, "Аркадий", 29);
        Student student3 = new Student(3L, "Пётр", 24);
        Collection<Student> students = List.of(student1, student2, student3);
        Mockito.when(studentRepositoryMock.findAll()).thenReturn((List<Student>) students);
        //check:
        assertEquals(students.size(), 3);  //Вариант 1 (через jupiter.api)
        assertTrue(out.getAllStudent().contains(student1));
        assertTrue(out.getAllStudent().contains(student2));
        assertTrue(out.getAllStudent().contains(student3));

        org.assertj.core.api.Assertions.assertThat(students.size()).isEqualTo(3); //Вариант 2 (через assertj), компоненты можно вразброс
        org.assertj.core.api.Assertions.assertThat(students)
                .containsExactlyInAnyOrder(
                        new Student(1L, "Юрий", 24),
                        new Student(2L, "Аркадий", 29),
                        new Student(3L, "Пётр", 24));
    }

    @Test
    void shouldGetStudentByColor_WhenCorrectAge_ThenResultStudentColor() {
        //test:
        Student student1 = new Student(1L, "Юрий", 24);
        Student student2 = new Student(2L, "Аркадий", 29);
        Student student3 = new Student(3L, "Пётр", 24);
        Student student4 = new Student(4L, "Виктор", 26);
        Collection<Student> students = List.of(student1, student2, student3, student4);
        Mockito.when(studentRepositoryMock.findAll()).thenReturn((List<Student>) students);
        //check:
        assertTrue(out.getStudentByAge(24).contains("Юрий")); //Вариант 1 (через jupiter.api)
        assertTrue(out.getStudentByAge(24).contains("Пётр"));
        assertEquals(out.getStudentByAge(24).size(), 2);

        Collection<String> students2 = out.getStudentByAge(29); //Вариант 2 (через assertj), компоненты можно вразброс
        org.assertj.core.api.Assertions.assertThat(students2).containsAll(List.of(student2.getName()));
    }

    @Test
    void shouldGetStudentByAge_WhenEmptyList_ThenNoStudentAgeException() {
        //test:
        Student student1 = new Student(1L, "Юрий", 24);
        Student student2 = new Student(2L, "Аркадий", 29);
        Student student3 = new Student(3L, "Пётр", 24);
        Student student4 = new Student(4L, "Виктор", 26);
        Collection<Student> students = List.of(student1, student2, student3, student4);
        Mockito.when(studentRepositoryMock.findAll()).thenReturn((List<Student>) students);
        //check:
        Assertions.assertThrows(NoStudentAgeException.class, () -> out.getStudentByAge(34));
        //test:
        List<Student> students2 = emptyList();
        Mockito.when(studentRepositoryMock.findAll()).thenReturn(students2);
        //check:
        Assertions.assertThrows(NoStudentAgeException.class, () -> out.getStudentByAge(24));
    }

    @Test  //ДЗ-3.3
    void shouldGetStudentByAge_WhenNotCorrectAge_ThenNullAgeException() {
        //test and check:
        Assertions.assertThrows(NullAgeException.class, () -> out.getStudentByAge(-1));

        Assertions.assertThrows(NullAgeException.class, () -> out.getStudentByAge(0));
    }

    @Test  //ДЗ-3.4 п.4.2
    void shouldFindStudentsByFaculty_name_CorrectFaculty_ThenStudents() {
        //test:
        Student student1 = new Student(1L, "Юрий", 24);
        Student student2 = new Student(2L, "Аркадий", 29);
        Student student3 = new Student(3L, "Пётр", 24);
        Student student4 = new Student(4L, "Виктор", 26);
        Collection<Student> students = List.of(student1, student2, student3, student4);
        Mockito.when(studentRepositoryMock.findStudentsByFaculty_name(any())).thenReturn((List<Student>) students);
        //check:
        assertEquals(out.findStudentsByFaculty_name(any()), students);
    }

    @Test //ДЗ-3.4 п.1.2
    void shouldFindByAgeBetween_CorrectParams_ThenStudents() {
        //test:
        Student student1 = new Student(1L, "Юрий", 24);
        Student student2 = new Student(2L, "Аркадий", 29);
        Student student3 = new Student(3L, "Пётр", 24);
        Student student4 = new Student(4L, "Виктор", 26);
        List<Student> students = List.of(student1, student2, student3, student4);
        Mockito.when(studentRepositoryMock.findByAgeBetween(anyInt(), anyInt())).thenReturn(students);
        //check:
        assertEquals(out.findByAgeBetween(2, 5), students);
    }
}