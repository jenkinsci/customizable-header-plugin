package io.jenkins.plugins.customizable_header;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

public class SystemMessage extends AbstractDescribableImpl<SystemMessage> {
  private final String message;
  private final SystemMessageColor color;

  @DataBoundConstructor
  public SystemMessage(String message, SystemMessageColor color) {
    this.message = message;
    this.color = color;
  }

  public String getMessage() {
    return message;
  }

  public SystemMessageColor getColor() {
    return color;
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<SystemMessage> {
    @NonNull
    @Override
    public String getDisplayName() {
      return "System Message";
    }

  }

  public static enum SystemMessageColor {
    red, orange, lightyellow;
  }

}
