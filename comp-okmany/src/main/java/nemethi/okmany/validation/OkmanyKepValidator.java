package nemethi.okmany.validation;

import validation.Validator;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static util.ByteUtils.listToBytes;

public class OkmanyKepValidator implements Validator<List<Byte>> {

    private static final byte[] JPEG_HEADER = new byte[]{(byte) 0xFF, (byte) 0xD8};
    private static final byte[] JPEG_FOOTER = new byte[]{(byte) 0xFF, (byte) 0xD9};
    private final ImageConverter imageConverter;

    public OkmanyKepValidator(ImageConverter imageConverter) {
        this.imageConverter = Objects.requireNonNull(imageConverter, "imageConverter");
    }

    @Override
    public List<String> validate(List<Byte> target) {
        byte[] bytes = listToBytes(target);
        List<String> errors = new ArrayList<>(validateJpegFormat(bytes));
        if (errors.isEmpty()) {
            errors.addAll(validateImageSize(bytes));
        }
        return errors;
    }

    private List<String> validateJpegFormat(byte[] bytes) {
        byte[] imageHeader = Arrays.copyOf(bytes, 2);
        byte[] imageFooter = Arrays.copyOfRange(bytes, bytes.length - 2, bytes.length);
        if (!Arrays.equals(imageHeader, JPEG_HEADER) || !Arrays.equals(imageFooter, JPEG_FOOTER)) {
            return Collections.singletonList("A kép nem JPEG típusú");
        }
        return Collections.emptyList();
    }

    private List<String> validateImageSize(byte[] bytes) {
        try {
            BufferedImage image = imageConverter.convertBytesToImage(bytes);
            if (image.getWidth() != 827 || image.getHeight() != 1063) {
                return Collections.singletonList("A kép mérete nem 827x1063");
            }
        } catch (IOException e) {
            return Collections.singletonList("Hibás, olvashatatlan kép");
        }
        return Collections.emptyList();
    }



}
