package nemethi.okmany.validation;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public abstract class ImageConverter {

    private static final int IMAGE_INDEX = 0;

    public BufferedImage convertBytesToImage(byte[] bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ImageReader reader = getImageReader();
        ImageInputStream inputStream = createImageInputStream(bis);
        reader.setInput(inputStream, true);
        ImageReadParam param = reader.getDefaultReadParam();
        return reader.read(IMAGE_INDEX, param);
    }

    abstract ImageReader getImageReader();
    abstract ImageInputStream createImageInputStream(ByteArrayInputStream bis)  throws IOException;
}
