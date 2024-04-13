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

  /**
   * Sets the color for the system message.
   * Only for backwards compatibility with CasC
   * @param color
   * @deprecated instead set the level in the constructor
   */
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
    color = null;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public SystemMessageColor getLevel() {
    return level;
  }

  /**
   * The color for the system message.
   * @return color
   * @deprecated use {@link #getLevel()}
   */
  @Deprecated
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
    info, warning, danger, success;
  }

}
