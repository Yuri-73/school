package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

import java.util.Collection;
import java.util.Objects;

@Entity
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "oid")
    private Long id;
    private String name;
    private String color;

    @JsonBackReference
    @OneToMany(mappedBy = "faculty")  //создаётся в таблице студента колонка faculty_id
    private Collection<Student> students;

    public Faculty() {
    }

    public Faculty(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Collection<Student> getStudents() {
        return students;
    }

    public void setStudents(Collection<Student> students) {
        this.students = students;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faculty faculty = (Faculty) o;
        return Objects.equals(id, faculty.id) && Objects.equals(name, faculty.name) && Objects.equals(color, faculty.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}

