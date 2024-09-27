package ru.hogwarts.school.model;

import javax.persistence.*;

@Entity
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filePath; //Путь файла на диске, куда будем сохранять
    private Long fileSize; //Размер файла на диске
    private String mediaType; //Тип этого файла на диске
    @Lob  //Создание поле внутри БД, где будем хранить нашу картинку, уменьшенную в размере
    private byte[] data; //Переменная экземпляра для хранения массива байт в БД
    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student; //У студента м.б. только одна картинка (и наоборот).

    public Avatar() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }  //Путь до локального диска, где сохранён файл

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    } //Мощность файла, сохранённого на локальном диске

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMediaType() {
        return mediaType;
    } //Тип файла, сохранённого на локальном диске

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public byte[] getData() {
        return data;
    }  //Массив, куда будет сохранен файл для БД

    public void setData(byte[] data) {
        this.data = data;
    }

    public Student getStudent() {
        return student;
    } // Этому студенту будет принадлежать аватарка. Геттер и сеттер возможны, т.к. появилась аннотация @OneToOne

    public void setStudent(Student student) {
        this.student = student;
    }
}
