package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Avatar;

import java.util.Optional;

public interface StudentAvatarRepository extends JpaRepository<Avatar, Long> {
    Optional<Avatar> findByStudentId(Long id);
}
