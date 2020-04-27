package nemethi.szemely.validation;

import com.mycompany.mavenproject1.OkmanyDTO;
import com.mycompany.mavenproject1.SzemelyDTO;
import nemethi.model.Allampolgarsag;
import nemethi.validation.Validator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.emptyList;
import static org.assertj.core.util.Lists.list;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SzemelyValidatorTest {

    private static final List<Allampolgarsag> ALLAMPOLG_LIST = getAllampolgList();
    private static final Date SZULETESI_DATUM = Date.from(Instant.EPOCH);
    private static final String VIS_NEV = "visNev";
    private static final String SZUL_NEV = "szulNev";
    private static final String A_NEV = "aNev";
    private static final String NEME = "F";
    private static final String HUN_ALLAMP_KOD = "HUN";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private NameTypeValidator<String, String> nevValidator;

    @Mock
    private Validator<Date> korValidator;
    @Mock
    private Validator<String> nemValidator;
    @Mock
    private Validator<String> allampolgValidator;
    @Mock
    private ValidationTargetModifier<List<OkmanyDTO>> okmanyListValidator;
    @Mock
    private SzemelyDTO szemely;
    @Mock
    private OkmanyDTO okmany1;
    @Mock
    private OkmanyDTO okmany2;

    private SzemelyValidator validator;

    @Before
    public void setUp() {
        initMocks(this);
        validator = new SzemelyValidator(nevValidator, korValidator, nemValidator, allampolgValidator, okmanyListValidator, ALLAMPOLG_LIST);
    }

    @Test
    public void nevValidatorCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("nevValidator");
        new SzemelyValidator(null, korValidator, nemValidator, allampolgValidator, okmanyListValidator, emptyList());
    }

    @Test
    public void korValidatorCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("korValidator");
        new SzemelyValidator(nevValidator, null, nemValidator, allampolgValidator, okmanyListValidator, emptyList());
    }

    @Test
    public void nemValidatorCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("nemValidator");
        new SzemelyValidator(nevValidator, korValidator, null, allampolgValidator, okmanyListValidator, emptyList());
    }

    @Test
    public void allampolgarsagValidatorCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("allampolgarsagValidator");
        new SzemelyValidator(nevValidator, korValidator, nemValidator, null, okmanyListValidator, emptyList());
    }

    @Test
    public void okmanyListValidatorCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("okmanyListValidator");
        new SzemelyValidator(nevValidator, korValidator, nemValidator, allampolgValidator, null, emptyList());
    }

    @Test
    public void collectionCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("allampolgarsagCollection");
        new SzemelyValidator(nevValidator, korValidator, nemValidator, allampolgValidator, okmanyListValidator, null);
    }

    @Test
    public void collectionCannotBeEmpty() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("empty collection");
        new SzemelyValidator(nevValidator, korValidator, nemValidator, allampolgValidator, okmanyListValidator, emptyList());
    }

    @Test
    public void validateWithNoErrors() {
        // given
        mockSzemely();
        when(szemely.getAllampKod()).thenReturn(HUN_ALLAMP_KOD);
        when(nevValidator.validate(anyString())).thenReturn(emptyList());
        when(korValidator.validate(SZULETESI_DATUM)).thenReturn(emptyList());
        when(nemValidator.validate(NEME)).thenReturn(emptyList());
        when(allampolgValidator.validate(HUN_ALLAMP_KOD)).thenReturn(emptyList());
        when(okmanyListValidator.validate(list(okmany1))).thenReturn(emptyList());
        when(okmanyListValidator.getModifiedTarget()).thenReturn(list(okmany2));

        // when
        List<String> errors = validator.validate(szemely);

        // then
        assertThat(errors).isEmpty();
        verifyValidators();
        verify(allampolgValidator).validate(HUN_ALLAMP_KOD);
        verifySzemely();
        verify(szemely).setAllampDekod("MAGYARORSZÁG ÁLLAMPOLGÁRA");
        verify(szemely).setOkmLista(newArrayList(okmany2));
        verifyNoMoreInteractions(szemely, nevValidator, korValidator, nemValidator, allampolgValidator, okmanyListValidator);
        verifyNoInteractions(okmany1, okmany2);
    }

    @Test
    public void validateReturnsErrorsFromOtherValidators() {
        // given
        mockSzemely();
        when(szemely.getAllampKod()).thenReturn(HUN_ALLAMP_KOD);
        when(nevValidator.validate(anyString())).thenReturn(list("invalid name"));
        when(korValidator.validate(SZULETESI_DATUM)).thenReturn(list("invalid birth date"));
        when(nemValidator.validate(NEME)).thenReturn(list("invalid sex"));
        when(allampolgValidator.validate(HUN_ALLAMP_KOD)).thenReturn(list("invalid nationality"));
        when(okmanyListValidator.validate(list(okmany1))).thenReturn(list("invalid okmany"));
        when(okmanyListValidator.getModifiedTarget()).thenReturn(list(okmany1));

        // when
        List<String> errors = validator.validate(szemely);

        // then
        assertThat(errors).containsExactly("invalid name", "invalid name", "invalid name",
                "invalid birth date", "invalid sex", "invalid nationality", "invalid okmany");
        verifyValidators();
        verify(allampolgValidator).validate(HUN_ALLAMP_KOD);
        verifySzemely();
        verify(szemely, never()).setAllampDekod(anyString());
        verify(szemely).setOkmLista(newArrayList(okmany1));
        verifyNoMoreInteractions(szemely, nevValidator, korValidator, nemValidator, allampolgValidator,
                okmanyListValidator);
        verifyNoInteractions(okmany1, okmany2);
    }

    @Test
    public void doesNotValidateNullValues() {
        // given
        mockSzemelyWithNullValues();

        // when
        List<String> errors = validator.validate(szemely);

        // then
        assertThat(errors).containsExactly("Hiányzó viselt név", "Hiányzó születési név",
                "Hiányzó anya név", "Hiányzó születési dátum", "Hiányzó nem",
                "Hiányzó állampolgárság", "Hiányzó okmánylista");
        verifySzemely();
        verifyNoMoreInteractions(szemely);
        verifyNoInteractions(nevValidator, korValidator, nemValidator, allampolgValidator, okmanyListValidator,
                okmany1, okmany2);
    }

    @Test
    public void doesNotValidateEmptyValues() {
        // given
        mockSzemelyWithEmptyValues();

        // when
        List<String> errors = validator.validate(szemely);

        // then
        assertThat(errors).containsExactly("Hiányzó viselt név", "Hiányzó születési név",
                "Hiányzó anya név", "Hiányzó születési dátum", "Hiányzó nem",
                "Hiányzó állampolgárság", "Hiányzó okmánylista");
        verifySzemely();
        verifyNoMoreInteractions(szemely);
        verifyNoInteractions(nevValidator, korValidator, nemValidator, allampolgValidator, okmanyListValidator,
                okmany1, okmany2);
    }

    @Test
    public void doesNotSetAllampDekodIfItCannotBeFound() {
        // given
        mockSzemely();
        String invalidCode = "invalid code";
        when(szemely.getAllampKod()).thenReturn(invalidCode);
        when(nevValidator.validate(anyString())).thenReturn(emptyList());
        when(korValidator.validate(SZULETESI_DATUM)).thenReturn(emptyList());
        when(nemValidator.validate(NEME)).thenReturn(emptyList());
        when(allampolgValidator.validate(invalidCode)).thenReturn(emptyList());
        when(okmanyListValidator.validate(list(okmany1))).thenReturn(emptyList());
        when(okmanyListValidator.getModifiedTarget()).thenReturn(list(okmany2));

        // when
        List<String> errors = validator.validate(szemely);

        // then
        assertThat(errors).isEmpty();
        verifyValidators();
        verify(allampolgValidator).validate(invalidCode);
        verifySzemely();
        verify(szemely).setAllampDekod(null);
        verify(szemely).setOkmLista(newArrayList(okmany2));
        verifyNoMoreInteractions(szemely, nevValidator, korValidator, nemValidator, allampolgValidator, okmanyListValidator);
        verifyNoInteractions(okmany1, okmany2);
    }

    private void mockSzemely() {
        when(szemely.getVisNev()).thenReturn(VIS_NEV);
        when(szemely.getSzulNev()).thenReturn(SZUL_NEV);
        when(szemely.getaNev()).thenReturn(A_NEV);
        when(szemely.getSzulDat()).thenReturn(SZULETESI_DATUM);
        when(szemely.getNeme()).thenReturn(NEME);
        when(szemely.getOkmLista()).thenReturn(newArrayList(okmany1));
    }

    private void mockSzemelyWithNullValues() {
        when(szemely.getVisNev()).thenReturn(null);
        when(szemely.getSzulNev()).thenReturn(null);
        when(szemely.getaNev()).thenReturn(null);
        when(szemely.getSzulDat()).thenReturn(null);
        when(szemely.getNeme()).thenReturn(null);
        when(szemely.getAllampKod()).thenReturn(null);
        when(szemely.getOkmLista()).thenReturn(null);
    }

    private void mockSzemelyWithEmptyValues() {
        when(szemely.getVisNev()).thenReturn("");
        when(szemely.getSzulNev()).thenReturn("");
        when(szemely.getaNev()).thenReturn("");
        when(szemely.getSzulDat()).thenReturn(null);
        when(szemely.getNeme()).thenReturn("");
        when(szemely.getAllampKod()).thenReturn("");
        when(szemely.getOkmLista()).thenReturn(newArrayList());
    }

    private void verifySzemely() {
        verify(szemely).getVisNev();
        verify(szemely).getSzulNev();
        verify(szemely).getaNev();
        verify(szemely).getSzulDat();
        verify(szemely).getNeme();
        verify(szemely).getAllampKod();
        verify(szemely).getOkmLista();
    }

    private void verifyValidators() {
        verify(nevValidator).setNameType("viselt");
        verify(nevValidator).validate(VIS_NEV);
        verify(nevValidator).setNameType("születési");
        verify(nevValidator).validate(SZUL_NEV);
        verify(nevValidator).setNameType("anya");
        verify(nevValidator).validate(A_NEV);
        verify(korValidator).validate(SZULETESI_DATUM);
        verify(nemValidator).validate(NEME);
        verify(okmanyListValidator).validate(list(okmany1));
        verify(okmanyListValidator).getModifiedTarget();
    }

    private static List<Allampolgarsag> getAllampolgList() {
        return list(new Allampolgarsag(HUN_ALLAMP_KOD, "MAGYARORSZÁG ÁLLAMPOLGÁRA"),
                new Allampolgarsag("LAO", "LAOSZ ÁLLAMPOLGÁRA"));
    }
}
