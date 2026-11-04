package dev.puzzleshq.lunar.values.primitive;

import dev.puzzleshq.lunar.values.ConfigValue;

import java.util.ArrayList;
import java.util.List;

public class ConfigValueByteArray extends ConfigValue<byte[]> {

    private int min;
    private int max;

    protected ConfigValueByteArray(String key, byte[] defaultValue, int min, int max) {
        super(key, defaultValue);
        this.min = min;
        this.max = max;
        validate(defaultValue);
    }

    protected <V> V validate(Object value) {
        if (!(value instanceof byte[]))
            throw new IllegalArgumentException("Failed to validate value '" + value + "' at path '" + String.join(".", path) + "'; '" + defaultValue().getClass().getName() + "' != '" + value.getClass().getName() + "'");
        byte[] v = (byte[]) value;
        if (v.length < min)
            throw new IllegalStateException("'" + String.join(".", path) + "' Value is shorter than expected " + v.length + " < " + min);
        if (v.length > max)
            throw new IllegalStateException("'" + String.join(".", path) + "' Value is longer than expected " + v.length + " > " + max);
        return (V) value;
    }

    public ConfigValueByteArray min(int min) {
        this.min = min;
        return this;
    }

    public ConfigValueByteArray max(int max) {
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
    public void set(byte[] value) {
        if (spec() == null)
            throw new IllegalStateException("Cannot get value when spec is not built!");

        cachedValue = validate(value);
        List<Integer> list = new ArrayList<>();
        for (byte b : value) list.add((int) b);
        spec().getBaseConfig().set(path, list);
    }

    @Override
    public byte[] get() {
        if (spec() == null)
            throw new IllegalStateException("Cannot get value when spec is not built!");

        if (cachedValue != null)
            return cachedValue;

        List<Number> v = spec().getBaseConfig().get(path);
        if (v == null) return defaultValue();
        int[] array = v.stream().mapToInt(Number::byteValue).toArray();
        byte[] outArray = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            outArray[i] = (byte) (array[i] & 0xFF);
        }

        return cachedValue = outArray;
    }
}
