package nemethi.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nemethi.model.OkmanyTipus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class OkmanyTipusSetMapper implements OkmanyTipusMapper {

    private final ObjectMapper mapper;

    public OkmanyTipusSetMapper(ObjectMapper mapper) {
        this.mapper = Objects.requireNonNull(mapper, "mapper");
    }

    @Override
    public Collection<OkmanyTipus> readToCollection(InputStream inputStream) throws IOException {
        Set<OkmanyTipus> okmanyTipusok = new HashSet<>();
        JsonNode rows = getRowsFromJson(inputStream, mapper, "okmánytípus");
        for (JsonNode row : rows) {
            String kod = row.get("kod").textValue();
            String ertek = row.get("ertek").textValue();
            okmanyTipusok.add(new OkmanyTipus(Integer.parseInt(kod), ertek));
        }
        return okmanyTipusok;
    }
}
