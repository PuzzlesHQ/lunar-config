package dev.puzzleshq.lunar.sources;

import java.util.List;

public interface BackingConfig {

    void set(List<String> path, Object value);
    <T> T get(List<String> path);
    void remove(List<String> path);
    boolean hasKey(List<String> path);

    void setComment(List<String> path, String value);
    String getComment(List<String> path);
    void removeComment(List<String> path);
    boolean hasComment(List<String> path);

    default void save() {}
    default void load() {}

    default <T> T getOrElse(List<String> path, T defaultValue) {
        T v = get(path);
        return v == null ? defaultValue : v;
    }

    default String getCommentOrElse(List<String> path, String defaultComment) {
        String comment = getComment(path);
        return comment == null ? defaultComment : comment;
    }
}