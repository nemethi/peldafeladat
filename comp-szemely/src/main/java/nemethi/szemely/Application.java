package nemethi.szemely;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.mavenproject1.OkmanyDTO;
import com.mycompany.mavenproject1.SzemelyDTO;
import nemethi.mapper.AllampolgarsagMapper;
import nemethi.mapper.AllampolgarsagSetMapper;
import nemethi.mapper.OkmanyTipusMapper;
import nemethi.mapper.OkmanyTipusSetMapper;
import nemethi.model.Allampolgarsag;
import nemethi.model.OkmanyTipus;
import nemethi.szemely.validation.NameTypeValidator;
import nemethi.szemely.validation.SzemelyAllampolgarsagValidator;
import nemethi.szemely.validation.SzemelyKorValidator;
import nemethi.szemely.validation.SzemelyNemValidator;
import nemethi.szemely.validation.SzemelyNevValidator;
import nemethi.szemely.validation.SzemelyOkmanyValidator;
import nemethi.szemely.validation.SzemelyValidator;
import nemethi.szemely.validation.ValidationTargetModifier;
import nemethi.validation.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.time.Clock;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class Application extends SpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Validator<SzemelyDTO> szemelyValidator(NameTypeValidator<String, String> nevValidator,
                                                  Validator<Date> korValidator,
                                                  Validator<String> nemValidator,
                                                  Validator<String> allampolgarsagValidator,
                                                  ValidationTargetModifier<List<OkmanyDTO>> okmanyListValidator,
                                                  Collection<Allampolgarsag> allampolgarsagCollection) {
        return new SzemelyValidator(nevValidator, korValidator, nemValidator, allampolgarsagValidator,
                okmanyListValidator, allampolgarsagCollection);
    }

    @Bean
    public NameTypeValidator<String, String> nevValidator() {
        return new SzemelyNevValidator("viselt");
    }

    @Bean
    public Validator<Date> korValidator(Clock clock,
                                        @Value("${szemely.age.min:18}") int minAge,
                                        @Value("${szemely.age.max:120}") int maxAge) {
        return new SzemelyKorValidator(clock, minAge, maxAge);
    }

    @Bean
    public Validator<String> nemValidator(@Value("${szemely.nemek:F,N}") Set<String> nemek) {
        return new SzemelyNemValidator(nemek);
    }

    @Bean
    public Validator<String> allampolgarsagValidator(Collection<Allampolgarsag> allampolgarsagCollection) {
        return new SzemelyAllampolgarsagValidator(allampolgarsagCollection);
    }

    @Bean
    public ValidationTargetModifier<List<OkmanyDTO>> okmanyListValidator(OkmanyServiceClient okmanyServiceClient,
                                                                         Collection<OkmanyTipus> okmanyTipusok) {
        return new SzemelyOkmanyValidator(okmanyServiceClient, okmanyTipusok);
    }

    @Bean
    public OkmanyServiceClient okmanyServiceClient(@Value("${okmanyservice.uri}") URI uri, RestTemplate restTemplate, ObjectMapper objectMapper) {
        return new OkmanyServiceClient(uri, restTemplate, objectMapper);
    }

    @Bean
    public RestTemplate restTemplate(MappingJackson2HttpMessageConverter messageConverter) {
        return new RestTemplateBuilder()
                .messageConverters(messageConverter)
                .build();
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
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

    @Bean
    public Collection<Allampolgarsag> allampolgarsagok(@Value("${allampolgarsagok.path}") String path,
                                                       AllampolgarsagMapper allampolgarsagMapper,
                                                       ResourceLoader resourceLoader) throws IOException {
        Resource resource = resourceLoader.getResource(path);
        return allampolgarsagMapper.readToCollection(resource.getInputStream());
    }

    @Bean
    public AllampolgarsagMapper allampolgarsagMapper(ObjectMapper objectMapper) {
        return new AllampolgarsagSetMapper(objectMapper);
    }

    @Bean
    public MappingJackson2HttpMessageConverter messageConverter(ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new Jackson2ObjectMapperBuilder()
                .simpleDateFormat("yyyy-MM-dd")
                .indentOutput(true)
                .build();
    }
}
