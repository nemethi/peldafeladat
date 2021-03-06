package nemethi.szemely.validation;

import nemethi.model.Allampolgarsag;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

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
    public void collectionCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(is("collection"));
        new SzemelyAllampolgarsagValidator(null);
    }

    @Test
    public void collectionCannotBeEmpty() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(is("empty collection"));
        new SzemelyAllampolgarsagValidator(Collections.emptyList());
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
        assertThat(errors).containsExactly("Érvénytelen állampolgárság");
    }
}
