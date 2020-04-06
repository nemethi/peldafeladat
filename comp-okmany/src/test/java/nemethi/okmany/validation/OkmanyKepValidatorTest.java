package nemethi.okmany.validation;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static util.ByteUtils.bytesToList;

public class OkmanyKepValidatorTest {

    private static final byte[] VALID_JPEG_IMAGE = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xD9};
    private static final byte[] INVALID_HEADER_IMAGE = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xD9};
    private static final byte[] INVALID_FOOTER_IMAGE = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0x00, (byte) 0x00};

    private OkmanyKepValidator validator;

    @Mock
    private ImageConverter imageConverter;
    @Mock
    private BufferedImage image;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
        validator = new OkmanyKepValidator(imageConverter);
    }

    @Test
    public void imageConverterCannotBeNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("imageConverter");
        validator = new OkmanyKepValidator(null);
    }

    @Test
    public void validateReturnsEmptyListOnValidImage() throws IOException {
        // given
        when(imageConverter.convertBytesToImage(VALID_JPEG_IMAGE)).thenReturn(image);
        when(image.getWidth()).thenReturn(827);
        when(image.getHeight()).thenReturn(1063);

        // when
        List<String> errors = validator.validate(bytesToList(VALID_JPEG_IMAGE));

        // then
        assertThat(errors).isEmpty();
        verify(imageConverter).convertBytesToImage(VALID_JPEG_IMAGE);
        verify(image).getWidth();
        verify(image).getHeight();
        verifyNoMoreInteractions(imageConverter, image);
    }

    @Test
    public void validateReturnsErrorListOnInvalidImageFormat() {
        // when
        List<String> errors = validator.validate(bytesToList(INVALID_HEADER_IMAGE));

        // then
        assertThat(errors).hasSize(1);
        assertThat(errors).containsExactly("A kép nem JPEG típusú");

        // when
        errors = validator.validate(bytesToList(INVALID_FOOTER_IMAGE));

        // then
        assertThat(errors).hasSize(1);
        assertThat(errors).containsExactly("A kép nem JPEG típusú");
        verifyNoInteractions(imageConverter, image);
    }

    @Test
    public void validateReturnsErrorListOnInvalidImageWidth() throws IOException {
        // given
        when(imageConverter.convertBytesToImage(VALID_JPEG_IMAGE)).thenReturn(image);
        when(image.getWidth()).thenReturn(1);

        // when
        List<String> errors = validator.validate(bytesToList(VALID_JPEG_IMAGE));

        // then
        assertThat(errors).hasSize(1);
        assertThat(errors).containsExactly("A kép mérete nem 827x1063");
        verify(imageConverter).convertBytesToImage(VALID_JPEG_IMAGE);
        verify(image).getWidth();
        verify(image, never()).getHeight();
        verifyNoMoreInteractions(imageConverter, image);
    }

    @Test
    public void validateReturnsErrorListOnInvalidImageHeight() throws IOException {
        // given
        when(imageConverter.convertBytesToImage(VALID_JPEG_IMAGE)).thenReturn(image);
        when(image.getWidth()).thenReturn(827);
        when(image.getHeight()).thenReturn(1);

        // when
        List<String> errors = validator.validate(bytesToList(VALID_JPEG_IMAGE));

        // then
        assertThat(errors).hasSize(1);
        assertThat(errors).containsExactly("A kép mérete nem 827x1063");
        verify(imageConverter).convertBytesToImage(VALID_JPEG_IMAGE);
        verify(image).getWidth();
        verify(image).getHeight();
        verifyNoMoreInteractions(imageConverter, image);
    }

    @Test
    public void validateReturnsErrorListOnImageConversionError() throws IOException {
        // when
        when(imageConverter.convertBytesToImage(VALID_JPEG_IMAGE)).thenThrow(new IOException("Error"));

        // when
        List<String> errors = validator.validate(bytesToList(VALID_JPEG_IMAGE));

        // then
        assertThat(errors).hasSize(1);
        assertThat(errors).containsExactly("Hibás, olvashatatlan kép");
        verify(imageConverter).convertBytesToImage(VALID_JPEG_IMAGE);
        verifyNoMoreInteractions(imageConverter);
        verifyNoInteractions(image);
    }
}
