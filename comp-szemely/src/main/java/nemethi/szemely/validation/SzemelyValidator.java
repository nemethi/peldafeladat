package nemethi.szemely.validation;

import com.mycompany.mavenproject1.OkmanyDTO;
import com.mycompany.mavenproject1.SzemelyDTO;
import nemethi.model.Allampolgarsag;
import nemethi.validation.Validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SzemelyValidator implements Validator<SzemelyDTO> {

    private static final String VISELT_NAME_TYPE = "viselt";
    private static final String SZULETESI_NAME_TYPE = "születési";
    private static final String ANYA_NAME_TYPE = "anya";
    private static final String MISSING_NAME_MESSAGE = "Hiányzó %s név";

    private final NameTypeValidator<String, String> nevValidator;
    private final Validator<Date> korValidator;
    private final Validator<String> nemValidator;
    private final Validator<String> allampolgarsagValidator;
    private final ValidationTargetModifier<List<OkmanyDTO>> okmanyListValidator;
    private final Collection<Allampolgarsag> allampolgarsagCollection;

    public SzemelyValidator(NameTypeValidator<String, String> nevValidator, Validator<Date> korValidator, Validator<String> nemValidator,
                            Validator<String> allampolgarsagValidator, ValidationTargetModifier<List<OkmanyDTO>> okmanyListValidator,
                            Collection<Allampolgarsag> allampolgarsagCollection) {

        this.nevValidator = Objects.requireNonNull(nevValidator, "nevValidator");
        this.korValidator = Objects.requireNonNull(korValidator, "korValidator");
        this.nemValidator = Objects.requireNonNull(nemValidator, "nemValidator");
        this.allampolgarsagValidator = Objects.requireNonNull(allampolgarsagValidator, "allampolgarsagValidator");
        this.okmanyListValidator = Objects.requireNonNull(okmanyListValidator, "okmanyListValidator");
        this.allampolgarsagCollection = Objects.requireNonNull(allampolgarsagCollection, "allampolgarsagCollection");
        requireNotEmpty(this.allampolgarsagCollection);
    }

    @Override
    public List<String> validate(SzemelyDTO szemely) {
        List<String> errors = new ArrayList<>();
        errors.addAll(validateNev(szemely.getVisNev(), VISELT_NAME_TYPE));
        errors.addAll(validateNev(szemely.getSzulNev(), SZULETESI_NAME_TYPE));
        errors.addAll(validateNev(szemely.getaNev(), ANYA_NAME_TYPE));
        errors.addAll(validateKor(szemely.getSzulDat()));
        errors.addAll(validateNem(szemely.getNeme()));
        errors.addAll(validateAllampolgarsag(szemely));
        errors.addAll(validateOkmanyList(szemely));
        return errors;
    }

    private List<String> validateNev(String nev, String nameType) {
        if (nev == null || nev.isEmpty()) {
            return Collections.singletonList(String.format(MISSING_NAME_MESSAGE, nameType));
        }
        nevValidator.setNameType(nameType);
        return nevValidator.validate(nev);
    }

    private List<String> validateKor(Date szuletesiDatum) {
        if (szuletesiDatum == null) {
            return Collections.singletonList("Hiányzó születési dátum");
        }
        return korValidator.validate(szuletesiDatum);
    }

    private List<String> validateNem(String nem) {
        if (nem == null || nem.isEmpty()) {
            return Collections.singletonList("Hiányzó nem");
        }
        return nemValidator.validate(nem);
    }

    private List<String> validateAllampolgarsag(SzemelyDTO szemely) {
        String allampKod = szemely.getAllampKod();
        if (allampKod == null || allampKod.isEmpty()) {
            return Collections.singletonList("Hiányzó állampolgárság");
        }
        List<String> errors = allampolgarsagValidator.validate(allampKod);
        if (errors.isEmpty()) {
            String allampDecoded = decodeAllampolgarsag(allampKod);
            szemely.setAllampDekod(allampDecoded);
        }
        return errors;
    }

    private String decodeAllampolgarsag(String allampKod) {
        for (Allampolgarsag allampolgarsag : allampolgarsagCollection) {
            if (allampKod.equals(allampolgarsag.getKod())) {
                return allampolgarsag.getAllampolg();
            }
        }
        return null;
    }

    private List<String> validateOkmanyList(SzemelyDTO szemely) {
        ArrayList<OkmanyDTO> okmanyLista = szemely.getOkmLista();
        if (okmanyLista == null || okmanyLista.isEmpty()) {
            return Collections.singletonList("Hiányzó okmánylista");
        }
        List<String> errors = okmanyListValidator.validate(okmanyLista);
        szemely.setOkmLista(new ArrayList<>(okmanyListValidator.getModifiedTarget()));
        return errors;
    }

    private void requireNotEmpty(Collection<?> collection) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("empty collection");
        }
    }
}
