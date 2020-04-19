package nemethi.szemely.validation;

import nemethi.szemely.Allampolgarsag;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SzemelyAllampolgarsagValidatorTest {

    private static final Allampolgarsag HUN = new Allampolgarsag("HUN", "MAGYARORSZÁG ÁLLAMPOLGÁRA");
    private static final Allampolgarsag KAZ = new Allampolgarsag("KAZ", "KAZAHSZTÁN ÁLLAMPOLGÁRA");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SzemelyAllampolgarsagValidator validator;

    @Before
    public void setUp() {
        validator = new SzemelyAllampolgarsagValidator(Arrays.asList(HUN, KAZ));
    }

    @Test
    public void constructorThrowsExceptionOnNullCollection() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("collection");
        new SzemelyAllampolgarsagValidator(null);
    }

    @Test
    public void validateReturnsEmptyListIfAllampolgIsFoundInCollection() {
        // when
        List<String> errors = validator.validate("KAZ");

        // then
        assertThat(errors).isEmpty();
    }

    @Test
    public void validateReturnsErrorListIfAllampolgIsNotFoundInCollection() {
        // when
        List<String> errors = validator.validate("ABC");

        // then
        assertThat(errors).hasSize(1);
        assertThat(errors).containsExactly("Érvénytelen állampolgárság");
    }
}
