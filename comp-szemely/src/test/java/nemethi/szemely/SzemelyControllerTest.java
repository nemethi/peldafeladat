package nemethi.szemely;

import com.mycompany.mavenproject1.SzemelyDTO;
import nemethi.response.SzemelyResponse;
import nemethi.validation.Validator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SzemelyControllerTest {

    private SzemelyController controller;

    @Mock
    private Validator<SzemelyDTO> validator;
    @Mock
    private SzemelyDTO szemely;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
        controller = new SzemelyController(validator);
    }

    @Test
    public void validatorCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("szemelyValidator");
        controller = new SzemelyController(null);
    }

    @Test
    public void controllerReturnsOkWhenValidatorReturnsNoErrors() {
        // given
        when(validator.validate(szemely)).thenReturn(Collections.emptyList());
        SzemelyResponse expectedResponse = new SzemelyResponse(szemely, Collections.emptyList());

        // when
        ResponseEntity<SzemelyResponse> responseEntity = controller.validateSzemely(szemely);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
        verify(validator).validate(szemely);
        verifyNoMoreInteractions(validator);
        verifyNoInteractions(szemely);
    }

    @Test
    public void controllerReturnsUnprocessableEntityWhenValidatorReturnsErrors() {
        // given
        when(validator.validate(szemely)).thenReturn(Collections.singletonList("Error"));
        SzemelyResponse expectedResponse = new SzemelyResponse(szemely, Collections.singletonList("Error"));

        // when
        ResponseEntity<SzemelyResponse> responseEntity = controller.validateSzemely(szemely);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
        verify(validator).validate(szemely);
        verifyNoMoreInteractions(validator);
        verifyNoInteractions(szemely);
    }
}
