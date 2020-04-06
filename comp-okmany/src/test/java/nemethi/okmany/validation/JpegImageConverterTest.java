package nemethi.okmany.validation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Spy;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class JpegImageConverterTest {

    private static final IOException IO_EXCEPTION = new IOException("Error");
    private static final byte[] BYTES = new byte[0];
    private static final int IMAGE_INDEX = 0;

    @Spy
    private JpegImageConverter imageConverter;
    @Mock
    private ImageReader reader;
    @Mock
    private ImageInputStream inputStream;
    @Mock
    private ImageReadParam param;
    @Mock
    private BufferedImage expectedImage;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void getImageReaderDoesNotReturnNull() {
        ImageReader imageReader = imageConverter.getImageReader();
        assertThat(imageReader).isNotNull();
    }

    @Test
    public void createImageInputStreamDoesNotReturnNull() throws IOException {
        ImageInputStream inputStream = imageConverter.createImageInputStream(new ByteArrayInputStream(BYTES));
        assertThat(inputStream).isNotNull();
    }

    @Test
    public void convertBytesToImage() throws IOException {
        // given
        doReturn(reader).when(imageConverter).getImageReader();
        doReturn(inputStream).when(imageConverter).createImageInputStream(any());
        when(reader.getDefaultReadParam()).thenReturn(param);
        when(reader.read(IMAGE_INDEX, param)).thenReturn(expectedImage);

        // when
        BufferedImage image = imageConverter.convertBytesToImage(BYTES);

        // then
        assertThat(image).isEqualTo(expectedImage);
        verify(reader).setInput(inputStream, true);
        verify(reader).getDefaultReadParam();
        verify(reader).read(IMAGE_INDEX, param);
        verifyNoMoreInteractions(reader);
    }

    @Test
    public void convertBytesToImageInputStreamException() throws IOException {
        // given
        doThrow(IO_EXCEPTION).when(imageConverter).createImageInputStream(any());

        // when
        thrown.expect(is(IO_EXCEPTION));
        imageConverter.convertBytesToImage(BYTES);
    }

    @Test
    public void convertBytesToImageReaderException() throws IOException {
        // given
        doReturn(reader).when(imageConverter).getImageReader();
        doReturn(inputStream).when(imageConverter).createImageInputStream(any());
        when(reader.getDefaultReadParam()).thenReturn(param);
        when(reader.read(IMAGE_INDEX, param)).thenThrow(IO_EXCEPTION);

        // when
        thrown.expect(is(IO_EXCEPTION));
        imageConverter.convertBytesToImage(BYTES);

        // then
        verify(reader).setInput(inputStream, true);
        verify(reader).getDefaultReadParam();
        verify(reader).read(IMAGE_INDEX, param);
        verifyNoMoreInteractions(reader);
    }
}
