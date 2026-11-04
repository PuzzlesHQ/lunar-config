package dev.puzzleshq.lunar;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import dev.puzzleshq.lunar.sources.BackingConfig;
import dev.puzzleshq.lunar.sources.BackingConfigs;
import dev.puzzleshq.lunar.values.ConfigValue;
import dev.puzzleshq.lunar.values.ConfigValueList;
import dev.puzzleshq.lunar.values.ConfigValueRange;
import dev.puzzleshq.lunar.values.ConfigValues;

import java.io.File;
import java.lang.reflect.*;
import java.util.*;

public class PuzzleConfigSpec {

    private final List<String> path;
    private final List<ConfigValue<?>> values;
    private final List<PuzzleConfigSpec> subSpecs;
    private final Set<String> keySet;
    private final PuzzleConfigSpec baseSpec;
    private final Map<String, String> keyToTranslation;
    private BackingConfig config;

    protected PuzzleConfigSpec(BackingConfig config, List<String> path, List<ConfigValue<?>> values, List<PuzzleConfigSpec> subSpecs, Set<String> keySet) {
        this.path = path;
        this.values = values;
        this.subSpecs = subSpecs;
        this.keySet = keySet;
        this.baseSpec = null;
        this.config = config;
        this.keyToTranslation = new HashMap<>();
    }

    protected PuzzleConfigSpec(PuzzleConfigSpec baseSpec, List<String> path, List<ConfigValue<?>> values, List<PuzzleConfigSpec> subSpecs, Set<String> keySet) {
        this.path = path;
        this.values = values;
        this.subSpecs = subSpecs;
        this.keySet = keySet;
        this.baseSpec = baseSpec;
        this.config = null;
        this.keyToTranslation = new HashMap<>();
    }

    public void save() {
        getBaseConfig().save();
    }

    public void load() {
        getBaseConfig().load();
    }

    public PuzzleConfigSpec config(BackingConfig config) {
        if (this.baseSpec != null)
            throw new IllegalArgumentException("Cannot change config source on sub-config!");

        this.config = config;
        return this;
    }

    public String getTranslationKey(String key) {
        String langKey = this.keyToTranslation.get(key);
        return langKey == null ? key : langKey;
    }

    public void setTranslationKey(String key, String langKey) {
        if (!keySet.contains(key))
            throw new IllegalArgumentException("Cannot set translation key on non-existent entry!");
        this.keyToTranslation.put(key, langKey);
    }

    public Set<String> getKeySet() {
        return keySet;
    }

    public List<PuzzleConfigSpec> getSubSpecs() {
        return subSpecs;
    }

    public List<ConfigValue<?>> getValues() {
        return values;
    }

    public List<String> getPath() {
        return path;
    }

    public BackingConfig getBaseConfig() {
        if (baseSpec != null) {
            return baseSpec.getBaseConfig();
        }
        return config;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static <T> ConfigPair<T> ofClass(Class<T> clazz) {
        Builder builder = new Builder();
        T t = ofClass(builder, clazz);
        return new ConfigPair<>(builder.build(), t);
    }

    public static <T> ConfigPair<T> ofClass(Class<T> clazz, File file) {
        Builder builder = new Builder();
        T t = ofClass(builder, clazz);
        return new ConfigPair<>(builder.build(BackingConfigs.of(FileConfig.builder(file).sync().build())), t);
    }

    public static <T> ConfigPair<T> ofClass(Class<T> clazz, BackingConfig config) {
        Builder builder = new Builder();
        T t = ofClass(builder, clazz);
        return new ConfigPair<>(builder.build(config), t);
    }

    private static <T> T ofClass(Builder builder, Class<T> clazz) {
        if (clazz.getAnnotation(ConfigClass.class) == null)
            throw new IllegalArgumentException(clazz.getName() + " must be annotated with @Config to be used by or as a config!");

        T obj;
        try {
            obj = clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException("Failed to instance '" + clazz.getName() + "'", e);
        }

        try {
            for (Field declaredField : clazz.getDeclaredFields()) {
                String fieldName = declaredField.getName();
                Class<?> fieldType = declaredField.getType();
                declaredField.setAccessible(true);

                FieldLangKey translationKey = declaredField.getAnnotation(FieldLangKey.class);
                if (translationKey != null)
                    builder.key2TranslationKey.put(fieldName, translationKey.value());
                List<String> newPath = new ArrayList<>(builder.path);
                newPath.add(fieldName);
                if (ConfigValue.class.isAssignableFrom(declaredField.getType())) {
                    ConfigValue<?> val = (ConfigValue<?>) declaredField.get(obj);
                    val.key(fieldName);
                    val.path(newPath);
                    builder.values.add(val);

                    Comment comment = declaredField.getAnnotation(Comment.class);
                    if (comment != null) {
                        val.comment(comment.value());
                    }
                } else if (fieldType.getAnnotation(ConfigClass.class) != null) {
                    Object v = ofClass(builder.section(fieldName), fieldType);
                    declaredField.set(obj, v);
                } else {
                    throw new IllegalArgumentException("Cannot use field '" + fieldName + "' on class '" + clazz + "' because the field's type wasn't annotated with @Config or it is not of type 'ConfigValue'");
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        return obj;
    }

//    public <T> T apply(Class<T> clazz) {
//        Map<String, Field> fieldMap = new HashMap<>();
//
//        T obj;
//        try {
//            obj = clazz.getConstructor().newInstance();
//        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
//                 NoSuchMethodException e) {
//            throw new RuntimeException("Failed to instance '" + clazz.getName() + "'", e);
//        }
//
//        for (String k : this.getKeySet()) {
//            try {
//                Field field = clazz.getDeclaredField(k);
//                field.setAccessible(true);
//                fieldMap.put(k, field);
//            } catch (NoSuchFieldException e) {
//                throw new IllegalArgumentException("Could not find field '" + k + "' on class '" + clazz.getName() + "'");
//            }
//        }
//
//        for (ConfigValue<?> value : this.getValues()) {
//            Field field = fieldMap.get(value.key());
//            try {
//                field.set(obj, value);
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        for (PuzzleConfigSpec subSpec : this.getSubSpecs()) {
//            Field field = fieldMap.get(subSpec.path.get(subSpec.path.size() - 1));
//            Object specObj = subSpec.apply(field.getType());
//            try {
//                field.set(obj, specObj);
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        return obj;
//    }

    protected static class Builder {

        private final List<ConfigValue<?>> values = new ArrayList<>();
        private final List<Builder> subBuilders = new ArrayList<>();
        private final Set<String> keySet = new HashSet<>();
        private final List<String> path;
        public Map<String, String> key2TranslationKey;

        private Builder() {
            this.path = new ArrayList<>();
            this.key2TranslationKey = new HashMap<>();
        }

        private Builder(List<String> path) {
            this.path = path;
            this.key2TranslationKey = new HashMap<>();
        }

        public <T> ConfigValue<T> defineValue(String key, T defaultValue) {
            if (!keySet.add(key))
                throw new IllegalArgumentException("Cannot add key '" + key + "' when value or section already has one defined!");

            List<String> newPath = new ArrayList<>(path);
            newPath.add(key);
            ConfigValue<T> value = ConfigValues.value(defaultValue);
            value.key(key);
            value.path(newPath);
            values.add(value);
            return value;
        }

        public <T extends Comparable<T>> ConfigValueRange<T> defineRanged(String key, T defaultValue, T min, T max) {
            if (!keySet.add(key))
                throw new IllegalArgumentException("Cannot add key '" + key + "' when value or section already has one defined!");

            List<String> newPath = new ArrayList<>(path);
            newPath.add(key);
            ConfigValueRange<T> value = ConfigValues.range(defaultValue, min, max);
            value.key(key);
            value.path(newPath);
            values.add(value);
            return value;
        }

        public <T> ConfigValueList<T> defineList(String key, List<T> defaultValue, int min, int max) {
            if (!keySet.add(key))
                throw new IllegalArgumentException("Cannot add key '" + key + "' when value or section already has one defined!");

            List<String> newPath = new ArrayList<>(path);
            newPath.add(key);
            ConfigValueList<T> value = ConfigValues.list(defaultValue, min, max);
            value.key(key);
            value.path(newPath);
            values.add(value);
            return value;
        }

        public <T> ConfigValueList<T> defineList(String key, T[] defaultValue, int min, int max) {
            return defineList(key, Arrays.asList(defaultValue), min, max);
        }

        public Builder section(String key) {
            if (!keySet.add(key))
                throw new IllegalArgumentException("Cannot add key '" + key + "' when value or section already has one defined!");

            List<String> newPath = new ArrayList<>(path);
            newPath.add(key);
            Builder builder = new Builder(newPath);
            this.subBuilders.add(builder);
            return builder;
        }

        protected PuzzleConfigSpec build(PuzzleConfigSpec baseSpec) {
            List<PuzzleConfigSpec> subSpecs = new ArrayList<>();
            PuzzleConfigSpec spec = new PuzzleConfigSpec(baseSpec, path, values, subSpecs, keySet);
            spec.keyToTranslation.putAll(key2TranslationKey);
            for (Builder subBuilder : this.subBuilders) {
                subSpecs.add(subBuilder.build(baseSpec));
            }
            for (ConfigValue<?> value : this.values) {
                value.spec(spec);
                if (!value.exist()) value.setDefault();
                spec.getBaseConfig().setComment(value.path(), value.comment());
            }

            return spec;
        }

        public PuzzleConfigSpec build() {
            return build(BackingConfigs.of(CommentedConfig.inMemory()));
        }

        public PuzzleConfigSpec build(BackingConfig config) {
            List<PuzzleConfigSpec> subSpecs = new ArrayList<>();
            PuzzleConfigSpec spec = new PuzzleConfigSpec(config, path, values, subSpecs, keySet);
            spec.keyToTranslation.putAll(key2TranslationKey);
            spec.load();
            for (Builder subBuilder : this.subBuilders) {
                subSpecs.add(subBuilder.build(spec));
            }
            for (ConfigValue<?> value : this.values) {
                value.spec(spec);
                if (!value.exist()) value.setDefault();
                spec.getBaseConfig().setComment(value.path(), value.comment());
            }

            return spec;
        }

    }

}
