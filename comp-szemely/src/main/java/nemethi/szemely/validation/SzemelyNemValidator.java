package nemethi.szemely.validation;

import nemethi.validation.Validator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SzemelyNemValidator implements Validator<String> {

    private final Collection<String> sexes;

    public SzemelyNemValidator(Collection<String> collection) {
        this.sexes = Objects.requireNonNull(collection, "collection");
    }

    @Override
    public List<String> validate(String sex) {
        if (sexes.contains(sex)) {
            return Collections.emptyList();
        }
        return Collections.singletonList("Érvénytelen nem");
    }
}
