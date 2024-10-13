SELECT s.name AS StudentName, s.age, f.name AS FacultyName
  FROM Student s
         LEFT JOIN Faculty f ON s.faculty_id = f.id;
//Создание таблицы с определёнными колонками из 2-х таблиц: студента (основной) и факультета (присоединяемой)

SELECT s.name AS StudentName, s.age
FROM Student s
         JOIN Avatar a ON s.id = a.student_id;
//Создание таблицы с определёнными колонками из 2-х таблиц: студента (основной) и аватара (присоединяемой)