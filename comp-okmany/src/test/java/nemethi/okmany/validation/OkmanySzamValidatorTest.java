package nemethi.okmany.validation;

import com.mycompany.mavenproject1.OkmanyDTO;
import nemethi.okmany.OkmanyTipus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OkmanySzamValidatorTest {

    private static final String SZEMELYI_IGAZOLVANY_TIPUS = "1";
    private static final String UTLEVEL_TIPUS = "2";
    private static final String VEZETOI_ENGEDELY_TIPUS = "3";
    private static final String EGYEB_TIPUS = "4";
    private static final String ISMERETLEN_TIPUS = "999";
    private static final String INVALID_NUMBER = "123456789ABC";
    private static final String VALID_SZEMELYI_NUMBER = "123456AB";
    private static final String VALID_UTLEVEL_NUMBER = "AB1234567";
    private static final String VALID_NUMBER = "123ABC";

    private OkmanySzamValidator validator;
    private Collection<OkmanyTipus> okmanyTipusok = initOkmanyTipusok();
    @Mock
    private OkmanyDTO okmany;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
        validator = new OkmanySzamValidator(okmanyTipusok);
    }

    @Test
    public void okmanyTipusokCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("okmanyTipusok");
        validator = new OkmanySzamValidator(null);
    }

    @Test
    public void validateReturnsEmptyListOnValidSzemelyi() {
        when(okmany.getOkmTipus()).thenReturn(SZEMELYI_IGAZOLVANY_TIPUS);
        when(okmany.getOkmanySzam()).thenReturn(VALID_SZEMELYI_NUMBER);

        // when
        List<String> errors = validator.validate(okmany);

        // then
        assertThat(errors).isEmpty();
        verifyOkmanyInteractions();
    }

    @Test
    public void validateReturnsErrorListOnInvalidSzemelyi() {
        when(okmany.getOkmTipus()).thenReturn(SZEMELYI_IGAZOLVANY_TIPUS);
        when(okmany.getOkmanySzam()).thenReturn(INVALID_NUMBER);

        // when
        List<String> errors = validator.validate(okmany);

        // then
        assertThat(errors).hasSize(1);
        assertThat(errors).containsExactly("Érvénytelen Személyi igazolvány szám");
        verifyOkmanyInteractions();
    }

    @Test
    public void validateReturnsEmptyListOnValidUtlevel() {
        when(okmany.getOkmTipus()).thenReturn(UTLEVEL_TIPUS);
        when(okmany.getOkmanySzam()).thenReturn(VALID_UTLEVEL_NUMBER);

        // when
        List<String> errors = validator.validate(okmany);

        // then
        assertThat(errors).isEmpty();
        verifyOkmanyInteractions();
    }

    @Test
    public void validateReturnsErrorListOnInvalidUtlevel() {
        when(okmany.getOkmTipus()).thenReturn(UTLEVEL_TIPUS);
        when(okmany.getOkmanySzam()).thenReturn(INVALID_NUMBER);

        // when
        List<String> errors = validator.validate(okmany);

        // then
        assertThat(errors).hasSize(1);
        assertThat(errors).containsExactly("Érvénytelen Útlevél szám");
        verifyOkmanyInteractions();
    }

    @Test
    public void validateReturnsEmptyListOnValidVezetoiEngedely() {
        when(okmany.getOkmTipus()).thenReturn(VEZETOI_ENGEDELY_TIPUS);
        when(okmany.getOkmanySzam()).thenReturn(VALID_NUMBER);

        // when
        List<String> errors = validator.validate(okmany);

        // then
        assertThat(errors).isEmpty();
        verifyOkmanyInteractions();
    }

    @Test
    public void validateReturnsErrorListOnInvalidVezetoiEngedely() {
        when(okmany.getOkmTipus()).thenReturn(VEZETOI_ENGEDELY_TIPUS);
        when(okmany.getOkmanySzam()).thenReturn(INVALID_NUMBER);

        // when
        List<String> errors = validator.validate(okmany);

        // then
        assertThat(errors).hasSize(1);
        assertThat(errors).containsExactly("Érvénytelen Vezetői engedély szám");
        verifyOkmanyInteractions();
    }

    @Test
    public void validateReturnsEmptyListOnValidEgyeb() {
        when(okmany.getOkmTipus()).thenReturn(EGYEB_TIPUS);
        when(okmany.getOkmanySzam()).thenReturn(VALID_NUMBER);

        // when
        List<String> errors = validator.validate(okmany);

        // then
        assertThat(errors).isEmpty();
        verifyOkmanyInteractions();
    }

    @Test
    public void validateReturnsErrorListOnInvalidEgyeb() {
        when(okmany.getOkmTipus()).thenReturn(EGYEB_TIPUS);
        when(okmany.getOkmanySzam()).thenReturn(INVALID_NUMBER);

        // when
        List<String> errors = validator.validate(okmany);

        // then
        assertThat(errors).hasSize(1);
        assertThat(errors).containsExactly("Érvénytelen Egyéb szám");
        verifyOkmanyInteractions();
    }

    @Test
    public void validateReturnsErrorListOnValidIsmeretlen() {
        when(okmany.getOkmTipus()).thenReturn(ISMERETLEN_TIPUS);
        when(okmany.getOkmanySzam()).thenReturn(VALID_NUMBER);

        // when
        List<String> errors = validator.validate(okmany);

        // then
        assertThat(errors).hasSize(1);
        assertThat(errors).containsExactly("Ismeretlen okmánytípus");
        verifyOkmanyInteractions();
    }

    @Test
    public void validateReturnsErrorListOnInvalidIsmeretlen() {
        when(okmany.getOkmTipus()).thenReturn(ISMERETLEN_TIPUS);
        when(okmany.getOkmanySzam()).thenReturn(null);

        // when
        List<String> errors = validator.validate(okmany);

        // then
        assertThat(errors).hasSize(2);
        assertThat(errors).containsExactly("Ismeretlen okmánytípus", "Érvénytelen Ismeretlen okmány szám");
        verifyOkmanyInteractions();
    }

    private void verifyOkmanyInteractions() {
        verify(okmany).getOkmTipus();
        verify(okmany).getOkmanySzam();
        verifyNoMoreInteractions(okmany);
    }

    private Collection<OkmanyTipus> initOkmanyTipusok() {
        OkmanyTipus szemelyi = new OkmanyTipus(1, "Személyi igazolvány");
        OkmanyTipus utlevel = new OkmanyTipus(2, "Útlevél");
        OkmanyTipus vezetoiEngedely = new OkmanyTipus(3, "Vezetői engedély");
        OkmanyTipus egyeb = new OkmanyTipus(4, "Egyéb");
        return Arrays.asList(szemelyi, utlevel, vezetoiEngedely, egyeb);
    }
}
