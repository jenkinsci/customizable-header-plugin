package io.jenkins.plugins.customizable_header;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.User;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import hudson.model.userproperty.UserPropertyCategory;
import io.jenkins.plugins.customizable_header.color.HeaderColor;
import java.io.IOException;
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

  private boolean enabled;

  private boolean thinHeader;

  private HeaderColor headerColor;

  private List<AbstractLink> links = new ArrayList<>();

  private Set<String> dismissedMessages = new HashSet<>();

  private ContextAwareLogo contextAwareLogo;

  @DataBoundConstructor
  public UserHeader() {
  }

  @Override
  protected void setUser(User u) {
    super.setUser(u);
    if (headerColor != null && headerColor.getUserColors() == null) {
      headerColor.setUserColors(true);
      try {
        u.save();
      } catch (IOException e) {
        //
      }
    }
  }

  @DataBoundSetter
  public void setContextAwareLogo(ContextAwareLogo contextAwareLogo) {
    this.contextAwareLogo = contextAwareLogo;
  }

  public ContextAwareLogo getContextAwareLogo() {
    return contextAwareLogo;
  }

  public boolean isEnabled() {
    return enabled;
  }

  @DataBoundSetter
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isThinHeader() {
    return thinHeader;
  }

  @DataBoundSetter
  public void setThinHeader(boolean thinHeader) {
    this.thinHeader = thinHeader;
  }

  @DataBoundSetter
  public void setHeaderColor(HeaderColor headerColor) {
    this.headerColor = headerColor;
  }

  public HeaderColor getHeaderColor() {
    return headerColor;
  }

  public List<AbstractLink> getLinks() {
    return links;
  }

  @DataBoundSetter
  public void setLinks(List<AbstractLink> links) {
    this.links = links;
  }

  public Object readResolve() throws IOException {
    if (dismissedMessages == null) {
      dismissedMessages = new HashSet<>();
    }
    return this;
  }

  public void setDismissedMessages(Set<String> dismissedMessages) {
    this.dismissedMessages = dismissedMessages;
  }

  public Set<String> getDismissedMessages() {
    return dismissedMessages;
  }

  @Override
  public UserProperty reconfigure(StaplerRequest2 req, @CheckForNull JSONObject form) {
    if (links != null) {
      links.clear();
    }
    contextAwareLogo = null;
    req.bindJSON(this, form);
    headerColor.setUserColors(true);
    return this;
  }

  @Extension
  @Symbol("customHeader")
  public static class DescriptorImpl extends UserPropertyDescriptor {

    @Override
    public UserProperty newInstance(User user) {
      UserHeader userHeader = new UserHeader();
      userHeader.setHeaderColor(new HeaderColor("inherit", "inherit", true));
      return userHeader;
    }

    @NonNull
    @Override
    public String getDisplayName() {
      return "Customizable Header";
    }

    @Override
    public @NonNull UserPropertyCategory getUserPropertyCategory() {
        return UserPropertyCategory.get(UserPropertyCategory.Appearance.class);
    }
  }
}
