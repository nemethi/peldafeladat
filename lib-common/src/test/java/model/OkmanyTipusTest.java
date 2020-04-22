package model;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;


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

    @Test
    public void getTipusByKodReturnsFirstResult() {
        // given
        OkmanyTipus tipus1 = new OkmanyTipus(1, "value1");
        OkmanyTipus tipus2 = new OkmanyTipus(2, "value2");
        OkmanyTipus tipus1Duplicate = new OkmanyTipus(1, "value3");

        // when
        OkmanyTipus result = OkmanyTipus.getTipusByKod(list(tipus1, tipus2, tipus1Duplicate), "1");

        // then
        assertThat(result).isEqualTo(tipus1);
    }

    @Test
    public void getTipusByKodReturnsNullOnNoMatch() {
        // given
        OkmanyTipus tipus1 = new OkmanyTipus(1, "value1");
        OkmanyTipus tipus2 = new OkmanyTipus(2, "value2");

        // when
        OkmanyTipus result = OkmanyTipus.getTipusByKod(list(tipus1, tipus2), "3");

        // then
        assertThat(result).isNull();
    }

    @Test
    public void getTipusByKodReturnsNullOnNonNumericKod() {
        // given
        OkmanyTipus tipus = new OkmanyTipus(1, "value1");

        // when
        OkmanyTipus result = OkmanyTipus.getTipusByKod(list(tipus), "abc");

        // then
        assertThat(result).isNull();
    }

    @Test
    public void getTipusByKodReturnsNullOnNullKod() {
        // given
        OkmanyTipus tipus = new OkmanyTipus(1, "value1");

        // when
        OkmanyTipus result = OkmanyTipus.getTipusByKod(list(tipus), null);

        // then
        assertThat(result).isNull();
    }
}
