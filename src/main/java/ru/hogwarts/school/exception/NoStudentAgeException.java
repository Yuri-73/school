package ru.hogwarts.school.exception;

public class NoStudentAgeException extends RuntimeException {
    public NoStudentAgeException(int age) {
        super(String.format("Студентов с возрастом %s нет в базе", age));
    }
}
