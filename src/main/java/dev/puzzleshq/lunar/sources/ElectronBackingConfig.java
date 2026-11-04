package dev.puzzleshq.lunar.sources;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;

import java.util.List;

public class ElectronBackingConfig implements BackingConfig {

    private final Config config;

    public ElectronBackingConfig(
            Config config
    ) {
        this.config = config;
    }

    @Override
    public void set(List<String> path, Object value) {
        config.set(path, value);
    }

    @Override
    public <T> T get(List<String> path) {
        return config.get(path);
    }

    @Override
    public void remove(List<String> path) {
        config.remove(path);
    }

    @Override
    public boolean hasKey(List<String> path) {
        return config.contains(path);
    }

    @Override
    public void setComment(List<String> path, String value) {
        if (config instanceof CommentedConfig)
            ((CommentedConfig) config).setComment(path, value);
    }

    @Override
    public String getComment(List<String> path) {
        if (config instanceof CommentedConfig)
            return ((CommentedConfig) config).getComment(path);
        return null;
    }

    @Override
    public void removeComment(List<String> path) {
        if (config instanceof CommentedConfig)
            ((CommentedConfig) config).removeComment(path);
    }

    @Override
    public boolean hasComment(List<String> path) {
        if (config instanceof CommentedConfig)
            return ((CommentedConfig) config).containsComment(path);
        return false;
    }

    @Override
    public void save() {
        if (config instanceof FileConfig)
            ((FileConfig) config).save();
    }

    @Override
    public void load() {
        if (config instanceof FileConfig)
            ((FileConfig) config).load();
    }
}
