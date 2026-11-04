package dev.puzzleshq.lunar.values.primitive;

public class ConfigValuePrimitives {

    public static ConfigValueDoubleArray array(double[] defaultValue, int min, int max) {
        return new ConfigValueDoubleArray(null, defaultValue, min, max);
    }

    public static ConfigValueFloatArray array(float[] defaultValue, int min, int max) {
        return new ConfigValueFloatArray(null, defaultValue, min, max);
    }

    public static ConfigValueLongArray array(long[] defaultValue, int min, int max) {
        return new ConfigValueLongArray(null, defaultValue, min, max);
    }

    public static ConfigValueIntArray array(int[] defaultValue, int min, int max) {
        return new ConfigValueIntArray(null, defaultValue, min, max);
    }

    public static ConfigValueShortArray array(short[] defaultValue, int min, int max) {
        return new ConfigValueShortArray(null, defaultValue, min, max);
    }

    public static ConfigValueByteArray array(byte[] defaultValue, int min, int max) {
        return new ConfigValueByteArray(null, defaultValue, min, max);
    }

}
