package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.NoStudentAgeException;
import ru.hogwarts.school.model.Student;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final Map<Long, Student> allStudent = new HashMap<>(); //Пустая пополняемая Мапа
    private Long countId = 0L; //Создаем поле идентификатора и инициализируем нулём, чтобы в первом же ключе инкриментируем

    public Student createStudent(Student student) { //Вносим в коллекцию-Мапу студентов по одному
        student.setId(++countId);
        allStudent.put(countId, student);
        return student;
    }

    public Student findStudent(Long id) { //Находим в коллекции студента по ID
        if (allStudent.containsKey(id)) {
            return allStudent.get(id);
        }
        return null;
    }

    public Student editStudent(Student student) { //Редактируем объект в коллекции по ID
        if (allStudent.containsKey(student.getId())) {
            allStudent.put(student.getId(), student);
            return student;
        }
        return null;
    }

    public Student deleteStudent(Long id) { //Удаляем из коллекции студента по ID
        if (allStudent.get(id) != null) {
            return allStudent.remove(id);
        }
        return null;
    }

    public Collection<Student> getAllStudent() { //Выводим из коллекции-Мапы всех студентов
        return allStudent.values(); //Превращение Мапы в Лист
    }

    public Collection<String> getStudentByAge(Integer age) { //Ищем в коллекции-Мапе студентов по возрасту на входе

        Collection<String> studentListByAge = getAllStudent()
                .stream()
                .filter(e -> e.getAge() == age)
                .map(e -> e.getName())
                .collect(Collectors.toList());
        if (studentListByAge.isEmpty())
            throw new NoStudentAgeException();
        return studentListByAge;
    }
}

