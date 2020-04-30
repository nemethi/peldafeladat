package nemethi.szemely.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.mavenproject1.OkmanyDTO;
import nemethi.response.OkmanyResponse;
import nemethi.szemely.Application;
import nemethi.szemely.OkmanyServiceClient;
import nemethi.util.DateUtils;
import org.assertj.core.api.Condition;
import org.assertj.core.util.DateUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.emptyList;
import static org.assertj.core.util.Lists.list;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ActiveProfiles("test")
@RestClientTest({OkmanyServiceClient.class, Application.class})
@RunWith(SpringRunner.class)
public class OkmanyServiceClientIT {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private OkmanyServiceClient client;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        server = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void sendOkmanyReturnsResponseOnOk() throws IOException {
        // given
        OkmanyDTO validOkmany = getOkmany();
        server.expect(requestTo("/validate"))
                .andExpect(method(POST))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().json(asJson(validOkmany)))
                .andRespond(withStatus(OK)
                        .contentType(APPLICATION_JSON_UTF8)
                        .body(asJson(validResponse())));

        // when
        OkmanyResponse response = client.sendOkmany(validOkmany);

        // then
        assertThat(response.getOkmany()).is(equalTo(validOkmany));
        assertThat(response.getErrors()).isEmpty();
        server.verify();
    }

    @Test
    public void sendOkmanyReturnsResponseOnUnprocessableEntity() throws IOException {
        // given
        OkmanyDTO invalidOkmany = getOkmany();
        server.expect(requestTo("/validate"))
                .andExpect(method(POST))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().json(asJson(invalidOkmany)))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .contentType(APPLICATION_JSON_UTF8)
                        .body(asJson(invalidResponse())));

        // when
        OkmanyResponse response = client.sendOkmany(invalidOkmany);

        // then
        assertThat(response.getOkmany()).is(equalTo(invalidOkmany));
        assertThat(response.getErrors()).containsExactly("error");
        server.verify();
    }

    @Test
    public void sendOkmanyThrowsExceptionOnErrorResponse() throws IOException {
        // given
        server.expect(requestTo("/validate"))
                .andExpect(method(POST))
                .andRespond(withStatus(BAD_REQUEST));

        // when
        thrown.expect(IOException.class);
        client.sendOkmany(getOkmany());

        // then
        server.verify();
    }

    private OkmanyDTO getOkmany() {
        OkmanyDTO okmanyDTO = new OkmanyDTO();
        okmanyDTO.setOkmTipus("validTipus");
        okmanyDTO.setOkmanySzam("validSzam");
        okmanyDTO.setLejarDat(DateUtil.now());
        okmanyDTO.setOkmanyKep("validKep".getBytes());
        return okmanyDTO;
    }

    private OkmanyResponse validResponse() {
        return new OkmanyResponse(getOkmany(), emptyList());
    }

    private OkmanyResponse invalidResponse() {
        return new OkmanyResponse(getOkmany(), list("error"));
    }

    private String asJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    private Condition<OkmanyDTO> equalTo(OkmanyDTO okmany) {
        return new Condition<OkmanyDTO>() {
            @Override
            public boolean matches(OkmanyDTO value) {
                LocalDate date1 = DateUtils.dateToLocalDate(value.getLejarDat());
                LocalDate date2 = DateUtils.dateToLocalDate(okmany.getLejarDat());
                return date1.equals(date2) &&
                        Arrays.equals(value.getOkmanyKep(), okmany.getOkmanyKep()) &&
                        value.getOkmanySzam().equals(okmany.getOkmanySzam()) &&
                        value.getOkmTipus().equals(okmany.getOkmTipus());
            }
        };
    }
}
