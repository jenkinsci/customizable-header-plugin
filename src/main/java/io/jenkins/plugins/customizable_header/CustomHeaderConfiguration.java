package io.jenkins.plugins.customizable_header;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import hudson.Extension;
import hudson.Util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import hudson.model.User;
import io.jenkins.plugins.customizable_header.color.HeaderColor;
import io.jenkins.plugins.customizable_header.headers.HeaderSelector;
import io.jenkins.plugins.customizable_header.headers.JenkinsHeaderSelector;
import io.jenkins.plugins.customizable_header.headers.LogoSelector;
import io.jenkins.plugins.customizable_header.logo.Logo;
import io.jenkins.plugins.customizable_header.logo.Symbol;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

@Extension
public class CustomHeaderConfiguration extends GlobalConfiguration {

  private static final Logger LOGGER = Logger.getLogger(CustomHeaderConfiguration.class.getName());
  private String title = "";

  private String cssResource;

  private String logoText = "Jenkins";

  private Logo logo = new Symbol("symbol-jenkins");

  private HeaderSelector header = new LogoSelector();

  private boolean enabled = true;

  private transient String cssResourceUrl;

  private HeaderColor headerColor = new HeaderColor("black", "grey", "white");

  private boolean thinHeader;

  private SystemMessage systemMessage;

  private List<AppNavLink> links = new ArrayList<>();

  @DataBoundConstructor
  public CustomHeaderConfiguration() {
    load();
  }

  @Override
  public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
    links.clear();
    return super.configure(req, json);
  }

  public Object readResolve() {
    if (systemMessage == null) {
      systemMessage = new SystemMessage("", SystemMessage.SystemMessageColor.lightyellow);
    }
    return this;
  }

  public SystemMessage getSystemMessage() {
    return systemMessage;
  }

  @DataBoundSetter
  public void setSystemMessage(SystemMessage systemMessage) {
    this.systemMessage = systemMessage;
  }

  public List<AppNavLink> getLinks() {
    return links;
  }

  @DataBoundSetter
  public void setLinks(List<AppNavLink> links) {
    this.links = links;
    save();
  }

  public boolean hasLinks() {
    if (links == null) {
      return false;
    }
    return links.size() != 0;
  }

  public boolean isThinHeader() {
    return thinHeader;
  }

  @DataBoundSetter
  public void setThinHeader(boolean thinHeader) {
    this.thinHeader = thinHeader;
    save();
  }

  @DataBoundSetter
  public void setHeader(HeaderSelector header) {
    this.header = header;
    save();
  }

  public HeaderSelector getHeader() {
    return header;
  }

  @DataBoundSetter
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    save();
  }

  public boolean isEnabled() {
    return enabled;
  }

  @DataBoundSetter
  public void setLogo(Logo logo) {
    this.logo = logo;
    save();
  }

  public Logo getLogo() {
    return logo;
  }

  @DataBoundSetter
  public void setHeaderColor(HeaderColor headerColor) {
    this.headerColor = headerColor;
    save();
  }

  /**
   * The globally configured Headercolor.
   * @return global headercolor.
   */
  public HeaderColor getHeaderColor() {
    return headerColor;
  }

  /**
   * The active header color.
   * If the user has overwritten the colors those colors are used.
   * @return
   */
  public HeaderColor getActiveHeaderColor() {
    User user = User.current();
    if (user != null) {
      UserHeader userHeader = user.getProperty(UserHeader.class);
      if (userHeader != null && userHeader.isOverwriteColors()) {
        return userHeader.getHeaderColor();
      }
    }
    return headerColor;
  }

  public HeaderSelector getActiveHeader() {
    if (enabled) {
      User user = User.current();
      if (user != null) {
        UserHeader userHeader = user.getProperty(UserHeader.class);
        if (userHeader != null && userHeader.isOverwriteHeader()) {
          return userHeader.getHeaderSelector();
        }
      }
      return header;
    }
    return new JenkinsHeaderSelector();
  }

  @DataBoundSetter
  public void setLogoText(String logoText) {
    this.logoText = logoText;
    save();
  }

  public String getLogoText() {
    return logoText;
  }

  public static CustomHeaderConfiguration get() {
    return GlobalConfiguration.all().get(CustomHeaderConfiguration.class);
  }

  public String getTitle() {
    return title;
  }

  @DataBoundSetter
  public void setTitle(String title) {
    this.title = title;
    save();
  }

  public String getCssResource() {
    return Util.fixEmptyAndTrim(cssResource);
  }

  @DataBoundSetter
  public void setCssResource(String cssResource) {
    this.cssResource = cssResource;
    setCssResourceUrl();
    save();
  }

  private void setCssResourceUrl() {
    if (Util.fixEmptyAndTrim(cssResource) != null) {
      try {
        URI uri = URI.create(cssResource);
        if (uri.isAbsolute()) {
          cssResourceUrl = cssResource;
        } else {
          cssResourceUrl = Jenkins.get().getRootUrl() + cssResource;
        }
      } catch (IllegalArgumentException iae) {
        LOGGER.log(Level.WARNING, "The given css resource is not a valid uri", iae);
        cssResourceUrl = "";
      }
    } else {
      cssResourceUrl = "";
    }
  }
  public String getCssResourceUrl() {
    if (cssResourceUrl == null) {
      setCssResourceUrl();
    }
    return cssResourceUrl;
  }
  public Logo defaultLogo() {
    return new Symbol("symbol-jenkins");
  }
}
