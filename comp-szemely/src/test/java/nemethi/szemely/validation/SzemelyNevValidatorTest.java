package nemethi.szemely.validation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SzemelyNevValidatorTest {

    private static final String INVALID_NAME_MESSAGE = "Érvénytelen viselt név";
    private static final String TOO_LONG_NAME_MESSAGE = "Túl hosszú viselt név";
    private static final String TOO_LONG_NAME = repeat("A", 40) + " " + repeat("B", 40);
    private static final String NAME_WITH_SPECIAL_CHARS = "Kovács-Szäbó D. Ödön/Péter L'Äuren";
    private static final String VISELT_NAME_TYPE = "viselt";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SzemelyNevValidator validator;

    @Before
    public void setUp() {
        validator = new SzemelyNevValidator(VISELT_NAME_TYPE);
    }

    @Test
    public void constructorThrowsExceptionWhenNameTypeIsNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("nameType");
        new SzemelyNevValidator(null);
    }

    @Test
    public void validateReturnsEmptyListWhenNameIsValid() {
        // when
        List<String> errors = validator.validate("Kovács István");

        // then
        assertThat(errors).isEmpty();
    }

    @Test
    public void validateReturnsErrorListWhenNameHasOnlyOnePart() {
        // when
        List<String> errors = validator.validate("Kovács");

        // then
        assertThat(errors).containsExactly(INVALID_NAME_MESSAGE);
    }

    @Test
    public void validateReturnsErrorListWhenNameIsTooLong() {
        // when
        List<String> errors = validator.validate(TOO_LONG_NAME);

        // then
        assertThat(errors).containsExactly(TOO_LONG_NAME_MESSAGE);
    }

    @Test
    public void validateReturnsEmptyListOnValidNameWithSpecialCharacters() {
        // when
        List<String> errors = validator.validate(NAME_WITH_SPECIAL_CHARS);

        // then
        assertThat(errors).isEmpty();
    }

    @Test
    public void validateDoesNotConsiderDrPrefixInName() {
        // when
        List<String> errors = validator.validate("dr. Kovács István");

        // then
        assertThat(errors).isEmpty();

        // when
        errors = validator.validate("Dr. Kovács");

        // then
        assertThat(errors).containsExactly(INVALID_NAME_MESSAGE);
    }

    @Test
    public void validateDoesNotConsiderDrSuffixInName() {
        // when
        List<String> errors = validator.validate("Kovács István dr.");

        // then
        assertThat(errors).isEmpty();

        // when
        errors = validator.validate("Kovács Dr.");

        // then
        assertThat(errors).containsExactly(INVALID_NAME_MESSAGE);
    }

    @Test
    public void validateDoesNotConsiderLeadingAndTrailingSpaces() {
        // when
        List<String> errors = validator.validate(" Kovács István ");

        // then
        assertThat(errors).isEmpty();
    }

    @Test
    public void nameTypeCanBeSetAndRetrieved() {
        // given
        assertThat(validator.getNameType()).isEqualTo(VISELT_NAME_TYPE);

        // when
        validator.setNameType("születési");

        // then
        assertThat(validator.getNameType()).isEqualTo("születési");
    }

    private static String repeat(String string, int times) {
        return String.join("", Collections.nCopies(times, string));
    }
}
