package nemethi.szemely.validation;

import nemethi.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SzemelyNevValidator implements Validator<String> {

    private static final String NEV_PATTERN = "([a-zA-ZäáéíóöőúüűÄÁÉÍÓÖŐÚÜŰ./'\\-]+) ( ?([a-zA-ZäáéíóöőúüűÄÁÉÍÓÖŐÚÜŰ./'\\-]+))+";
    private static final String INVALID_NAME_MESSAGE = "Érvénytelen %s név";
    private static final String TOO_LONG_NAME_MESSAGE = "Túl hosszú %s név";
    private final String nameType;

    public SzemelyNevValidator(String nameType) {
        this.nameType = Objects.requireNonNull(nameType, "nameType");
    }

    @Override
    public List<String> validate(String name) {
        List<String> errors = new ArrayList<>();
        name = removeDrPrefixAndSuffix(name);
        if (!name.matches(NEV_PATTERN)) {
            errors.add(String.format(INVALID_NAME_MESSAGE, nameType));
        }
        if (name.length() > 80) {
            errors.add(String.format(TOO_LONG_NAME_MESSAGE, nameType));
        }
        return errors;
    }

    private String removeDrPrefixAndSuffix(String name) {
        name = name.trim();
        if (name.regionMatches(true, 0, "dr.", 0, 3)) {
            name = name.substring(3);
        }

        if (name.regionMatches(true, name.length() - 3, "dr.", 0, 3)) {
            name = name.substring(0, name.length() - 3);
        }

        return name.trim();
    }
}
