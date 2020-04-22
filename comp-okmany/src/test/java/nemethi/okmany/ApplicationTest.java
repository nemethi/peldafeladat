package nemethi.okmany;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.TextNode;
import com.mycompany.mavenproject1.OkmanyDTO;
import model.OkmanyTipus;
import nemethi.okmany.validation.ImageConverter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import validation.Validator;

import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
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
    private JsonNode jsonNode;
    @Mock
    private TextNode kodNode;
    @Mock
    private TextNode ertekNode;
    @Mock
    private ArrayNode arrayNode;
    @Mock
    private InputStream inputStream;
    @Mock
    private Iterator<JsonNode> iterator;

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
    public void okmanyTipusokReturnsValidCollectionOnValidJson() throws IOException {
        // given
        String kod = "1";
        String ertek = "value";
        when(resourceLoader.getResource(PATH)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readTree(inputStream)).thenReturn(jsonNode);
        when(jsonNode.get("rows")).thenReturn(arrayNode);
        when(arrayNode.getNodeType()).thenReturn(JsonNodeType.ARRAY);
        when(arrayNode.isArray()).thenReturn(true);
        when(arrayNode.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true).thenReturn(false);
        when(iterator.next()).thenReturn(jsonNode);
        when(jsonNode.get("kod")).thenReturn(kodNode);
        when(kodNode.textValue()).thenReturn(kod);
        when(jsonNode.get("ertek")).thenReturn(ertekNode);
        when(ertekNode.textValue()).thenReturn(ertek);

        // when
        Collection<OkmanyTipus> okmanyTipusok = application.okmanyTipusok(PATH, objectMapper, resourceLoader);

        // then
        assertThat(okmanyTipusok).hasSize(1);
        assertThat(okmanyTipusok).containsExactly(new OkmanyTipus(Integer.parseInt(kod), ertek));
        verify(resourceLoader).getResource(PATH);
        verify(resource).getInputStream();
        verify(objectMapper).readTree(inputStream);
        verify(jsonNode).get("rows");
        verify(arrayNode).getNodeType();
        verify(arrayNode).isArray();
        verify(arrayNode).iterator();
        verify(iterator, times(2)).hasNext();
        verify(iterator).next();
        verify(jsonNode).get("kod");
        verify(kodNode).textValue();
        verify(jsonNode).get("ertek");
        verify(ertekNode).textValue();
        verifyNoMoreInteractions(resourceLoader, resource, objectMapper,
                jsonNode, arrayNode, iterator, kodNode, ertekNode);
    }

    @Test
    public void okmanyTipusokThrowsExceptionOnInvalidFile() throws IOException {
        // given
        when(resourceLoader.getResource(PATH)).thenReturn(resource);
        when(resource.getInputStream()).thenThrow(new IOException("Error"));

        // when
        thrown.expect(IOException.class);
        thrown.expectMessage("Error");
        application.okmanyTipusok(PATH, objectMapper, resourceLoader);

        // then
        verify(resourceLoader).getResource(PATH);
        verify(resource).getInputStream();
        verifyNoMoreInteractions(resourceLoader, resource);
    }

    @Test
    public void okmanyTipusokThrowsExceptionOnInvalidJson() throws IOException {
        // given
        when(resourceLoader.getResource(PATH)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readTree(inputStream)).thenThrow(new IOException("Error"));

        // when
        thrown.expect(IOException.class);
        thrown.expectMessage("Error");
        application.okmanyTipusok(PATH, objectMapper, resourceLoader);

        // then
        verify(resourceLoader).getResource(PATH);
        verify(resource).getInputStream();
        verify(objectMapper).readTree(inputStream);
        verifyNoMoreInteractions(resourceLoader, resource, objectMapper);
    }

    @Test
    public void okmanyTipusokThrowsExceptionOnNullRows() throws IOException {
        // given
        when(resourceLoader.getResource(PATH)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readTree(inputStream)).thenReturn(jsonNode);
        when(jsonNode.get("rows")).thenReturn(null);

        // when
        thrown.expect(IOException.class);
        thrown.expectMessage("Érvénytelen formátumú okmánytípus kódszótár");
        application.okmanyTipusok(PATH, objectMapper, resourceLoader);

        // then
        verify(resourceLoader).getResource(PATH);
        verify(resource).getInputStream();
        verify(objectMapper).readTree(inputStream);
        verify(jsonNode).get("rows");
        verifyNoMoreInteractions(resourceLoader, resource, objectMapper, jsonNode);
    }

    @Test
    public void okmanyTipusokThrowsExceptionOnNullNode() throws IOException {
        // given
        when(resourceLoader.getResource(PATH)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readTree(inputStream)).thenReturn(jsonNode);
        when(jsonNode.get("rows")).thenReturn(jsonNode);
        when(jsonNode.getNodeType()).thenReturn(JsonNodeType.NULL);

        // when
        thrown.expect(IOException.class);
        thrown.expectMessage("Érvénytelen formátumú okmánytípus kódszótár");
        application.okmanyTipusok(PATH, objectMapper, resourceLoader);

        // then
        verify(resourceLoader).getResource(PATH);
        verify(resource).getInputStream();
        verify(objectMapper).readTree(inputStream);
        verify(jsonNode).get("rows");
        verify(jsonNode).getNodeType();
        verifyNoMoreInteractions(resourceLoader, resource, objectMapper, jsonNode);
    }

    @Test
    public void okmanyTipusokThrowsExceptionOnMissingArray() throws IOException {
        // given
        when(resourceLoader.getResource(PATH)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readTree(inputStream)).thenReturn(jsonNode);
        when(jsonNode.get("rows")).thenReturn(jsonNode);
        when(jsonNode.getNodeType()).thenReturn(JsonNodeType.STRING);
        when(jsonNode.isArray()).thenReturn(false);

        // when
        thrown.expect(IOException.class);
        thrown.expectMessage("Érvénytelen formátumú okmánytípus kódszótár");
        application.okmanyTipusok(PATH, objectMapper, resourceLoader);

        // then
        verify(resourceLoader).getResource(PATH);
        verify(resource).getInputStream();
        verify(objectMapper).readTree(inputStream);
        verify(jsonNode).get("rows");
        verify(jsonNode).getNodeType();
        verify(jsonNode).isArray();
        verifyNoMoreInteractions(resourceLoader, resource, objectMapper, jsonNode);
    }
}
