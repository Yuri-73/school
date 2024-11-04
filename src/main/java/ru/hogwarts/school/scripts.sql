select * from student --Вывод таблицы со всеми студентами;
select * from faculty --Вывод таблицы со всеми факультетами;
select  name from student --Вывод таблицы только с именами студентов (шаг-3.2 ДЗ);
select  name, age from student --Вывод таблицы с именами и возрастом студентов в такой последовательности;
select  name, age, id from student --Вывод таблицы с именами, возрастом и id студентов в такой последовательности;
select * from Student ORDER BY age --Вывод таблицы со всеми студентами, отсортированными по возрасту (шаг-3.5 ДЗ);
select name from Student ORDER BY age --Вывод только имён студентов, отсортированными по возрасту;
select * from Student where name like '%о%' --Вывод студентов, в именах которых есть буква 'о' (шаг-3.3 ДЗ);
select name from Student where name like '%о%' --Их же, но одни имена в таблице;
select * from Student where age>=20 AND age<=23 --Нахождение студентов в определённом возрасте (шаг 3.1 ДЗ);
select * from Student where age<id+5 --Поиск студента, идентификатор+5 которого больше его возраста (шаг-3.4 ДЗ)
select * from Student where age<id --Поиск студента, идентификатор которого больше его возраста (шаг-3.4 ДЗ)

select * from Student, Faculty where student.faculty_id = faculty.id and faculty.name = 'Пуффендуй' --После включения в таблицу столбца faculty_id,
--связал два экземпляра вместе в одну таблицу, чтобы в исходной таблице появился факультет, к которому относятся некоторые студенты;
select student.id, student.name, student.age from student, faculty where student.faculty_id = faculty.id and faculty.name = 'АО' --То же, но столбцов факультетов
--нет, но в условии подразумеваются;
select student.id, student.name, student.age from student, faculty where student.faculty_id = faculty.id and faculty.color = 'синий' --То же с фильтрацией и выбором студентов с факультетом по цвету