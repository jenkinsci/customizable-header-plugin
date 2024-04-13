package io.jenkins.plugins.customizable_header;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class SystemMessage extends AbstractDescribableImpl<SystemMessage> {
  private final String message;
  private transient String color;

  private SystemMessageColor level;

  @DataBoundConstructor
  public SystemMessage(String message, SystemMessageColor level) {
    this.message = message;
    this.level = level;
  }

  @DataBoundSetter
  @Deprecated
  public void setColor(String color) {
    if (color != null) {
      if (color.equals("lightyellow")) {
        level = SystemMessageColor.info;
      }
      if (color.equals("red")) {
        level = SystemMessageColor.info;
      }
      if (color.equals("orange")) {
        level = SystemMessageColor.info;
      }
    }
  }

  public Object readResolve() {
    setColor(color);
    return this;
  }

  public String getMessage() {
    return message;
  }

  public SystemMessageColor getLevel() {
    return level;
  }

  public String getColor() {
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

  public enum SystemMessageColor {
    danger, warning, info, success;
  }

}
