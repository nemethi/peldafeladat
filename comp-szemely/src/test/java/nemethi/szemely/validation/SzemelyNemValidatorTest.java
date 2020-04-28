package nemethi.szemely.validation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;
import static org.assertj.core.util.Sets.newHashSet;

public class SzemelyNemValidatorTest {

    private static final Set<String> SEXES = newHashSet(list("F", "N"));

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
