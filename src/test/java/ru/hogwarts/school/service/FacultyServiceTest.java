package ru.hogwarts.school.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.hogwarts.school.exception.NoFacultyColorException;
import ru.hogwarts.school.exception.NullEmptyColorException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class FacultyServiceTest {

    @Mock
    private FacultyRepository facultyRepositoryMock;

    private FacultyService out;

    @BeforeEach
    public void setUp() {
        out = new FacultyService(facultyRepositoryMock);
    }

    @Test
    void shouldCreateFaculty_WhenFaculty_ThenCorrectResult() {
        //test
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        Mockito.when(facultyRepositoryMock.save(faculty1)).thenReturn(faculty1);
        //check
        assertEquals(out.createFaculty(faculty1), faculty1);

    }

    @Test
    void shouldFindFaculty_WhenCorrectId_ThenCorrectResult() {
        //test
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        Mockito.when(facultyRepositoryMock.findById(faculty1.getId())).thenReturn(Optional.of((faculty1)));
        //check
        assertEquals(out.findFaculty(faculty1.getId()), faculty1);
    }

    @Test
    void shouldFindFaculty_WhenNotCorrectId_ThenNull() {
        //test
        Mockito.when(facultyRepositoryMock.findById(any())).thenReturn(Optional.ofNullable(null));
        //check
        assertEquals(out.findFaculty(2l), null);
    }

    @Test
    void shouldEditFaculty_WhenCorrectFaculty_ThenCorrectResult() {
        //test
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        Faculty faculty2 = new Faculty(1L, "РЭО", "чёрный");
        Mockito.when(facultyRepositoryMock.findById(faculty1.getId())).thenReturn(Optional.of((faculty1)));
        Mockito.when(facultyRepositoryMock.save(faculty2)).thenReturn(faculty2);
        //check
        assertEquals(out.editFaculty(faculty2), faculty2);
    }

    @Test
    void shouldEditFaculty_WhenNotCorrectFaculty_ThenNull() {
        //test
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        Faculty faculty2 = new Faculty(1L, "РЭО", "чёрный");
        Mockito.when(facultyRepositoryMock.findById(faculty1.getId())).thenReturn(Optional.ofNullable(null));
        //check
        assertEquals(out.editFaculty(faculty2), null);
    }

    @Test
    void shouldDeleteFaculty_WhenCorrectId_ThenRemoveFaculty() {
        //test
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        Mockito.when(facultyRepositoryMock.findById(faculty1.getId())).thenReturn(Optional.of((faculty1)));
        //check
        assertEquals(out.deleteFaculty(1l), faculty1);
    }

    @Test
    void shouldDeleteFaculty_WhenNotCorrectId_ThenNull() {
        //test
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        Mockito.when(facultyRepositoryMock.findById(faculty1.getId())).thenReturn(Optional.ofNullable(null));
        //check
        assertEquals(out.deleteFaculty(faculty1.getId()), null);
    }

    @Test
    @DisplayName("Возвращает список из факультетов")
    void shouldGetAllFaculty() {
        //test:
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        Faculty faculty2 = new Faculty(2L, "РиРНО", "голубой");
        Faculty faculty3 = new Faculty(3L, "АВ", "чёрный");
        Collection<Faculty> faculties = List.of(faculty1, faculty2, faculty3);
        Mockito.when(facultyRepositoryMock.findAll()).thenReturn((List<Faculty>) faculties);
        //check:
        assertEquals(faculties.size(), 3);  //Вариант 1 (через jupiter.api)
        assertTrue(out.getAllFaculty().contains(faculty1));
        assertTrue(out.getAllFaculty().contains(faculty2));
        assertTrue(out.getAllFaculty().contains(faculty3));

        org.assertj.core.api.Assertions.assertThat(faculties.size()).isEqualTo(3); //Вариант 2 (через assertj), компоненты можно вразброс
        org.assertj.core.api.Assertions.assertThat(faculties)
                .containsExactlyInAnyOrder(
                        new Faculty(1L, "АО", "синий"),
                        new Faculty(2L, "РиРНО", "голубой"),
                        new Faculty(3L, "АВ", "чёрный"));
    }

    @Test
    void shouldGetFacultyByColor_WhenCorrectAge_ThenResultFacultyColor() {
        //test:
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        Faculty faculty2 = new Faculty(2L, "РиРНО", "голубой");
        Faculty faculty3 = new Faculty(3L, "АВ", "чёрный");
        Faculty faculty4 = new Faculty(4L, "СД", "синий");
        Collection<Faculty> faculties = List.of(faculty1, faculty2, faculty3, faculty4);
        Mockito.when(facultyRepositoryMock.findAll()).thenReturn((List<Faculty>) faculties);
        //check:
        assertTrue(out.getFacultyByColor("синий").contains("АО")); //Вариант 1 (через jupiter.api)
        assertTrue(out.getFacultyByColor("синий").contains("СД"));
        assertEquals(out.getFacultyByColor("синий").size(), 2);

        Collection<String> students = out.getFacultyByColor("синий"); //Вариант 2 (через assertj), компоненты можно вразброс
        org.assertj.core.api.Assertions.assertThat(students).containsAll(List.of(faculty1.getName(), faculty4.getName()));
    }

    @Test
    void shouldGetFacultyByAge_WhenEmptyList_ThenNoFacultyColorException() {
        //test:
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        Faculty faculty2 = new Faculty(2L, "РиРНО", "голубой");
        Faculty faculty3 = new Faculty(3L, "АВ", "чёрный");
        Faculty faculty4 = new Faculty(4L, "СД", "синий");
        Collection<Faculty> faculties = List.of(faculty1, faculty2, faculty3, faculty4);
        Mockito.when(facultyRepositoryMock.findAll()).thenReturn((List<Faculty>) faculties);
        //check:
        Assertions.assertThrows(NoFacultyColorException.class, () -> out.getFacultyByColor("белый"));
        //test:
        List<Faculty> faculties2 = emptyList();
        Mockito.when(facultyRepositoryMock.findAll()).thenReturn(faculties2);
        //check:
        Assertions.assertThrows(NoFacultyColorException.class, () -> out.getFacultyByColor("синий"));
    }

    @Test
    void shouldGetFacultyByAge_WhenNotCorrectColor_ThenNullEmptyColorException() {
        //test and check:
        Assertions.assertThrows(NullEmptyColorException.class, () -> out.getFacultyByColor(""));

        Assertions.assertThrows(NullEmptyColorException.class, () -> out.getFacultyByColor(null));
    }

    //ДЗ-3.4:
    @Test //ДЗ-3.4 для метода, созданного по шагу 1.2(2)* (доп-но к заданию)
    // (можно было все тест-методы этого ДЗ не делать, потому что они, по-сути, проверяют транзит и сами себя)
    void shouldFindByNameAndColor_WhenCorrectNameAndColor_ThenFaculties() {
        //test:
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        Mockito.when(facultyRepositoryMock.findByNameAndColor(any(), any())).thenReturn(faculty1);
        //check:
        assertEquals(out.findByNameAndColor(any(), any()), faculty1);
    }

    @Test //ДЗ-3.4 для метода, созданного по шагу 1.2(1)
    void shouldFindByColorIgnoreCase_WhenNullColor_ThenFaculties() {
        //test:
        Faculty faculty1 = new Faculty(1L, "АО", "синий");

        Mockito.when(facultyRepositoryMock.findByColorIgnoreCase(any())).thenReturn((faculty1));
        //check:
        assertEquals(out.findByColorIgnoreCase(any()), faculty1);
    }

    @Test //ДЗ-3.4 для метода, созданного по шагу 1.2(3)
    void shouldFindByName_WhenCorrectName_ThenFaculties() {
        //test:
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        Mockito.when(facultyRepositoryMock.findByName(any())).thenReturn(faculty1);
        //check:
        assertEquals(out.findByName(any()), faculty1);
    }

//    @Test //ДЗ-3.4 для метода, созданного по шагу 4.2
//    void shouldgetStudentsOfFaculty_WhenCorrectId_ThenStudents() {
//        //test:
//        Student student1 = new Student(1l, "Bob", 33);
//        Student student2 = new Student(2l, "Jon", 35);
//
//        Faculty faculty = new Faculty(1L, "АО", "синий");
//        faculty.setStudents(List.of(student1, student2));
//
//        Mockito.when(facultyRepositoryMock.findById(any())).thenReturn(Optional.of((faculty)));
//        //check:
//        assertEquals(out.getStudentsOfFaculty(any()), faculty.getStudents());
//    }

//    @Test //ДЗ-3.4 для метода, созданного по шагу 4.2
//    void shouldgetStudentsOfFaculty_WhenNotCorrectId_ThenEmptyList() {
//        //test:
//        Student student1 = new Student(1l, "Bob", 33);
//        Student student2 = new Student(2l, "Jon", 35);
//
//        Faculty faculty = new Faculty(1L, "АО", "синий");
//        Mockito.when(facultyRepositoryMock.findById(any())).thenReturn(Optional.of((faculty)));
//        //check:
//        assertEquals(out.getStudentsOfFaculty(any()), Collections.emptyList());
//    }
}