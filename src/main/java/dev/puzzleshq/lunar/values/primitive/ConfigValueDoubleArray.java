package dev.puzzleshq.lunar.values.primitive;

import dev.puzzleshq.lunar.values.ConfigValue;

import java.util.ArrayList;
import java.util.List;

public class ConfigValueDoubleArray extends ConfigValue<double[]> {

    private int min;
    private int max;

    protected ConfigValueDoubleArray(String key, double[] defaultValue, int min, int max) {
        super(key, defaultValue);
        this.min = min;
        this.max = max;
        validate(defaultValue);
    }

    protected <V> V validate(Object value) {
        if (!(value instanceof double[]))
            throw new IllegalArgumentException("Failed to validate value '" + value + "' at path '" + String.join(".", path) + "'; '" + defaultValue().getClass().getName() + "' != '" + value.getClass().getName() + "'");
        double[] v = (double[]) value;
        if (v.length < min)
            throw new IllegalStateException("'" + String.join(".", path) + "' Value is shorter than expected " + v.length + " < " + min);
        if (v.length > max)
            throw new IllegalStateException("'" + String.join(".", path) + "' Value is longer than expected " + v.length + " > " + max);
        return (V) value;
    }

    public ConfigValueDoubleArray min(int min) {
        this.min = min;
        return this;
    }

    public ConfigValueDoubleArray max(int max) {
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
    public void set(double[] value) {
        if (spec() == null)
            throw new IllegalStateException("Cannot get value when spec is not built!");

        cachedValue = validate(value);
        List<Double> list = new ArrayList<>();
        for (double b : value) list.add(b);
        spec().getBaseConfig().set(path, list);
    }

    @Override
    public double[] get() {
        if (spec() == null)
            throw new IllegalStateException("Cannot get value when spec is not built!");

        if (cachedValue != null)
            return cachedValue;

        List<Number> v = spec().getBaseConfig().get(path);
        if (v == null) return defaultValue();
        Number[] array = (Number[]) v.toArray();
        double[] outArray = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            outArray[i] = array[i].doubleValue();
        }

        return cachedValue = outArray;
    }
}
