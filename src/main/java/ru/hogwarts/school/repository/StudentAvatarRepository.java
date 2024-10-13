package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Avatar;

import java.util.List;
import java.util.Optional;

public interface StudentAvatarRepository extends JpaRepository<Avatar, Long> {
    Optional<Avatar> findByStudentId(Long id);

    // Пагинация шаг 2 ДЗ-4.1 всего 1 метод: постраничный вывод аватарок:
    List<Avatar> findAll();
}
