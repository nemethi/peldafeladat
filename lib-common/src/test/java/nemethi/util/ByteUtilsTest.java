package nemethi.util;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ByteUtilsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void listToBytes() {
        // given
        List<Byte> list = Arrays.asList((byte) 0, (byte) 1);

        // when
        byte[] bytes = ByteUtils.listToBytes(list);

        // then
        assertThat(bytes).hasSize(list.size());
        assertThat(bytes).containsExactly((byte) 0, (byte) 1);
    }

    @Test
    public void listToBytesWithEmptyList() {
        // when
        byte[] bytes = ByteUtils.listToBytes(Collections.emptyList());

        // then
        assertThat(bytes).isEmpty();
    }

    @Test
    public void listToBytesWithNullList() {
        // when
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("list");
        ByteUtils.listToBytes(null);
    }

    @Test
    public void bytesToList() {
        // given
        byte[] bytes = new byte[]{0, 1};

        // when
        List<Byte> list = ByteUtils.bytesToList(bytes);

        // then
        assertThat(list).hasSize(bytes.length);
        assertThat(list).containsExactly((byte) 0, (byte) 1);
    }

    @Test
    public void bytesToListWithEmptyArray() {
        // when
        List<Byte> list = ByteUtils.bytesToList(new byte[0]);

        // then
        assertThat(list).isEmpty();
    }

    @Test
    public void bytesToListWithNullArray() {
        // when
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("bytes");
        ByteUtils.bytesToList(null);
    }
}
