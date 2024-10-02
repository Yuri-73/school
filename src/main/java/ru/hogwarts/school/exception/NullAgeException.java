package ru.hogwarts.school.exception;

public class NullAgeException extends RuntimeException {
    public NullAgeException(int age) {
        super(String.format("Возраст студента %s нулевой или отрицательный", age));
    }
}
