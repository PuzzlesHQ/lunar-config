package dev.puzzleshq.lunar.values;

import dev.puzzleshq.lunar.PuzzleConfigSpec;

import java.util.List;

public class ConfigValue<T> {

    private final StringBuilder commentBuilder;
    private String key;
    private final T defaultValue;
    private boolean hasComment;
    protected List<String> path;
    private PuzzleConfigSpec spec;
    protected volatile T cachedValue;

    protected ConfigValue(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.commentBuilder = new StringBuilder();
    }

    public void clearCache() {
        cachedValue = null;
    }

    public void key(String key) {
        this.key = key;
    }

    public void spec(PuzzleConfigSpec spec) {
        this.spec = spec;
    }

    public PuzzleConfigSpec spec() {
        return spec;
    }

    public void path(List<String> path) {
        this.path = path;
    }

    public List<String> path() {
        return path;
    }

    public ConfigValue<T> resetComments() {
        hasComment = false;
        commentBuilder.delete(0, commentBuilder.length());

        if (spec != null)
            spec.getBaseConfig().removeComment(path);
        return this;
    }

    public ConfigValue<T> comment(String comment) {
        hasComment = true;
        if (commentBuilder.length() != 0 && commentBuilder.charAt(commentBuilder.length() - 1) != '\n')
            commentBuilder.append('\n');
        commentBuilder.append(comment);

        if (spec != null)
            spec.getBaseConfig().setComment(path, comment);
        return this;
    }

    public ConfigValue<T> comment(String... comments) {
        hasComment = true;
        if (commentBuilder.length() != 0 && commentBuilder.charAt(commentBuilder.length() - 1) != '\n')
            commentBuilder.append('\n');
        for (int i = 0; i < comments.length; i++) {
            commentBuilder.append(comments[i]);
            if (i < comments.length - 1) commentBuilder.append("\n");
        }
        if (spec != null)
            spec.getBaseConfig().setComment(path, comment());

        return this;
    }

    public boolean hasComment() {
        return hasComment;
    }

    public String comment() {
        return commentBuilder.toString();
    }

    public String key() {
        return key;
    }

    public T defaultValue() {
        return defaultValue;
    }

    public void set(T value) {
        if (spec == null)
            throw new IllegalStateException("Cannot get value when spec is not built!");

        spec.getBaseConfig().set(path, cachedValue = validate(value));
    }

    protected <V> V validate(Object value) {
        if (!defaultValue.getClass().isInstance(value))
            throw new IllegalArgumentException("Failed to validate value '" + value + "' at path '" + String.join(".", path) + "'");
        return (V) value;
    }

    public T get() {
        if (spec == null)
            throw new IllegalStateException("Cannot get value when spec is not built!");

        if (cachedValue != null)
            return cachedValue;

        Object value = spec.getBaseConfig().getOrElse(path, defaultValue);
        return cachedValue = validate(value);
    }

    public boolean exist() {
        if (spec == null)
            throw new IllegalStateException("Cannot check if value exist when spec is not built!");

        return spec.getBaseConfig().hasKey(path);
    }

    public boolean commentExist() {
        if (spec == null)
            throw new IllegalStateException("Cannot check if comment exist when spec is not built!");

        return spec.getBaseConfig().hasComment(path);
    }

    public void setDefault() {
        set(defaultValue);
    }

    public String langKey() {
        if (spec == null)
            throw new IllegalStateException("Cannot get lang key when spec is not built!");

        return spec().getTranslationKey(key);
    }

    public void langKey(String value) {
        if (spec == null)
            throw new IllegalStateException("Cannot set lang key when spec is not built!");

        spec().setTranslationKey(key, value);
    }

}
