SELECT s.name AS StudentName, s.age, f.name AS FacultyName
  FROM Student s
         LEFT JOIN Faculty f ON s.faculty_id = f.id;
--Создание таблицы с определёнными колонками из 2-х таблиц: студента (основной) и факультета (присоединяемой)

SELECT s.name AS StudentName, s.age
FROM Student s
         JOIN Avatar a ON s.id = a.student_id;
--Создание таблицы с определёнными колонками из 2-х таблиц: студента (основной) и аватара (присоединяемой)

SELECT s.id, a.id
FROM Student s
         INNER JOIN Avatar a ON s.id = a.student_id; --Создание существующих значений таблицы с определёнными колонками из 2-х таблиц: id студента (основной) и id аватара (присоединяемой)