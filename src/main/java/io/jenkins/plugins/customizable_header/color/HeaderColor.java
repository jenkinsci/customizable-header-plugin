package io.jenkins.plugins.customizable_header.color;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.User;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;
import io.jenkins.plugins.customizable_header.ThemeSample;
import java.util.List;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.Stapler;

public class HeaderColor implements Describable<HeaderColor> {

  private final String backgroundColor;
  private final String color;
  private Boolean userColors = null;

  @DataBoundConstructor
  public HeaderColor(String backgroundColor, String color) {
    this(backgroundColor, color, false);
  }

  public HeaderColor(String backgroundColor, String color, boolean userColors) {
    this.backgroundColor = backgroundColor;
    this.color = color;
    this.userColors = userColors;
  }

  public void setUserColors(boolean userColors) {
    this.userColors = userColors;
  }

  public Boolean getUserColors() {
    if (userColors == null) {
      return false;
    }
    return userColors;
  }

  public String getBackgroundColor() {
    return backgroundColor;
  }

  public String getActiveBackgroundColor() {
    if (userColors != null && userColors && "inherit".equals(backgroundColor)) {
      return CustomHeaderConfiguration.get().getHeaderColor().getBackgroundColor();
    }
    return backgroundColor;
  }

  public String getColor() {
    return color;
  }

  public String getActiveColor() {
    if (userColors != null && userColors && "inherit".equals(color)) {
      return CustomHeaderConfiguration.get().getHeaderColor().getColor();
    }
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
      List<ThemeSample> samples = new java.util.ArrayList<>(List.of(
          new ThemeSample("Default", null, null),
          new ThemeSample("Classic", "color-mix(in srgb, var(--black) 85%, transparent)", "var(--white)"),
          new ThemeSample("Hudson", "linear-gradient(#3465A4, #89A3DC calc(100% - 4px), #FCAF3E calc(100% - 4px), #FCAF3E) no-repeat", "var(--white)"),
          new ThemeSample("Accent", "var(--accent-color)", "var(--white)"),
          new ThemeSample("Rainbow", "linear-gradient(45deg in oklch, var(--red), var(--orange), var(--yellow), var(--green), var(--blue), var(--indigo), var(--purple))", "var(--white)"),
          new ThemeSample("Gradient", "linear-gradient(90deg, rgba(28,99,190,1) 0%, rgba(53,224,192,1) 100%);", "var(--white)")
      ));
      if (Stapler.getCurrentRequest2().findAncestorObject(User.class) != null) {
        HeaderColor global = CustomHeaderConfiguration.get().getHeaderColor();
        samples.add(new ThemeSample("Inherit", global.getBackgroundColor(), global.getColor(), true));
      }
      return samples;
    }
  }
}
