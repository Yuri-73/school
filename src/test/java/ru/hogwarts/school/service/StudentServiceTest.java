package ru.hogwarts.school.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.hogwarts.school.exception.NoStudentAgeException;
import ru.hogwarts.school.model.Student;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {
    StudentService out = new StudentService();

    @Test
    void shouldCreateStudent_WhenStudent_ThenCorrectResult() {
        Student student1 = new Student(1L, "Виктор", 23);

        assertEquals(student1, out.createStudent(student1));
        Collection<Student> students = out.getAllStudent();
        assertTrue(out.getAllStudent().contains(student1));
    }

    @Test
    void shouldFindStudent_WhenCorrectId_ThenCorrectResult() {
        Student student1 = new Student(1L, "Виктор", 23);
        out.createStudent(student1);
        assertEquals(student1, out.findStudent(1l));
    }

    @Test
    void shouldFindStudent_WhenNotCorrectId_ThenNull() {
        Student student1 = new Student(1L, "Виктор", 23);
        out.createStudent(student1);
        assertEquals(null, out.findStudent(2l));
    }

    @Test
    void shouldEditStudent_WhenCorrectStudent_ThenCorrectResult() {
        Student student1 = new Student(1L, "Виктор", 23);
        out.createStudent(student1);
        Collection<Student> students = out.getAllStudent();
        assertEquals(student1, out.findStudent(1l));
        Student student2 = new Student(1L, "Олег", 25);
        assertEquals(student2, out.editStudent(student2));
        assertTrue(out.getAllStudent().contains(student2));
        assertFalse(out.getAllStudent().contains(student1));
    }

    @Test
    void shouldEditStudent_WhenNotCorrectStudent_ThenNull() {
        Student student1 = new Student(1L, "Виктор", 23);
        out.createStudent(student1);
        Collection<Student> students = out.getAllStudent();
        assertEquals(student1, out.findStudent(1l));
        Student student2 = new Student(0L, "Олег", 25);
        assertEquals(null, out.editStudent(student2));
    }


    @Test
    void shouldDeleteStudent_WhenCorrectId_ThenRemoveStudent() {
        Student student1 = new Student(1L, "Виктор", 23);
        out.createStudent(student1);
        Collection<Student> students = out.getAllStudent();
        assertEquals(student1, out.findStudent(1l));
        out.deleteStudent(1l);
        assertTrue(students.isEmpty());
    }

    @Test
    void shouldDeleteStudent_WhenNotCorrectId_ThenNull() {
        Student student1 = new Student(1L, "Виктор", 23);
        out.createStudent(student1);
        Collection<Student> students = out.getAllStudent();
        assertEquals(student1, out.findStudent(1l));
        out.deleteStudent(0l);
        assertTrue(!students.isEmpty());
        assertTrue(out.getAllStudent().contains(student1));
    }

    @Test
    @DisplayName("Возвращает коллекцию из студентов")
    void shouldGetAllStudent() {
        Student student1 = new Student(1L, "Виктор", 23);
        Student student2 = new Student(2L, "Юрий", 24);
        Student student3 = new Student(3L, "Олег", 21);
        out.createStudent(student1);
        out.createStudent(student2);
        out.createStudent(student3);
        Collection<Student> students = out.getAllStudent();
        assertEquals(students.size(), 3);
        assertTrue(out.getAllStudent().contains(student1));
        assertTrue(out.getAllStudent().contains(student2));
        assertTrue(out.getAllStudent().contains(student3));
    }

    @Test
    void shouldGetStudentByAge_WhenCorrectAge_ThenResultStudentAge() {
        Student student1 = new Student(1L, "Виктор", 21);
        Student student2 = new Student(2L, "Юрий", 24);
        Student student3 = new Student(3L, "Олег", 21);
        Student student4 = new Student(4L, "Пётр", 21);
        out.createStudent(student1);
        out.createStudent(student2);
        out.createStudent(student3);
        out.createStudent(student4);
        assertTrue(out.getStudentByAge(21).contains("Виктор"));
        assertTrue(out.getStudentByAge(21).contains("Олег"));
        assertTrue(out.getStudentByAge(21).contains("Пётр"));
        assertEquals(out.getStudentByAge(21).size(), 3);
    }

    @Test
    void shouldGetStudentByAge_WhenNotCorrectAge_ThenResultEmptyCollection() {
        Student student1 = new Student(1L, "Виктор", 21);
        Student student2 = new Student(2L, "Юрий", 24);
        Student student3 = new Student(3L, "Олег", 21);
        Student student4 = new Student(4L, "Пётр", 21);
        out.createStudent(student1);
        out.createStudent(student2);
        out.createStudent(student3);
        out.createStudent(student4);
        Assertions.assertThrows(NoStudentAgeException.class, () -> out.getStudentByAge(22));
    }
}