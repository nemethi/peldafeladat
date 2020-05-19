package nemethi.functional;

import com.mycompany.mavenproject1.OkmanyDTO;
import com.mycompany.mavenproject1.SzemelyDTO;
import nemethi.response.SzemelyResponse;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.util.Lists.list;
import static org.assertj.core.util.Lists.newArrayList;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

public class ServicesFT {

    private static final String MAGYARORSZAG_ALLAMPOLGARA = "MAGYARORSZÁG ÁLLAMPOLGÁRA";
    private static final String INVALID_SZAM = "invalidSzam";
    private static final String[] INVALID_SZEMELYI_IGAZOLVANY_ERRORS = new String[]{
            "Ismeretlen okmánytípus",
            "Érvénytelen Ismeretlen okmány szám",
            "A kép mérete nem 827x1063",
            "Az okmány érvényessége lejárt"};
    private static final String[] INVALID_UTLEVEL_ERRORS = new String[]{
            "Érvénytelen Útlevél szám",
            "Hiányzó okmánykép",
            "Az okmány érvényessége lejárt"};
    private static final String[] INVALID_VEZETOI_ENGEDELY_ERRORS = new String[]{
            "Érvénytelen Vezetői engedély szám",
            "Hibás, olvashatatlan kép",
            "Az okmány érvényessége lejárt"};
    private static final String[] INVALID_EGYEB_OKMANY_ERRORS = new String[]{
            "Ismeretlen okmánytípus",
            "Érvénytelen Ismeretlen okmány szám",
            "Hiányzó okmánykép",
            "Hiányzó lejárati idő"};
    private static final String[] INVALID_EGT_OKMANY_ERRORS = new String[]{
            "Érvénytelen EGT által elfogadott egyéb okmány szám",
            "A kép mérete nem 827x1063",
            "Az okmány érvényessége lejárt"};
    private static final String[] INVALID_IDEIGLENES_SZEMELYI_ERRORS = new String[]{
            "Érvénytelen Ideiglenes személyazonosító igazolvány szám",
            "A kép mérete nem 827x1063",
            "Az okmány érvényessége lejárt"};
    private static final String[] INVALID_SZEMELY_ERRORS = {
            "Érvénytelen viselt név",
            "Hiányzó születési név",
            "Hiányzó anya név",
            "Érvénytelen születési idő",
            "Érvénytelen nem",
            "Érvénytelen állampolgárság"};

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private RestTemplate restTemplate;

    @BeforeClass
    public static void beforeClass() {
        launchOkmanyService();
        launchSzemelyService();
    }

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(emptyErrorHandler());
    }

    @Test
    public void validateValidSzemelyWithOneValidOkmanyOfEachKind() throws IOException {
        // given
        SzemelyDTO szemely = validSzemelyWith(validSzemelyi(), validUtlevel(), validVezetoiEngedely(),
                validEgyebOkmany(), validEGTOkmany(), validIdeiglenesSzemelyi());

        // when
        ResponseEntity<SzemelyResponse> response = sendRequest(szemely);

        // then
        assertOkResponse(response);
    }

    @Test
    public void validateValidSzemelyWithOneInvalidOkmanyOfEachKind() throws IOException {
        // given
        SzemelyDTO szemely = validSzemelyWith(invalidSzemelyi(), invalidUtlevel(), invalidVezetoiEngedely(),
                invalidEgyebOkmany(), invalidEGTOkmany(), invalidIdeiglenesSzemelyi());

        // when
        ResponseEntity<SzemelyResponse> response = sendRequest(szemely);

        // then
        assertErrorResponse(response, concat(INVALID_SZEMELYI_IGAZOLVANY_ERRORS, INVALID_UTLEVEL_ERRORS,
                INVALID_VEZETOI_ENGEDELY_ERRORS, INVALID_EGYEB_OKMANY_ERRORS, INVALID_EGT_OKMANY_ERRORS,
                INVALID_IDEIGLENES_SZEMELYI_ERRORS));
        softly.assertThat(response.getBody().getSzemely().getOkmLista()).noneMatch(OkmanyDTO::isErvenyes);
    }

    @Test
    public void validateValidSzemelyWithMultipleValidOkmanyOfSameKind() throws IOException {
        // given
        SzemelyDTO szemely = validSzemelyWith(validSzemelyi("123456AB"), validUtlevel(),
                validSzemelyi("123456YZ"));

        // when
        ResponseEntity<SzemelyResponse> response = sendRequest(szemely);

        // then
        assertErrorResponse(response, "Több, mint 1 érvényes Személyazonosító igazolvány");
    }

    @Test
    public void validateValidSzemelyWithMultipleInvalidOkmanyOfSameKind() throws IOException {
        // given
        SzemelyDTO szemely = validSzemelyWith(invalidSzemelyi("invalidSzemelyi1"),
                invalidUtlevel(), invalidSzemelyi("invalidSzemelyi2"));

        // when
        ResponseEntity<SzemelyResponse> response = sendRequest(szemely);

        // then
        assertErrorResponse(response, concat(INVALID_SZEMELYI_IGAZOLVANY_ERRORS,
                INVALID_UTLEVEL_ERRORS, INVALID_SZEMELYI_IGAZOLVANY_ERRORS));
    }

    @Test
    public void validateInvalidSzemelyWithValidOkmany() throws IOException {
        // given
        SzemelyDTO szemely = invalidSzemelyWith(validSzemelyi());

        // when
        ResponseEntity<SzemelyResponse> response = sendRequest(szemely);

        // then
        assertErrorResponseWithDecodedAllampolgarsag(response, null, INVALID_SZEMELY_ERRORS);
    }

    @Test
    public void validateInvalidSzemelyWithInvalidOkmany() throws IOException {
        // given
        SzemelyDTO szemely = invalidSzemelyWith(invalidSzemelyi());

        // when
        ResponseEntity<SzemelyResponse> response = sendRequest(szemely);

        // then
        assertErrorResponseWithDecodedAllampolgarsag(response, null,
                concat(INVALID_SZEMELY_ERRORS, INVALID_SZEMELYI_IGAZOLVANY_ERRORS));
    }

    private ResponseEntity<SzemelyResponse> sendRequest(SzemelyDTO requestBody) {
        RequestEntity<SzemelyDTO> request = RequestEntity.post(URI.create("http://localhost:8081/validate"))
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .body(requestBody);

        return restTemplate.exchange(request, SzemelyResponse.class);
    }

    private void assertOkResponse(ResponseEntity<SzemelyResponse> response) {
        SzemelyResponse responseBody = response.getBody();
        softly.assertThat(response.getStatusCode()).isEqualTo(OK);
        softly.assertThat(responseBody.getSzemely().getAllampDekod()).isEqualTo(MAGYARORSZAG_ALLAMPOLGARA);
        softly.assertThat(responseBody.getSzemely().getOkmLista()).allMatch(OkmanyDTO::isErvenyes);
        softly.assertThat(responseBody.getErrors()).isEmpty();
    }

    private void assertErrorResponse(ResponseEntity<SzemelyResponse> response, String... errors) {
        assertErrorResponseWithDecodedAllampolgarsag(response, MAGYARORSZAG_ALLAMPOLGARA, errors);
    }

    private void assertErrorResponseWithDecodedAllampolgarsag(ResponseEntity<SzemelyResponse> response,
                                                              String allampolgarsag, String... errors) {
        SzemelyResponse responseBody = response.getBody();
        softly.assertThat(response.getStatusCode()).isEqualTo(UNPROCESSABLE_ENTITY);
        softly.assertThat(responseBody.getSzemely().getAllampDekod()).isEqualTo(allampolgarsag);
        softly.assertThat(responseBody.getErrors()).isEqualTo(list(errors));
    }

    private SzemelyDTO validSzemelyWith(OkmanyDTO... okmLista) {
        SzemelyDTO szemely = new SzemelyDTO();
        szemely.setVisNev("Dr. Kovács István");
        szemely.setSzulNev("Kovács István");
        szemely.setaNev("Tóth Mária");
        szemely.setNeme("F");
        szemely.setSzulDat(todayMinusYears(50));
        szemely.setAllampKod("HUN");
        szemely.setOkmLista(newArrayList(okmLista));
        return szemely;
    }

    private SzemelyDTO invalidSzemelyWith(OkmanyDTO... okmLista) {
        SzemelyDTO szemely = new SzemelyDTO();
        szemely.setVisNev("Dr. Dr.");
        szemely.setSzulNev("");
        szemely.setaNev(null);
        szemely.setNeme("invalidNem");
        szemely.setSzulDat(todayPlusOneYear());
        szemely.setAllampKod("ABC123");
        szemely.setOkmLista(newArrayList(okmLista));
        return szemely;
    }

    private OkmanyDTO invalidSzemelyi() throws IOException {
        return invalidSzemelyi(INVALID_SZAM);
    }

    private OkmanyDTO invalidSzemelyi(String okmanySzam) throws IOException {
        OkmanyDTO szemelyi = new OkmanyDTO();
        szemelyi.setOkmTipus("0");
        szemelyi.setOkmanySzam(okmanySzam);
        szemelyi.setLejarDat(todayMinusYears(1));
        szemelyi.setOkmanyKep(invalidKep());
        return szemelyi;
    }

    private OkmanyDTO validSzemelyi() throws IOException {
        return validSzemelyi("123456AB");
    }

    private OkmanyDTO validSzemelyi(String okmanySzam) throws IOException {
        OkmanyDTO szemelyi = new OkmanyDTO();
        szemelyi.setOkmTipus("1");
        szemelyi.setOkmanySzam(okmanySzam);
        szemelyi.setLejarDat(todayPlusOneYear());
        szemelyi.setOkmanyKep(validKep());
        return szemelyi;
    }

    private OkmanyDTO invalidUtlevel() {
        OkmanyDTO utlevel = new OkmanyDTO();
        utlevel.setOkmTipus("2");
        utlevel.setOkmanySzam(INVALID_SZAM);
        utlevel.setLejarDat(todayMinusYears(1));
        utlevel.setOkmanyKep(new byte[0]);
        return utlevel;
    }

    private OkmanyDTO validUtlevel() throws IOException {
        OkmanyDTO utlevel = new OkmanyDTO();
        utlevel.setOkmTipus("2");
        utlevel.setOkmanySzam("AB1234567");
        utlevel.setLejarDat(todayPlusOneYear());
        utlevel.setOkmanyKep(validKep());
        return utlevel;
    }

    private OkmanyDTO invalidVezetoiEngedely() {
        OkmanyDTO vezetoiEng = new OkmanyDTO();
        vezetoiEng.setOkmTipus("3");
        vezetoiEng.setOkmanySzam(INVALID_SZAM);
        vezetoiEng.setLejarDat(todayMinusYears(1));
        vezetoiEng.setOkmanyKep(new byte[]{0x00});
        return vezetoiEng;
    }

    private OkmanyDTO validVezetoiEngedely() throws IOException {
        OkmanyDTO vezetoiEng = new OkmanyDTO();
        vezetoiEng.setOkmTipus("3");
        vezetoiEng.setOkmanySzam("VEZ1234567");
        vezetoiEng.setLejarDat(todayPlusOneYear());
        vezetoiEng.setOkmanyKep(validKep());
        return vezetoiEng;
    }

    private OkmanyDTO invalidEgyebOkmany() {
        OkmanyDTO egyeb = new OkmanyDTO();
        egyeb.setOkmTipus(null);
        egyeb.setOkmanySzam(null);
        egyeb.setLejarDat(null);
        egyeb.setOkmanyKep(null);
        return egyeb;
    }

    private OkmanyDTO validEgyebOkmany() throws IOException {
        OkmanyDTO egyeb = new OkmanyDTO();
        egyeb.setOkmTipus("4");
        egyeb.setOkmanySzam("1234567890");
        egyeb.setLejarDat(todayPlusOneYear());
        egyeb.setOkmanyKep(validKep());
        return egyeb;
    }

    private OkmanyDTO invalidEGTOkmany() throws IOException {
        OkmanyDTO egt = new OkmanyDTO();
        egt.setOkmTipus("5");
        egt.setOkmanySzam("");
        egt.setLejarDat(todayMinusYears(1));
        egt.setOkmanyKep(invalidKep());
        return egt;
    }

    private OkmanyDTO validEGTOkmany() throws IOException {
        OkmanyDTO egt = new OkmanyDTO();
        egt.setOkmTipus("5");
        egt.setOkmanySzam("EGT");
        egt.setLejarDat(todayPlusOneYear());
        egt.setOkmanyKep(validKep());
        return egt;
    }

    private OkmanyDTO invalidIdeiglenesSzemelyi() throws IOException {
        OkmanyDTO szemelyi = new OkmanyDTO();
        szemelyi.setOkmTipus("6");
        szemelyi.setOkmanySzam(INVALID_SZAM);
        szemelyi.setLejarDat(todayMinusYears(1));
        szemelyi.setOkmanyKep(invalidKep());
        return szemelyi;
    }

    private OkmanyDTO validIdeiglenesSzemelyi() throws IOException {
        OkmanyDTO szemelyi = new OkmanyDTO();
        szemelyi.setOkmTipus("6");
        szemelyi.setOkmanySzam("1");
        szemelyi.setLejarDat(todayPlusOneYear());
        szemelyi.setOkmanyKep(validKep());
        return szemelyi;
    }

    private byte[] validKep() throws IOException {
        return Files.readAllBytes(new File("src/test/resources/arckep_jo.jpg").toPath());
    }

    private byte[] invalidKep() throws IOException {
        return Files.readAllBytes(new File("src/test/resources/arckep_rosz.jpg").toPath());
    }

    private Date todayPlusOneYear() {
        LocalDate todayPlusOneYear = LocalDate.now().plus(1, ChronoUnit.YEARS);
        return Date.from(asInstant(todayPlusOneYear));
    }

    private Date todayMinusYears(int years) {
        LocalDate desiredDate = LocalDate.now().minus(years, ChronoUnit.YEARS);
        return Date.from(asInstant(desiredDate));
    }

    private Instant asInstant(LocalDate localDate) {
        return localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
    }

    private String[] concat(String[]... arrays) {
        int totalLength = Arrays.stream(arrays)
                .reduce(0, (tempSum, array) -> tempSum + array.length, Integer::sum);
        return Arrays.stream(arrays).flatMap(Arrays::stream).toArray(v -> new String[totalLength]);
    }

    private ResponseErrorHandler emptyErrorHandler() {
        return new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
            }
        };
    }

    private static void launchOkmanyService() {
        SpringApplicationBuilder okmanyService =
                new SpringApplicationBuilder(nemethi.okmany.Application.class)
                        .properties("okmanytipusok.path=classpath:kodszotar46_okmanytipus.json");
        okmanyService.run("--server.port=8080");
    }

    private static void launchSzemelyService() {
        SpringApplicationBuilder szemelyService =
                new SpringApplicationBuilder(nemethi.szemely.Application.class)
                        .properties("okmanytipusok.path=classpath:kodszotar46_okmanytipus.json",
                                "szemely.age.min=18",
                                "szemely.age.max=120",
                                "szemely.nemek=F,N",
                                "allampolgarsagok.path=classpath:kodszotar21_allampolg.json",
                                "okmanyservice.uri=http://localhost:8080/validate");
        szemelyService.run("--server.port=8081");
    }
}
