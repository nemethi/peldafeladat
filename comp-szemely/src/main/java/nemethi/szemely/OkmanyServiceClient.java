package nemethi.szemely;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.mavenproject1.OkmanyDTO;
import nemethi.response.OkmanyResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

public class OkmanyServiceClient {

    private final URI uri;
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;

    public OkmanyServiceClient(URI uri, RestTemplate restTemplate, ObjectMapper mapper) {
        this.uri = Objects.requireNonNull(uri, "uri");
        this.restTemplate = Objects.requireNonNull(restTemplate, "restTemplate");
        this.mapper = Objects.requireNonNull(mapper, "mapper");
    }

    public OkmanyResponse sendOkmany(OkmanyDTO okmany) throws IOException {
        HttpEntity<OkmanyDTO> request = new HttpEntity<>(okmany, defaultHeaders());
        try {
            return restTemplate.postForObject(uri, request, OkmanyResponse.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(UNPROCESSABLE_ENTITY)) {
                return mapper.readValue(e.getResponseBodyAsString(), OkmanyResponse.class);
            }
            throw new IOException(e);
        }
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE);
        headers.add(ACCEPT, APPLICATION_JSON_UTF8_VALUE);
        return headers;
    }
}
