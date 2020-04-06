package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ByteUtils {

    private ByteUtils() {}

    public static byte[] listToBytes(List<Byte> list) {
        Objects.requireNonNull(list, "list");
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }

    public static List<Byte> bytesToList(byte[] bytes) {
        Objects.requireNonNull(bytes, "bytes");
        List<Byte> list = new ArrayList<>();
        for (byte b : bytes) {
            list.add(b);
        }
        return list;
    }
}
