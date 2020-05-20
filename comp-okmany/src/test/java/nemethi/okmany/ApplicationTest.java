package nemethi.okmany;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mycompany.mavenproject1.OkmanyDTO;
import nemethi.mapper.OkmanyTipusMapper;
import nemethi.model.OkmanyTipus;
import nemethi.okmany.validation.ImageConverter;
import nemethi.validation.Validator;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ApplicationTest {

    private static final String PATH = "/test/path";
    private static final IOException IO_EXCEPTION = new IOException("Error");

    private Application application;

    @Mock
    private Validator<OkmanyDTO> szamvalidator;
    @Mock
    private Validator<List<Byte>> kepValidator;
    @Mock
    private Validator<Date> ervenyessegValidator;
    @Mock
    private ImageConverter imageConverter;
    @Mock
    private ResourceLoader resourceLoader;
    @Mock
    private Resource resource;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private InputStream inputStream;
    @Mock
    private OkmanyTipusMapper okmanyTipusMapper;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
        application = new Application();
    }

    @Test
    public void okmanyValidatorReturnsValidInstance() {
        Validator<OkmanyDTO> okmanyValidator =
                application.okmanyValidator(szamvalidator, kepValidator, ervenyessegValidator);
        assertThat(okmanyValidator).isNotNull();
    }

    @Test
    public void szamValidatorReturnsValidInstance() {
        Validator<OkmanyDTO> szamValidator = application.szamValidator(
                Collections.singletonList(new OkmanyTipus(1,"value")));
        assertThat(szamValidator).isNotNull();
    }

    @Test
    public void kepvalidatorReturnsValidInstance() {
        Validator<List<Byte>> kepValidator = application.kepValidator(imageConverter);
        assertThat(kepValidator).isNotNull();
    }

    @Test
    public void ervenyessegValidatorReturnsValidInstance() {
        Validator<Date> ervenyessegValidator = application.ervenyessegValidator(Clock.systemDefaultZone());
        assertThat(ervenyessegValidator).isNotNull();
    }

    @Test
    public void imageConverterReturnsValidInstance() {
        ImageConverter imageConverter = application.imageConverter();
        assertThat(imageConverter).isNotNull();
    }

    @Test
    public void clockReturnsValidInstance() {
        Clock clock = application.clock();
        assertThat(clock).isNotNull();
    }

    @Test
    public void objectMapperReturnsValidInstance() {
        ObjectMapper objectMapper = application.objectMapper();
        assertThat(objectMapper).has(dateFormat("yyyy-MM-dd"));
        assertThat(objectMapper).is(indentingOutput());
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
