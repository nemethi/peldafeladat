package nemethi.okmany;

import com.mycompany.mavenproject1.OkmanyDTO;
import nemethi.response.OkmanyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import nemethi.validation.Validator;

import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class OkmanyController {

    private final Validator<OkmanyDTO> okmanyValidator;

    @Autowired
    public OkmanyController(Validator<OkmanyDTO> okmanyValidator) {
        this.okmanyValidator = Objects.requireNonNull(okmanyValidator, "okmanyValidator");
    }

    @PostMapping(path = "/validate",
            consumes = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE},
            produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<OkmanyResponse> validateOkmany(@RequestBody OkmanyDTO okmany) {
        List<String> errors = okmanyValidator.validate(okmany);
        HttpStatus status;
        if (errors.isEmpty()) {
            status = OK;
        } else {
            status = UNPROCESSABLE_ENTITY;
        }
        OkmanyResponse response = new OkmanyResponse(okmany, errors);
        return new ResponseEntity<>(response, status);
    }

}
