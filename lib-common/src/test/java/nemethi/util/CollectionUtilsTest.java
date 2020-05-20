package nemethi.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;

public class CollectionUtilsTest {

    private static final String EMPTY_COLLECTION_MESSAGE = "empty collection";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void requireNonEmptyThrowsExceptionIfEmptyWithMessage() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(is(EMPTY_COLLECTION_MESSAGE));
        CollectionUtils.requireNonEmpty(Collections.emptyList(), EMPTY_COLLECTION_MESSAGE);
    }

    @Test
    public void requireNonEmptyDoesNothingIfNotEmpty() {
        CollectionUtils.requireNonEmpty(Collections.singletonList(1), EMPTY_COLLECTION_MESSAGE);
    }
}
