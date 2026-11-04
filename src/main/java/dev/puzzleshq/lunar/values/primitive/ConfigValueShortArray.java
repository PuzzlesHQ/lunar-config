package dev.puzzleshq.lunar.values.primitive;

import dev.puzzleshq.lunar.values.ConfigValue;

import java.util.ArrayList;
import java.util.List;

public class ConfigValueShortArray extends ConfigValue<short[]> {

    private int min;
    private int max;

    protected ConfigValueShortArray(String key, short[] defaultValue, int min, int max) {
        super(key, defaultValue);
        this.min = min;
        this.max = max;
        validate(defaultValue);
    }

    protected <V> V validate(Object value) {
        if (!(value instanceof short[]))
            throw new IllegalArgumentException("Failed to validate value '" + value + "' at path '" + String.join(".", path) + "'; '" + defaultValue().getClass().getName() + "' != '" + value.getClass().getName() + "'");
        short[] v = (short[]) value;
        if (v.length < min)
            throw new IllegalStateException("'" + String.join(".", path) + "' Value is shorter than expected " + v.length + " < " + min);
        if (v.length > max)
            throw new IllegalStateException("'" + String.join(".", path) + "' Value is longer than expected " + v.length + " > " + max);
        return (V) value;
    }

    public ConfigValueShortArray min(int min) {
        this.min = min;
        return this;
    }

    public ConfigValueShortArray max(int max) {
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
    public void set(short[] value) {
        if (spec() == null)
            throw new IllegalStateException("Cannot get value when spec is not built!");

        cachedValue = validate(value);
        List<Integer> list = new ArrayList<>();
        for (short b : value) list.add((int) b);
        spec().getBaseConfig().set(path, list);
    }

    @Override
    public short[] get() {
        if (spec() == null)
            throw new IllegalStateException("Cannot get value when spec is not built!");

        if (cachedValue != null)
            return cachedValue;

        List<Number> v = spec().getBaseConfig().get(path);
        if (v == null) return defaultValue();
        int[] array = v.stream().mapToInt(Number::shortValue).toArray();
        short[] outArray = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            outArray[i] = (short) (array[i] & 0xFFFF);
        }

        return cachedValue = outArray;
    }

}
