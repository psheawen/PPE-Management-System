package mycom.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTime {
    private static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter dateFormatter1 = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    private static DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    public static LocalDateTime currentDateTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        return dateTime;
    }

    public static String formattedLocalDateTime(LocalDateTime dateTime) {
        String formattedDateTime = dateTime.format(dateTimeFormat);
        return formattedDateTime;
    }

    public static LocalDateTime toDateTimeObject(String datetime) {
        LocalDateTime localDateTime = LocalDateTime.parse(datetime, dateTimeFormat);
        return localDateTime;
    }

    public static LocalDate getDate(LocalDateTime datetime) {
        return datetime.toLocalDate();
    }

    public static LocalTime getTime(LocalDateTime datetime) {
        return datetime.toLocalTime();
    }

    public static String toDateStringWords(LocalDate date) {
        return date.format(dateFormatter1);
    }

    public static String toDateString(LocalDate date) {
        return date.format(dateFormatter2);
    }

    public static String toTimeString(LocalTime time) {
        return time.format(timeFormatter);
    }
}
