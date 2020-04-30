package nemethi.szemely.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.mavenproject1.OkmanyDTO;
import com.mycompany.mavenproject1.SzemelyDTO;
import nemethi.response.OkmanyResponse;
import nemethi.response.SzemelyResponse;
import nemethi.szemely.OkmanyServiceClient;
import org.assertj.core.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.util.Lists.emptyList;
import static org.assertj.core.util.Lists.list;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringRunner.class)
public class SzemelyControllerIT {

    private static final OkmanyDTO OKMANY = createOkmany("1", "123", null, DateUtil.now(), false);
    private static final SzemelyDTO SZEMELY = createSzemely("Dr. Kovács István", "Kovács István", "Tóth Mária",
            DateUtil.parse("1971-01-23"), "F", "HUN", null, newArrayList(OKMANY));
    private static final String URL = "/validate";
    private static final String MAGYARORSZAG_ALLAMPOLGARA = "MAGYARORSZÁG ÁLLAMPOLGÁRA";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private OkmanyServiceClient client;

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
        OkmanyDTO validatedOkmany = getValidatedOkmany();
        SzemelyDTO validatedSzemely = getValidatedSzemely(validatedOkmany);
        OkmanyResponse okmanyResponse = new OkmanyResponse(validatedOkmany, emptyList());
        SzemelyResponse szemelyResponse = new SzemelyResponse(validatedSzemely, emptyList());

        when(client.sendOkmany(any())).thenReturn(okmanyResponse);

        // when + then
        mockMvc.perform(post(URL)
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .content(asJson(SZEMELY)))
                .andExpect(status().isOk())
                .andExpect(content().json(asJson(szemelyResponse)));

        // then
        verify(client).sendOkmany(any());
        verifyNoMoreInteractions(client);
    }

    @Test
    public void controllerReturnsUnprocessableEntityWithErrorListOnContentWithAllNullData() throws Exception {
        // given
        SzemelyDTO szemely = createSzemely(null, null, null, null, null, null, null, null);
        List<String> errors = list("Hiányzó viselt név", "Hiányzó születési név", "Hiányzó anya név", "Hiányzó születési dátum", "Hiányzó nem", "Hiányzó állampolgárság", "Hiányzó okmánylista");

        // when + then
        assertRequestWithInvalidContent(szemely, new SzemelyResponse(szemely, errors));
        verifyNoInteractions(client);
    }

    @Test
    public void controllerReturnsUnprocessableEntityWithErrorListOnInvalidSzemelyWithValidOkmany() throws Exception {
        // given
        SzemelyDTO invalidSzemely = copySzemely(SZEMELY);
        invalidSzemely.setVisNev("abc");
        OkmanyDTO validatedOkmany = getValidatedOkmany();
        SzemelyDTO validatedSzemely = getValidatedSzemely(validatedOkmany, invalidSzemely);
        OkmanyResponse okmanyResponse = new OkmanyResponse(validatedOkmany, emptyList());
        SzemelyResponse szemelyResponse = new SzemelyResponse(validatedSzemely, list("Érvénytelen viselt név"));

        when(client.sendOkmany(any())).thenReturn(okmanyResponse);

        // when + then
        assertRequestWithInvalidContent(invalidSzemely, szemelyResponse);
        verify(client).sendOkmany(any());
        verifyNoMoreInteractions(client);
    }

    @Test
    public void controllerReturnsUnprocessableEntityWithErrorListOnValidSzemelyWithInvalidOkmany() throws Exception {
        // given
        OkmanyDTO invalidOkmany = copyOkmany(OKMANY);
        invalidOkmany.setOkmTipus("0");
        OkmanyDTO validatedOkmany = getValidatedOkmany(invalidOkmany);
        SzemelyDTO validatedSzemely = getValidatedSzemely(validatedOkmany);
        OkmanyResponse okmanyResponse = new OkmanyResponse(validatedOkmany, list("Érvénytelen okmánytípus"));
        SzemelyResponse szemelyResponse = new SzemelyResponse(validatedSzemely, list("Érvénytelen okmánytípus"));

        when(client.sendOkmany(any())).thenReturn(okmanyResponse);

        // when + then
        assertRequestWithInvalidContent(SZEMELY, szemelyResponse);
        verify(client).sendOkmany(any());
        verifyNoMoreInteractions(client);
    }

    @Test
    public void controllerReturnsUnprocessableEntityWithErrorListWhenOkmanyServiceFails() throws Exception {
        // given
        SzemelyDTO validatedSzemely = getValidatedSzemely(OKMANY);
        String error = String.format("Nem sikerült validálni a(z) %s számú okmányt", OKMANY.getOkmanySzam());
        SzemelyResponse szemelyResponse = new SzemelyResponse(validatedSzemely, list(error));

        when(client.sendOkmany(any())).thenThrow(IOException.class);

        // when + then
        assertRequestWithInvalidContent(SZEMELY, szemelyResponse);
        verify(client).sendOkmany(any());
        verifyNoMoreInteractions(client);
    }

    private void assertRequestWithInvalidContent(SzemelyDTO content, SzemelyResponse expectedResponse) throws Exception {
        mockMvc.perform(post(URL)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(asJson(content)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().json(asJson(expectedResponse)));
    }

    private static SzemelyDTO createSzemely(String visNev, String szulNev, String aNev, Date szulDat, String neme, String allampKod, String allampDekod, ArrayList<OkmanyDTO> okmLista) {
        SzemelyDTO szemely = new SzemelyDTO();
        szemely.setVisNev(visNev);
        szemely.setSzulNev(szulNev);
        szemely.setaNev(aNev);
        szemely.setSzulDat(szulDat);
        szemely.setNeme(neme);
        szemely.setAllampKod(allampKod);
        szemely.setAllampDekod(allampDekod);
        szemely.setOkmLista(newArrayList(okmLista));
        return szemely;
    }

    private SzemelyDTO getValidatedSzemely(OkmanyDTO validatedOkmany) {
        return getValidatedSzemely(validatedOkmany, SZEMELY);
    }

    private SzemelyDTO getValidatedSzemely(OkmanyDTO validatedOkmany, SzemelyDTO szemely) {
        SzemelyDTO validatedSzemely = copySzemely(szemely);
        validatedSzemely.setAllampDekod(MAGYARORSZAG_ALLAMPOLGARA);
        validatedSzemely.setOkmLista(newArrayList(validatedOkmany));
        return validatedSzemely;
    }

    private OkmanyDTO getValidatedOkmany() {
        return getValidatedOkmany(OKMANY);
    }

    private OkmanyDTO getValidatedOkmany(OkmanyDTO okmany) {
        OkmanyDTO validatedOkmany = copyOkmany(okmany);
        validatedOkmany.setErvenyes(true);
        return validatedOkmany;
    }

    private SzemelyDTO copySzemely(SzemelyDTO szemely) {
        SzemelyDTO result = new SzemelyDTO();
        result.setVisNev(szemely.getVisNev());
        result.setSzulNev(szemely.getSzulNev());
        result.setaNev(szemely.getaNev());
        result.setSzulDat(szemely.getSzulDat());
        result.setNeme(szemely.getNeme());
        result.setAllampKod(szemely.getAllampKod());
        result.setAllampDekod(szemely.getAllampDekod());
        result.setOkmLista(newArrayList(szemely.getOkmLista()));
        return result;
    }

    private static OkmanyDTO createOkmany(String okmTipus, String okmanySzam, byte[] okmanyKep, Date lejarDat, boolean ervenyes) {
        OkmanyDTO okmany = new OkmanyDTO();
        okmany.setOkmTipus(okmTipus);
        okmany.setOkmanySzam(okmanySzam);
        okmany.setOkmanyKep(okmanyKep);
        okmany.setLejarDat(lejarDat);
        okmany.setErvenyes(ervenyes);
        return okmany;
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

    private String asJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }
}
