package nemethi.okmany.validation;

import validation.Validator;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class OkmanyErvenyessegValidator implements Validator<Date> {

    private final Clock clock;

    public OkmanyErvenyessegValidator(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @Override
    public List<String> validate(Date target) {
        LocalDate expirationDate = convertToLocalDate(target);
        if (expirationDate.isBefore(now())) {
            return Collections.singletonList("Az okmány érvényessége lejárt");
        }
        return Collections.emptyList();
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private LocalDate now() {
        return Instant.now(clock).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
