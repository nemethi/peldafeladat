package nemethi.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class DateUtilsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void dateToLocalDateThrowsExceptionOnNullDate() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("date");
        DateUtils.dateToLocalDate(null);
    }

    @Test
    public void dateToLocalDateConvertsDateCorrectly() throws ParseException {
        // given
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse("2020-02-29");

        // when
        LocalDate localDate = DateUtils.dateToLocalDate(date);

        // then
        assertThat(localDate.getYear()).isEqualTo(2020);
        assertThat(localDate.getMonthValue()).isEqualTo(2);
        assertThat(localDate.getDayOfMonth()).isEqualTo(29);
    }
}
