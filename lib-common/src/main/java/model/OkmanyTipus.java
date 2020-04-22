package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Objects;

public class OkmanyTipus {

    private final int kod;
    private final String ertek;

    @JsonCreator
    public OkmanyTipus(@JsonProperty("kod") int kod, @JsonProperty("ertek") String ertek) {
        this.kod = kod;
        this.ertek = ertek;
    }

    @JsonGetter("kod")
    public int getKod() {
        return kod;
    }

    @JsonGetter("ertek")
    public String getErtek() {
        return ertek;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OkmanyTipus that = (OkmanyTipus) o;
        return Objects.equals(kod, that.kod) &&
                Objects.equals(ertek, that.ertek);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kod, ertek);
    }

    @Override
    public String toString() {
        return "OkmanyTipus{" +
                "kod='" + kod + '\'' +
                ", ertek='" + ertek + '\'' +
                '}';
    }

    public static OkmanyTipus getTipusByKod(Collection<? extends OkmanyTipus> collection, String kod) {
        int code;
        try {
            code = Integer.parseInt(kod);
        } catch (NumberFormatException e) {
            return null;
        }
        for (OkmanyTipus tipus : collection) {
            if (tipus.getKod() == code) {
                return tipus;
            }
        }
        return null;
    }
}
