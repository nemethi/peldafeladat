package nemethi.okmany.validation;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class JpegImageConverter extends ImageConverter {

    private static final String JPEG = "jpeg";

    ImageReader getImageReader() {
        return ImageIO.getImageReadersByFormatName(JPEG).next();
    }

    ImageInputStream createImageInputStream(ByteArrayInputStream bis) throws IOException {
        return ImageIO.createImageInputStream(bis);
    }
}
