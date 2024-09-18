package ru.hogwarts.school.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.StudentAvatarService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class AvatarController {
    private final StudentAvatarService studentAvatarService;

    public AvatarController(StudentAvatarService studentAvatarService) {
        this.studentAvatarService = studentAvatarService;
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //Загрузка файла на диск и в БД
    public ResponseEntity<String> uploadAvatar(@PathVariable Long id, @RequestParam MultipartFile avat) throws IOException {
        if (avat.getSize() >= 1024 * 300) {
            return ResponseEntity.badRequest().body("Файл слишком большой");
        }
        studentAvatarService.uploadAvatar(id, avat);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/avatar/preview")
    public ResponseEntity<byte[]> downloadAvatar(@PathVariable Long id) {
        // Чтение файла с из БД (здесь id студента)
        Avatar avatar = studentAvatarService.findAvatar(id); // Получаем информацию о картинке как объекта из БД
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType())); // Преобразование строчки с названием типа обратно в MediaType.
        //Получится объект типа MediaType, который мы отправляем в качестве заголовка
        headers.setContentLength(avatar.getData().length); // Другой заголовок - длина контента.
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getData());
    }

    @GetMapping(value = "/{id}/avatar")
    public void downloadAvatar(@PathVariable Long id, HttpServletResponse response) throws IOException {
        // Чтение файла с с локального диска
        Avatar avatar = studentAvatarService.findAvatar(id); // Получаем информацию о картинке как объекте. Но почему получаем также, как из БД?   ***?***
        Path path = Path.of(avatar.getFilePath()); // Путь к файлу на ЖД
        try (InputStream is = Files.newInputStream(path); // Объявляем переменные обоих потоков.
             // Выходной поток уже был создан, поэтому не через new, а через геттер:
             OutputStream os = response.getOutputStream()) {
            response.setContentType(avatar.getMediaType()); // Оба заголовка. Вернем тот же MediaType, который был сохранён в БД.
            response.setContentLength((int)avatar.getFileSize());
            is.transferTo(os);
        }
    }
}
