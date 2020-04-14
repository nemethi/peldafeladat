package nemethi.response;

import com.mycompany.mavenproject1.OkmanyDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OkmanyResponseTest {

    private static final OkmanyDTO OKMANY = new OkmanyDTO();
    private static final List<String> ERRORS = Collections.singletonList("error");
    private OkmanyResponse response;

    @Before
    public void setUp() {
        response = new OkmanyResponse(OKMANY, ERRORS);
    }

    @Test
    public void constructorAndGettersWork() {
        assertThat(response).isNotNull();
        assertThat(response.getOkmany()).isEqualTo(OKMANY);
        assertThat(response.getErrors()).isEqualTo(ERRORS);
    }

    @Test
    public void equalsIsReflexive() {
        assertThat(response.equals(response)).isTrue();
    }

    @Test
    public void equalsReturnsFalseOnNull() {
        assertThat(response.equals(null)).isFalse();
    }

    @Test
    public void equalsReturnsFalseOnDifferentClass() {
        assertThat(response.equals(new Object())).isFalse();
    }

    @Test
    public void equalsReturnsFalseOnDifferentOkmany() {
        OkmanyResponse differentOkmany = new OkmanyResponse(new OkmanyDTO(), ERRORS);
        assertThat(response.equals(differentOkmany)).isFalse();
    }

    @Test
    public void equalsReturnsFalseOnDifferentErrors() {
        OkmanyResponse differentErrors = new OkmanyResponse(OKMANY, Collections.singletonList("different"));
        assertThat(response.equals(differentErrors)).isFalse();
    }

    @Test
    public void equalObjectsHaveSameHashCode() {
        OkmanyResponse okmanyResponse = new OkmanyResponse(OKMANY, ERRORS);
        assertThat(response.hashCode()).isEqualTo(okmanyResponse.hashCode());
    }

    @Test
    public void toStringContainsTheFieldNamesAndValues() {
        String string = response.toString();
        assertThat(string).contains("okmany", OKMANY.toString(), "errors", ERRORS.toString());
    }
}
