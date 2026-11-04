package dev.puzzleshq.lunar.values;

import java.util.List;

public class ConfigValueList<T> extends ConfigValue<List<T>> {

    private int min;
    private int max;

    protected ConfigValueList(String key, List<T> defaultValue, int min, int max) {
        super(key, defaultValue);
        this.min = min;
        this.max = max;
        validate(defaultValue);
    }

    protected <V> V validate(Object value) {
        if (!(value instanceof List))
            throw new IllegalArgumentException("Failed to validate value '" + value + "' at path '" + String.join(".", path) + "'; '" + defaultValue().getClass().getName() + "' != '" + value.getClass().getName() + "'");
        List v = (List) value;
        if (v.size() < min)
            throw new IllegalStateException("'" + String.join(".", path) + "' Value is shorter than expected " + v.size() + " < " + min);
        if (v.size() > max)
            throw new IllegalStateException("'" + String.join(".", path) + "' Value is longer than expected " + v.size() + " > " + max);
        return (V) value;
    }

    public ConfigValueList<T> min(int min) {
        this.min = min;
        return this;
    }

    public ConfigValueList<T> max(int max) {
        this.max = max;
        return this;
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

}
