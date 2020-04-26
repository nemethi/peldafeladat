package nemethi.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.TextNode;
import nemethi.mapper.OkmanyTipusSetMapper;
import nemethi.model.OkmanyTipus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OkmanyTipusSetMapperTest {

    private static final String ERROR_MESSAGE = "Érvénytelen formátumú okmánytípus kódszótár";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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

    private OkmanyTipusSetMapper okmanyTipusMapper;

    @Before
    public void setUp() {
        initMocks(this);
        okmanyTipusMapper = new OkmanyTipusSetMapper(objectMapper);
    }

    @Test
    public void mapperCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("mapper");
        new OkmanyTipusSetMapper(null);
    }

    @Test
    public void readToCollectionReturnsValidCollectionOnValidJson() throws IOException {
        // given
        String kod = "1";
        String ertek = "value";
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
        Collection<OkmanyTipus> okmanyTipusok = okmanyTipusMapper.readToCollection(inputStream);

        // then
        assertThat(okmanyTipusok).containsExactly(new OkmanyTipus(Integer.parseInt(kod), ertek));
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
        verifyNoMoreInteractions(objectMapper, jsonNode, arrayNode, iterator, kodNode, ertekNode);
    }

    @Test
    public void readToCollectionThrowsExceptionOnInvalidJson() throws IOException {
        // given
        when(objectMapper.readTree(inputStream)).thenThrow(new IOException("Error"));

        // when
        thrown.expect(IOException.class);
        thrown.expectMessage("Error");
        okmanyTipusMapper.readToCollection(inputStream);

        // then
        verify(objectMapper).readTree(inputStream);
        verifyNoMoreInteractions(objectMapper);
    }

    @Test
    public void readToCollectionThrowsExceptionOnNullInputStream() throws IOException {
        // when
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("inputStream");
        okmanyTipusMapper.readToCollection(null);
    }

    @Test
    public void readToCollectionThrowsExceptionOnNullRows() throws IOException {
        // given
        when(objectMapper.readTree(inputStream)).thenReturn(jsonNode);
        when(jsonNode.get("rows")).thenReturn(null);

        // when
        thrown.expect(IOException.class);
        thrown.expectMessage(ERROR_MESSAGE);
        okmanyTipusMapper.readToCollection(inputStream);

        // then
        verify(objectMapper).readTree(inputStream);
        verify(jsonNode).get("rows");
        verifyNoMoreInteractions(objectMapper, jsonNode);
    }

    @Test
    public void readToCollectionThrowsExceptionOnNullNode() throws IOException {
        // given
        when(objectMapper.readTree(inputStream)).thenReturn(jsonNode);
        when(jsonNode.get("rows")).thenReturn(jsonNode);
        when(jsonNode.getNodeType()).thenReturn(JsonNodeType.NULL);

        // when
        thrown.expect(IOException.class);
        thrown.expectMessage(ERROR_MESSAGE);
        okmanyTipusMapper.readToCollection(inputStream);

        // then
        verify(objectMapper).readTree(inputStream);
        verify(jsonNode).get("rows");
        verify(jsonNode).getNodeType();
        verifyNoMoreInteractions(objectMapper, jsonNode);
    }

    @Test
    public void readToCollectionThrowsExceptionOnMissingArray() throws IOException {
        // given
        when(objectMapper.readTree(inputStream)).thenReturn(jsonNode);
        when(jsonNode.get("rows")).thenReturn(jsonNode);
        when(jsonNode.getNodeType()).thenReturn(JsonNodeType.STRING);
        when(jsonNode.isArray()).thenReturn(false);

        // when
        thrown.expect(IOException.class);
        thrown.expectMessage(ERROR_MESSAGE);
        okmanyTipusMapper.readToCollection(inputStream);

        // then
        verify(objectMapper).readTree(inputStream);
        verify(jsonNode).get("rows");
        verify(jsonNode).getNodeType();
        verify(jsonNode).isArray();
        verifyNoMoreInteractions(objectMapper, jsonNode);
    }
}
