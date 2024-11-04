package ru.hogwarts.school.controller;

import net.bytebuddy.pool.TypePool;
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
import java.util.List;

import static java.util.Collections.emptyList;

@RestController
public class AvatarController {
    private final StudentAvatarService studentAvatarService;

    public AvatarController(StudentAvatarService studentAvatarService) {
        this.studentAvatarService = studentAvatarService;
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    /**
     * Загрузка файла на диск и в БД
     */
    public ResponseEntity<String> uploadAvatar(@PathVariable Long id, @RequestParam MultipartFile avat) throws IOException {
        if (avat.getSize() >= 1024 * 300) {
            return ResponseEntity.badRequest().body("Файл слишком большой");
        }
        studentAvatarService.uploadAvatar(id, avat);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/avatar/preview")
    /**
     * Чтение файла с из БД по id студента
     */
    public ResponseEntity<byte[]> downloadAvatar(@PathVariable Long id) {
        /**
         * Получаем информацию о картинке как объекта из БД
         */
        Avatar avatar = studentAvatarService.findAvatar(id);
        HttpHeaders headers = new HttpHeaders();
        /**
         * Преобразование строчки с названием типа обратно в MediaType.
         */
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        /**
         * Получится объект типа MediaType, который мы отправляем в качестве заголовка
         */
        headers.setContentLength(avatar.getData().length);
        /**
         * Другой заголовок - длина контента.
         */
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getData());
    }

    @GetMapping(value = "/{id}/avatar")
    /**
     * Чтение файла с с локального диска
     */
    public void downloadAvatar(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Avatar avatar = studentAvatarService.findAvatar(id); // Получаем информацию о картинке как объекте. Но почему получаем также, как из БД?   ***?***
        /**
         * Путь к файлу на ЖД
         */
        Path path = Path.of(avatar.getFilePath());
        /**
         * Объявляем переменные обоих потоков
         */
        try (InputStream is = Files.newInputStream(path);
             /**
              * Выходной поток уже был создан, поэтому не через new, а через геттер:
              */
            OutputStream os = response.getOutputStream()) {
            /**
             * Вернет тот же MediaType, который был сохранён в БД
             */
            response.setContentType(avatar.getMediaType());
            response.setContentLength((int) avatar.getFileSize());
            is.transferTo(os);
        }
    }

    @GetMapping(value = "/page-avatars")
    /**
     * Пагинация шаг 2 ДЗ-4.1 всего 1 метод: постраничный вывод аватарок:
     */
    public ResponseEntity<List<Avatar>> getAllAvatarsPage(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
                                                          @RequestParam(value = "size", required = false, defaultValue = "10") Integer pageSize) {
        if (pageNumber >= 1 && pageSize > 0) {
            List<Avatar> avatars = studentAvatarService.getAllAvatarsPage(pageNumber, pageSize);
            return ResponseEntity.ok(avatars);
        }
        return ResponseEntity.ok(emptyList());
    }
}
