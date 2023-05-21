package io.jenkins.plugins.customizable_header;

import hudson.Extension;
import hudson.Util;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.jenkins.plugins.customizable_header.color.HeaderColor;
import io.jenkins.plugins.customizable_header.logo.Logo;
import io.jenkins.plugins.customizable_header.logo.Symbol;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

@Extension
public class CustomHeaderConfiguration extends GlobalConfiguration {

  private static final Logger LOGGER = Logger.getLogger(CustomHeaderConfiguration.class.getName());
  private String title = "";

  private String cssResource;

  private String logoText = "Jenkins";

  private Logo logo = new Symbol("symbol-jenkins");

  private boolean enabled = true;

  private transient String cssResourceUrl;

  private HeaderColor headerColor = new HeaderColor("black", "grey", "white");

  @DataBoundConstructor
  public CustomHeaderConfiguration() {
    load();
  }

  @DataBoundSetter
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
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
  }

  public HeaderColor getHeaderColor() {
    return headerColor;
  }

  @DataBoundSetter
  public void setLogoText(String logoText) {
    this.logoText = logoText;
    save();
  }

  public String getLogoText() {
    return logoText;
  }

  public static CustomHeaderConfiguration getInstance() {
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
