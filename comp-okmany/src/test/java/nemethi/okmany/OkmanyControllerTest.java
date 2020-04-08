package nemethi.okmany;

import com.mycompany.mavenproject1.OkmanyDTO;
import nemethi.response.OkmanyResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import validation.Validator;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OkmanyControllerTest {

    private OkmanyController controller;

    @Mock
    private Validator<OkmanyDTO> validator;
    @Mock
    private OkmanyDTO okmany;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
        controller = new OkmanyController(validator);
    }

    @Test
    public void controllerThrowsExceptionOnNullValidator() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("okmanyValidator");
        controller = new OkmanyController(null);
    }

    @Test
    public void controllerReturnsOkWhenValidatorReturnsNoErrors() {
        // given
        when(validator.validate(okmany)).thenReturn(Collections.emptyList());
        OkmanyResponse expectedResponse = new OkmanyResponse(okmany, Collections.emptyList());

        // when
        ResponseEntity<OkmanyResponse> responseEntity = controller.validateOkmany(okmany);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
        verify(validator).validate(okmany);
        verifyNoMoreInteractions(validator);
        verifyNoInteractions(okmany);
    }

    @Test
    public void controllerReturnsUnprocessableEntityWhenValidatorReturnsErrors() {
        // given
        when(validator.validate(okmany)).thenReturn(Collections.singletonList("Error"));
        OkmanyResponse expectedResponse = new OkmanyResponse(okmany, Collections.singletonList("Error"));

        // when
        ResponseEntity<OkmanyResponse> responseEntity = controller.validateOkmany(okmany);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
        verify(validator).validate(okmany);
        verifyNoMoreInteractions(validator);
        verifyNoInteractions(okmany);
    }
}
