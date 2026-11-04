package dev.puzzleshq.lunar;

public class ConfigPair<T> {

    private final PuzzleConfigSpec spec;
    private final T config;

    public ConfigPair(PuzzleConfigSpec spec, T config) {
        this.spec = spec;
        this.config = config;
    }

    public PuzzleConfigSpec getSpec() {
        return spec;
    }

    public T getConfig() {
        return config;
    }

}
