package dev.windv.wvc.settings;

public class SliderSetting extends Setting {
    private double value;
    private double min;
    private double max;
    private boolean integer;

    public SliderSetting(String name, double defaultValue, double min, double max, boolean integer) {
        super(name);
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.integer = integer;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        double precision = 1.0 / 0.1; // Default 0.1 precision
        this.value = Math.round(Math.max(min, Math.min(max, value)) * precision) / precision;
    }

    public float getValueFloat() {
        return (float) value;
    }

    public int getValueInt() {
        return (int) value;
    }

    public double getMin() { return min; }
    public double getMax() { return max; }
}
