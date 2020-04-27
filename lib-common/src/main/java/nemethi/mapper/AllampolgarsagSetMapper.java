package nemethi.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nemethi.model.Allampolgarsag;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AllampolgarsagSetMapper implements AllampolgarsagMapper {

    private final ObjectMapper mapper;

    public AllampolgarsagSetMapper(ObjectMapper mapper) {
        this.mapper = Objects.requireNonNull(mapper, "mapper");
    }

    @Override
    public Collection<Allampolgarsag> readToCollection(InputStream inputStream) throws IOException {
        Set<Allampolgarsag> allampolgarsagok = new HashSet<>();
        JsonNode rows = getRowsFromJson(inputStream, mapper, "állampolgárság");
        for (JsonNode row : rows) {
            String kod = row.get("kod").textValue();
            String allampolgarsag = row.get("allampolgarsag").textValue();
            allampolgarsagok.add(new Allampolgarsag(kod, allampolgarsag));
        }
        return allampolgarsagok;
    }
}
