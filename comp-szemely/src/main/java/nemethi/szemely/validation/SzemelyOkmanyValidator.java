package nemethi.szemely.validation;

import com.mycompany.mavenproject1.OkmanyDTO;
import model.OkmanyTipus;
import nemethi.response.OkmanyResponse;
import nemethi.szemely.OkmanyServiceClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SzemelyOkmanyValidator implements ValidationTargetModifier<List<OkmanyDTO>> {

    private static final String NOT_UNIQUE_OKMANY_MESSAGE = "Több, mint 1 érvényes %s";
    private final OkmanyServiceClient client;
    private final Collection<OkmanyTipus> okmanyTipusok;
    private final Set<String> validOkmanyTipusok;
    private final List<OkmanyDTO> validatedOkmanyList;
    private boolean validateInvoked;

    public SzemelyOkmanyValidator(OkmanyServiceClient client, Collection<OkmanyTipus> okmanyTipusok) {
        this.client = Objects.requireNonNull(client, "client");
        this.okmanyTipusok = Objects.requireNonNull(okmanyTipusok, "okmanyTipusok");
        validOkmanyTipusok = new HashSet<>();
        validatedOkmanyList = new ArrayList<>();
    }

    @Override
    public List<String> validate(List<OkmanyDTO> target) {
        List<String> errors = new ArrayList<>();
        for (OkmanyDTO okmany : target) {
            try {
                errors.addAll(validateOkmany(okmany));
            } catch (IOException e) {
                errors.add(String.format("Nem sikerült validálni a(z) %s számú okmányt", okmany.getOkmanySzam()));
                validatedOkmanyList.add(okmany);
            }
        }
        validateInvoked = true;
        return errors;
    }

    @Override
    public List<OkmanyDTO> getModifiedTarget() {
        if (validateInvoked) {
            return new ArrayList<>(validatedOkmanyList);
        }
        throw new IllegalStateException("This method must be invoked after validate()");
    }

    private List<String> validateOkmany(OkmanyDTO okmany) throws IOException {
        List<String> errors = new ArrayList<>();
        OkmanyResponse response = client.sendOkmany(okmany);
        validatedOkmanyList.add(response.getOkmany());
        List<String> okmanyErrors = response.getErrors();
        if (okmanyErrors.isEmpty()) {
            errors.addAll(checkIfOkmanyIsUnique(okmany));
        } else {
            errors.addAll(okmanyErrors);
        }
        return errors;
    }

    private List<String> checkIfOkmanyIsUnique(OkmanyDTO okmany) {
        String okmTipus = okmany.getOkmTipus();
        if (!validOkmanyTipusok.add(okmTipus)) {
            OkmanyTipus tipus = OkmanyTipus.getTipusByKod(okmanyTipusok, okmTipus);
            if (tipus != null) {
                return Collections.singletonList(String.format(NOT_UNIQUE_OKMANY_MESSAGE, tipus.getErtek()));
            }
            return Collections.singletonList(String.format(NOT_UNIQUE_OKMANY_MESSAGE, "igazolvány ugyanabból a típusból"));
        }
        return Collections.emptyList();
    }
}
