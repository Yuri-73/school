package ru.hogwarts.school.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.hogwarts.school.exception.NoFacultyColorException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FacultyServiceTest {
    FacultyService out = new FacultyService();

    @Test
    void shouldCreateFaculty_WhenFaculty_ThenCorrectResult() {
        Faculty faculty1 = new Faculty(1L, "АО", "синий");

        assertEquals(faculty1, out.createFaculty(faculty1));
        Collection<Faculty> faculties = out.getAllFaculty();
        assertTrue(out.getAllFaculty().contains(faculty1));
    }

    @Test
    void shouldFindFaculty_WhenCorrectId_ThenCorrectResult() {
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        out.createFaculty(faculty1);
        assertEquals(faculty1, out.findFaculty(1l));
    }

    @Test
    void shouldFindFaculty_WhenNotCorrectId_ThenNull() {
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        out.createFaculty(faculty1);
        assertEquals(null, out.findFaculty(2l));
    }

    @Test
    void shouldEditFaculty_WhenCorrectFaculty_ThenCorrectResult() {
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        out.createFaculty(faculty1);
        Collection<Faculty> faculties = out.getAllFaculty();
        assertEquals(faculty1, out.findFaculty(1l));
        Faculty faculty2 = new Faculty(1L, "РиРНО", "голубой");
        assertEquals(faculty2, out.editFaculty(faculty2));
        assertTrue(out.getAllFaculty().contains(faculty2));
        assertFalse(out.getAllFaculty().contains(faculty1));
    }

    @Test
    void shouldEditFaculty_WhenNotCorrectFaculty_ThenNull() {
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        out.createFaculty(faculty1);
        Collection<Faculty> faculties = out.getAllFaculty();
        assertEquals(faculty1, out.findFaculty(1l));
        Faculty faculty2 = new Faculty(0L, "РиРНО", "голубой");
        assertEquals(null, out.editFaculty(faculty2));
    }


    @Test
    void shouldDeleteFaculty_WhenCorrectId_ThenRemoveFaculty() {
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        out.createFaculty(faculty1);
        Collection<Faculty> faculties = out.getAllFaculty();
        assertEquals(faculty1, out.findFaculty(1l));

        assertEquals(faculty1, out.deleteFaculty(1l));
        assertTrue(faculties.isEmpty());
    }

    @Test
    void shouldDeleteFaculty_WhenNotCorrectId_ThenNull() {
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        out.createFaculty(faculty1);
        Collection<Faculty> faculties = out.getAllFaculty();
        assertEquals(faculty1, out.findFaculty(1l));
        out.deleteFaculty(0l);
        assertTrue(!faculties.isEmpty());
        assertTrue(out.getAllFaculty().contains(faculty1));
    }

    @Test
    @DisplayName("Возвращает коллекцию из факультетов")
    void shouldGetAllFaculty() {
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        Faculty faculty2 = new Faculty(2L, "РиРНО", "голубой");
        Faculty faculty3 = new Faculty(3L, "АВ", "чёрный");
        out.createFaculty(faculty1);
        out.createFaculty(faculty2);
        out.createFaculty(faculty3);
        Collection<Faculty> faculties = out.getAllFaculty();
        assertEquals(faculties.size(), 3);
        assertTrue(out.getAllFaculty().contains(faculty1));
        assertTrue(out.getAllFaculty().contains(faculty2));
        assertTrue(out.getAllFaculty().contains(faculty3));

        org.assertj.core.api.Assertions.assertThat(faculties.size()).isEqualTo(3); //Вариант 2 (через assertj)
        //check - убеждаемся, что Лист содержит указанные 3 объекта в любом порядке (через assertj):
        org.assertj.core.api.Assertions.assertThat(faculties)
                .containsExactlyInAnyOrder(
                        new Faculty(1L, "АО", "синий"),
                        new Faculty(2L, "РиРНО", "голубой"),
                        new Faculty(3L, "АВ", "чёрный"));
    }

    @Test
    void shouldGetFacultyByAge_WhenCorrectAge_ThenResultFacultyAge() {
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        Faculty faculty2 = new Faculty(2L, "РиРНО", "голубой");
        Faculty faculty3 = new Faculty(3L, "АВ", "чёрный");
        Faculty faculty4 = new Faculty(4L, "СД", "синий");
        out.createFaculty(faculty1);
        out.createFaculty(faculty2);
        out.createFaculty(faculty3);
        out.createFaculty(faculty4);
        assertTrue(out.getFacultyByColor("синий").contains("АО"));
        assertTrue(out.getFacultyByColor("синий").contains("СД"));
        assertEquals(out.getFacultyByColor("синий").size(), 2);

        Collection<String> students = out.getFacultyByColor("синий");
        org.assertj.core.api.Assertions.assertThat(students).containsAll(List.of(faculty1.getName(), faculty4.getName()));
    }

    @Test
    void shouldGetFacultyByAge_WhenNotCorrectColor_ThenResultEmptyCollection() {
        Faculty faculty1 = new Faculty(1L, "АО", "синий");
        Faculty faculty2 = new Faculty(2L, "РиРНО", "голубой");
        Faculty faculty3 = new Faculty(3L, "АВ", "чёрный");
        Faculty faculty4 = new Faculty(4L, "СД", "синий");
        out.createFaculty(faculty1);
        out.createFaculty(faculty2);
        out.createFaculty(faculty3);
        out.createFaculty(faculty4);
        Assertions.assertThrows(NoFacultyColorException.class, () -> out.getFacultyByColor("белый"));
    }
}