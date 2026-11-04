import dev.puzzleshq.lunar.*;
import dev.puzzleshq.lunar.sources.BackingConfig;
import dev.puzzleshq.lunar.sources.BackingConfigs;
import dev.puzzleshq.lunar.values.ConfigValue;
import dev.puzzleshq.lunar.values.ConfigValues;
import dev.puzzleshq.lunar.values.primitive.ConfigValueIntArray;

import java.io.File;
import java.util.Arrays;

public class Example {

    @ConfigClass
    public static class ExampleConfig {

        @FieldLangKey("example.config.number")
        @Comment("This is an example for a number!")
        ConfigValue<Integer> number = ConfigValues.value(5);

        @FieldLangKey("example.config.test")
        ExampleConfigTest test;

        @ConfigClass
        public static class ExampleConfigTest {

            @FieldLangKey("example.config.test.hi")
            @Comment("This is an example for a boolean!")
            ConfigValue<Boolean> hi = ConfigValues.value(true);

            ExampleConfigTest2 test2;

        }

        @ConfigClass
        public static class ExampleConfigTest2 {

            @FieldLangKey("example.config.test.test2.hi")
            @Comment("This is an example for an array!")
            ConfigValueIntArray hi = ConfigValues.array(new int[]{1, 2}, 1, 2);

        }

    }

    public static void main(String[] args) {
        BackingConfig config = BackingConfigs.of(new File("test.toml"), false, true, true);
        ConfigPair<ExampleConfig> configPair = PuzzleConfigSpec.ofClass(ExampleConfig.class, config);
        PuzzleConfigSpec spec = configPair.getSpec();
        ExampleConfig exampleConfig = configPair.getConfig();
        spec.save();

        System.out.println(exampleConfig.number.get());
        System.out.println(exampleConfig.test.hi.get());
        System.out.println(exampleConfig.test.hi.langKey());
        System.out.println(Arrays.toString(exampleConfig.test.test2.hi.get()));
    }

}
