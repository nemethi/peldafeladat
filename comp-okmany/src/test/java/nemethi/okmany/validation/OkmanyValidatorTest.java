package nemethi.okmany.validation;

import com.mycompany.mavenproject1.OkmanyDTO;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import nemethi.validation.Validator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OkmanyValidatorTest {

    private static final byte[] OKMANYKEP_BYTES = new byte[]{0, 1};
    private static final List<Byte> OKMANYKEP_LIST = Arrays.asList((byte) 0, (byte) 1);
    private static final Date LEJAR_DATE = new Date();

    private OkmanyValidator validator;
    @Mock
    private OkmanyDTO target;
    @Mock
    private Validator<OkmanyDTO> szamValidator;
    @Mock
    private Validator<List<Byte>> kepValidator;
    @Mock
    private Validator<Date> ervenyessegValidator;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
        validator = new OkmanyValidator(szamValidator, kepValidator, ervenyessegValidator);
    }

    @Test
    public void szamValidatorCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("szamValidator");
        validator = new OkmanyValidator(null, kepValidator, ervenyessegValidator);
    }

    @Test
    public void kepValidatorCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("kepValidator");
        validator = new OkmanyValidator(szamValidator, null, ervenyessegValidator);
    }

    @Test
    public void ervenyessegValidatorCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("ervenyessegValidator");
        validator = new OkmanyValidator(szamValidator, kepValidator, null);
    }

    @Test
    public void validateWithNoErrors() {
        // given
        when(szamValidator.validate(target)).thenReturn(Collections.emptyList());
        when(target.getOkmanyKep()).thenReturn(OKMANYKEP_BYTES);
        when(kepValidator.validate(OKMANYKEP_LIST)).thenReturn(Collections.emptyList());
        when(target.getLejarDat()).thenReturn(LEJAR_DATE);
        when(ervenyessegValidator.validate(LEJAR_DATE)).thenReturn(Collections.emptyList());

        // when
        List<String> errors = validator.validate(target);

        // then
        assertThat(errors).isEmpty();
        verify(szamValidator).validate(target);
        verify(target, times(3)).getOkmanyKep();
        verify(kepValidator).validate(OKMANYKEP_LIST);
        verify(target, times(2)).getLejarDat();
        verify(ervenyessegValidator).validate(LEJAR_DATE);
        verify(target).setErvenyes(true);
        verifyNoMoreInteractions(target, szamValidator, kepValidator, ervenyessegValidator);
    }

    @Test
    public void validateReturnsErrorListOnNullOkmanyKep() {
        // given
        when(szamValidator.validate(target)).thenReturn(Collections.emptyList());
        when(target.getOkmanyKep()).thenReturn(null);
        when(target.getLejarDat()).thenReturn(LEJAR_DATE);
        when(ervenyessegValidator.validate(LEJAR_DATE)).thenReturn(Collections.emptyList());

        // when
        List<String> errors = validator.validate(target);

        // then
        assertThat(errors).hasSize(1);
        assertThat(errors).containsExactly("Hiányzó okmánykép");
        verify(szamValidator).validate(target);
        verify(target).getOkmanyKep();
        verify(kepValidator, never()).validate(any());
        verify(target, times(2)).getLejarDat();
        verify(ervenyessegValidator).validate(LEJAR_DATE);
        verify(target).setErvenyes(true);
        verifyNoMoreInteractions(target, szamValidator, kepValidator, ervenyessegValidator);
    }

    @Test
    public void validateReturnsErrorListOnZeroLengthOkmanyKep() {
        // given
        when(szamValidator.validate(target)).thenReturn(Collections.emptyList());
        when(target.getOkmanyKep()).thenReturn(new byte[0]);
        when(target.getLejarDat()).thenReturn(LEJAR_DATE);
        when(ervenyessegValidator.validate(LEJAR_DATE)).thenReturn(Collections.emptyList());

        // when
        List<String> errors = validator.validate(target);

        // then
        assertThat(errors).hasSize(1);
        assertThat(errors).containsExactly("Hiányzó okmánykép");
        verify(szamValidator).validate(target);
        verify(target, times(2)).getOkmanyKep();
        verify(kepValidator, never()).validate(any());
        verify(target, times(2)).getLejarDat();
        verify(ervenyessegValidator).validate(LEJAR_DATE);
        verify(target).setErvenyes(true);
        verifyNoMoreInteractions(target, szamValidator, kepValidator, ervenyessegValidator);
    }

    @Test
    public void validateReturnsErrorListOnNullLejarDat() {
        // given
        when(szamValidator.validate(target)).thenReturn(Collections.emptyList());
        when(target.getOkmanyKep()).thenReturn(OKMANYKEP_BYTES);
        when(kepValidator.validate(OKMANYKEP_LIST)).thenReturn(Collections.emptyList());
        when(target.getLejarDat()).thenReturn(null);

        // when
        List<String> errors = validator.validate(target);

        // then
        assertThat(errors).hasSize(1);
        assertThat(errors).containsExactly("Hiányzó lejárati idő");
        verify(szamValidator).validate(target);
        verify(target, times(3)).getOkmanyKep();
        verify(kepValidator).validate(OKMANYKEP_LIST);
        verify(target).getLejarDat();
        verify(target).setErvenyes(false);
        verify(ervenyessegValidator, never()).validate(any());
        verifyNoMoreInteractions(target, szamValidator, kepValidator, ervenyessegValidator);
    }

    @Test
    public void validateReturnsErrorsFromOtherValidators() {
        // given
        when(szamValidator.validate(target)).thenReturn(Collections.singletonList("szamValidator"));
        when(target.getOkmanyKep()).thenReturn(OKMANYKEP_BYTES);
        when(kepValidator.validate(OKMANYKEP_LIST)).thenReturn(Collections.singletonList("kepValidator"));
        when(target.getLejarDat()).thenReturn(LEJAR_DATE);
        when(ervenyessegValidator.validate(LEJAR_DATE)).thenReturn(Collections.singletonList("ervenyessegValidator"));

        // when
        List<String> errors = validator.validate(target);

        // then
        assertThat(errors).hasSize(3);
        assertThat(errors).containsExactly("szamValidator", "kepValidator", "ervenyessegValidator");
        verify(szamValidator).validate(target);
        verify(target, times(3)).getOkmanyKep();
        verify(kepValidator).validate(OKMANYKEP_LIST);
        verify(target, times(2)).getLejarDat();
        verify(ervenyessegValidator).validate(LEJAR_DATE);
        verify(target).setErvenyes(false);
        verifyNoMoreInteractions(target, szamValidator, kepValidator, ervenyessegValidator);
    }
}
