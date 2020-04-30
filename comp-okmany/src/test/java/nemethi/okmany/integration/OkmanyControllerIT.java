package nemethi.okmany.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.mavenproject1.OkmanyDTO;
import nemethi.response.OkmanyResponse;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.YEARS;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class OkmanyControllerIT {

    private static final String URL = "/validate";
    private static final String SZEMELYI_IG_TIPUS = "1";
    private static final String UTLEVEL_TIPUS = "2";
    private static final String JOGOSITVANY_TIPUS = "3";
    private static final String VALID_SZEMELYI_IG_SZAM = "123456AB";
    private static final String INVALID_SZAM = "AB123";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Value("classpath:arckep_jo.jpg")
    private Resource validImage;
    @Value("classpath:arckep_rosz.jpg")
    private Resource invalidImage;
    @Value("classpath:nem_jpeg.png")
    private Resource pngImage;

    @Test
    public void controllerReturnsBadRequestOnEmptyContent() throws Exception {
        mockMvc.perform(post(URL)
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void controllerReturnsOkOnValidContent() throws Exception {
        // given
        OkmanyDTO okmany = createOkmany(SZEMELYI_IG_TIPUS, VALID_SZEMELYI_IG_SZAM, validImage, todayPlus(1, YEARS), false);
        OkmanyDTO expectedOkmany = copyOkmany(okmany);
        expectedOkmany.setErvenyes(true);
        OkmanyResponse expected = new OkmanyResponse(expectedOkmany, Collections.emptyList());

        // when + then
        mockMvc.perform(post(URL)
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .content(asJson(okmany)))
                .andExpect(status().isOk())
                .andExpect(content().json(asJson(expected)));
    }

    @Test
    public void controllerReturnsUnprocessableEntityWithErrorListOnContentWithAllNullData() throws Exception {
        // given
        OkmanyDTO okmany = createOkmany(null, null, null, null, false);
        OkmanyDTO expectedOkmany = copyOkmany(okmany);
        List<String> errors = Lists.list("Ismeretlen okmánytípus", "Érvénytelen Ismeretlen okmány szám",
                "Hiányzó okmánykép", "Hiányzó lejárati idő");
        OkmanyResponse expected = new OkmanyResponse(expectedOkmany, errors);

        // when + then
        assertRequestWithInvalidContent(okmany, expected);
    }

    @Test
    public void controllerReturnsUnprocessableEntityWithErrorListOnInvalidContent() throws Exception {
        // given
        OkmanyDTO okmany = createOkmany(UTLEVEL_TIPUS, INVALID_SZAM, invalidImage, todayMinus(1, YEARS), true);
        OkmanyDTO expectedOkmany = copyOkmany(okmany);
        expectedOkmany.setErvenyes(false);
        List<String> errors = Lists.list("Érvénytelen Útlevél szám", "A kép mérete nem 827x1063",
                "Az okmány érvényessége lejárt");
        OkmanyResponse expected = new OkmanyResponse(expectedOkmany, errors);

        // when + then
        assertRequestWithInvalidContent(okmany, expected);
    }

    @Test
    public void controllerReturnsUnprocessableEntityWithErrorListOnNotJpegImage() throws Exception {
        // given
        OkmanyDTO okmany = createOkmany(JOGOSITVANY_TIPUS, INVALID_SZAM, pngImage, todayPlus(1, DAYS), true);
        OkmanyDTO expectedOkmany = copyOkmany(okmany);
        List<String> errors = Lists.list("A kép nem JPEG típusú");
        OkmanyResponse expected = new OkmanyResponse(expectedOkmany, errors);

        // when + then
        assertRequestWithInvalidContent(okmany, expected);
    }

    private void assertRequestWithInvalidContent(OkmanyDTO content, OkmanyResponse expected) throws Exception {
        mockMvc.perform(post(URL)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(asJson(content)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().json(asJson(expected)));
    }

    private OkmanyDTO createOkmany(String okmTipus, String okmanySzam, Resource okmanyKep, Date lejarDat, boolean ervenyes) throws IOException {
        OkmanyDTO okmany = new OkmanyDTO();
        okmany.setOkmTipus(okmTipus);
        okmany.setOkmanySzam(okmanySzam);
        okmany.setOkmanyKep(asBytes(okmanyKep));
        okmany.setLejarDat(lejarDat);
        okmany.setErvenyes(ervenyes);
        return okmany;
    }

    private Date todayPlus(int amount, TemporalUnit unit) {
        return Date.from(Instant.now().atZone(ZoneId.systemDefault()).plus(amount, unit).toInstant());
    }

    private Date todayMinus(int amount, TemporalUnit unit) {
        return Date.from(Instant.now().atZone(ZoneId.systemDefault()).minus(amount, unit).toInstant());
    }

    private String asJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    private byte[] asBytes(Resource resource) throws IOException {
        if (resource == null) {
            return null;
        }
        return Files.readAllBytes(resource.getFile().toPath());
    }

    private OkmanyDTO copyOkmany(OkmanyDTO okmany) {
        OkmanyDTO result = new OkmanyDTO();
        result.setOkmTipus(okmany.getOkmTipus());
        result.setOkmanySzam(okmany.getOkmanySzam());
        result.setOkmanyKep(okmany.getOkmanyKep());
        result.setLejarDat(okmany.getLejarDat());
        result.setErvenyes(okmany.isErvenyes());
        return result;
    }
}
