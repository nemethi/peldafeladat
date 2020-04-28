package nemethi.szemely;

import com.mycompany.mavenproject1.SzemelyDTO;
import nemethi.response.SzemelyResponse;
import nemethi.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class SzemelyController {

    private final Validator<SzemelyDTO> szemelyValidator;

    @Autowired
    public SzemelyController(Validator<SzemelyDTO> szemelyValidator) {
        this.szemelyValidator = Objects.requireNonNull(szemelyValidator, "szemelyValidator");
    }

    @PostMapping(path = "/validate",
            consumes = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE},
            produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<SzemelyResponse> validateSzemely(@RequestBody SzemelyDTO szemely) {
        List<String> errors = szemelyValidator.validate(szemely);
        HttpStatus status;
        if (errors.isEmpty()) {
            status = OK;
        } else {
            status = UNPROCESSABLE_ENTITY;
        }
        SzemelyResponse response = new SzemelyResponse(szemely, errors);
        return new ResponseEntity<>(response, status);
    }
}
