package jbyoshi.sponge.pickaxe;

import java.lang.reflect.Array;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class ToArrayCollector {
    private ToArrayCollector() {
    }

    public static <E> Collector<E, ?, E[]> of(Class<E> elementType) {
        return Collectors.collectingAndThen(Collectors.<E>toList(), list ->
                list.toArray((E[]) Array.newInstance(elementType, list.size())));
    }
}
