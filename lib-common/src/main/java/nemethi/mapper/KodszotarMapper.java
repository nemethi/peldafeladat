package nemethi.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Objects;

public interface KodszotarMapper<T> {

    Collection<T> readToCollection(InputStream inputStream) throws IOException;

    default JsonNode getRowsFromJson(InputStream inputStream, ObjectMapper mapper, String kodszotarTipus) throws IOException {
        JsonNode jsonDict = mapper.readTree(Objects.requireNonNull(inputStream, "inputStream"));
        JsonNode rows = jsonDict.get("rows");
        if (rows == null || rows.isNull() || !rows.isArray()) {
            throw new IOException(String.format("Érvénytelen formátumú %s kódszótár", kodszotarTipus));
        }
        return rows;
    }
}
