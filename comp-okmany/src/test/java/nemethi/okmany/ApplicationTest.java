package nemethi.okmany;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.mavenproject1.OkmanyDTO;
import nemethi.mapper.OkmanyTipusMapper;
import nemethi.mapper.OkmanyTipusSetMapper;
import nemethi.model.OkmanyTipus;
import nemethi.okmany.validation.ImageConverter;
import nemethi.validation.Validator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ApplicationTest {

    public static final String PATH = "/test/path";

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
        Validator<OkmanyDTO> szamValidator = application.szamValidator(Collections.emptyList());
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
        assertThat(objectMapper).isNotNull();
    }

    @Test
    public void okmanyTipusMapperReturnsValidInstance() {
        OkmanyTipusMapper okmanyTipusMapper = application.okmanyTipusMapper(objectMapper);
        assertThat(okmanyTipusMapper).isNotNull();
        assertThat(okmanyTipusMapper).isInstanceOf(OkmanyTipusSetMapper.class);
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
}
