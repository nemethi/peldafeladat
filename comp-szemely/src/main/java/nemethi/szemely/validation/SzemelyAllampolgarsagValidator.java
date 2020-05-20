package nemethi.szemely.validation;

import nemethi.model.Allampolgarsag;
import nemethi.util.CollectionUtils;
import nemethi.validation.Validator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SzemelyAllampolgarsagValidator implements Validator<String> {

    private final Collection<Allampolgarsag> allampolgarsagCollection;

    public SzemelyAllampolgarsagValidator(Collection<Allampolgarsag> collection) {
        this.allampolgarsagCollection = Objects.requireNonNull(collection, "collection");
        CollectionUtils.requireNonEmpty(this.allampolgarsagCollection, "empty collection");
    }

    @Override
    public List<String> validate(String allampolg) {
        for (Allampolgarsag allampolgarsag : allampolgarsagCollection) {
            if (allampolg.equals(allampolgarsag.getKod())) {
                return Collections.emptyList();
            }
        }
        return Collections.singletonList("Érvénytelen állampolgárság");
    }
}
