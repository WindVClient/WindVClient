package dev.windv.wvc.settings;

public abstract class Setting {
    private String name;

    public Setting(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
