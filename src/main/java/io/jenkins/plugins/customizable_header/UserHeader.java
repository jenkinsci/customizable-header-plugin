package io.jenkins.plugins.customizable_header;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.User;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import hudson.model.userproperty.UserPropertyCategory;
import io.jenkins.plugins.customizable_header.color.HeaderColor;
import io.jenkins.plugins.customizable_header.logo.Icon;
import io.jenkins.plugins.customizable_header.logo.Logo;
import io.jenkins.plugins.customizable_header.logo.LogoDescriptor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest2;

public class UserHeader extends UserProperty {

  private boolean enabled;

  private boolean thinHeader;

  private HeaderColor headerColor;

  private Logo logo;

  private List<AbstractLink> links = new ArrayList<>();

  private Set<String> dismissedMessages = new HashSet<>();

  @DataBoundConstructor
  public UserHeader() {
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

  @DataBoundSetter
  public void setLogo(Logo logo) {
    this.logo = logo;
  }

  public Logo getLogo() {
    return logo;
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

    @Override
    public @NonNull UserPropertyCategory getUserPropertyCategory() {
        return UserPropertyCategory.get(UserPropertyCategory.Appearance.class);
    }
  }

  // TODO - this should be combined with CustomHeaderConfiguration#401
  public List<Descriptor<Logo>> getLogoDescriptors() {
    return LogoDescriptor.all().stream()
            .filter(d -> !(d instanceof Icon.DescriptorImpl))
            .collect(Collectors.toList());
  }

  // TODO - this should be combined with CustomHeaderConfiguration#407
  public List<ThemeSample> getSamples() {
    return List.of(
            new ThemeSample("Default", null, null),
            new ThemeSample("Classic", "color-mix(in srgb, var(--black) 85%, transparent)", "var(--white)"),
            new ThemeSample("Hudson", "linear-gradient(#3465A4, #89A3DC calc(100% - 4px), #FCAF3E calc(100% - 4px), #FCAF3E) no-repeat",  "var(--white)"),
            new ThemeSample("Accent", "var(--accent-color)", "var(--white)"),
            new ThemeSample("Rainbow", "linear-gradient(45deg in oklch, var(--red), var(--orange), var(--yellow), var(--green), var(--blue), var(--indigo), var(--purple))", "var(--white)")
    );
  }
}
