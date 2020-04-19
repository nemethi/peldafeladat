package nemethi.szemely.validation;

import validation.Validator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SzemelyNemValidator implements Validator<String> {

    private final Collection<String> sexes;

    public SzemelyNemValidator(Collection<String> sexes) {
        this.sexes = Objects.requireNonNull(sexes, "sexes");
    }

    @Override
    public List<String> validate(String sex) {
        if (sexes.contains(sex)) {
            return Collections.emptyList();
        }
        return Collections.singletonList("Érvénytelen nem");
    }
}
