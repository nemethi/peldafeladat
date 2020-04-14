package nemethi.okmany;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class OkmanyTipusTest {

    private static final int KOD = 0;
    private static final String ERTEK = "value";

    private OkmanyTipus okmanyTipus;

    @Before
    public void setUp() {
        okmanyTipus = new OkmanyTipus(KOD, ERTEK);
    }

    @Test
    public void constructorAndGettersWork() {
        assertThat(okmanyTipus).isNotNull();
        assertThat(okmanyTipus.getKod()).isEqualTo(KOD);
        assertThat(okmanyTipus.getErtek()).isEqualTo(ERTEK);
    }

    @Test
    public void equalsIsReflexive() {
        assertThat(okmanyTipus.equals(okmanyTipus)).isTrue();
    }

    @Test
    public void equalsReturnsFalseOnNull() {
        assertThat(okmanyTipus.equals(null)).isFalse();
    }

    @Test
    public void equalsReturnsFalseOnDifferentClass() {
        assertThat(okmanyTipus.equals(new Object())).isFalse();
    }

    @Test
    public void equalsReturnsFalseOnDifferentKod() {
        assertThat(okmanyTipus.equals(new OkmanyTipus(1, ERTEK))).isFalse();
    }

    @Test
    public void equalsReturnsFalseOnDifferentErtek() {
        assertThat(okmanyTipus.equals(new OkmanyTipus(KOD, "different"))).isFalse();
    }

    @Test
    public void equalsReturnsTrueOnMatchingKodAndErtek() {
        assertThat(okmanyTipus.equals(new OkmanyTipus(KOD, ERTEK))).isTrue();
    }

    @Test
    public void equalObjectsHaveSameHashCode() {
        OkmanyTipus tipus = new OkmanyTipus(KOD, ERTEK);
        assertThat(okmanyTipus.hashCode()).isEqualTo(tipus.hashCode());
    }

    @Test
    public void toStringContainsTheFieldNamesAndValues() {
        String string = okmanyTipus.toString();
        assertThat(string).contains("kod", String.valueOf(KOD), "ertek", ERTEK);
    }
}
