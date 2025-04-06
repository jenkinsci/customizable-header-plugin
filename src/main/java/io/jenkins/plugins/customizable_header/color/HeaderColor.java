package io.jenkins.plugins.customizable_header.color;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import io.jenkins.plugins.customizable_header.ThemeSample;
import java.util.List;
import org.kohsuke.stapler.DataBoundConstructor;

public class HeaderColor implements Describable<HeaderColor> {

  private final String backgroundColor;
  private final String color;

  @DataBoundConstructor
  public HeaderColor(String backgroundColor, String color) {
    this.backgroundColor = backgroundColor;
    this.color = color;
  }

  public HeaderColor(HeaderColor headerColor) {
    this.backgroundColor = headerColor.getBackgroundColor();
    this.color = headerColor.getColor();
  }

  public String getBackgroundColor() {
    return backgroundColor;
  }

  public String getColor() {
    return color;
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<HeaderColor> {

    @NonNull
    @Override
    public String getDisplayName() {
      return "Color";
    }

    public List<ThemeSample> getSamples() {
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
}
