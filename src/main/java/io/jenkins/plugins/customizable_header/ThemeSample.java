package io.jenkins.plugins.customizable_header;

import java.util.List;

public final class ThemeSample {

    private final String displayName;

    private final String backgroundColor;

    private final String color;

    public ThemeSample(String displayName, String backgroundColor, String color) {
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

  public static List<ThemeSample> getSamples() {
    return List.of(
        new ThemeSample("Default", null, null),
        new ThemeSample("Classic", "color-mix(in srgb, var(--black) 85%, transparent)", "var(--white)"),
        new ThemeSample("Hudson", "linear-gradient(#3465A4, #89A3DC calc(100% - 4px), #FCAF3E calc(100% - 4px), #FCAF3E) no-repeat",  "var(--white)"),
        new ThemeSample("Accent", "var(--accent-color)", "var(--white)"),
        new ThemeSample("Rainbow", "linear-gradient(45deg in oklch, var(--red), var(--orange), var(--yellow), var(--green), var(--blue), var(--indigo), var(--purple))", "var(--white)"),
        new ThemeSample("Gradient", "linear-gradient(90deg, rgba(28,99,190,1) 0%, rgba(53,224,192,1) 100%);", "var(--white)")
    );
  }

}
