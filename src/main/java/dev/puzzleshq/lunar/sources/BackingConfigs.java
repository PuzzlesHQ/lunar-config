package dev.puzzleshq.lunar.sources;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileConfigBuilder;

import java.io.File;

public class BackingConfigs {

    public static ElectronBackingConfig of(Config config) {
        return new ElectronBackingConfig(config);
    }

    public static ElectronBackingConfig of(File file) {
        return of(FileConfig.builder(file).sync().autoreload().autosave().build());
    }

    public static ElectronBackingConfig of(File file, boolean async, boolean autosave, boolean autoreload) {
        FileConfigBuilder builder = FileConfig.builder(file);
        if (!async) builder.sync();
        if (autoreload) builder.autoreload();
        if (autosave) builder.autosave();

        ElectronBackingConfig backing = null;

        builder.onSave(() -> {
            backing.getOnSaveHook().run();
        });
        builder.onLoad(() -> {
            backing.getOnLoadHook().run();
        });

        return new ElectronBackingConfig(builder.build());
    }

}
