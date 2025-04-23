package io.jenkins.plugins.customizable_header;

import hudson.Extension;
import hudson.model.PageDecorator;
import io.jenkins.plugins.customizable_header.logo.ImageLogo;
import io.jenkins.plugins.customizable_header.logo.Logo;
import org.jenkinsci.Symbol;

@Extension
@Symbol("customHeader")
public class CustomHeaderDecorator extends PageDecorator {

  public String getCssResourceUrl() {
    return CustomHeaderConfiguration.get().getCssResourceUrl();
  }

  public boolean isImageLogo() {
    return CustomHeaderConfiguration.get().getActiveLogo() instanceof ImageLogo;
  }

  public String getImageUrl() {
    Logo logo = CustomHeaderConfiguration.get().getActiveLogo();
    if (logo instanceof ImageLogo) {
      return ((ImageLogo) logo).getUrl();
    }
    return "";
  }

  public boolean isEnabled() {
    return CustomHeaderConfiguration.get().isEnabled();
  }
}
