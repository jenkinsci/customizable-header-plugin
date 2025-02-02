package io.jenkins.plugins.customizable_header;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.User;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import io.jenkins.plugins.customizable_header.color.HeaderColor;
import io.jenkins.plugins.customizable_header.headers.HeaderSelector;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest2;

public class UserHeader extends UserProperty {

  private boolean overwriteHeader;

  private boolean overwriteColors;
  private HeaderColor headerColor;

  private HeaderSelector headerSelector;

  private List<AbstractLink> links = new ArrayList<>();

  private Set<String> dismissedMessages = new HashSet<>();

  @DataBoundConstructor
  public UserHeader() {
  }

  public boolean isOverwriteColors() {
    return overwriteColors;
  }

  @DataBoundSetter
  public void setOverwriteColors(boolean overwriteColors) {
    this.overwriteColors = overwriteColors;
  }

  @DataBoundSetter
  public void setHeaderColor(HeaderColor headerColor) {
    this.headerColor = headerColor;
  }

  @DataBoundSetter
  public void setHeaderSelector(HeaderSelector headerSelector) {
    this.headerSelector = headerSelector;
  }

  public HeaderColor getHeaderColor() {
    return headerColor;
  }

  public HeaderSelector getHeaderSelector() {
    return headerSelector;
  }

  @DataBoundSetter
  public void setOverwriteHeader(boolean overwriteHeader) {
    this.overwriteHeader = overwriteHeader;
  }

  public boolean isOverwriteHeader() {
    return overwriteHeader;
  }

  public List<AbstractLink> getLinks() {
    return links;
  }

  @DataBoundSetter
  public void setLinks(List<AbstractLink> links) {
    this.links = links;
  }

  public Object readResolve() {
    if (dismissedMessages == null) {
      dismissedMessages = new HashSet<>();
    }
    return this;
  }

  public Set<String> getDismissedMessages() {
    return dismissedMessages;
  }

  @Override
  public UserProperty reconfigure(StaplerRequest2 req, @CheckForNull JSONObject form) {
    if (links != null) {
      links.clear();
    }
    req.bindJSON(this, form);
    return this;
  }

  @Extension
  @Symbol("customHeader")
  public static class DescriptorImpl extends UserPropertyDescriptor {

    @Override
    public UserProperty newInstance(User user) {
      UserHeader userHeader = new UserHeader();
      HeaderColor globalHeaderColor = CustomHeaderConfiguration.get().getHeaderColor();
      userHeader.setHeaderColor(new HeaderColor(globalHeaderColor));
      return userHeader;
    }

    @Override
    public boolean isEnabled() {
      return CustomHeaderConfiguration.get().isEnabled();
    }

    @NonNull
    @Override
    public String getDisplayName() {
      return "Customizable Header";
    }

    //        @Override
    //        public @NonNull UserPropertyCategory getUserPropertyCategory() {
    //            return UserPropertyCategory.get(UserPropertyCategory.Appearance.class);
    //        }

    // replace with above method when bumping core to version including:
    // https://github.com/jenkinsci/jenkins/pull/7268
    public @CheckForNull String getUserPropertyCategoryAsString() {
        return "appearance";
    }
  }
}
