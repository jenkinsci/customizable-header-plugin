package io.jenkins.plugins.customizable_header;

import hudson.Extension;
import hudson.Util;
import java.util.logging.Logger;

import io.jenkins.plugins.customizable_header.color.HeaderColor;
import io.jenkins.plugins.customizable_header.logo.DefaultLogo;
import io.jenkins.plugins.customizable_header.logo.Logo;
import jenkins.model.GlobalConfiguration;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

@Extension
public class CustomHeaderConfiguration extends GlobalConfiguration {

  private static final Logger LOGGER = Logger.getLogger(CustomHeaderConfiguration.class.getName());
  private String title = "";

  private String cssResource;

  private String logoText = "Jenkins";

  private Logo logo = new DefaultLogo();

  private HeaderColor headerColor = new HeaderColor("black", "grey", "white");

  @DataBoundConstructor
  public CustomHeaderConfiguration() {
    load();
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

  public void setCssResource(String cssResource) {
    this.cssResource = cssResource;
    save();
  }

  public Logo defaultLogo() {
    return new DefaultLogo();
  }
}
