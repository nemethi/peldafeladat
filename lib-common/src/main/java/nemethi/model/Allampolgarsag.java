package nemethi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Allampolgarsag {

    private final String kod;
    private final String allampolg;

    @JsonCreator
    public Allampolgarsag(@JsonProperty("kod") String kod, @JsonProperty("allampolgarsag") String allampolg) {
        this.kod = kod;
        this.allampolg = allampolg;
    }

    @JsonGetter("kod")
    public String getKod() {
        return kod;
    }

    @JsonGetter("allampolgarsag")
    public String getAllampolg() {
        return allampolg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Allampolgarsag that = (Allampolgarsag) o;
        return Objects.equals(kod, that.kod) &&
                Objects.equals(allampolg, that.allampolg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kod, allampolg);
    }

    @Override
    public String toString() {
        return "Allampolgarsag{" +
                "kod='" + kod + '\'' +
                ", allampolg='" + allampolg + '\'' +
                '}';
    }
}
