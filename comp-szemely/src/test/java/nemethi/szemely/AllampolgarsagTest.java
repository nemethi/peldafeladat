package nemethi.szemely;

import nemethi.model.Allampolgarsag;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AllampolgarsagTest {

    private static final String KOD = "kod";
    private static final String ALLAMPOLG = "allampolgarsag";

    private Allampolgarsag allampolgarsag;

    @Before
    public void setUp() {
        allampolgarsag = new Allampolgarsag(KOD, ALLAMPOLG);
    }

    @Test
    public void constructorsAndGettersWork() {
        assertThat(allampolgarsag).isNotNull();
        assertThat(allampolgarsag.getKod()).isEqualTo(KOD);
        assertThat(allampolgarsag.getAllampolg()).isEqualTo(ALLAMPOLG);
    }

    @Test
    public void equalsIsReflexive() {
        assertThat(allampolgarsag.equals(allampolgarsag)).isTrue();
    }

    @Test
    public void equalsReturnsFalseOnNull() {
        assertThat(allampolgarsag.equals(null)).isFalse();
    }

    @Test
    public void equalsReturnsFalseOnDifferentClass() {
        assertThat(allampolgarsag.equals(new Object())).isFalse();
    }

    @Test
    public void equalsReturnsFalseOnDifferentKod() {
        Allampolgarsag different = new Allampolgarsag("different", ALLAMPOLG);
        assertThat(allampolgarsag.equals(different)).isFalse();
    }

    @Test
    public void equalsReturnsFalseOnDifferentAllampolg() {
        Allampolgarsag different = new Allampolgarsag(KOD, "different");
        assertThat(allampolgarsag.equals(different)).isFalse();
    }

    @Test
    public void equalsReturnsTrueOnMatchingKodAndAllampolg() {
        Allampolgarsag matching = new Allampolgarsag(KOD, ALLAMPOLG);
        assertThat(allampolgarsag.equals(matching)).isTrue();
    }

    @Test
    public void equalObjectsHaveSameHashCode() {
        Allampolgarsag matching = new Allampolgarsag(KOD, ALLAMPOLG);
        assertThat(allampolgarsag.hashCode()).isEqualTo(matching.hashCode());
    }

    @Test
    public void toStringContainsTheFieldNamesAndValues() {
        String string = allampolgarsag.toString();
        assertThat(string).contains("kod", KOD, "allampolg", ALLAMPOLG);
    }
}
