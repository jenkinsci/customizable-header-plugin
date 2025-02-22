package io.jenkins.plugins.customizable_header.color;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

public class HeaderColor extends AbstractDescribableImpl<HeaderColor> {

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
  }
}
