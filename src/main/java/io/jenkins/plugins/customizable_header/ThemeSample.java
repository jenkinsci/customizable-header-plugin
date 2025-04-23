package io.jenkins.plugins.customizable_header;

public final class ThemeSample {

  private final String displayName;

  private final String backgroundColor;

  private final String color;

  private final boolean inherit;

  public ThemeSample(String displayName, String backgroundColor, String color) {
    this(displayName, backgroundColor, color, false);
  }

  public ThemeSample(String displayName, String backgroundColor, String color, boolean inherit) {
    this.displayName = displayName;
    this.backgroundColor = backgroundColor;
    this.color = color;
    this.inherit = inherit;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getBackgroundColor() {
    return backgroundColor;
  }

  public String getConfigBackgroundColor() {
    if (inherit) {
      return "inherit";
    }
    return backgroundColor;
  }

  public String getConfigColor() {
    if (inherit) {
      return "inherit";
    }
    return color;
  }

  public String getColor() {
    return color;
  }
}
