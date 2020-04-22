package nemethi.okmany.validation;

import com.mycompany.mavenproject1.OkmanyDTO;
import nemethi.model.OkmanyTipus;
import nemethi.validation.Validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class OkmanySzamValidator implements Validator<OkmanyDTO> {

    private static final String SZEM_IG_PATTERN = "\\d{6}[A-Za-z]{2}";
    private static final String UTLEVEL_PATTERN = "[A-Za-z]{2}\\d{7}";
    public static final String INVALID_NUMBER_MESSAGE = "Érvénytelen %s szám";

    private final Collection<OkmanyTipus> okmanyTipusok;

    public OkmanySzamValidator(Collection<OkmanyTipus> okmanyTipusok) {
        this.okmanyTipusok = Objects.requireNonNull(okmanyTipusok, "okmanyTipusok");
    }

    @Override
    public List<String> validate(OkmanyDTO target) {
        List<String> errors = new ArrayList<>();
        OkmanyTipus tipus = OkmanyTipus.getTipusByKod(okmanyTipusok, target.getOkmTipus());
        if (tipus == null) {
            errors.add("Ismeretlen okmánytípus");
            tipus = new OkmanyTipus(Integer.MAX_VALUE, "Ismeretlen okmány");
        }
        errors.addAll(validateOkmanySzam(target, tipus));
        return errors;
    }

    private List<String> validateOkmanySzam(OkmanyDTO target, OkmanyTipus tipus) {
        String okmanySzam = target.getOkmanySzam();
        okmanySzam = okmanySzam == null ? "" : okmanySzam.trim();
        if (tipus.getKod() == 1) {
            return validateOkmanySzamAgainstPattern(okmanySzam, tipus.getErtek(), SZEM_IG_PATTERN);
        } else if (tipus.getKod() == 2) {
            return validateOkmanySzamAgainstPattern(okmanySzam, tipus.getErtek(), UTLEVEL_PATTERN);
        } else {
            return validateEgyebOkmany(okmanySzam, tipus);
        }
    }

    private List<String> validateEgyebOkmany(String okmanySzam, OkmanyTipus tipus) {
        int length = okmanySzam.length();
        if (length < 1 || length > 10) {
            return Collections.singletonList(String.format(INVALID_NUMBER_MESSAGE, tipus.getErtek()));
        }
        return Collections.emptyList();
    }

    private List<String> validateOkmanySzamAgainstPattern(String okmanySzam, String okmanyNev, String pattern) {
        if (!okmanySzam.matches(pattern)) {
            return Collections.singletonList(String.format(INVALID_NUMBER_MESSAGE, okmanyNev));
        }
        return Collections.emptyList();
    }
}
