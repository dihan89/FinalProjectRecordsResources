/*
Принцип проекта и соглашения для удобства проверки

Названия и описания ресурсов (например, библиотеки, музея и т.п.)
хранятся в txt-файлах  (папка “resourcesMy”).

Для каждого ресурса формируется своё расписание, т.е. указываются интервалы времени, доступные для
записи (возможное время для индивидуальной экскурсии,  посещения библиотеки и т.д.).
Расписание  для конкретной даты указывается в текстовых файлах в папке “schedule”.

Например,  строка "2023-12-10 09:00:00 25 5" означает, что 23 декабря, начиная с 9-00,
может состояться 5 индивидуальных экскурсий по 25 минут каждая.

Для удобства проверки в качестве примера по умолчанию все ресурсы имеют расписание на 2023-11-12

*/


package recordToResource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Runner {
    public static void main(String[] args) {
        SpringApplication.run(Runner.class, args);
    }
}
