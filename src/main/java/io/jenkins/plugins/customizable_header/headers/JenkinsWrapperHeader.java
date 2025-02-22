package io.jenkins.plugins.customizable_header.headers;

import hudson.Extension;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;
import io.jenkins.plugins.customizable_header.logo.Logo;

@Extension
public class JenkinsWrapperHeader extends jenkins.views.JenkinsHeader implements SystemMessageProvider, LinkProvider {

  public String getBackgroundColor() {
    return CustomHeaderConfiguration.get().getActiveHeaderColor().getBackgroundColor();
  }

  public Logo getLogo() {
    return CustomHeaderConfiguration.get().getLogo();
  }

  public String getLogoText() {
    return CustomHeaderConfiguration.get().getLogoText();
  }

  public String getTitle() {
    return CustomHeaderConfiguration.get().getTitle();
  }

  // TODO - rename this
  public boolean getCustomizeAllowed() {
    return CustomHeaderConfiguration.get().isEnabled();
  }
}
