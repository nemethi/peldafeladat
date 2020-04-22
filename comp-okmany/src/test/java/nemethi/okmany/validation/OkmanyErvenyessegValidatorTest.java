package nemethi.okmany.validation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OkmanyErvenyessegValidatorTest {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Clock NOW = Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), ZoneId.systemDefault());

    private OkmanyErvenyessegValidator validator;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        validator = new OkmanyErvenyessegValidator(NOW);
    }

    @Test
    public void clockCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("clock");
        validator = new OkmanyErvenyessegValidator(null);
    }

    @Test
    public void validateReturnsEmptyListIfDateIsAfterNow() throws ParseException {
        // given
        Date future = DATE_FORMAT.parse("2020-01-02");

        // when
        List<String> errors = validator.validate(future);

        // then
        assertThat(errors).isEmpty();
    }

    @Test
    public void validateReturnsEmptyListIfDateIsNow() throws ParseException {
        // given
        Date now = DATE_FORMAT.parse("2020-01-01");

        // when
        List<String> errors = validator.validate(now);

        // then
        assertThat(errors).isEmpty();
    }

    @Test
    public void validateReturnsErrorListIfDateIsBeforeNow() throws ParseException {
        // given
        Date past = DATE_FORMAT.parse("2019-12-31");

        // when
        List<String> errors = validator.validate(past);

        // then
        assertThat(errors).containsExactly("Az okmány érvényessége lejárt");
    }
}
