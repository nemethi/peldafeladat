package nemethi.szemely.validation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SzemelyNemValidatorTest {

    private static final List<String> SEXES = Arrays.asList("F", "N");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SzemelyNemValidator validator;

    @Before
    public void setUp() {
        validator = new SzemelyNemValidator(SEXES);
    }

    @Test
    public void constructorThrowsExceptionOnNullSexes() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("sexes");
        new SzemelyNemValidator(null);
    }

    @Test
    public void validateReturnsEmptyListIfSexIsFoundInSexes() {
        // when
        List<String> errors = validator.validate("N");

        // then
        assertThat(errors).isEmpty();
    }

    @Test
    public void validateReturnsErrorListWhenSexIsNotFoundInSexes() {
        // when
        List<String> errors = validator.validate("X");

        // then
        assertThat(errors).containsExactly("Érvénytelen nem");
    }
}
