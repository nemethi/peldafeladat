package nemethi.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mycompany.mavenproject1.OkmanyDTO;

import java.util.List;
import java.util.Objects;

public class OkmanyResponse {

    private final OkmanyDTO okmany;
    private final List<String> errors;

    @JsonCreator
    public OkmanyResponse(@JsonProperty("okmany") OkmanyDTO okmany,
                          @JsonProperty("hibak") List<String> errors) {
        this.okmany = okmany;
        this.errors = errors;
    }

    @JsonGetter("okmany")
    public OkmanyDTO getOkmany() {
        return okmany;
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
        OkmanyResponse that = (OkmanyResponse) o;
        return Objects.equals(okmany, that.okmany) &&
                Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(okmany, errors);
    }

    @Override
    public String toString() {
        return "OkmanyResponse{" +
                "okmany=" + okmany +
                ", errors=" + errors +
                '}';
    }
}
