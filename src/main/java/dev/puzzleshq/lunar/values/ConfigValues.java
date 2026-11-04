package dev.puzzleshq.lunar.values;

import dev.puzzleshq.lunar.values.primitive.ConfigValuePrimitives;

import java.util.Arrays;
import java.util.List;

public class ConfigValues extends ConfigValuePrimitives {

    public static <T> ConfigValueList<T> list(T[] defaultValue, int min, int max) {
        return new ConfigValueList<>(null, Arrays.asList(defaultValue), min, max);
    }

    public static <T> ConfigValueList<T> list(List<T> defaultValue, int min, int max) {
        return new ConfigValueList<>(null, defaultValue, min, max);
    }

    public static <T extends Comparable<T>> ConfigValueRange<T> range(T defaultValue, T min, T max) {
        return new ConfigValueRange<>(null, defaultValue, min, max);
    }

    public static <T> ConfigValue<T> value(T defaultValue) {
        return new ConfigValue<>(null, defaultValue);
    }

}
