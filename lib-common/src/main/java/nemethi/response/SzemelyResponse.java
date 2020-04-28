package nemethi.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mycompany.mavenproject1.SzemelyDTO;

import java.util.List;
import java.util.Objects;

public class SzemelyResponse {

    private final SzemelyDTO szemely;
    private final List<String> errors;

    @JsonCreator
    public SzemelyResponse(@JsonProperty("szemely") SzemelyDTO szemely,
                           @JsonProperty("hibak") List<String> errors) {
        this.szemely = szemely;
        this.errors = errors;
    }

    @JsonGetter("szemely")
    public SzemelyDTO getSzemely() {
        return szemely;
    }

    @JsonGetter("hibak")
    public List<String> getErrors() {
        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SzemelyResponse that = (SzemelyResponse) o;
        return Objects.equals(szemely, that.szemely) &&
                Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(szemely, errors);
    }

    @Override
    public String toString() {
        return "SzemelyResponse{" +
                "szemely=" + szemely +
                ", errors=" + errors +
                '}';
    }
}
