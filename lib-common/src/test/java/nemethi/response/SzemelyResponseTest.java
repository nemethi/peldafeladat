package nemethi.response;

import com.mycompany.mavenproject1.SzemelyDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SzemelyResponseTest {

    private static final SzemelyDTO SZEMELY = new SzemelyDTO();
    private static final List<String> ERRORS = Collections.singletonList("error");
    private SzemelyResponse response;

    @Before
    public void setUp() {
        response = new SzemelyResponse(SZEMELY, ERRORS);
    }

    @Test
    public void constructorAndGettersWork() {
        assertThat(response).isNotNull();
        assertThat(response.getSzemely()).isEqualTo(SZEMELY);
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
    public void equalsReturnsFalseOnDifferentSzemely() {
        SzemelyResponse differentSzemely = new SzemelyResponse(new SzemelyDTO(), ERRORS);
        assertThat(response.equals(differentSzemely)).isFalse();
    }

    @Test
    public void equalsReturnsFalseOnDifferentErrors() {
        SzemelyResponse differentErrors = new SzemelyResponse(SZEMELY, Collections.singletonList("different"));
        assertThat(response.equals(differentErrors)).isFalse();
    }

    @Test
    public void equalObjectsHaveSameHashCode() {
        SzemelyResponse szemelyResponse = new SzemelyResponse(SZEMELY, ERRORS);
        assertThat(response.hashCode()).isEqualTo(szemelyResponse.hashCode());
    }

    @Test
    public void toStringContainsTheFieldNamesAndValues() {
        String string = response.toString();
        assertThat(string).contains("szemely", SZEMELY.toString(), "errors", ERRORS.toString());
    }
}
