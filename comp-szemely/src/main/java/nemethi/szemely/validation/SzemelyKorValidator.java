package nemethi.szemely.validation;

import nemethi.validation.Validator;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static java.time.LocalDate.now;
import static nemethi.util.DateUtils.dateToLocalDate;

public class SzemelyKorValidator implements Validator<Date> {

    private final Clock clock;
    private final int minAge;
    private final int maxAge;

    public SzemelyKorValidator(Clock clock, int minAge, int maxAge) {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    @Override
    public List<String> validate(Date target) {
        LocalDate birthDate = dateToLocalDate(target);
        int age = Period.between(birthDate, now(clock)).getYears();
        List<String> errors = new ArrayList<>();
        if (age < minAge || age > maxAge) {
            errors.add("Érvénytelen születési idő");
        }
        return errors;
    }
}
