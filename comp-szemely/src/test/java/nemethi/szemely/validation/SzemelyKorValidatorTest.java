package nemethi.szemely.validation;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SzemelyKorValidatorTest {

    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 120;
    private static final String ERROR_MESSAGE = "Érvénytelen születési idő";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Clock NOW = Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), ZoneId.systemDefault());

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SzemelyKorValidator validator;

    @Before
    public void setUp() {
        validator = new SzemelyKorValidator(NOW, MIN_AGE, MAX_AGE);
    }

    @Test
    public void constructorThrowsExceptionOnNullClock() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("clock");
        new SzemelyKorValidator(null, 0, 1);
    }

    @Test
    public void validateReturnsEmptyListIfAgeIsEqualToMinAge() throws ParseException {
        // when
        List<String> errors = validator.validate(DATE_FORMAT.parse("2002-01-01"));

        // then
        assertThat(errors).isEmpty();
    }

    @Test
    public void validateReturnsEmptyListIfAgeIsEqualToMaxAge() throws ParseException {
        // when
        List<String> errors = validator.validate(DATE_FORMAT.parse("1900-01-01"));

        // then
        assertThat(errors).isEmpty();
    }

    @Test
    public void validateReturnsEmptyListIfAgeIsBetweenMinAgeAndMaxAge() throws ParseException {
        // when
        List<String> errors = validator.validate(DATE_FORMAT.parse("2001-12-31"));

        // then
        assertThat(errors).isEmpty();
    }

    @Test
    public void validateReturnsErrorListIfAgeIsLessThanMinAge() throws ParseException {
        // when
        List<String> errors = validator.validate(DATE_FORMAT.parse("2002-01-02"));

        // then
        assertThat(errors).containsExactly(ERROR_MESSAGE);
    }

    @Test
    public void validateReturnsErrorListIfAgeIsBiggerThanMaxAge() throws ParseException {
        // when
        List<String> errors = validator.validate(DATE_FORMAT.parse("1899-01-01"));

        // then
        assertThat(errors).containsExactly(ERROR_MESSAGE);
    }
}
