package io.jenkins.plugins.customizable_header;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.User;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class SystemMessage implements Describable<SystemMessage> {

  public static final DateTimeFormatter DATE_OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
  public static final DateTimeFormatter DATE_INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d H:m");

  private final String message;
  private transient String color;

  private SystemMessageColor level;
  private LocalDateTime expireDate;
  private String uid;

  private Boolean dismissible = true;

  @DataBoundConstructor
  public SystemMessage(String message, SystemMessageColor level, String uid) {
    this.message = Util.fixEmptyAndTrim(message);
    this.level = level;
    if (Util.fixEmptyAndTrim(uid) == null) {
      uid = UUID.randomUUID().toString();
    }
    this.uid = uid;
  }

  public Boolean getDismissible() {
    return dismissible;
  }

  @DataBoundSetter
  public void setDismissible(Boolean dismissible) {
    if (dismissible != null) {
      this.dismissible = dismissible;
    }
  }

  @DataBoundSetter
  public void setExpireDate(String expireDate) {
    if (Util.fixEmptyAndTrim(expireDate) != null) {
      this.expireDate = LocalDateTime.parse(expireDate, DATE_INPUT_FORMATTER);
    }
  }

  public String getUid() {
    return uid;
  }

  public String getExpireDate() {
    if (expireDate != null) {
      return expireDate.format(DATE_OUTPUT_FORMATTER);
    }
    return null;
  }

  public boolean isDismissed() {
    User user = User.current();
    if (user != null && dismissible) {
      UserHeader userHeader = user.getProperty(UserHeader.class);
      if (userHeader != null) {
        return userHeader.getDismissedMessages().contains(uid);
      }
    }
    return false;
  }

  public boolean isExpired() {
    if (expireDate != null) {
      LocalDateTime now = LocalDateTime.now();
      return expireDate.isBefore(now);
    }
    return false;
  }

  /**
   * Sets the color for the system message.
   * Only for backwards compatibility with CasC
   *
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
        level = SystemMessageColor.danger;
      }
      if (color.equals("orange")) {
        level = SystemMessageColor.warning;
      }
    }
  }

  public Object readResolve() {
    setColor(color);
    color = null;
    if (dismissible == null) {
      dismissible = true;
    }
    return this;
  }

  public String getMessage() {
    return message;
  }

  public SystemMessageColor getLevel() {
    return level;
  }

  public String getEscapedMessage() {
    if (message == null) {
      return "";
    }

    if (isExpired()) {
      return "";
    }

    StringWriter writer = new StringWriter();
    try {
      Jenkins.get().getMarkupFormatter().translate(message, writer);
      return writer.toString();
    } catch (IOException e) {
      return "";
    }

  }

  /**
   * The color for the system message.
   *
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

    public String getMinDate() {
      ZonedDateTime now = ZonedDateTime.now();
      return now.format(DATE_OUTPUT_FORMATTER);
    }

  }

  public enum SystemMessageColor {
    info("Informational"), warning("Warning"), danger("Error"), success("Success");
    private String text;

    SystemMessageColor(String text) {
      this.text = text;
    }

    public String getText() {
      return text;
    }

  }

}
