CREATE TABLE People (
                        PersonID SERIAL PRIMARY KEY,
                        Name VARCHAR(50) NOT NULL,
                        Age INT NOT NULL,
                        HasLicense BOOLEAN NOT NULL,
                        car_id serial REFERENCES Cars(CarID)
);
/* Получается, что при ссылке на идентификатор машины его уникальность уже не действует
      (как в своей табличке) и можно значения id делать повторяющимися в колонке водителей,
   что удовлетворяет условию задания  */

CREATE TABLE Cars (
                      CarID SERIAL PRIMARY KEY,
                      Brand VARCHAR(50) NOT NULL,
                      Model VARCHAR(50) NOT NULL,
                      Price DECIMAL(10, 2) NOT NULL
);

