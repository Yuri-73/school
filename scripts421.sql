ALTER TABLE Student
    ADD CONSTRAINT CheckStudentAge CHECK(Age>=16);
--Запись ограничения 'CheckStudentAge' студентов по возрасту.
--Не пройдёт ограничение, если в таблице уже есть студенты с возрастом, менее 16

ALTER TABLE Student
DROP CONSTRAINT CheckStudentAge; --Снятие ограничения студентов по возрасту

ALTER TABLE Student
    ADD CONSTRAINT UniqueStudentName UNIQUE(Name);  --Ввод в готовую таблицу ограничения студентов по уникальности имени, т.е. вписать одинаковое имя не получится

ALTER TABLE Student
DROP CONSTRAINT UniqueStudentName; --Снятие ограничения студентов по уникальности имени


ALTER TABLE Student
    ALTER COLUMN name SET NOT NULL; --Запись ограничения по нулевому имени, но после создания таблицы

ALTER TABLE Student
ADD COLUMN name TEXT;
--Наверное, где-то удалял эту колонку имени, чтобы опять вставить :), но уже с другим т/д

ALTER TABLE Student
    ALTER COLUMN Age SET DEFAULT 20; //Запись ограничения возраста после создания таблицы по умолчанию (если ничего в колонку имени не писали)

ALTER TABLE Faculty
    ADD CONSTRAINT UniqueFacultyNameColor UNIQUE (Name, Color);
--Запись ограничения 'UniqueFacultyNameColor' студентов на уникальность: имя факультетам + цвет факультета,
--чтобы строки не были одинаковы сразу по двум свойствам. Будет ограничивать только будущие внесения факультетов