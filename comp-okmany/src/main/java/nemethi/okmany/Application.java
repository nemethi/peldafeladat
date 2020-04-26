package nemethi.okmany;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.mavenproject1.OkmanyDTO;
import nemethi.mapper.OkmanyTipusMapper;
import nemethi.mapper.OkmanyTipusSetMapper;
import nemethi.model.OkmanyTipus;
import nemethi.okmany.validation.ImageConverter;
import nemethi.okmany.validation.JpegImageConverter;
import nemethi.okmany.validation.OkmanyErvenyessegValidator;
import nemethi.okmany.validation.OkmanyKepValidator;
import nemethi.okmany.validation.OkmanySzamValidator;
import nemethi.okmany.validation.OkmanyValidator;
import nemethi.validation.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.time.Clock;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class Application extends SpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Validator<OkmanyDTO> okmanyValidator(Validator<OkmanyDTO> szamValidator,
                                                Validator<List<Byte>> kepValidator,
                                                Validator<Date> ervenyessegValidator) {
        return new OkmanyValidator(szamValidator, kepValidator, ervenyessegValidator);
    }

    @Bean
    public Validator<OkmanyDTO> szamValidator(Collection<OkmanyTipus> okmanyTipusok) {
        return new OkmanySzamValidator(okmanyTipusok);
    }

    @Bean
    public Validator<List<Byte>> kepValidator(ImageConverter imageConverter) {
        return new OkmanyKepValidator(imageConverter);
    }

    @Bean
    public Validator<Date> ervenyessegValidator(Clock clock) {
        return new OkmanyErvenyessegValidator(clock);
    }

    @Bean
    public ImageConverter imageConverter() {
        return new JpegImageConverter();
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new Jackson2ObjectMapperBuilder()
                .simpleDateFormat("yyyy-MM-dd")
                .indentOutput(true)
                .build();
    }

    @Bean
    public OkmanyTipusMapper okmanyTipusMapper(ObjectMapper objectMapper) {
        return new OkmanyTipusSetMapper(objectMapper);
    }

    @Bean
    public Collection<OkmanyTipus> okmanyTipusok(@Value("${okmanytipusok.path}") String path,
                                                 OkmanyTipusMapper okmanyTipusMapper,
                                                 ResourceLoader resourceLoader) throws IOException {
        Resource resource = resourceLoader.getResource(path);
        return okmanyTipusMapper.readToCollection(resource.getInputStream());
    }
}
