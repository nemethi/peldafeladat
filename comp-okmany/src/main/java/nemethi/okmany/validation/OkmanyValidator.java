package nemethi.okmany.validation;

import com.mycompany.mavenproject1.OkmanyDTO;
import nemethi.validation.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static nemethi.util.ByteUtils.bytesToList;

public class OkmanyValidator implements Validator<OkmanyDTO> {

    private final Validator<OkmanyDTO> szamValidator;
    private final Validator<List<Byte>> kepValidator;
    private final Validator<Date> ervenyessegValidator;

    public OkmanyValidator(Validator<OkmanyDTO> szamValidator, Validator<List<Byte>> kepValidator,
                           Validator<Date> ervenyessegValidator) {
        this.szamValidator = Objects.requireNonNull(szamValidator, "szamValidator");
        this.kepValidator = Objects.requireNonNull(kepValidator, "kepValidator");
        this.ervenyessegValidator = Objects.requireNonNull(ervenyessegValidator, "ervenyessegValidator");
    }

    @Override
    public List<String> validate(OkmanyDTO target) {
        List<String> errors = new ArrayList<>();
        errors.addAll(szamValidator.validate(target));
        errors.addAll(validateKep(target));
        errors.addAll(validateErvenyesseg(target));
        return errors;
    }

    private List<String> validateKep(OkmanyDTO target) {
        byte[] okmanyKep = target.getOkmanyKep();
        if (okmanyKep == null || okmanyKep.length == 0) {
            return Collections.singletonList("Hiányzó okmánykép");
        } else if (okmanyKep.length == 1) {
            return Collections.singletonList("Hibás, olvashatatlan kép");
        } else {
            List<Byte> byteList = bytesToList(okmanyKep);
            return kepValidator.validate(byteList);
        }
    }

    private List<String> validateErvenyesseg(OkmanyDTO target) {
        if (target.getLejarDat() == null) {
            target.setErvenyes(false);
            return Collections.singletonList("Hiányzó lejárati idő");
        } else {
            List<String> dateErrors = ervenyessegValidator.validate(target.getLejarDat());
            target.setErvenyes(dateErrors.isEmpty());
            return dateErrors;
        }
    }
}
