package dev.puzzleshq.lunar.values.primitive;

import dev.puzzleshq.lunar.values.ConfigValue;

import java.util.ArrayList;
import java.util.List;

public class ConfigValueFloatArray extends ConfigValue<float[]> {

    private int min;
    private int max;

    protected ConfigValueFloatArray(String key, float[] defaultValue, int min, int max) {
        super(key, defaultValue);
        this.min = min;
        this.max = max;
        validate(defaultValue);
    }

    protected <V> V validate(Object value) {
        if (!(value instanceof float[]))
            throw new IllegalArgumentException("Failed to validate value '" + value + "' at path '" + String.join(".", path) + "'; '" + defaultValue().getClass().getName() + "' != '" + value.getClass().getName() + "'");
        float[] v = (float[]) value;
        if (v.length < min)
            throw new IllegalStateException("'" + String.join(".", path) + "' Value is shorter than expected " + v.length + " < " + min);
        if (v.length > max)
            throw new IllegalStateException("'" + String.join(".", path) + "' Value is longer than expected " + v.length + " > " + max);
        return (V) value;
    }

    public ConfigValueFloatArray min(int min) {
        this.min = min;
        return this;
    }

    public ConfigValueFloatArray max(int max) {
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
    public void set(float[] value) {
        if (spec() == null)
            throw new IllegalStateException("Cannot get value when spec is not built!");

        cachedValue = validate(value);
        List<Float> list = new ArrayList<>();
        for (float b : value) list.add(b);
        spec().getBaseConfig().set(path, list);
    }

    @Override
    public float[] get() {
        if (spec() == null)
            throw new IllegalStateException("Cannot get value when spec is not built!");

        if (cachedValue != null)
            return cachedValue;

        List<Number> v = spec().getBaseConfig().get(path);
        if (v == null) return defaultValue();
        Number[] array = (Number[]) v.toArray();
        float[] outArray = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            outArray[i] = array[i].floatValue();
        }

        return cachedValue = outArray;
    }
}
