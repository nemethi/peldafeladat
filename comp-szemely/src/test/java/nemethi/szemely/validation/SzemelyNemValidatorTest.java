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
    public void collectionCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("collection");
        new SzemelyNemValidator(null);
    }

    @Test
    public void validateReturnsEmptyListIfSexIsFoundInCollection() {
        // when
        List<String> errors = validator.validate("N");

        // then
        assertThat(errors).isEmpty();
    }

    @Test
    public void validateReturnsErrorListWhenSexIsNotFoundInCollection() {
        // when
        List<String> errors = validator.validate("X");

        // then
        assertThat(errors).containsExactly("Érvénytelen nem");
    }
}
