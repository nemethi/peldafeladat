package util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

public final class DateUtils {

    private DateUtils() {
    }

    public static LocalDate dateToLocalDate(Date date) {
        Objects.requireNonNull(date, "date");
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
