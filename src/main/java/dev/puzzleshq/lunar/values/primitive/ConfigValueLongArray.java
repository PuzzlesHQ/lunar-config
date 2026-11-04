package dev.puzzleshq.lunar.values.primitive;

import dev.puzzleshq.lunar.values.ConfigValue;

import java.util.ArrayList;
import java.util.List;

public class ConfigValueLongArray extends ConfigValue<long[]> {

    private int min;
    private int max;

    protected ConfigValueLongArray(String key, long[] defaultValue, int min, int max) {
        super(key, defaultValue);
        this.min = min;
        this.max = max;
        validate(defaultValue);
    }

    protected <V> V validate(Object value) {
        if (!(value instanceof long[]))
            throw new IllegalArgumentException("Failed to validate value '" + value + "' at path '" + String.join(".", path) + "'; '" + defaultValue().getClass().getName() + "' != '" + value.getClass().getName() + "'");
        long[] v = (long[]) value;
        if (v.length < min)
            throw new IllegalStateException("'" + String.join(".", path) + "' Value is shorter than expected " + v.length + " < " + min);
        if (v.length > max)
            throw new IllegalStateException("'" + String.join(".", path) + "' Value is longer than expected " + v.length + " > " + max);
        return (V) value;
    }

    public ConfigValueLongArray min(int min) {
        this.min = min;
        return this;
    }

    public ConfigValueLongArray max(int max) {
        this.max = max;
        return this;
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

    @Override
    public void set(long[] value) {
        if (spec() == null)
            throw new IllegalStateException("Cannot get value when spec is not built!");

        cachedValue = validate(value);
        List<Long> list = new ArrayList<>();
        for (long b : value) list.add(b);
        spec().getBaseConfig().set(path, list);
    }

    @Override
    public long[] get() {
        if (spec() == null)
            throw new IllegalStateException("Cannot get value when spec is not built!");

        if (cachedValue != null)
            return cachedValue;

        List<Number> v = spec().getBaseConfig().get(path);
        if (v == null) return defaultValue();
        long[] array = v.stream().mapToLong(Number::longValue).toArray();

        return cachedValue = array;
    }
}
