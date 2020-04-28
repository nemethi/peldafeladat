package nemethi.szemely;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mycompany.mavenproject1.OkmanyDTO;
import com.mycompany.mavenproject1.SzemelyDTO;
import nemethi.mapper.AllampolgarsagMapper;
import nemethi.mapper.OkmanyTipusMapper;
import nemethi.model.Allampolgarsag;
import nemethi.model.OkmanyTipus;
import nemethi.szemely.validation.NameTypeValidator;
import nemethi.szemely.validation.ValidationTargetModifier;
import nemethi.validation.Validator;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;
import static org.assertj.core.util.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ApplicationTest {

    private static final String PATH = "/test/path";
    private static final IOException IO_EXCEPTION = new IOException("Error");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private NameTypeValidator<String, String> nevValidator;
    @Mock
    private Validator<Date> korValidator;
    @Mock
    private Validator<String> nemValidator;
    @Mock
    private Validator<String> allampolgarsagValidator;
    @Mock
    private ValidationTargetModifier<List<OkmanyDTO>> okmanyListValidator;
    @Mock
    private Collection<Allampolgarsag> allampolgarsagCollection;
    @Mock
    private OkmanyServiceClient client;
    @Mock
    private Collection<OkmanyTipus> okmanyTipusCollection;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ResourceLoader resourceLoader;
    @Mock
    private Resource resource;
    @Mock
    private InputStream inputStream;
    @Mock
    private OkmanyTipusMapper okmanyTipusMapper;
    @Mock
    private AllampolgarsagMapper allampolgarsagMapper;

    private Application application;

    @Before
    public void setUp() {
        initMocks(this);
        application = new Application();
    }

    @Test
    public void szemelyValidatorReturnsValidInstance() {
        Validator<SzemelyDTO> szemelyValidator =
                application.szemelyValidator(nevValidator, korValidator, nemValidator,
                        allampolgarsagValidator, okmanyListValidator, allampolgarsagCollection);
        assertThat(szemelyValidator).isNotNull();
    }

    @Test
    public void nevValidatorReturnsValidInstance() {
        NameTypeValidator<String, String> nevValidator = application.nevValidator();
        assertThat(nevValidator).isNotNull();
    }

    @Test
    public void korValidatorReturnsValidInstance() {
        Validator<Date> korValidator = application.korValidator(Clock.systemDefaultZone(), 18, 120);
        assertThat(korValidator).isNotNull();
    }

    @Test
    public void nemValidatorReturnsValidInstance() {
        Validator<String> nemValidator = application.nemValidator(newHashSet(list("F", "N")));
        assertThat(nemValidator).isNotNull();
    }

    @Test
    public void allampolgarsagValidatorReturnsValidInstance() {
        Validator<String> allampolgarsagValidator = application.allampolgarsagValidator(allampolgarsagCollection);
        assertThat(allampolgarsagValidator).isNotNull();
    }

    @Test
    public void okmanyListValidatorReturnsValidInstance() {
        ValidationTargetModifier<List<OkmanyDTO>> okmanyListValidator = application.okmanyListValidator(client, okmanyTipusCollection);
        assertThat(okmanyListValidator).isNotNull();
    }

    @Test
    public void okmanyServiceClientReturnsValidInstance() {
        URI uri = URI.create("http://localhost:8080");
        OkmanyServiceClient okmanyServiceClient = application.okmanyServiceClient(uri, restTemplate, objectMapper);
        assertThat(okmanyServiceClient).isNotNull();
    }

    @Test
    public void restTemplateReturnsValidInstance() {
        RestTemplate restTemplate = application.restTemplate();
        assertThat(restTemplate).isNotNull();
    }

    @Test
    public void clockReturnsValidInstance() {
        Clock clock = application.clock();
        assertThat(clock).isNotNull();
    }

    @Test
    public void okmanyTipusMapperReturnsValidInstance() {
        OkmanyTipusMapper okmanyTipusMapper = application.okmanyTipusMapper(objectMapper);
        assertThat(okmanyTipusMapper).isNotNull();
    }

    @Test
    public void okmanyTipusokReturnsValidCollection() throws IOException {
        // given
        Set<OkmanyTipus> okmanyTipusok = new HashSet<>();
        okmanyTipusok.add(new OkmanyTipus(1, "ertek"));
        when(resourceLoader.getResource(PATH)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);
        when(okmanyTipusMapper.readToCollection(inputStream)).thenReturn(okmanyTipusok);

        // when
        Collection<OkmanyTipus> collection = application.okmanyTipusok(PATH, okmanyTipusMapper, resourceLoader);

        // then
        assertThat(collection).isEqualTo(okmanyTipusok);
        verify(resourceLoader).getResource(PATH);
        verify(resource).getInputStream();
        verify(okmanyTipusMapper).readToCollection(inputStream);
        verifyNoMoreInteractions(resourceLoader, resource, okmanyTipusMapper);
    }

    @Test
    public void okmanyTipusokThrowsExceptionOnResourceError() throws IOException {
        // given
        when(resourceLoader.getResource(PATH)).thenReturn(resource);
        when(resource.getInputStream()).thenThrow(IO_EXCEPTION);

        // when
        thrown.expect(is(IO_EXCEPTION));
        application.okmanyTipusok(PATH, okmanyTipusMapper, resourceLoader);

        // then
        verify(resourceLoader).getResource(PATH);
        verify(resource).getInputStream();
        verifyNoMoreInteractions(resourceLoader, resource);
        verifyNoInteractions(okmanyTipusMapper);
    }

    @Test
    public void okmanyTipusokThrowsExceptionOnInvalidJson() throws IOException {
        // given
        when(resourceLoader.getResource(PATH)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);
        when(okmanyTipusMapper.readToCollection(inputStream)).thenThrow(IO_EXCEPTION);

        // when
        thrown.expect(is(IO_EXCEPTION));
        application.okmanyTipusok(PATH, okmanyTipusMapper, resourceLoader);

        // then
        verify(resourceLoader).getResource(PATH);
        verify(resource).getInputStream();
        verify(okmanyTipusMapper).readToCollection(inputStream);
        verifyNoMoreInteractions(resourceLoader, resource, okmanyTipusMapper);
    }

    @Test
    public void allampolgarsagMapperReturnsValidInstance() {
        AllampolgarsagMapper allampolgarsagMapper = application.allampolgarsagMapper(objectMapper);
        assertThat(allampolgarsagMapper).isNotNull();
    }

    @Test
    public void allampolgarsagokReturnsValidCollection() throws IOException {
        // given
        Set<Allampolgarsag> allampolgarsagok = new HashSet<>();
        allampolgarsagok.add(new Allampolgarsag("HUN", "MAGYARORSZÁG ÁLLAMPOLGÁRA"));
        when(resourceLoader.getResource(PATH)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);
        when(allampolgarsagMapper.readToCollection(inputStream)).thenReturn(allampolgarsagok);

        // when
        Collection<Allampolgarsag> collection = application.allampolgarsagok(PATH, allampolgarsagMapper, resourceLoader);

        // then
        assertThat(collection).isEqualTo(allampolgarsagok);
        verify(resourceLoader).getResource(PATH);
        verify(resource).getInputStream();
        verify(allampolgarsagMapper).readToCollection(inputStream);
        verifyNoMoreInteractions(resourceLoader, resource, allampolgarsagMapper);
    }

    @Test
    public void allampolgarsagokThrowsExceptionOnResourceError() throws IOException {
        // given
        when(resourceLoader.getResource(PATH)).thenReturn(resource);
        when(resource.getInputStream()).thenThrow(IO_EXCEPTION);

        // when
        thrown.expect(is(IO_EXCEPTION));
        application.allampolgarsagok(PATH, allampolgarsagMapper, resourceLoader);

        // then
        verify(resourceLoader).getResource(PATH);
        verify(resource).getInputStream();
        verifyNoMoreInteractions(resourceLoader, resource);
        verifyNoInteractions(allampolgarsagMapper);
    }

    @Test
    public void allampolgarsagokThrowsExceptionOnInvalidJson() throws IOException {
        // given
        when(resourceLoader.getResource(PATH)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);
        when(allampolgarsagMapper.readToCollection(inputStream)).thenThrow(IO_EXCEPTION);

        // when
        thrown.expect(is(IO_EXCEPTION));
        application.allampolgarsagok(PATH, allampolgarsagMapper, resourceLoader);

        // then
        verify(resourceLoader).getResource(PATH);
        verify(resource).getInputStream();
        verify(allampolgarsagMapper).readToCollection(inputStream);
        verifyNoMoreInteractions(resourceLoader, resource, allampolgarsagMapper);
    }

    @Test
    public void objectMapperReturnsValidInstance() {
        ObjectMapper objectMapper = application.objectMapper();
        assertThat(objectMapper).has(dateFormat("yyyy-MM-dd"));
        assertThat(objectMapper).is(indentingOutput());
    }

    private Condition<ObjectMapper> dateFormat(String format) {
        DateFormat expectedFormat = new SimpleDateFormat(format);
        return new Condition<ObjectMapper>() {
            @Override
            public boolean matches(ObjectMapper value) {
                return value.getDateFormat().equals(expectedFormat);
            }
        };
    }

    private Condition<ObjectMapper> indentingOutput() {
        return new Condition<ObjectMapper>() {
            @Override
            public boolean matches(ObjectMapper value) {
                return value.isEnabled(SerializationFeature.INDENT_OUTPUT);
            }
        };
    }
}
