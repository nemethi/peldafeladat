package nemethi.util;

import java.util.Collection;

public final class CollectionUtils {

    private CollectionUtils() {}

    public static void requireNonEmpty(Collection<?> collection, String message) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
}
