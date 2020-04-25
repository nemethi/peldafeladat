package nemethi.szemely;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.mavenproject1.OkmanyDTO;
import nemethi.response.OkmanyResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.util.Lists.list;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

public class OkmanyServiceClientTest {

    private static final URI TEST_URI = URI.create("http://test/uri");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ObjectMapper mapper;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private OkmanyDTO okmany;
    @Mock
    private OkmanyResponse response;
    @Mock
    private HttpClientErrorException exception;
    @Captor
    private ArgumentCaptor<HttpEntity<OkmanyDTO>> captor;

    private OkmanyServiceClient client;

    @Before
    public void setUp() {
        initMocks(this);
        client = new OkmanyServiceClient(TEST_URI, restTemplate, mapper);
    }

    @Test
    public void uriCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("uri");
        new OkmanyServiceClient(null, restTemplate, mapper);
    }

    @Test
    public void restTemplateCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("restTemplate");
        new OkmanyServiceClient(TEST_URI, null, mapper);
    }

    @Test
    public void mapperCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("mapper");
        new OkmanyServiceClient(TEST_URI, restTemplate, null);
    }

    @Test
    public void sendOkmanyReturnsResponseOnOkRequest() throws IOException {
        // given
        when(restTemplate.postForObject(eq(TEST_URI), any(HttpEntity.class), eq(OkmanyResponse.class)))
                .thenReturn(response);

        // when
        OkmanyResponse okmanyResponse = client.sendOkmany(okmany);

        // then
        assertThat(okmanyResponse).isEqualTo(response);
        verify(restTemplate).postForObject(eq(TEST_URI), captor.capture(), eq(OkmanyResponse.class));
        assertRequest(captor.getValue());
        verifyNoMoreInteractions(restTemplate);
        verifyNoInteractions(okmany, response, mapper);
    }

    @Test
    public void sendOkmanyReturnsResponseOnUnprocessableEntityRequest() throws IOException {
        // given
        String responseBody = "responseBody";
        when(restTemplate.postForObject(eq(TEST_URI), any(HttpEntity.class), eq(OkmanyResponse.class)))
                .thenThrow(exception);
        when(exception.getStatusCode()).thenReturn(UNPROCESSABLE_ENTITY);
        when(exception.getResponseBodyAsString()).thenReturn(responseBody);
        when(mapper.readValue(responseBody, OkmanyResponse.class)).thenReturn(response);

        // when
        OkmanyResponse okmanyResponse = client.sendOkmany(okmany);

        // then
        assertThat(okmanyResponse).isEqualTo(response);
        verify(restTemplate).postForObject(eq(TEST_URI), captor.capture(), eq(OkmanyResponse.class));
        assertRequest(captor.getValue());
        verify(exception).getStatusCode();
        verify(exception).getResponseBodyAsString();
        verify(mapper).readValue(responseBody, OkmanyResponse.class);
        verifyNoMoreInteractions(restTemplate, exception, mapper);
        verifyNoInteractions(okmany);
    }

    @Test
    public void sendOkmanyThrowsExceptionOnOtherClientErrors() throws IOException {
        // given
        when(restTemplate.postForObject(eq(TEST_URI), any(HttpEntity.class), eq(OkmanyResponse.class)))
                .thenThrow(exception);
        when(exception.getStatusCode()).thenReturn(NOT_FOUND);

        // when
        thrown.expect(IOException.class);
        thrown.expectCause(is(exception));
        client.sendOkmany(okmany);

        // then
        verify(restTemplate).postForObject(eq(TEST_URI), captor.capture(), eq(OkmanyResponse.class));
        assertRequest(captor.getValue());
        verify(exception).getStatusCode();
        verifyNoMoreInteractions(restTemplate, exception);
        verifyNoInteractions(okmany, mapper);
    }

    @Test
    public void sendOkmanyRethrowsExceptionThrownByMapper() throws IOException {
        // given
        String responseBody = "responseBody";
        IOException ioException = new IOException("mapper error");
        when(restTemplate.postForObject(eq(TEST_URI), any(HttpEntity.class), eq(OkmanyResponse.class)))
                .thenThrow(exception);
        when(exception.getStatusCode()).thenReturn(UNPROCESSABLE_ENTITY);
        when(exception.getResponseBodyAsString()).thenReturn(responseBody);
        when(mapper.readValue(responseBody, OkmanyResponse.class)).thenThrow(ioException);

        // when
        thrown.expect(is(ioException));
        client.sendOkmany(okmany);

        // then
        verify(restTemplate).postForObject(eq(TEST_URI), captor.capture(), eq(OkmanyResponse.class));
        assertRequest(captor.getValue());
        verify(exception).getStatusCode();
        verify(exception).getResponseBodyAsString();
        verify(mapper).readValue(responseBody, OkmanyResponse.class);
        verifyNoMoreInteractions(restTemplate, exception, mapper);
        verifyNoInteractions(okmany);
    }

    private void assertRequest(HttpEntity<OkmanyDTO> request) {
        assertThat(request.getBody()).isEqualTo(okmany);
        assertThat(request.getHeaders()).containsOnly(
                entry(CONTENT_TYPE, list(APPLICATION_JSON_UTF8_VALUE)),
                entry(ACCEPT, list(APPLICATION_JSON_UTF8_VALUE)));
    }

}
