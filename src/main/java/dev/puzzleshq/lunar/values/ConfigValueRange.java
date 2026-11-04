package dev.puzzleshq.lunar.values;

public class ConfigValueRange<T extends Comparable<T>> extends ConfigValue<T> {

    private T min;
    private T max;

    protected ConfigValueRange(String key, T defaultValue, T min, T max) {
        super(key, defaultValue);
        this.min = min;
        this.max = max;
    }

    public ConfigValueRange<T> min(T min) {
        this.min = min;
        return this;
    }

    public ConfigValueRange<T> max(T max) {
        this.max = max;
        return this;
    }

    public T min() {
        return min;
    }

    public T max() {
        return max;
    }

}
