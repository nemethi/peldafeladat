package nemethi.szemely.validation;

import com.mycompany.mavenproject1.OkmanyDTO;
import nemethi.model.OkmanyTipus;
import nemethi.response.OkmanyResponse;
import nemethi.szemely.OkmanyServiceClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.emptyList;
import static org.assertj.core.util.Lists.list;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class SzemelyOkmanyValidatorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private OkmanyServiceClient client;
    @Mock
    private OkmanyDTO okmany1;
    @Mock
    private OkmanyDTO okmany2;
    @Mock
    private OkmanyDTO validatedOkmany1;
    @Mock
    private OkmanyDTO validatedOkmany2;
    @Mock
    private OkmanyResponse response;
    private List<OkmanyTipus> okmanyTipusok = getOkmanyTipusok();

    private SzemelyOkmanyValidator validator;

    @Before
    public void setUp() {
        initMocks(this);
        validator = new SzemelyOkmanyValidator(client, okmanyTipusok);
    }

    @Test
    public void clientCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("client");
        new SzemelyOkmanyValidator(null, emptyList());
    }

    @Test
    public void collectionCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("okmanyTipusok");
        new SzemelyOkmanyValidator(client, null);
    }

    @Test
    public void validateReturnsEmptyListOnValidOkmanyList() throws IOException {
        // given
        when(client.sendOkmany(any())).thenReturn(response);
        when(response.getOkmany()).thenReturn(validatedOkmany1).thenReturn(validatedOkmany2);
        when(response.getErrors()).thenReturn(emptyList());
        when(okmany1.getOkmTipus()).thenReturn("1");
        when(okmany2.getOkmTipus()).thenReturn("2");

        // when
        List<String> errors = validator.validate(list(okmany1, okmany2));
        List<OkmanyDTO> okmanyList = validator.getModifiedTarget();

        // then
        assertThat(errors).isEmpty();
        assertThat(okmanyList).containsExactly(validatedOkmany1, validatedOkmany2);
        assertThat(validator.getModifiedTarget()).isEmpty();
        verify(client).sendOkmany(okmany1);
        verify(client).sendOkmany(okmany2);
        verify(response, times(2)).getOkmany();
        verify(response, times(2)).getErrors();
        verify(okmany1).getOkmTipus();
        verify(okmany2).getOkmTipus();
        verifyNoMoreInteractions(client, response, okmany1, okmany2);
        verifyNoInteractions(validatedOkmany1, validatedOkmany2);
    }

    @Test
    public void validateReturnsErrorListOnValidOkmanyListWithKnownNotUniqueOkmanyTipus() throws IOException {
        // given
        when(client.sendOkmany(any())).thenReturn(response);
        when(response.getOkmany()).thenReturn(validatedOkmany1).thenReturn(validatedOkmany2);
        when(response.getErrors()).thenReturn(emptyList());
        when(okmany1.getOkmTipus()).thenReturn("1");
        when(okmany2.getOkmTipus()).thenReturn("1");

        // when
        List<String> errors = validator.validate(list(okmany1, okmany2));
        List<OkmanyDTO> okmanyList = validator.getModifiedTarget();

        // then
        assertThat(errors).containsExactly("Több, mint 1 érvényes Személyi igazolvány");
        assertThat(okmanyList).containsExactly(validatedOkmany1, validatedOkmany2);
        verify(client).sendOkmany(okmany1);
        verify(client).sendOkmany(okmany2);
        verify(response, times(2)).getOkmany();
        verify(response, times(2)).getErrors();
        verify(okmany1).getOkmTipus();
        verify(okmany2).getOkmTipus();
        verifyNoMoreInteractions(client, response, okmany1, okmany2);
        verifyNoInteractions(validatedOkmany1, validatedOkmany2);
    }

    @Test
    public void validateReturnsErrorListOnValidOkmanyListWithUnknownNotUniqueOkmanyTipus() throws IOException {
        // given
        when(client.sendOkmany(any())).thenReturn(response);
        when(response.getOkmany()).thenReturn(validatedOkmany1).thenReturn(validatedOkmany2);
        when(response.getErrors()).thenReturn(emptyList());
        when(okmany1.getOkmTipus()).thenReturn("3");
        when(okmany2.getOkmTipus()).thenReturn("3");

        // when
        List<String> errors = validator.validate(list(okmany1, okmany2));
        List<OkmanyDTO> okmanyList = validator.getModifiedTarget();

        // then
        assertThat(errors).containsExactly("Több, mint 1 érvényes igazolvány ugyanabból a típusból");
        assertThat(okmanyList).containsExactly(validatedOkmany1, validatedOkmany2);
        verify(client).sendOkmany(okmany1);
        verify(client).sendOkmany(okmany2);
        verify(response, times(2)).getOkmany();
        verify(response, times(2)).getErrors();
        verify(okmany1).getOkmTipus();
        verify(okmany2).getOkmTipus();
        verifyNoMoreInteractions(client, response, okmany1, okmany2);
        verifyNoInteractions(validatedOkmany1, validatedOkmany2);
    }

    @Test
    public void validateAddsErrorToListWhenOkmanyValidationThrowsException() throws IOException {
        // given
        String okmanySzam = "123";
        when(client.sendOkmany(any())).thenThrow(IOException.class).thenReturn(response);
        when(response.getOkmany()).thenReturn(validatedOkmany1);
        when(response.getErrors()).thenReturn(emptyList());
        when(okmany1.getOkmanySzam()).thenReturn(okmanySzam);
        when(okmany2.getOkmTipus()).thenReturn("2");

        // when
        List<String> errors = validator.validate(list(okmany1, okmany2));
        List<OkmanyDTO> okmanyList = validator.getModifiedTarget();

        // then
        assertThat(errors).containsExactly(String.format("Nem sikerült validálni a(z) %s számú okmányt", okmanySzam));
        assertThat(okmanyList).containsExactly(okmany1, validatedOkmany1);
        verify(client).sendOkmany(okmany1);
        verify(client).sendOkmany(okmany2);
        verify(response).getOkmany();
        verify(response).getErrors();
        verify(okmany1).getOkmanySzam();
        verify(okmany2).getOkmTipus();
        verifyNoMoreInteractions(client, response, okmany1, okmany2);
        verifyNoInteractions(validatedOkmany1, validatedOkmany2);
    }

    @Test
    public void validateAddsErrorsFromResponse() throws IOException {
        // given
        when(client.sendOkmany(any())).thenReturn(response);
        when(response.getOkmany()).thenReturn(validatedOkmany1).thenReturn(validatedOkmany2);
        when(response.getErrors()).thenReturn(list("abc")).thenReturn(list("def"));

        // when
        List<String> errors = validator.validate(list(okmany1, okmany2));
        List<OkmanyDTO> okmanyList = validator.getModifiedTarget();

        // then
        assertThat(errors).containsExactly("abc", "def");
        assertThat(okmanyList).containsExactly(validatedOkmany1, validatedOkmany2);
        verify(client).sendOkmany(okmany1);
        verify(client).sendOkmany(okmany2);
        verify(response, times(2)).getOkmany();
        verify(response, times(2)).getErrors();
        verifyNoMoreInteractions(client, response);
        verifyNoInteractions(okmany1, okmany2, validatedOkmany1, validatedOkmany2);
    }

    @Test
    public void getModifiedTargetThrowsExceptionIfItWasInvokedBeforeValidate() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This method must be invoked after validate()");
        validator.getModifiedTarget();
    }

    private List<OkmanyTipus> getOkmanyTipusok() {
        OkmanyTipus tipus1 = new OkmanyTipus(1, "Személyi igazolvány");
        OkmanyTipus tipus2 = new OkmanyTipus(2, "Útlevél");
        return list(tipus1, tipus2);
    }
}
