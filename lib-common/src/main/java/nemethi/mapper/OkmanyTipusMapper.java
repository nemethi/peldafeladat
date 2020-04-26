package nemethi.mapper;

import nemethi.model.OkmanyTipus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public interface OkmanyTipusMapper {

    Collection<OkmanyTipus> readToCollection(InputStream inputStream) throws IOException;
}
