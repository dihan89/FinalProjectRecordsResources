package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;

public class TimeTest {

    @Test
    public void testTime(){

        Time time = Time.valueOf("19:53:00");
        Date date = Date.valueOf("2022-12-11");
        LocalDateTime ldt = LocalDateTime.of(date.toLocalDate(), time.toLocalTime());
        ldt = ldt.plusHours(8);
        Duration d = Duration.ofMinutes(2);
        ldt = ldt.plusMinutes(d.toMinutes());
        Assertions.assertEquals(Date.valueOf(ldt.toLocalDate()), Date.valueOf("2022-12-12"));
        Assertions.assertEquals(Time.valueOf(ldt.toLocalTime()), Time.valueOf("3:55:00"));
    }
}
