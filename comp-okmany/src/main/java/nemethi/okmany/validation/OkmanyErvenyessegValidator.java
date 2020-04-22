package nemethi.okmany.validation;

import nemethi.validation.Validator;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static java.time.LocalDate.now;
import static nemethi.util.DateUtils.dateToLocalDate;

public class OkmanyErvenyessegValidator implements Validator<Date> {

    private final Clock clock;

    public OkmanyErvenyessegValidator(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @Override
    public List<String> validate(Date target) {
        LocalDate expirationDate = dateToLocalDate(target);
        if (expirationDate.isBefore(now(clock))) {
            return Collections.singletonList("Az okmány érvényessége lejárt");
        }
        return Collections.emptyList();
    }
}
