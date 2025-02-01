package io.jenkins.plugins.customizable_header;

public final class Thingy {
    private final String displayName;
    private final String backgroundColor;
    private final String color;

    public Thingy(String displayName, String backgroundColor, String color) {
        this.displayName = displayName;
        this.backgroundColor = backgroundColor;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getColor() {
        return color;
    }
}
