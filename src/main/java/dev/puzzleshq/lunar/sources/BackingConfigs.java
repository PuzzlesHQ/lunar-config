package dev.puzzleshq.lunar.sources;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileConfigBuilder;
import com.electronwill.nightconfig.core.io.IoUtils;

import java.io.File;

public class BackingConfigs {

    public static ElectronBackingConfig of(Config config) {
        return new ElectronBackingConfig(config);
    }

    public static ElectronBackingConfig of(File file) {
        return of(file, false, true, true);
    }

    public static ElectronBackingConfig of(File file, boolean async, boolean autosave, boolean autoreload) {
        FileConfigBuilder builder = FileConfig.builder(file);
        if (!async) builder.sync();
        if (autoreload) builder.autoreload();
        if (autosave) builder.autosave();

        ElectronBackingConfig backing = null;

        Runnable loadHook = () -> {
            backing.getOnLoadHook().run();
        };

        Runnable saveHook = () -> {
            backing.getOnSaveHook().run();
        };

        builder.onSave(saveHook);
        builder.onLoad(loadHook);
        builder.onAutoReload(loadHook);
        builder.onAutoSave(saveHook);

        return new ElectronBackingConfig(builder.build());
    }

}
